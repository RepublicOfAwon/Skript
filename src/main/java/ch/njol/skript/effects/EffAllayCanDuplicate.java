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
import org.bukkit.entity.Allay;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.Nullable;

@Name("Allay Duplicate")
@Description({
	"Set whether an allay can or cannot duplicate itself.",
	"This is not the same as breeding allays."
})
@Examples({
	"allow all allays to duplicate",
	"prevent all allays from duplicating"
})
@Since("2.11")
public class EffAllayCanDuplicate extends Effect {

	static {
		Skript.registerEffect(EffAllayCanDuplicate.class,
			"allow %livingentities% to (duplicate|clone)",
			"prevent %livingentities% from (duplicating|cloning)");
	}

	private Expression<LivingEntity> entities;
	private boolean duplicate;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		entities = (Expression<LivingEntity>) exprs[0];
		duplicate = matchedPattern == 0;
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (LivingEntity entity : entities.executeArray(event)) {
			if (entity instanceof Allay allay)
				allay.setCanDuplicate(duplicate);
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		if (duplicate)
			return "allow " + entities.toString(event, debug) + " to duplicate";
		return "prevent " + entities.toString(event, debug) + " from duplicating";
	}

}