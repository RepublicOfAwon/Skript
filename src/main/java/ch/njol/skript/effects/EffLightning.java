package ch.njol.skript.effects;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;

/**
 * @author Peter Güttinger
 */
@Name("Lightning")
@Description("Strike lightning at a given location. Can use 'lightning effect' to create a lightning that does not harm entities or start fires.")
@Examples({"strike lightning at the player",
		"strike lightning effect at the victim"})
@Since("1.4")
public class EffLightning extends Effect {
	
	static {
		Skript.registerEffect(EffLightning.class, "(create|strike) lightning(1¦[ ]effect|) %directions% %locations%");
	}
	
	@SuppressWarnings("null")
	private Expression<Location> locations;
	
	private boolean effectOnly;
	
	@Nullable
	public static Entity lastSpawned = null;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		locations = Direction.combine((Expression<? extends Direction>) exprs[0], (Expression<? extends Location>) exprs[1]);
		effectOnly = parseResult.mark == 1;
		return this;
	}
	
	@Override
	protected void executeVoid(final VirtualFrame e) {
		for (final Location l : locations.executeArray(e)) {
			if (effectOnly)
				lastSpawned = l.getWorld().strikeLightningEffect(l);
			else
				lastSpawned = l.getWorld().strikeLightning(l);
		}
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "strike lightning " + (effectOnly ? "effect " : "") + locations.toString(e, debug);
	}
	
}