package ch.njol.skript.lang.util;

import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.lang.KeyProviderExpression;
import ch.njol.skript.lang.KeyReceiverExpression;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.apache.commons.lang3.ArrayUtils;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.skriptlang.skript.lang.converter.ConverterInfo;
import org.skriptlang.skript.lang.converter.Converters;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.WeakHashMap;
import java.util.function.Consumer;

/**
 * A {@link ConvertedExpression} that converts a keyed expression to another type with consideration of keys.
 * This expression is used when the source expression is a {@link KeyProviderExpression}
 *
 * @see ConvertedExpression
 */
public class ConvertedKeyProviderExpression<F, T> extends ConvertedExpression<F, T> implements KeyProviderExpression<T>, KeyReceiverExpression<T> {

	private final WeakHashMap<Event, String[]> arrayKeysCache = new WeakHashMap<>();
	private final WeakHashMap<Event, String[]> allKeysCache = new WeakHashMap<>();
	private final boolean supportsKeyedChange;

	public ConvertedKeyProviderExpression(KeyProviderExpression<? extends F> source, Class<T> to, ConverterInfo<? super F, ? extends T> info) {
		super(source, to, info);
		this.supportsKeyedChange = source instanceof KeyReceiverExpression<?>;
	}

	public ConvertedKeyProviderExpression(KeyProviderExpression<? extends F> source, Class<T>[] toExact, Collection<ConverterInfo<? super F, ? extends T>> converterInfos, boolean performFromCheck) {
		super(source, toExact, converterInfos, performFromCheck);
		this.supportsKeyedChange = source instanceof KeyReceiverExpression<?>;
	}

	@Override
	public T[] executeArray(VirtualFrame frame) {
		if (!canReturnKeys()) {
			return super.executeArray(frame);
		}
		return get(getSource().getArray(frame), getSource().getArrayKeys(frame), keys -> arrayKeysCache.put(frame, keys));
	}

	@Override
	public T[] executeAll(VirtualFrame frame) {
		if (!canReturnKeys()) {
			return super.executeAll(frame);
		}
		return get(getSource().getAll(frame), getSource().getAllKeys(frame), keys -> allKeysCache.put(frame, keys));
	}

	private T[] get(F[] source, String[] keys, Consumer<String[]> convertedKeysConsumer) {
		//noinspection unchecked
		T[] converted = (T[]) Array.newInstance(to, source.length);
		Converters.convert(source, converted, converter);
		for (int i = 0; i < converted.length; i++)
			keys[i] = converted[i] != null ? keys[i] : null;
		convertedKeysConsumer.accept(ArrayUtils.removeAllOccurrences(keys, null));
		converted = ArrayUtils.removeAllOccurrences(converted, null);
		return converted;
	}

	@Override
	public KeyProviderExpression<? extends F> getSource() {
		return (KeyProviderExpression<? extends F>) super.getSource();
	}

	@Override
	public @NotNull String @NotNull [] getArrayKeys(VirtualFrame event) throws IllegalStateException {
		if (!arrayKeysCache.containsKey(event))
			throw new IllegalStateException();
		return arrayKeysCache.remove(event);
	}

	@Override
	public @NotNull String @NotNull [] getAllKeys(VirtualFrame event) {
		if (!allKeysCache.containsKey(event))
			throw new IllegalStateException();
		return allKeysCache.remove(event);
	}

	@Override
	public boolean canReturnKeys() {
		return getSource().canReturnKeys();
	}

	@Override
	public boolean areKeysRecommended() {
		return getSource().areKeysRecommended();
	}

	@Override
	public void change(Event event, Object @NotNull [] delta, ChangeMode mode, @NotNull String @NotNull [] keys) {
		if (supportsKeyedChange) {
			((KeyReceiverExpression<?>) getSource()).change(event, delta, mode, keys);
		} else {
			getSource().change(event, delta, mode);
		}
	}

	@Override
	public boolean isLoopOf(String input) {
		return getSource().isLoopOf(input);
	}

	@Override
	public boolean isSingle() {
		return false;
	}
}
