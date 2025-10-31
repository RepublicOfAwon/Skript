package ch.njol.skript.test.runner;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.ContextlessVirtualFrame;
import ch.njol.skript.registrations.EventValues;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

@NoDoc
public class EvtTestCase extends SkriptEvent {

	static {
		if (TestMode.ENABLED && !TestMode.GEN_DOCS) {
			Skript.registerEvent("Test Case", EvtTestCase.class, SkriptTestEvent.class, "test %string% [when <.+>]")
					.description("Contents represent one test case.")
					.examples("")
					.since("2.5");

			EventValues.registerEventValue(SkriptTestEvent.class, Block.class, ignored -> SkriptJUnitTest.getBlock());
			EventValues.registerEventValue(SkriptTestEvent.class, Location.class, ignored -> SkriptJUnitTest.getTestLocation());
			EventValues.registerEventValue(SkriptTestEvent.class, World.class, ignored -> SkriptJUnitTest.getTestWorld());
		}
	}

	private Literal<String> name;

	@Nullable
	private Condition condition;

	@Override
	@SuppressWarnings("unchecked")
	public boolean init(Expression<?>[] args, int matchedPattern, SkriptParser.ParseResult parseResult) {
		name = (Literal<String>) args[0];
		if (!parseResult.regexes.isEmpty()) { // Do not parse or run unless condition is met
			String cond = parseResult.regexes.get(0).group();
			condition = Condition.parse(cond, "Can't understand this condition: " + cond);
		}
		return true;
	}

	@Override
	public boolean check(Event event) {
		String n = name.getSingle();
		if (n == null)
			return false;
		Skript.info("Running test case " + n);
		TestTracker.testStarted(n);
		return true;
	}

	@Override
	public boolean shouldLoadEvent() {
		var c = ContextlessVirtualFrame.get();
		c.getArguments()[0] = new SkriptTestEvent();
		return condition != null ? condition.executeBoolean(c) : true;
	}

	public String getTestName() {
		return name.getSingle();
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "test " + name.getSingle();
	}

}
