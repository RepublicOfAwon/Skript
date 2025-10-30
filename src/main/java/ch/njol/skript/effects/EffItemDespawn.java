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
import ch.njol.skript.lang.SyntaxStringBuilder;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.Item;
import org.jetbrains.annotations.Nullable;

@Name("Item Despawn")
@Description("Prevent a dropped item from naturally despawning through Minecraft's timer.")
@Examples({
	"prevent all dropped items from naturally despawning",
	"allow all dropped items to naturally despawn"
})
@Since("2.11")
public class EffItemDespawn extends Effect {

	static {
		Skript.registerEffect(EffItemDespawn.class,
			"(prevent|disallow) %itementities% from (naturally despawning|despawning naturally)",
			"allow natural despawning of %itementities%",
			"allow %itementities% to (naturally despawn|despawn naturally)");
	}

	private Expression<Item> entities;
	private boolean prevent;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		prevent = matchedPattern == 0;
		//noinspection unchecked
		entities = (Expression<Item>) exprs[0];
		return this;
	}

	@Override
	protected void executeVoid(VirtualFrame event) {
		for (Item item : entities.executeArray(event)) {
			item.setUnlimitedLifetime(prevent);
		}
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		SyntaxStringBuilder builder = new SyntaxStringBuilder(event, debug);
		if (prevent) {
			builder.append("prevent", entities, "from naturally despawning");
		} else {
			builder.append("allow", entities, "to naturally despawn");
		}
		return builder.toString();
	}

}