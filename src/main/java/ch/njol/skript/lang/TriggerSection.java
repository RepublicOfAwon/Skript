package ch.njol.skript.lang;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

/**
 * Represents a section of a trigger, e.g. a conditional or a loop
 */
public abstract class TriggerSection extends TriggerItem {

	@Children
	protected TriggerItem[] children;

	/**
	 * Reserved for new Trigger(...)
	 */
	protected TriggerSection(List<TriggerItem> items) {
		setTriggerItems(items);
	}

	protected TriggerSection() {

	}

	protected void setTriggerItems(List<TriggerItem> items) {
		this.children = items.toArray(new TriggerItem[0]);
	}

	int i = 0;

	@Override
	public Object execute(VirtualFrame frame) {
		for (; i < children.length;) {
			try {
				children[i].execute(frame);
			} finally {
				i++;
			}
		}
		i = 0;
		return null;
	}

	/**
	 * @return The execution intent of the section's trigger.
	 */
	protected @Nullable ExecutionIntent triggerExecutionIntent() {

		for (TriggerItem child : children) {
			ExecutionIntent executionIntent = child.executionIntent();
			if (executionIntent != null)
				return executionIntent.use();
		}
		return null;
	}

}
