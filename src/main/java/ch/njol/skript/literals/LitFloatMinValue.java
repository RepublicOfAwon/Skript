package ch.njol.skript.literals;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.util.SimpleLiteral;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

@Name("Minimum Float Value")
@Description("A number representing the minimum value of a float number type.")
@Example("if {_number} <= minimum float value:")
@Since("2.13")
public class LitFloatMinValue extends SimpleLiteral<Float> {

	static {
		Skript.registerExpression(LitFloatMinValue.class, Float.class, ExpressionType.SIMPLE, "[the] min[imum] float value");
	}

	public LitFloatMinValue() {
		super(Float.MIN_VALUE, false);
	}

	@Override
	public SyntaxElement init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return this;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "min float value";
	}

}