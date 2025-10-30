package ch.njol.skript.effects;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.Player;
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

@Name("Title - Reset")
@Description("Resets the title of the player to the default values.")
@Examples({"reset the titles of all players",
		"reset the title"})
@Since("2.3")
public class EffResetTitle extends Effect {
	
	static {
		Skript.registerEffect(EffResetTitle.class,
				"reset [the] title[s] [of %players%]",
				"reset [the] %players%'[s] title[s]");
	}
	
	@SuppressWarnings("null")
	private Expression<Player> recipients;
	
	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		recipients = (Expression<Player>) exprs[0];
		return this;
	}
	
	@Override
	protected void executeVoid(VirtualFrame e) {
		for (Player recipient : recipients.executeArray(e))
			recipient.resetTitle();
	}
	
	@Override
	public String toString(@Nullable VirtualFrame e, boolean debug) {
		return "reset the title of " + recipients.toString(e, debug);
	}
	
}