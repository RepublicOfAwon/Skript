package org.skriptlang.skript.bukkit.loottables.elements.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SectionUtils;
import ch.njol.skript.registrations.EventValues;
import ch.njol.skript.util.Direction;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Location;
import org.bukkit.event.Event;
import org.bukkit.loot.LootContext;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.bukkit.loottables.LootContextCreateEvent;
import org.skriptlang.skript.bukkit.loottables.LootContextWrapper;

import java.util.List;

@Name("Create Loot Context")
@Description("Create a loot context.")
@Examples({
	"set {_player} to player",
	"set {_context} to a loot context at player:",
		"\tset loot luck value to 10",
		"\tset looter to {_player}",
		"\tset looted entity to last spawned pig",
	"give player loot items of loot table \"minecraft:entities/iron_golem\" with loot context {_context}"
})
@Since("2.10")
public class ExprSecCreateLootContext extends SectionExpression<LootContext> {

	static {
		Skript.registerExpression(ExprSecCreateLootContext.class, LootContext.class, ExpressionType.COMBINED,
			"[a] loot context %direction% %location%");
		EventValues.registerEventValue(LootContextCreateEvent.class, LootContext.class, event -> event.getContextWrapper().getContext());
	}

	private Trigger trigger;
	private Expression<Location> location;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int pattern, Kleenean isDelayed, ParseResult result, @Nullable SectionNode node, @Nullable List<TriggerItem> triggerItems) {
		//noinspection unchecked
		location = Direction.combine((Expression<Direction>) exprs[0], (Expression<Location>) exprs[1]);

		if (node != null) {
			//noinspection unchecked
			trigger = SectionUtils.loadLinkedCode("create loot context", (beforeLoading, afterLoading)
					-> loadCode(node, "create loot context", beforeLoading, afterLoading, LootContextCreateEvent.class));
			return trigger != null ? this : null;
		}

		return this;
	}

	@Override
	protected LootContext @Nullable [] execute(VirtualFrame frame) {
		Location loc = location.executeSingle(frame);
		if (loc == null)
			return new LootContext[0];

		LootContextWrapper wrapper = new LootContextWrapper(loc);
		if (trigger != null) {
			LootContextCreateEvent contextEvent = new LootContextCreateEvent(wrapper);
			Event previousEvent = (Event) frame.getArguments()[0];
			frame.getArguments()[0] = contextEvent;
			trigger.execute(frame);
			frame.getArguments()[0] = previousEvent;

		}
		return new LootContext[]{wrapper.getContext()};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends LootContext> getReturnType() {
		return LootContext.class;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "a loot context " + location.toString(event, debug);
	}

}
