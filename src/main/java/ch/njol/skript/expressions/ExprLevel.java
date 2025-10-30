package ch.njol.skript.expressions;

import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerLevelChangeEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Events;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.expressions.base.SimplePropertyExpression;

/**
 * @author Peter Güttinger
 */
@Name("Level")
@Description("The level of a player.")
@Examples({"reduce the victim's level by 1",
		"set the player's level to 0"})
@Since("unknown (before 2.1)")
@Events("level change")
public class ExprLevel extends SimplePropertyExpression<Player, Long> {
	static {
		register(ExprLevel.class, Long.class, "level", "players");
	}
	
	@Override
	protected Long[] get(final VirtualFrame frame, final Player[] source) {
		return super.get(source, p -> {
			Event event = (Event) frame.getArguments()[0];
			if (event instanceof PlayerLevelChangeEvent && ((PlayerLevelChangeEvent) event).getPlayer() == p && !Delay.isDelayed(event)) {
				return (long) (getTime() < 0 ? ((PlayerLevelChangeEvent) event).getOldLevel() : ((PlayerLevelChangeEvent) event).getNewLevel());
			}
			return (long) p.getLevel();
		});
	}
	
	@Override
	@Nullable
	public Long convert(final Player p) {
		assert false;
		return null;
	}
	
	@Override
	public Class<Long> getReturnType() {
		return Long.class;
	}
	
	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.REMOVE_ALL)
			return null;
		if (getParser().isCurrentEvent(PlayerRespawnEvent.class) && !getParser().getHasDelayBefore().isTrue()) {
			Skript.error("Cannot change a player's level in a respawn event. Add a delay of 1 tick or change the 'new level' in a death event.");
			return null;
		}
		if (getParser().isCurrentEvent(EntityDeathEvent.class) && getTime() == 0 && getExpr().isDefault() && !getParser().getHasDelayBefore().isTrue()) {
			Skript.warning("Changing the player's level in a death event will change the player's level before he dies. " +
					"Use either 'past level of player' or 'new level of player' to clearly state whether to change the level before or after he dies.");
		}
		if (getTime() == -1 && !getParser().isCurrentEvent(EntityDeathEvent.class))
			return null;
		return new Class[] {Number.class};
	}
	
	@Override
	public void change(final VirtualFrame frame, final @Nullable Object[] delta, final ChangeMode mode) {
		Event e = (Event) frame.getArguments()[0];
		assert mode != ChangeMode.REMOVE_ALL;
		
		final int l = delta == null ? 0 : ((Number) delta[0]).intValue();
		
		for (final Player p : getExpr().executeArray(frame)) {
			int level;
			if (getTime() > 0 && e instanceof PlayerDeathEvent && ((PlayerDeathEvent) e).getEntity() == p && !Delay.isDelayed(e)) {
				level = ((PlayerDeathEvent) e).getNewLevel();
			} else {
				level = p.getLevel();
			}
			switch (mode) {
				case SET:
					level = l;
					break;
				case ADD:
					level += l;
					break;
				case REMOVE:
					level -= l;
					break;
				case DELETE:
				case RESET:
					level = 0;
					break;
				case REMOVE_ALL:
					assert false;
					continue;
			}
			if (level < 0)
				continue;
			if (getTime() > 0 && e instanceof PlayerDeathEvent && ((PlayerDeathEvent) e).getEntity() == p && !Delay.isDelayed(e)) {
				((PlayerDeathEvent) e).setNewLevel(level);
			} else {
				p.setLevel(level);
			}
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean setTime(final int time) {
		return super.setTime(time, getExpr(), PlayerLevelChangeEvent.class, PlayerDeathEvent.class);
	}
	
	@Override
	protected String getPropertyName() {
		return "level";
	}
	
}
