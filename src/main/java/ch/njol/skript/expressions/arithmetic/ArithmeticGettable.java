package ch.njol.skript.expressions.arithmetic;

import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.Node;
import org.jetbrains.annotations.Nullable;

/**
 * Represents component that can can be used within an arithmetic context.
 *
 * @param <T> the return type of the gettable
 * @see ArithmeticExpressionInfo
 */
public abstract class ArithmeticGettable<T> extends Node {

	/**
	 * Retrieves the value based on the given event context.
	 *
	 * @param event event context
	 * @return the computed value
	 */
	public abstract @Nullable T execute(VirtualFrame event);

	/**
	 * Return type of this gettable
	 *
	 * @return the return type of this gettable
	 */
	public abstract Class<? extends T> getReturnType();

}
