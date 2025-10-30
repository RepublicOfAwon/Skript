package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.ExprColoured;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.chat.BungeeConverter;
import ch.njol.skript.util.chat.ChatMessages;
import ch.njol.skript.util.chat.MessageComponent;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.oracle.truffle.api.frame.VirtualFrame;
import net.md_5.bungee.api.chat.BaseComponent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("Message")
@Description({"Sends a message to the given player. Only styles written",
		"in given string or in <a href=#ExprColored>formatted expressions</a> will be parsed.",
		"Adding an optional sender allows the messages to be sent as if a specific player sent them.",
		"This is useful with Minecraft 1.16.4's new chat ignore system, in which players can choose to ignore other players,",
		"but for this to work, the message needs to be sent from a player."})
@Examples({"message \"A wild %player% appeared!\"",
		"message \"This message is a distraction. Mwahaha!\"",
		"send \"Your kill streak is %{kill streak::%uuid of player%}%.\" to player",
		"if the targeted entity exists:",
		"\tmessage \"You're currently looking at a %type of the targeted entity%!\"",
		"on chat:",
		"\tcancel event",
		"\tsend \"[%player%] >> %message%\" to all players from player"})
@RequiredPlugins("Minecraft 1.16.4+ for optional sender")
@Since("1.0, 2.2-dev26 (advanced features), 2.5.2 (optional sender), 2.6 (sending objects)")
public class EffMessage extends Effect {
	
	static {
		Skript.registerEffect(EffMessage.class, "(message|send [message[s]]) %objects% [to %commandsenders%] [from %-player%]");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<?>[] messages;

	/**
	 * Used for {@link Debuggable#toString(VirtualFrame, boolean)}
	 */
	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<?> messageExpr;

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Expression<CommandSender> recipients;
	
	@Nullable
	private Expression<Player> sender;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parser) {
		messageExpr = LiteralUtils.defendExpression(exprs[0]);

		messages = messageExpr instanceof ExpressionList ?
			((ExpressionList<?>) messageExpr).getExpressions() : new Expression[] {messageExpr};
		recipients = (Expression<CommandSender>) exprs[1];
		sender = (Expression<Player>) exprs[2];
		return LiteralUtils.canInitSafely(messageExpr) ? this : null;
	}

	@Override
	protected void executeVoid(VirtualFrame e) {
		Player sender = this.sender != null ? this.sender.executeSingle(e) : null;

		CommandSender[] commandSenders = recipients.executeArray(e);

		for (Expression<?> message : getMessages()) {

			Object[] messageArray = null;
			List<MessageComponent> messageComponents = null;

			for (CommandSender receiver : commandSenders) {
				if (receiver instanceof Player && message instanceof VariableString) {
					if (messageComponents == null)
						messageComponents = ((VariableString) message).getMessageComponents(e);
				} else {
					if (messageArray == null)
						messageArray = message.executeArray(e);
				}

				if (receiver instanceof Player) { // Can use JSON formatting
					if (message instanceof VariableString) { // Process formatting that is safe
						sendMessage((Player) receiver, sender,
							BungeeConverter.convert(messageComponents)
						);
					} else if (message instanceof ExprColoured && ((ExprColoured) message).isUnsafeFormat()) { // Manually marked as trusted
						for (Object object : messageArray) {
							sendMessage((Player) receiver, sender, BungeeConverter.convert(ChatMessages.parse((String) object)));
						}
					} else { // It is just a string, no idea if it comes from a trusted source -> don't parse anything
						for (Object object : messageArray) {
							List<MessageComponent> components = ChatMessages.fromParsedString(toString(object));
							sendMessage((Player) receiver, sender, BungeeConverter.convert(components));
						}
					}
				} else { // Not a player, send plain text with legacy formatting
					for (Object object : messageArray) {
						receiver.sendMessage(toString(object));
					}
				}
			}
		}
	}
	
	private void sendMessage(Player receiver, @Nullable Player sender, BaseComponent... components) {
		if (sender != null)
			receiver.spigot().sendMessage(sender.getUniqueId(), components);
		else
			receiver.spigot().sendMessage(components);
	}

	private Expression<?>[] getMessages() {
		if (messageExpr instanceof ExpressionList && !messageExpr.getAnd()) {
			return new Expression[] {CollectionUtils.getRandom(messages)};
		}
		return messages;
	}

	private String toString(Object object) {
		return object instanceof String ? (String) object : Classes.toString(object);
	}

	@Override
	public String toString(@Nullable VirtualFrame e, boolean debug) {
		return "send " + messageExpr.toString(e, debug) + " to " + recipients.toString(e, debug) +
			(sender != null ? " from " + sender.toString(e, debug) : "");
	}
	
}
