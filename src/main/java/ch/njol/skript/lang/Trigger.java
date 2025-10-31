package ch.njol.skript.lang;

import ch.njol.skript.variables.Variables;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.nodes.ControlFlowException;
import com.oracle.truffle.api.nodes.RootNode;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.script.Script;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class Trigger extends RootNode {

	private final String name;
	private final SkriptEvent event;

	private final @Nullable Script script;
	private int line = -1; // -1 is default: it means there is no line number available
	private String debugLabel;

	@Child
	protected TriggerSection body;

	public Trigger(@Nullable Script script, String name, SkriptEvent event, List<TriggerItem> items) {
		super(null);
		this.body = new TriggerSection(items) {
			@Override
			public String toString(@Nullable VirtualFrame event, boolean debug) {
				return "trigger_body";
			}
		};
		this.script = script;
		this.name = name;
		this.event = event;
		this.debugLabel = "unknown trigger";
	}

	/**
	 * Executes this trigger for a certain event.
	 * @param event The event to execute this Trigger with.
	 * @return false if an exception occurred.
	 */
	public boolean execute(Event event, Map<String, Object> params) {
		boolean success = TriggerItem.walk(this, event, params);

		// Clear local variables
		//Variables.removeLocals(event);
		/*
		 * Local variables can be used in delayed effects by backing reference
		 * of VariablesMap up. Basically:
		 *
		 * Object localVars = Variables.removeLocals(event);
		 *
		 * ... and when you want to continue execution:
		 *
		 * Variables.setLocalVariables(event, localVars);
		 *
		 * See Delay effect for reference.
		 */

		return success;
	}

	public boolean execute(Event event) {
		return TriggerItem.walk(this, event, new HashMap<>());
	}

	public static class DelayException extends ControlFlowException {
		final CompletableFuture<Void> future;

		public DelayException(CompletableFuture<Void> future) {
			this.future = future;
		}
	}

	public static class ReturnException extends ControlFlowException {

	}

	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return name + " (" + this.event.toString(event, debug) + ")";
	}

	/**
	 * @return The name of this trigger.
	 */
	public String getName() {
		return name;
	}

	@Override
	public Object execute(VirtualFrame frame) {
		Map<String, Object> maps = (Map<String, Object>) frame.getArguments()[1];
		for (Map.Entry<String, Object> entry : maps.entrySet()) {
			Variables.setVariable(entry.getKey(), entry.getValue(), frame, true);
		}
		maps.clear();
		try {
			this.body.execute(frame);
		} catch (ReturnException e) {

		} catch (DelayException e) {
			e.future.thenAccept(v -> execute(frame));
			return null;
		}
		Variables.removeLocals(frame);
		return null;
	}

	public SkriptEvent getEvent() {
		return event;
	}

	/**
	 * @return The script this trigger was created from.
	 */
	public @Nullable Script getScript() {
		return script;
	}

	/**
	 * Sets line number for this trigger's start.
	 * Only used for debugging.
	 * @param line Line number
	 */
	public void setLineNumber(int line) {
		this.line  = line;
	}

	/**
	 * @return The line number where this trigger starts. This should ONLY be used for debugging!
	 */
	public int getLineNumber() {
		return line;
	}

	public void setDebugLabel(String label) {
		this.debugLabel = label;
	}

	public String getDebugLabel() {
		return debugLabel;
	}

}
