package ch.njol.skript.events;

import java.util.Locale;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.util.ContextlessVirtualFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.GameMode;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

/**
 * @author Peter Güttinger
 */
public final class EvtGameMode extends SkriptEvent {
	static {
		Skript.registerEvent("Gamemode Change", EvtGameMode.class, PlayerGameModeChangeEvent.class, "game[ ]mode change [to %gamemode%]")
				.description("Called when a player's <a href='#gamemode'>gamemode</a> changes.")
				.examples("on gamemode change:", "on gamemode change to adventure:")
				.since("1.0");
	}

	@Nullable
	private Literal<GameMode> mode;

	@SuppressWarnings("unchecked")
	@Override
	public boolean init(final Expression<?>[] args, final int matchedPattern, final ParseResult parser) {
		mode = (Literal<GameMode>) args[0];
		return true;
	}

	@Override
	public boolean check(final Event e) {
		if (mode != null) {
			return ((Expression<GameMode>)mode).check(ContextlessVirtualFrame.get(e), m -> ((PlayerGameModeChangeEvent) e).getNewGameMode().equals(m));
		}
		return true;
	}

	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "gamemode change" + (mode != null ? " to " + mode.toString().toLowerCase(Locale.ENGLISH) : "");
	}

}
