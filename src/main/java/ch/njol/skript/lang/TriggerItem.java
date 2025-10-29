package ch.njol.skript.lang;

import ch.njol.skript.Skript;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

/**
 * Represents a trigger item, i.e. a trigger section, a condition or an effect.
 * 
 * @author Peter Güttinger
 * @see TriggerSection
 * @see Trigger
 * @see Statement
 */
public abstract class TriggerItem implements Debuggable {

	protected TriggerItem() {
	}

	/**
	 * Executes this item and returns the next item to run.
	 * <p>
	 * If this method is overridden, {@link #run(Event)} is not used anymore and can be ignored.
	 * 
	 * @param event The event
	 * @return The next item to run or null to stop execution
	 */
	protected Object walk(Event event) {
		return run(event);
	}

	/**
	 * Executes this item.
	 * 
	 * @param event The event to run this item with
	 * @return True if the next item should be run, or false for the item following this item's parent.
	 */
	protected abstract boolean run(Event event);

	/**
	 * @param start The item to start at
	 * @param event The event to run the items with
	 * @return false if an exception occurred
	 */
	public static boolean walk(TriggerItem start, Event event) {
		try {
			start.walk(event);
			return true;
		} catch (StackOverflowError err) {
			if (Skript.debug())
				err.printStackTrace();
		} catch (Exception ex) {
			if (ex.getStackTrace().length != 0) // empty exceptions have already been printed
				Skript.exception(ex, start);
		} catch (Throwable throwable) {
			// not all Throwables are Exceptions, but we usually don't want to catch them (without rethrowing)
			Skript.markErrored();
			throw throwable;
		}
		return false;
	}

	/**
	 * Returns whether this item stops the execution of the current trigger or section(s).
	 * <br>
	 * If present, and there are statement(s) after this one, the parser will print a warning
	 * to the user.
	 * <p>
	 * <b>Note: This method is used purely to print warnings and doesn't affect parsing, execution or anything else.</b>
	 *
	 * @return whether this item stops the execution of the current trigger or section.
	 */
	public @Nullable ExecutionIntent executionIntent() {
		return null;
	}

	@Override
	public final String toString() {
		return toString(null, false);
	}

}
