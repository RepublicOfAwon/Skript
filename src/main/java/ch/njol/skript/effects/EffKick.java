package ch.njol.skript.effects;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
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

/**
 * @author Peter Güttinger
 */
@Name("Kick")
@Description("Kicks a player from the server.")
@Examples({"on place of TNT, lava, or obsidian:",
		"	kick the player due to \"You may not place %block%!\"",
		"	cancel the event"})
@Since("1.0")
public class EffKick extends Effect {
	static {
		Skript.registerEffect(EffKick.class, "kick %players% [(by reason of|because [of]|on account of|due to) %-string%]");
	}
	
	@SuppressWarnings("null")
	private Expression<Player> players;
	@Nullable
	private Expression<String> reason;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		players = (Expression<Player>) exprs[0];
		reason = (Expression<String>) exprs[1];
		return this;
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "kick " + players.toString(e, debug) + (reason != null ? " on account of " + reason.toString(e, debug) : "");
	}
	
	@Override
	protected void executeVoid(final VirtualFrame frame) {
		Event e = (Event) frame.getArguments()[0];
		final String r = reason != null ? reason.executeSingle(frame) : "";
		if (r == null)
			return;
		for (final Player p : players.executeArray(frame)) {
			if (e instanceof PlayerLoginEvent && p.equals(((PlayerLoginEvent) e).getPlayer()) && !Delay.isDelayed(e)) {
				((PlayerLoginEvent) e).disallow(Result.KICK_OTHER, r);
			} else if (e instanceof PlayerKickEvent && p.equals(((PlayerKickEvent) e).getPlayer()) && !Delay.isDelayed(e)) {
				((PlayerKickEvent) e).setLeaveMessage(r);
			} else {
				p.kickPlayer(r);
			}
		}
	}
	
}