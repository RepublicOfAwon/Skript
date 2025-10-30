package ch.njol.skript.expressions;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.effects.Delay;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;

/**
 * @author Peter Güttinger
 */
@Name("World")
@Description("The world the event occurred in.")
@Examples({"world is \"world_nether\"",
		"teleport the player to the world's spawn",
		"set the weather in the player's world to rain",
		"set {_world} to world of event-chunk"})
@Since("1.0")
public class ExprWorld extends PropertyExpression<Object, World> {

	static {
		Skript.registerExpression(ExprWorld.class, World.class, ExpressionType.PROPERTY, "[the] world [of %locations/entities/chunk%]", "%locations/entities/chunk%'[s] world");
	}
	
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parser) {
		Expression<?> expr = exprs[0];
		if (expr == null) {
			expr = new EventValueExpression<>(World.class);
			if (!((EventValueExpression<?>) expr).init())
				return null;
		}
		setExpr(expr);
		return this;
	}
	
	@Override
	protected World[] get(final VirtualFrame frame, final Object[] source) {
		Event event = (Event) frame.getArguments()[0];
		if (source instanceof World[]) // event value (see init)
			return (World[]) source;
		return get(source, obj -> {
			if (obj instanceof Entity) {
				if (getTime() > 0 && event instanceof PlayerTeleportEvent && obj.equals(((PlayerTeleportEvent) event).getPlayer()) && !Delay.isDelayed(event))
					return ((PlayerTeleportEvent) event).getTo().getWorld();
				else
					return ((Entity) obj).getWorld();
			} else if (obj instanceof Location) {
				return ((Location) obj).getWorld();
			} else if (obj instanceof Chunk) {
				return ((Chunk) obj).getWorld();
			}
			assert false : obj;
			return null;
		});
	}

	@Override
	@Nullable
	public Class<?>[] acceptChange(final ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return CollectionUtils.array(World.class);
		return null;
	}

	@Override
	public void change(VirtualFrame e, @Nullable Object[] delta, ChangeMode mode) {
		if (delta == null)
			return;

		for (Object o : getExpr().executeArray(e)) {
			if (o instanceof Location) {
				((Location) o).setWorld((World) delta[0]);
			}
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public boolean setTime(final int time) {
		return super.setTime(time, getExpr(), PlayerTeleportEvent.class);
	}

	@Override
	public Class<World> getReturnType() {
		return World.class;
	}

	@Override
	public String toString(final @Nullable VirtualFrame e, final boolean debug) {
		return "the world" + (getExpr().isDefault() ? "" : " of " + getExpr().toString(e, debug));
	}

}