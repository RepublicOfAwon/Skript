package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.*;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.AbstractHorse;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Panda;
import org.jetbrains.annotations.Nullable;

@Name("Force Eating")
@Description("Make a panda or horse type (horse, camel, donkey, llama, mule) start/stop eating.")
@Example("""
	if last spawned panda is eating:
		make last spawned panda stop eating
	""")
@Since("2.11")
public class EffEating extends Effect {

	private static final boolean SUPPORTS_HORSES = Skript.methodExists(AbstractHorse.class, "isEating");

	static {
		Skript.registerEffect(EffEating.class,
			"make %livingentities% (:start|stop) eating",
			"force %livingentities% to (:start|stop) eating");
	}

	private Expression<LivingEntity> entities;
	private boolean start;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		entities = (Expression<LivingEntity>) exprs[0];
		start = parseResult.hasTag("start");
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (LivingEntity entity : entities.executeArray(event)) {
			if (entity instanceof Panda panda) {
				panda.setEating(start);
			} else if (SUPPORTS_HORSES && entity instanceof AbstractHorse horse) {
				horse.setEating(start);
			}
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		builder.append("make", entities);
		if (start) {
			builder.append("start");
		} else {
			builder.append("stop");
		}
		builder.append("eating");
		return builder.toString();
	}

}