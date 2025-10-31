package ch.njol.skript.lang;

import org.jetbrains.annotations.Nullable;

/**
 * A literal, e.g. a number, string or item. Literals are constants which do not depend on the event and can thus e.g. be used in events.
 *
 * @author Peter Güttinger
 */
public interface Literal<T> {

	T[] getArray();

	T getSingle();

	T[] getAll();

}