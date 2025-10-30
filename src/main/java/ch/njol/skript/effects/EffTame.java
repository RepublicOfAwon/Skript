package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Tameable;
import org.jetbrains.annotations.Nullable;

@Name("Tame / Untame")
@Description("Tame a tameable entity (horse, parrot, cat, etc.).")
@Examples({
    "tame {_horse}",
    "untame {_horse}"
})
@Since("2.10")
public class EffTame extends Effect {

	static {
		Skript.registerEffect(EffTame.class, "[:un](tame|domesticate) %entities%");
	}

	private boolean tame;
	private Expression<Entity> entities;

	@Override
	@SuppressWarnings("unchecked")
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		tame = !parseResult.hasTag("un");
		entities = (Expression<Entity>) exprs[0];
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (Entity entity : entities.executeArray(event)) {
			if (entity instanceof Tameable)
				((Tameable) entity).setTamed(tame);
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return (tame ? "tame " : "untame ") + entities.toString(event, debug);
	}

}