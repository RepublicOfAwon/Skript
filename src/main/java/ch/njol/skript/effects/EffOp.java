package ch.njol.skript.effects;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.OfflinePlayer;
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
@Name("op/deop")
@Description("Grant/revoke a user operator state.")
@Examples({"op the player",
		"deop all players"})
@Since("1.0")
public class EffOp extends Effect {
	
	static {
		Skript.registerEffect(EffOp.class, "[de[-]]op %offlineplayers%");
	}
	
	@SuppressWarnings("null")
	private Expression<OfflinePlayer> players;
	private boolean op;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		players = (Expression<OfflinePlayer>) exprs[0];
		op = !parseResult.expr.substring(0, 2).equalsIgnoreCase("de");
		return this;
	}
	
	@Override
	protected void executeVoid(final VirtualFrame e) {
		for (final OfflinePlayer p : players.executeArray(e)) {
			p.setOp(op);
		}
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return (op ? "" : "de") + "op " + players.toString(e, debug);
	}
	
}