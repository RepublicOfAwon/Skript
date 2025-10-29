package ch.njol.skript.lang.function;

import ch.njol.skript.lang.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
public class EffFunctionCall extends Effect {
	
	private final FunctionReference<?> function;
	
	public EffFunctionCall(final FunctionReference<?> function) {
		this.function = function;
	}
	
	@Nullable
	public static EffFunctionCall parse(final String line) {
		final FunctionReference<?> function = new SkriptParser(line, SkriptParser.ALL_FLAGS, ParseContext.DEFAULT).parseFunction((Class<?>[]) null);
		if (function != null)
			return new EffFunctionCall(function);
		return null;
	}
	
	@Override
	protected void execute(final Event event) {
		function.execute(event);
		function.resetReturnValue(); // Function might have return value that we're ignoring
	}
	
	@Override
	public String toString(@Nullable final Event event, final boolean debug) {
		return function.toString(event, debug);
	}
	
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		assert false;
		return null;
	}
	
}