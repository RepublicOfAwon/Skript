package ch.njol.skript.effects;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.FireworkEffect;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.inventory.meta.FireworkMeta;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Launch firework")
@Description("Launch firework effects at the given location(s).")
@Examples("launch ball large colored red, purple and white fading to light green and black at player's location with duration 1")
@Since("2.4")
public class EffFireworkLaunch extends Effect {
	
	static {
		Skript.registerEffect(EffFireworkLaunch.class, "(launch|deploy) [[a] firework [with effect[s]]] %fireworkeffects% at %locations% [([with] (duration|power)|timed) %number%]");
	}

	@Nullable
	public static Entity lastSpawned = null;

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<FireworkEffect> effects;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Location> locations;
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<Number> lifetime;
	
	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		effects = (Expression<FireworkEffect>) exprs[0];
		locations = (Expression<Location>) exprs[1];
		lifetime = (Expression<Number>) exprs[2];
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		FireworkEffect[] effects = this.effects.executeArray(event);
		int power = lifetime.executeOptional(event).orElse(1).intValue();
		power = Math.min(127, Math.max(0, power));
		for (Location location : locations.executeArray(event)) {
			World world = location.getWorld();
			if (world == null)
				continue;
			Firework firework = world.spawn(location, Firework.class);
			FireworkMeta meta = firework.getFireworkMeta();
			meta.addEffects(effects);
			meta.setPower(power);
			firework.setFireworkMeta(meta);
			lastSpawned = firework;
		}
	}
	
	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "Launch firework(s) " + effects.toString(event, debug) +
				" at location(s) " + locations.toString(event, debug) +
				" timed " + lifetime.toString(event, debug);
	}

}