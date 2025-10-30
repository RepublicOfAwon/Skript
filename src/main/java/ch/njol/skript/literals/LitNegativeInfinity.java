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

@Name("Negative Infinity")
@Description("A number representing negative infinity.")
@Example("if {_number} is -infinity:")
@Since("2.2-dev32d")
public class LitNegativeInfinity extends SimpleLiteral<Double> {

	static {
		Skript.registerExpression(LitNegativeInfinity.class, Double.class, ExpressionType.SIMPLE,
				"(-|minus |negative )(infinity|∞) [value]",
				"value of (-|minus |negative )(infinity|∞)");
	}

	public LitNegativeInfinity() {
		super(Double.NEGATIVE_INFINITY, false);
	}

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return this;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "negative infinity";
	}

}