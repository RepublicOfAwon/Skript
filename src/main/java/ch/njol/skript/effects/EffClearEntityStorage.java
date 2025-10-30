package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.block.Block;
import org.bukkit.block.EntityBlockStorage;
import org.jetbrains.annotations.Nullable;

@Name("Clear Entity Storage")
@Description("Clear the stored entities of an entity block storage (i.e. beehive).")
@Examples("clear the stored entities of {_beehive}")
@Since("2.11")
public class EffClearEntityStorage extends Effect {

	static {
		if (Skript.methodExists(EntityBlockStorage.class, "clearEntities"))
			Skript.registerEffect(EffClearEntityStorage.class,
				"(clear|empty) the (stored entities|entity storage) of %blocks%");
	}

	private Expression<Block> blocks;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		blocks = (Expression<Block>) exprs[0];
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (Block block : blocks.executeArray(event)) {
			if (!(block.getState() instanceof EntityBlockStorage<?> blockStorage))
				continue;
			blockStorage.clearEntities();
			blockStorage.update(true, false);
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "clear the stored entities of " + blocks.toString(event, debug);
	}

}