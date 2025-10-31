package org.skriptlang.skript.test.tests.regression;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.util.ContextlessVirtualFrame;
import ch.njol.skript.test.runner.SkriptJUnitTest;
import ch.njol.skript.variables.Variables;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.block.data.type.Tripwire;
import org.bukkit.event.Event;
import org.junit.Assert;
import org.junit.Test;

import java.util.Objects;

public class BlockDataNotCloned6829Test extends SkriptJUnitTest {

	public void run(String unparsedEffect, VirtualFrame event) {
		Effect effect = Effect.parse(unparsedEffect, "Can't understand this effect: " + unparsedEffect);
		if (effect == null)
			throw new IllegalStateException();
		effect.execute(event);
	}

	@Test
	public void test() {
		VirtualFrame event = ContextlessVirtualFrame.get();
		run("set {_original tripwire} to tripwire[]", event);
		run("set {_another tripwire} to {_original tripwire}", event);
		Tripwire originalTripwire = (Tripwire) Objects.requireNonNull(Variables.getVariable("original tripwire", event, true));
		Tripwire anotherTripwire = (Tripwire) Objects.requireNonNull(Variables.getVariable("another tripwire", event, true));
		anotherTripwire.setDisarmed(true);
		Assert.assertFalse(originalTripwire.isDisarmed());
	}

}
