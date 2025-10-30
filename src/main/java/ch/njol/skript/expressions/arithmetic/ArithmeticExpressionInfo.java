package ch.njol.skript.expressions.arithmetic;

import com.oracle.truffle.api.frame.VirtualFrame;

import ch.njol.skript.lang.Expression;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.arithmetic.Arithmetics;

/**
 * Arithmetic gettable wrapped around an expression.
 *
 * @param <T> expression type
 */
public final class ArithmeticExpressionInfo<T> extends ArithmeticGettable<T> {

	Expression<? extends T> expression;
	public ArithmeticExpressionInfo(Expression<T> tExpression) {
		this.expression = tExpression;
	}

	@Override
	public @Nullable T execute(VirtualFrame event) {
		T object = expression.executeSingle(event);
		return object == null ? Arithmetics.getDefaultValue(expression.getReturnType()) : object;
	}

	@Override
	public Class<? extends T> getReturnType() {
		return expression.getReturnType();
	}

}
