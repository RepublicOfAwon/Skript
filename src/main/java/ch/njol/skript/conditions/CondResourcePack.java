package ch.njol.skript.conditions;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent.Status;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Condition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;

@Name("Resource Pack")
@Description("Checks state of the resource pack in a <a href='#resource_pack_request_action'>resource pack request response</a> event.")
@Examples({"on resource pack response:",
		"	if the resource pack wasn't accepted:",
		"		kick the player due to \"You have to install the resource pack to play in this server!\""})
@Since("2.4")
@Events("resource pack request response")
public class CondResourcePack extends Condition {

	static {
		Skript.registerCondition(CondResourcePack.class,
				"[the] resource pack (was|is|has) [been] %resourcepackstate%",
				"[the] resource pack (was|is|has)(n't| not) [been] %resourcepackstate%");
	}

	@SuppressWarnings("null")
	private Expression<Status> states;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		if (!getParser().isCurrentEvent(PlayerResourcePackStatusEvent.class)) {
			Skript.error("The resource pack condition can't be used outside of a resource pack response event");
			return null;
		}
		states = (Expression<Status>) exprs[0];
		setNegated(matchedPattern == 1);
		return this;
	}
	
	@Override
	public boolean executeBoolean(VirtualFrame e) {
		if (!(e instanceof PlayerResourcePackStatusEvent))
			return isNegated();

		Status state = ((PlayerResourcePackStatusEvent) e).getStatus();
		return states.check(e, state::equals, isNegated());
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "resource pack was " + (isNegated() ? "not " : "") + states.toString(e, debug);
	}
	
}