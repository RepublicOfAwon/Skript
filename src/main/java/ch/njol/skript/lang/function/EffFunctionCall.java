package ch.njol.skript.lang.function;

import ch.njol.skript.lang.*;
import com.oracle.truffle.api.frame.VirtualFrame;
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
	protected void executeVoid(final VirtualFrame event) {
		function.execute(event);
		function.resetReturnValue(); // Function might have return value that we're ignoring
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame event, final boolean debug) {
		return function.toString(event, debug);
	}
	
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		assert false;
		return null;
	}
	
}