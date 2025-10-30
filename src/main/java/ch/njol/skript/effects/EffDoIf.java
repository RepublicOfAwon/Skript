package ch.njol.skript.effects;

import ch.njol.skript.lang.*;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.util.Kleenean;

@Name("Do If")
@Description("Execute an effect if a condition is true.")
@Examples({"on join:",
		"\tgive a diamond to the player if the player has permission \"rank.vip\""})
@Since("2.3")
public class EffDoIf extends Effect  {

	static {
		Skript.registerEffect(EffDoIf.class, "<.+> if <.+>");
	}

	@SuppressWarnings("null")
	private Effect effect;

	@SuppressWarnings("null")
	private Condition condition;

	@SuppressWarnings("null")
	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		String eff = parseResult.regexes.get(0).group();
		String cond = parseResult.regexes.get(1).group();
		effect = Effect.parse(eff, "Can't understand this effect: " + eff);
		if (effect instanceof EffDoIf) {
			Skript.error("Do if effects may not be nested!");
			return null;
		}
		condition = Condition.parse(cond, "Can't understand this condition: " + cond);

		if (effect == null || condition == null) {
			return null;
		}

		// handle special hint cases
		// if this statement could result in execution stopping, we want to pass up hints
		if (effect.executionIntent() instanceof ExecutionIntent.StopSections intent) {
			// copy up current hints
			getParser().getHintManager().mergeScope(0, intent.levels(), true);
		}

		return this;
	}

	@Override
	protected void execute(Event e) {}
	
	@Override
	public Object walk(Event e) {
		if (condition.check(e)) {
			effect.walk(e);
		}
		return null;
	}

	@Override
	public String toString(@Nullable Event e, boolean debug) {
		return effect.toString(e, debug) + " if " + condition.toString(e, debug);
	}

}