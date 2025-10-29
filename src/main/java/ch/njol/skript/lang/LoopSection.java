package ch.njol.skript.lang;

import com.oracle.truffle.api.nodes.ControlFlowException;
import org.bukkit.event.Event;

import java.util.Map;
import java.util.WeakHashMap;

/**
 * Represents a loop section.
 * 
 * @see ch.njol.skript.sections.SecWhile
 * @see ch.njol.skript.sections.SecLoop
 */
public abstract class LoopSection extends Section implements SyntaxElement, Debuggable, SectionExitHandler {

	protected final transient Map<Event, Long> currentLoopCounter = new WeakHashMap<>();

	/**
	 * @param event The event where the loop is used to return its loop iterations
	 * @return The loop iteration number
	 */
	public long getLoopCounter(Event event) {
		return currentLoopCounter.getOrDefault(event, 1L);
	}

	/**
	 * Exit the loop, used to reset the loop properties such as iterations counter
	 * @param event The event where the loop is used to reset its relevant properties
	 */
	@Override
	public void exit(Event event) {
		currentLoopCounter.remove(event);
	}

	public static final class BreakException extends ControlFlowException {

	}
	public static final class ContinueException extends ControlFlowException {

	}

}
