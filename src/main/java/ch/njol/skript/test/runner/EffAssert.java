package ch.njol.skript.test.runner;

import ch.njol.skript.Skript;
import ch.njol.skript.config.Node;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.log.ParseLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.script.Script;

@Name("Assert")
@Description("Assert that condition is true. Test fails when it is not.")
@NoDoc
public class EffAssert extends Effect {

	private static final String DEFAULT_ERROR = "Assertion failed.";

	static {
		if (TestMode.ENABLED)
			Skript.registerEffect(EffAssert.class,
				"assert [:unsafely] <.+> [(1:to fail)] with [error] %string%",
				"assert [:unsafely] <.+> [(1:to fail)] with [error] %string%, expected [value] %object%, [and] (received|got) [value] %object%",
				"assert [:unsafely] <.+> [(1:to fail)]");
	}

	private @Nullable Condition condition;
	private Script script;
	private int line;

	private @Nullable Expression<String> errorMsg;
	private @Nullable Expression<?> expected;
	private @Nullable Expression<?> got;
	private boolean shouldFail;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (isDelayed == Kleenean.TRUE && !parseResult.hasTag("unsafely") && !TestMode.JUNIT && !TestMode.DEV_MODE) {
			Skript.error("Assertions cannot be delayed");
			return null;
		}

		String conditionString = parseResult.regexes.get(0).group();
		if (matchedPattern < 2)
			this.errorMsg = (Expression<String>) exprs[0];
		boolean canInit = true;
		if (exprs.length > 1) {
			this.expected = LiteralUtils.defendExpression(exprs[1]);
			this.got = LiteralUtils.defendExpression(exprs[2]);
			canInit = LiteralUtils.canInitSafely(expected, got);
		}
		this.shouldFail = parseResult.mark != 0;
		this.script = getParser().getCurrentScript();
		Node node = getParser().getNode();
		this.line = node != null ? node.getLine() : -1;

		try (ParseLogHandler logHandler = SkriptLogger.startParseLogHandler()) {
			this.condition = Condition.parse(conditionString, "Can't understand this condition: " + conditionString);

			if (shouldFail)
				return this;

			if (condition == null) {
				logHandler.printError();
			} else {
				logHandler.printLog();
			}
		}

		return (condition != null) && canInit ? this : null;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
	}

	@Override
	public Object execute(VirtualFrame frame) {
		if (shouldFail && condition == null)
			return null;

		if (condition.executeBoolean(frame) == shouldFail) {
			String message = errorMsg != null ? errorMsg.executeOptional(frame).orElse(DEFAULT_ERROR) : DEFAULT_ERROR;

			// generate expected/got message if possible
			String expectedMessage = "";
			String gotMessage = "";
			if (expected != null)
				expectedMessage = VerboseAssert.getExpressionValue(expected, frame);
			if (got != null)
				gotMessage = VerboseAssert.getExpressionValue(got, frame);

			if (condition instanceof VerboseAssert) {
				if (expectedMessage.isEmpty())
					expectedMessage = ((VerboseAssert) condition).getExpectedMessage(frame);
				if (gotMessage.isEmpty())
					gotMessage = ((VerboseAssert) condition).getReceivedMessage(frame);
			}

			if (!expectedMessage.isEmpty() && !gotMessage.isEmpty())
				message += " (Expected " + expectedMessage + ", but got " + gotMessage + ")";

			if (SkriptJUnitTest.getCurrentJUnitTest() != null) {
				TestTracker.junitTestFailed(SkriptJUnitTest.getCurrentJUnitTest(), message);
			} else {
				if (line >= 0) {
					TestTracker.testFailed(message, script, line);
				} else {
					TestTracker.testFailed(message, script);
				}
			}
			throw new ControlFlowException();
		}
		return null;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		if (condition == null)
			return "assertion";
		return "assert " + condition.toString(event, debug);
	}

}