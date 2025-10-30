package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.player.PlayerEggThrowEvent;
import org.jetbrains.annotations.Nullable;

@Name("Make Egg Hatch")
@Description("Makes the egg hatch in a Player Egg Throw event.")
@Examples({
	"on player egg throw:",
		"\t# EGGS FOR DAYZ!",
		"\tmake the egg hatch"
})
@Events("Egg Throw")
@Since("2.7")
public class EffMakeEggHatch extends Effect {

	static {
		Skript.registerEffect(EffMakeEggHatch.class,
				"make [the] egg [:not] hatch"
		);
	}

	private boolean not;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		if (!getParser().isCurrentEvent(PlayerEggThrowEvent.class)) {
			Skript.error("You can't use the 'make the egg hatch' effect outside of a Player Egg Throw event.");
			return null;
		}
		not = parseResult.hasTag("not");
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame e) {
		if (e instanceof PlayerEggThrowEvent) {
			PlayerEggThrowEvent event = (PlayerEggThrowEvent) e;
			event.setHatching(!not);
			if (!not && event.getNumHatches() == 0) // Make it hatch something!
				event.setNumHatches((byte) 1);
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "make the egg " + (not ? "not " : "") + "hatch";
	}

}