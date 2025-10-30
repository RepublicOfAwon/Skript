package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.function.DynamicFunctionReference;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;
import ch.njol.skript.registrations.experiments.ReflectionExperimentSyntax;
import org.skriptlang.skript.util.Executable;

@Name("Run (Experimental)")
@Description("Executes a task (a function). Any returned result is discarded.")
@Examples({
		"set {_function} to the function named \"myFunction\"",
		"run {_function}",
		"run {_function} with arguments {_things::*}",
})
@Since("2.10")
@Keywords({"run", "execute", "reflection", "function"})
@SuppressWarnings({"rawtypes", "unchecked"})
public class EffRun extends Effect implements ReflectionExperimentSyntax {

	static {
		Skript.registerEffect(EffRun.class,
				"run %executable% [arguments:with arg[ument]s %-objects%]",
				"execute %executable% [arguments:with arg[ument]s %-objects%]");
	}

	// We don't bother with the generic type here because we have no way to verify it
	// from the expression, and it makes casting more difficult to no benefit.
	private Expression<Executable> executable;
	private Expression<?> arguments;
	private DynamicFunctionReference.Input input;
	private boolean hasArguments;

	@Override
	public SyntaxElement init(Expression<?>[] expressions, int pattern, Kleenean isDelayed, ParseResult result) {
		this.executable = ((Expression<Executable>) expressions[0]);
		this.hasArguments = result.hasTag("arguments");
		if (hasArguments) {
			this.arguments = LiteralUtils.defendExpression(expressions[1]);
			Expression<?>[] arguments;
			if (this.arguments instanceof ExpressionList<?>) {
				arguments = ((ExpressionList<?>) this.arguments).getExpressions();
			} else {
				arguments = new Expression[]{this.arguments};
			}
			this.input = new DynamicFunctionReference.Input(arguments);
			return LiteralUtils.canInitSafely(this.arguments) ? this : null;
		} else {
			this.input = new DynamicFunctionReference.Input();
		}
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		Executable task = executable.executeSingle(event);
		if (task == null)
			return;
		Object[] arguments;
		if (task instanceof DynamicFunctionReference<?> reference) {
			Expression<?> validated = reference.validate(input);
			if (validated == null)
				return;
			arguments = validated.executeArray(event);
		} else if (hasArguments) {
			arguments = this.arguments.executeArray(event);
		} else {
			arguments = new Object[0];
		}
		task.execute(event, arguments);
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		if (hasArguments)
			return "run " + executable.toString(event, debug) + " with arguments " + arguments.toString(event, debug);
		return "run " + executable.toString(event, debug);
	}

}