package ch.njol.skript.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@Name("While Loop")
@Description("While Loop sections are loops that will just keep repeating as long as a condition is met.")
@Examples({
	"while size of all players < 5:",
	"\tsend \"More players are needed to begin the adventure\" to all players",
	"\twait 5 seconds",
	"",
	"set {_counter} to 1",
	"do while {_counter} > 1: # false but will increase {_counter} by 1 then get out",
	"\tadd 1 to {_counter}",
	"",
	"# Be careful when using while loops with conditions that are almost ",
	"# always true for a long time without using 'wait %timespan%' inside it, ",
	"# otherwise it will probably hang and crash your server.",
	"while player is online:",
	"\tgive player 1 dirt",
	"\twait 1 second # without using a delay effect the server will crash",
})
@Since("2.0, 2.6 (do while)")
public class SecWhile extends LoopSection {

	static {
		Skript.registerSection(SecWhile.class, "[:do] while <.+>");
	}

	@SuppressWarnings("NotNullFieldNotInitialized")
	private Condition condition;

	private boolean doWhile;
	private boolean ranDoWhile = false;

	@Override
	public SyntaxElement init(Expression<?>[] exprs,
                              int matchedPattern,
                              Kleenean isDelayed,
                              ParseResult parseResult,
                              SectionNode sectionNode,
                              List<TriggerItem> triggerItems) {
		String expr = parseResult.regexes.get(0).group();

		condition = Condition.parse(expr, "Can't understand this condition: " + expr);
		if (condition == null)
			return null;

		doWhile = parseResult.hasTag("do");
		loadOptionalCode(sectionNode);
		return this;
	}

	@Override
	public Object walk(Event event) {
		while (true) {
			if ((doWhile && !ranDoWhile) || condition.check(event)) {
				ranDoWhile = true;
				currentLoopCounter.put(event, (currentLoopCounter.getOrDefault(event, 0L)) + 1);
				try {
					super.walk(event);
				} catch (ContinueException ignored) {
					continue;
				} catch (BreakException ignored) {
					break;
				}
			} else {
				exit(event);
				return null;
			}
		}
		return null;
	}

	@Override
	public @Nullable ExecutionIntent executionIntent() {
		return doWhile ? triggerExecutionIntent() : null;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return (doWhile ? "do " : "") + "while " + condition.toString(event, debug);
	}

	@Override
	public void exit(Event event) {
		ranDoWhile = false;
		super.exit(event);
	}

}
