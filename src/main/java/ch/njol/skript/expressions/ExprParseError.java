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
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Parse Error")
@Description("The error which caused the last <a href='#ExprParse'>parse operation</a> to fail, which might not be set if a pattern was used and the pattern didn't match the provided text at all.")
@Examples({"set {var} to line 1 parsed as integer",
		"if {var} is not set:",
		"	parse error is set:",
		"		message \"&lt;red&gt;Line 1 is invalid: %last parse error%\"",
		"	else:",
		"		message \"&lt;red&gt;Please put an integer on line 1!\""})
@Since("2.0")
public class ExprParseError extends SimpleExpression<String> {
	static {
		Skript.registerExpression(ExprParseError.class, String.class, ExpressionType.SIMPLE, "[the] [last] [parse] error");
	}
	
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		return this;
	}
	
	@Override
	protected String[] execute(final VirtualFrame e) {
		return ExprParse.lastError == null ? new String[0] : new String[] {ExprParse.lastError};
	}
	
	@Override
	public boolean isSingle() {
		return true;
	}
	
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "the last parse error";
	}
	
}