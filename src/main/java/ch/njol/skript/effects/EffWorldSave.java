package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

@Name("Save World")
@Description({
	"Save all worlds or a given world manually.",
	"Note: saving many worlds at once may possibly cause the server to freeze."
})
@Examples({
	"save \"world_nether\"",
	"save all worlds"
})
@Since("2.8.0")
public class EffWorldSave extends Effect {

	static {
		Skript.registerEffect(EffWorldSave.class, "save [[the] world[s]] %worlds%");
	}

	private Expression<World> worlds;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		worlds = (Expression<World>) exprs[0];
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (World world : worlds.executeArray(event))
			world.save();
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "save the world(s) " + worlds.toString(event, debug);
	}

}