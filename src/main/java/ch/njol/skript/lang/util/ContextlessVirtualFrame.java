package ch.njol.skript.lang.util;

import ch.njol.skript.lang.parser.ParserInstance;
import com.oracle.truffle.api.frame.FrameDescriptor;
import com.oracle.truffle.api.frame.MaterializedFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

/**
 * This class is intended for usage in places of Skript that require an Event.
 * Of course, not everything is always context/event dependent.
 * For example, if one were to load a SectionNode or parse something into a {@link ch.njol.skript.lang.SyntaxElement},
 *  and {@link ParserInstance#getCurrentEvents()} was null or empty, the resulting elements
 *  would not be dependent upon a specific Event. Thus, there would be no reason for an Event to be required.
 * So, this classes exists to avoid dangerously passing null in these places.
 * @see #get()
 */
public final class ContextlessVirtualFrame implements VirtualFrame {

	private final Object[] args = new Object[] { new Event() {
		@Override
		public @NotNull HandlerList getHandlers() {
			return null;
		}
	}};

	private ContextlessVirtualFrame() { }

	/**
	 * @return A new ContextlessEvent instance to be used for context-less {@link ch.njol.skript.lang.SyntaxElement}s.
	 */
	public static ContextlessVirtualFrame get() {
		return new ContextlessVirtualFrame();
	}

	@Override
	public FrameDescriptor getFrameDescriptor() {
		return null;
	}

	@Override
	public Object[] getArguments() {
		return args;
	}

	@Override
	public MaterializedFrame materialize() {
		return null;
	}
}
