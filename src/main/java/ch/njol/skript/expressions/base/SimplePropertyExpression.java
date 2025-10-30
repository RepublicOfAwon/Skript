package ch.njol.skript.expressions.base;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.converter.Converter;

/**
 * A base class for property expressions that requires only few overridden methods
 * 
 * @see PropertyExpression
 * @see PropertyExpression#register(Class, Class, String, String)
 */
public abstract class SimplePropertyExpression<F, T> extends PropertyExpression<F, T> implements Converter<F, T> {

	protected String rawExpr;
  
	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (LiteralUtils.hasUnparsedLiteral(expressions[0])) {
			setExpr(LiteralUtils.defendExpression(expressions[0]));
			return LiteralUtils.canInitSafely(getExpr()) ? this : null;
		}
		setExpr((Expression<? extends F>) expressions[0]);
		rawExpr = parseResult.expr;
		return this;
	}

	@Override
	@Nullable
	public abstract T convert(F from);

	@Override
	protected T[] get(VirtualFrame event, F[] source) {
		return super.get(source, this);
	}

	/**
	 * Used to collect the property type used in the register method.
	 * This forms the toString of this SimplePropertyExpression.
	 * 
	 * @return The name of the type used when registering this SimplePropertyExpression.
	 */
	protected abstract String getPropertyName();

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return getPropertyName() + " of " + getExpr().toString(event, debug);
	}

}