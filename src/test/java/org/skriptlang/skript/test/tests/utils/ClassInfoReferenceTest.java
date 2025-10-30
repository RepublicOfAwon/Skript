package org.skriptlang.skript.test.tests.utils;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionList;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.ContextlessVirtualFrame;
import ch.njol.skript.registrations.Classes;
import ch.njol.skript.util.ClassInfoReference;
import ch.njol.skript.variables.Variables;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.junit.Assert;
import org.junit.Test;


public class ClassInfoReferenceTest {

	@NotNull
	private Expression<ClassInfoReference> parseAndWrap(String expr) {
		ParseResult parseResult = SkriptParser.parse(expr, "%classinfos%", SkriptParser.ALL_FLAGS, ParseContext.DEFAULT);
		if (parseResult == null)
			throw new IllegalStateException("Parsed expression " + expr + " is null");
		return ClassInfoReference.wrap((Expression<ClassInfo<?>>) parseResult.exprs[0]);
	}

	@Test
	public void testWrapper() {
		ClassInfoReference reference = parseAndWrap("object").executeSingle(null);
        Assert.assertEquals(Object.class, reference.getClassInfo().getC());
		Assert.assertTrue(reference.isPlural().isFalse());

		reference = parseAndWrap("string").executeSingle(null);
		Assert.assertEquals(String.class, reference.getClassInfo().getC());
		Assert.assertTrue(reference.isPlural().isFalse());

		reference = parseAndWrap("players").executeSingle(null);
		Assert.assertEquals(Player.class, reference.getClassInfo().getC());
		Assert.assertTrue(reference.isPlural().isTrue());

		Event event = ContextlessVirtualFrame.get();
		Variables.setVariable("classinfo", Classes.getExactClassInfo(Block.class), event, true);
		reference = parseAndWrap("{_classinfo}").executeSingle(event);
		Assert.assertEquals(Block.class, reference.getClassInfo().getC());
		Assert.assertTrue(reference.isPlural().isUnknown());

		ExpressionList<ClassInfoReference> referenceList = (ExpressionList<ClassInfoReference>) parseAndWrap("blocks, player or entities");
        Assert.assertFalse(referenceList.getAnd());
		Expression<? extends ClassInfoReference>[] childExpressions = referenceList.getExpressions();

		ClassInfoReference firstReference = childExpressions[0].executeSingle(null);
		Assert.assertEquals(Block.class, firstReference.getClassInfo().getC());
		Assert.assertTrue(firstReference.isPlural().isTrue());

		ClassInfoReference secondReference = childExpressions[1].executeSingle(null);
		Assert.assertEquals(Player.class, secondReference.getClassInfo().getC());
		Assert.assertTrue(secondReference.isPlural().isFalse());

		ClassInfoReference thirdReference = childExpressions[2].executeSingle(null);
		Assert.assertEquals(Entity.class, thirdReference.getClassInfo().getC());
		Assert.assertTrue(thirdReference.isPlural().isTrue());

		referenceList = (ExpressionList<ClassInfoReference>) parseAndWrap("{_block} and {_player}");
		Assert.assertTrue(referenceList.getAnd());
		childExpressions = referenceList.getExpressions();

		event = ContextlessVirtualFrame.get();
		Variables.setVariable("block", Classes.getExactClassInfo(Block.class), event, true);
		Variables.setVariable("player", Classes.getExactClassInfo(Player.class), event, true);
		firstReference = childExpressions[0].executeSingle(event);
		Assert.assertEquals(Block.class, firstReference.getClassInfo().getC());
		Assert.assertTrue(firstReference.isPlural().isUnknown());

		secondReference = childExpressions[1].executeSingle(event);
		Assert.assertEquals(Player.class, secondReference.getClassInfo().getC());
		Assert.assertTrue(secondReference.isPlural().isUnknown());

	}

}
