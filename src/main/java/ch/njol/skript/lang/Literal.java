package ch.njol.skript.lang;

import org.jetbrains.annotations.Nullable;

/**
 * A literal, e.g. a number, string or item. Literals are constants which do not depend on the event and can thus e.g. be used in events.
 * 
 * @author Peter Güttinger
 */
public abstract class Literal<T> extends Expression<T> {

	public abstract T[] getArray();

	public abstract T getSingle();

	@Override
	@SuppressWarnings("unchecked")
	public abstract <R> @Nullable Literal<? extends R> getConvertedExpression(Class<R>... to);

	public abstract T[] getAll();

}
