package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

@Name("Loop Iteration")
@Description("Returns the loop's current iteration count (for both normal and while loops).")
@Examples({
	"while player is online:",
	"\tgive player 1 stone",
	"\twait 5 ticks",
	"\tif loop-counter > 30:",
	"\t\tstop loop",
	"",
	"loop {top-balances::*}:",
	"\tif loop-iteration <= 10:",
	"\t\tbroadcast \"#%loop-iteration% %loop-index% has $%loop-value%\"",
})
@Since("2.8.0")
public class ExprLoopIteration extends SimpleExpression<Long> {

	static {
		Skript.registerExpression(ExprLoopIteration.class, Long.class, ExpressionType.SIMPLE, "[the] loop(-| )(counter|iteration)[-%-*number%]");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private LoopSection loop;

	private int loopNumber;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		loopNumber = -1;
		if (exprs[0] != null)
			loopNumber = ((Literal<Number>) exprs[0]).getSingle().intValue();

		int i = 1;
		LoopSection loop = null;

		for (LoopSection l : getParser().getCurrentSections(LoopSection.class)) {
			if (i < loopNumber) {
				i++;
				continue;
			}
			if (loop != null) {
				Skript.error("There are multiple loops. Use loop-iteration-1/2/3/etc. to specify which loop-iteration you want.");
				return null;
			}
			loop = l;
			if (i == loopNumber)
				break;
		}

		if (loop == null) {
			Skript.error("The loop iteration expression must be used in a loop");
			return null;
		}

		this.loop = loop;
		return this;
	}

	@Override
	protected Long[] execute(VirtualFrame event) {
		return new Long[]{loop.getLoopCounter(event)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Long> getReturnType() {
		return Long.class;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "loop-iteration" + (loopNumber != -1 ? ("-" + loopNumber) : "");
	}

}