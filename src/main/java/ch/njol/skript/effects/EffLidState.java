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
import org.bukkit.block.Block;
import org.bukkit.block.Lidded;
import org.jetbrains.annotations.Nullable;

@Name("Open/Close Lid")
@Description("Open or close the lid of the block(s).")
@Examples({
	"open the lid of {_chest}",
	"close the lid of {_blocks::*}"
})
@Since("2.10")
public class EffLidState extends Effect {

	static {
		Skript.registerEffect(EffLidState.class,
			"(open|:close) [the] lid[s] (of|for) %blocks%",
			"(open|:close) %blocks%'[s] lid[s]"
		);
	}

	private boolean setOpen;
	private Expression<Block> blocks;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		setOpen = !parseResult.hasTag("close");
		blocks = (Expression<Block>) exprs[0];
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (Block block : blocks.executeArray(event)) {
			if (block.getState() instanceof Lidded lidded) {
				if (setOpen) {
					lidded.open();
				} else {
					lidded.close();
				}
			}
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return (setOpen ? "open" : "close") + " lid of " + blocks.toString(event, debug);
	}

}