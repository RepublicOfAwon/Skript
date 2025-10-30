package ch.njol.skript.test.runner;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.NoDoc;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

@Name("Experimental Only")
@Description("A do-nothing syntax that only parses when `example feature` is enabled.")
@NoDoc
public class ExprExperimentalOnly extends SimpleExpression<Boolean> {

	static {
		if (TestMode.ENABLED)
			Skript.registerExpression(ExprExperimentalOnly.class, Boolean.class, ExpressionType.SIMPLE, "experimental only");
	}

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return this.getParser().hasExperiment(TestFeatures.EXAMPLE_FEATURE) ? this : null;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "experimental only";
	}

	@Override
	protected @Nullable Boolean[] execute(VirtualFrame event) {
		return new Boolean[]{true};
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends Boolean> getReturnType() {
		return Boolean.class;
	}

}
