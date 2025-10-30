package org.skriptlang.skript.bukkit.fishing.elements;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.player.PlayerFishEvent;
import org.jetbrains.annotations.Nullable;

@Name("Apply Fishing Lure")
@Description("Sets whether the lure enchantment should be applied, which reduces the wait time.")
@Examples({
	"on fishing line cast:",
		"\tapply lure enchantment bonus"
})
@Events("Fishing")
@Since("2.10")
public class EffFishingLure extends Effect {

	static {
		Skript.registerEffect(EffFishingLure.class,
			"apply [the] lure enchantment bonus",
			"remove [the] lure enchantment bonus");
	}

	private boolean remove;

	@Override
	public SyntaxElement init(Expression<?>[] expressions, int matchedPattern,
                              Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentEvent(PlayerFishEvent.class)) {
			Skript.error("The 'fishing hook lure' effect can only be used in a fishing event.");
			return null;
		}
		remove = matchedPattern == 1;
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		if (!(event instanceof PlayerFishEvent fishEvent))
			return;

		fishEvent.getHook().setApplyLure(!remove);
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return (remove ? "remove" : "apply") + " the lure enchantment bonus";
	}

}