package ch.njol.skript.lang;

/**
 * Represents an expression that can be used as the default value of a certain type or event.
 */
public interface DefaultExpression<T> {

	/**
	 * Called when an expression is initialized.
	 *
	 * @return Whether the expression is valid in its context. Skript will error if false.
	 */
	boolean init();

	boolean isDefault();

}
