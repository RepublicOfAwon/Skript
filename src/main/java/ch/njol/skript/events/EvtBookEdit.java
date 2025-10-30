package ch.njol.skript.events;

import ch.njol.skript.Skript;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.player.PlayerEditBookEvent;
import org.jetbrains.annotations.Nullable;

public class EvtBookEdit extends SkriptEvent{
	
	static {
		Skript.registerEvent("Book Edit", EvtBookEdit.class, PlayerEditBookEvent.class, "book (edit|change|write)")
			.description("Called when a player edits a book.")
			.examples("on book edit:")
			.since("2.2-dev31");
	}
	
	@Override
	public boolean init(Literal<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		return true;
	}
	
	@Override
	public boolean check(VirtualFrame e) {
		if (!(e instanceof PlayerEditBookEvent)){
			return false;
		}
		return !((PlayerEditBookEvent) e).isSigning();
	}
	
	@Override
	public String toString(@Nullable VirtualFrame e, boolean debug) {
		return "book edit";
	}
}
