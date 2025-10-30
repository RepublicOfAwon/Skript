package ch.njol.skript.test.runner;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

import java.util.List;

@NoDoc
public class SecReturnable extends Section implements ReturnHandler<Object> {

	static {
		Skript.registerSection(SecReturnable.class, "returnable [:plural] %*classinfo% section");
	}

	private ClassInfo<?> returnValueType;
	private boolean singleReturnValue;
	private static Object @Nullable [] returnedValues;

	@Override
	public SyntaxElement init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult, SectionNode sectionNode, List<TriggerItem> triggerItems) {
		returnValueType = ((Literal<ClassInfo<?>>) expressions[0]).getSingle();
		singleReturnValue = !parseResult.hasTag("plural");
		loadReturnableSectionCode(sectionNode);
		return this;
	}

	@Override
	public void returnValues(VirtualFrame event, Expression<?> value) {
		returnedValues = value.executeArray(event);
	}

	@Override
	public boolean isSingleReturnValue() {
		return singleReturnValue;
	}

	@Override
	public @Nullable Class<?> returnValueType() {
		return returnValueType.getC();
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "returnable " + (singleReturnValue ? "" : "plural ") + returnValueType.toString(event, debug) + " section";
	}

	@NoDoc
	public static class ExprLastReturnValues extends SimpleExpression<Object> {

		static {
			Skript.registerExpression(ExprLastReturnValues.class, Object.class, ExpressionType.SIMPLE, "[the] last return[ed] value[s]");
		}

		@Override
		public SyntaxElement init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
			return this;
		}

		@Override
		public @Nullable Object[] execute(VirtualFrame event) {
			Object[] returnedValues = SecReturnable.returnedValues;
			SecReturnable.returnedValues = null;
			return returnedValues;
		}

		@Override
		public boolean isSingle() {
			return false;
		}

		@Override
		public Class<?> getReturnType() {
			return Object.class;
		}

		@Override
		public String toString(@Nullable VirtualFrame event, boolean debug) {
			return "last returned values";
		}

	}

}