package ch.njol.skript.util;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Bukkit;

import ch.njol.skript.Skript;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Trigger;
import ch.njol.skript.timings.SkriptTimings;
import ch.njol.skript.variables.Variables;
import org.bukkit.event.Event;

import java.util.concurrent.CompletableFuture;

/**
 * Effects that extend this class are ran asynchronously. Next trigger item will be ran
 * in main server thread, as if there had been a delay before.
 * <p>
 * Majority of Skript and Minecraft APIs are not thread-safe, so be careful.
 *
 * Make sure to add set {@link ch.njol.skript.ScriptLoader#hasDelayBefore} to
 * {@link ch.njol.util.Kleenean#TRUE} in the {@code init} method.
 */
public abstract class AsyncEffect extends Effect {
	
	@Override
	public Object execute(VirtualFrame frame) {

		Event e = (Event) frame.getArguments()[0];

		Delay.addDelayedEvent(e); // Mark this event as delayed
		Object localVars = Variables.removeLocals(frame); // Back up local variables

		if (!Skript.getInstance().isEnabled()) // See https://github.com/SkriptLang/Skript/issues/3702
			return null;

		CompletableFuture<Void> future = new CompletableFuture<>();

		Bukkit.getScheduler().runTaskAsynchronously(Skript.getInstance(), () -> {
			// Re-set local variables
			if (localVars != null)
				Variables.setLocalVariables(frame, localVars);
			
			executeVoid(frame); // Execute this effect

			Bukkit.getScheduler().runTask(Skript.getInstance(), () -> { // Walk to next item synchronously
				Object timing = null;
//				if (SkriptTimings.enabled()) { // getTrigger call is not free, do it only if we must
//					Trigger trigger = getTrigger();
//					if (trigger != null) {
//						timing = SkriptTimings.start(trigger.getDebugLabel());
//					}
//				}

				future.complete(null);

				Variables.removeLocals(frame); // Clean up local vars, we may be exiting now

				SkriptTimings.stop(timing); // Stop timing if it was even started
			});
		});
		throw new Trigger.DelayException(future);
	}
}
