package ch.njol.skript.events;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.util.ContextlessVirtualFrame;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.event.Event;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.entity.EntityTransformEvent.TransformReason;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptEvent;
import ch.njol.skript.lang.SkriptParser.ParseResult;

public class EvtEntityTransform extends SkriptEvent {

	static {
		Skript.registerEvent("Entity Transform", EvtEntityTransform.class, EntityTransformEvent.class, "(entit(y|ies)|%*-entitydatas%) transform[ing] [due to %-transformreasons%]")
				.description("Called when an entity is about to be replaced by another entity.",
						"Examples when it's called include; when a zombie gets cured and a villager spawns, " +
						"an entity drowns in water like a zombie that turns to a drown, " +
						"an entity that gets frozen in powder snow, " +
						"a mooshroom that when sheared, spawns a new cow.")
				.examples("on a zombie transforming due to curing:", "on mooshroom transforming:", "on zombie, skeleton or slime transform:")
				.keywords("entity transform")
				.since("2.8.0");
	}

	@Nullable
	private Literal<TransformReason> reasons;

	@Nullable
	private Literal<EntityData<?>> datas;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] args, int matchedPattern, ParseResult parseResult) {
		datas = (Literal<EntityData<?>>) args[0];
		reasons = (Literal<TransformReason>) args[1];
		return true;
	}

	@Override
	public boolean check(Event event) {
		if (!(event instanceof EntityTransformEvent))
			return false;
		EntityTransformEvent transformEvent = (EntityTransformEvent) event;
		if (reasons != null && !((Expression<TransformReason>)reasons).check(ContextlessVirtualFrame.get(event), reason -> transformEvent.getTransformReason().equals(reason)))
			return false;
		if (datas != null && !((Expression<EntityData<?>>)datas).check(ContextlessVirtualFrame.get(event), data -> data.isInstance(transformEvent.getEntity())))
			return false;
		return true;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		if (datas == null)
			return "entities transforming" + (reasons == null ? "" : " due to " + ((Expression)reasons).toString(event, debug));
		return ((Expression)datas).toString(event, debug) + " transforming" + (reasons == null ? "" : " due to " + ((Expression)reasons).toString(event, debug));
	}

}
