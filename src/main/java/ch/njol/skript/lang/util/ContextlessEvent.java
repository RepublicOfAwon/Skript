package ch.njol.skript.lang.util;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public final class ContextlessEvent extends Event {

	private ContextlessEvent() { }

	/**
	 * @return A new ContextlessEvent instance to be used for context-less {@link ch.njol.skript.lang.SyntaxElement}s.
	 */
	public static ContextlessEvent get() {
		return new ContextlessEvent();
	}

	/**
	 * This method should never be called.
	 */
	@Override
	@NotNull
	public HandlerList getHandlers() {
		throw new IllegalStateException();
	}

}
