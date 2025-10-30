package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.ReturnHandler.ReturnHandlerStack;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.skript.log.RetainingLogHandler;
import ch.njol.skript.log.SkriptLogger;
import ch.njol.skript.registrations.Classes;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;
import org.jetbrains.annotations.UnknownNullability;

import java.util.List;

@Name("Return")
@Description("Makes a trigger or a section (e.g. a function) return a value")
@Examples({
	"function double(i: number) :: number:",
		"\treturn 2 * {_i}",
	"",
	"function divide(i: number) returns number:",
		"\treturn {_i} / 2"
})
@Since("2.2, 2.8.0 (returns aliases)")
public class EffReturn extends Effect {

	static {
		Skript.registerEffect(EffReturn.class, "return %objects%");
		ParserInstance.registerData(ReturnHandlerStack.class, ReturnHandlerStack::new);
	}

	private @UnknownNullability ReturnHandler<?> handler;
	private @UnknownNullability Expression<?> value;
	private @UnknownNullability List<SectionExitHandler> sectionsToExit;
	private int breakLevels;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		ParserInstance parser = getParser();
		handler = parser.getData(ReturnHandlerStack.class).getCurrentHandler();
		if (handler == null) {
			Skript.error("The return statement cannot be used here");
			return null;
		}

		if (!isDelayed.isFalse()) {
			Skript.error("A return statement after a delay is useless, as the calling trigger will resume when the delay starts (and won't get any returned value)");
			return null;
		}

		Class<?> returnType = handler.returnValueType();
		if (returnType == null) {
			Skript.error(handler + " doesn't return any value. Please use 'stop' or 'exit' if you want to stop the trigger.");
			return null;
		}

		RetainingLogHandler log = SkriptLogger.startRetainingLog();
		Expression<?> convertedExpr;
		try {
			convertedExpr = exprs[0].getConvertedExpression(returnType);
			if (convertedExpr == null) {
				String typeName = Classes.getSuperClassInfo(returnType).getName().withIndefiniteArticle();
				log.printErrors(handler + " is declared to return " + typeName + ", but " + exprs[0].toString(null, false) + " is not of that type.");
				return null;
			}
			log.printLog();
		} finally {
			log.stop();
		}

		if (handler.isSingleReturnValue() && !convertedExpr.isSingle()) {
			String typeName = Classes.getSuperClassInfo(returnType).getName().getSingular();
			Skript.error(handler + " is defined to only return a single " + typeName + ", but this return statement can return multiple values.");
			return null;
		}
		value = convertedExpr;

		List<TriggerSection> innerSections = parser.getSectionsUntil((TriggerSection) handler);
		innerSections.add(0, (TriggerSection) handler);
		breakLevels = innerSections.size();
		if (parser.getCurrentSections().size() < breakLevels)
			breakLevels = -1;
		sectionsToExit = innerSections.stream()
			.filter(SectionExitHandler.class::isInstance)
			.map(SectionExitHandler.class::cast)
			.toList();
		return this;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		//noinspection rawtypes,unchecked
		((ReturnHandler) handler).returnValues(frame, value);

		for (SectionExitHandler section : sectionsToExit)
			section.exit(frame);


		throw new Trigger.ReturnException();
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		assert false;
	}

	@Override
	public ExecutionIntent executionIntent() {
		if (breakLevels == -1)
			return ExecutionIntent.stopTrigger();
		return ExecutionIntent.stopSections(breakLevels);
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "return " + value.toString(event, debug);
	}

}