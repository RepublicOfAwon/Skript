package ch.njol.skript.test.runner;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

@NoDoc
public class EffRunRunnable extends Effect {

	static {
		if (TestMode.ENABLED)
			Skript.registerEffect(EffRunRunnable.class, "run runnable %object%");
	}

	private Expression<?> task;

	@Override
	public SyntaxElement init(Expression<?>[] expressions, int pattern, Kleenean delayed, ParseResult result) {
		this.task = LiteralUtils.defendExpression(expressions[0]);
		return LiteralUtils.canInitSafely(task) ? this : null;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		Object single = this.task.executeSingle(event);
		if (single instanceof Runnable runnable) {
			runnable.run();
		} else {
			Bukkit.getLogger().severe("Tried to run a non-runnable " + single);
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "run runnable " + task.toString(event, debug);
	}

}
