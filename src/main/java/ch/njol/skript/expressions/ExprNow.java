package ch.njol.skript.expressions;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.Date;
import ch.njol.util.Kleenean;

@Name("Now")
@Description("The current <a href='#date'>system time</a> of the server. Use <a href='#ExprTime'>time</a> to get the <a href='#time'>Minecraft time</a> of a world.")
@Examples({"broadcast \"Current server time: %now%\""})
@Since("1.4")
public class ExprNow extends SimpleExpression<Date> {
	
	static {
		Skript.registerExpression(ExprNow.class, Date.class, ExpressionType.SIMPLE, "now");
	}
	
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		return this;
	}
	
	@Override
	protected Date[] execute(final VirtualFrame e) {
		return new Date[] {new Date()};
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends Date> getReturnType() {
		return Date.class;
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "now";
	}
	
}