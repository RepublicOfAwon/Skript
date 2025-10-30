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
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.util.Kleenean;
import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.BaseComponent;

@Name("Action Bar")
@Description("Sends an action bar message to the given player(s).")
@Examples("send action bar \"Hello player!\" to player")
@Since("2.3")
public class EffActionBar extends Effect {

	static {
		Skript.registerEffect(EffActionBar.class, "send [the] action[ ]bar [with text] %string% [to %players%]");
	}

	private Expression<String> message;

	private Expression<Player> recipients;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		message = (Expression<String>) exprs[0];
		recipients = (Expression<Player>) exprs[1];
		return this;
	}

	@Override
	@SuppressWarnings("deprecation")
	protected void executeVoid(VirtualFrame event) {
		String msg = message.executeSingle(event);
		if (msg == null)
			return;
		BaseComponent[] components = BungeeConverter.convert(ChatMessages.parseToArray(msg));
		for (Player player : recipients.executeArray(event))
			player.spigot().sendMessage(ChatMessageType.ACTION_BAR, components);
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "send action bar " + message.toString(event, debug) + " to " + recipients.toString(event, debug);
	}

}