package ch.njol.skript.test.runner;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

@NoDoc
public class ExprDefaultNumberValue extends SimpleExpression<Number> {

	static {
		if (TestMode.ENABLED)
			Skript.registerExpression(ExprDefaultNumberValue.class, Number.class, ExpressionType.PROPERTY,
					"default number [%number%]");
	}

	Expression<Number> value;

	@Override
	public SyntaxElement init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		value = (Expression<Number>) expressions[0];
		return this;
	}

	@Override
	protected Number @Nullable [] execute(VirtualFrame event) {
		return new Number[]{value.executeSingle(event)};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Number> getReturnType() {
		return Number.class;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "default number";
	}

}