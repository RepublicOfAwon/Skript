package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerGameModeChangeEvent;
import org.jetbrains.annotations.Nullable;

/**
 * @author Peter Güttinger
 */
@Name("Game Mode")
@Description("The gamemode of a player. (<a href=\"#gamemode\">Gamemodes</a>)")
@Examples({"player's gamemode is survival",
		"set the player's gamemode to creative"})
@Since("1.0")
public class ExprGameMode extends PropertyExpression<Player, GameMode> {
	
	static {
		Skript.registerExpression(ExprGameMode.class, GameMode.class, ExpressionType.PROPERTY, "[the] game[ ]mode of %players%", "%players%'[s] game[ ]mode");
	}
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(final Expression<?>[] vars, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		setExpr((Expression<Player>) vars[0]);
		return this;
	}
	
	@Override
	protected GameMode[] get(final VirtualFrame e, final Player[] source) {
		return get(source, player -> {
			Event event = (Event) e.getArguments()[0];
			if (getTime() >= 0 && event instanceof PlayerGameModeChangeEvent && ((PlayerGameModeChangeEvent) event).getPlayer() == player && !Delay.isDelayed(event))
				return ((PlayerGameModeChangeEvent) event).getNewGameMode();
			return player.getGameMode();
		});
	}
	
	@Override
	public Class<GameMode> getReturnType() {
		return GameMode.class;
	}
	
	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "the gamemode of " + getExpr().toString(e, debug);
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET || mode == ChangeMode.RESET)
			return CollectionUtils.array(GameMode.class);
		return null;
	}
	
	@Override
	public void change(final VirtualFrame e, final @Nullable Object[] delta, final ChangeMode mode) throws UnsupportedOperationException {
		final GameMode m = delta == null ? Bukkit.getDefaultGameMode() : (GameMode) delta[0];
		for (final Player p : getExpr().executeArray(e)) {
			Event event = (Event) e.getArguments()[0];
			if (getTime() >= 0 && event instanceof PlayerGameModeChangeEvent && ((PlayerGameModeChangeEvent) event).getPlayer() == p && !Delay.isDelayed(event)) {
				if (((PlayerGameModeChangeEvent) event).getNewGameMode() != m)
					((PlayerGameModeChangeEvent) event).setCancelled(true);
			}
			p.setGameMode(m);
		}
	}
	
	@Override
	public boolean setTime(final int time) {
		return super.setTime(time, PlayerGameModeChangeEvent.class);
	}
	
}