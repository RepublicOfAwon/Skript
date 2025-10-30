package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.skript.util.LiteralUtils;
import ch.njol.skript.util.Utils;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

@Name("Ternary")
@Description("A shorthand expression for returning something based on a condition.")
@Examples({"set {points} to 500 if {admin::%player's uuid%} is set else 100"})
@Since("2.2-dev36")
public class ExprTernary extends SimpleExpression<Object> {

	static {
		Skript.registerExpression(ExprTernary.class, Object.class, ExpressionType.COMBINED,
				"%objects% if <.+>[,] (otherwise|else) %objects%");
	}

	private Class<?>[] types;
	private Class<?> superType;

	private Expression<Object> ifTrue;
	private Condition condition;
	private Expression<Object> ifFalse;

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
		ifTrue = LiteralUtils.defendExpression(exprs[0]);
		ifFalse = LiteralUtils.defendExpression(exprs[1]);
		if (ifFalse instanceof ExprTernary || ifTrue instanceof ExprTernary) {
			Skript.error("Ternary operators may not be nested!");
			return null;
		}
		if (!LiteralUtils.canInitSafely(ifTrue, ifFalse)) {
			return null;
		}

		String cond = parseResult.regexes.get(0).group();
		condition = Condition.parse(cond, "Can't understand this condition: " + cond);
		if (condition == null) {
			return null;
		}

		Set<Class<?>> types = new HashSet<>();
		Collections.addAll(types, ifTrue.possibleReturnTypes());
		Collections.addAll(types, ifFalse.possibleReturnTypes());
		this.types = types.toArray(new Class<?>[0]);
		this.superType = Utils.getSuperType(this.types);

		return this;
	}

	@Override
	protected Object[] execute(VirtualFrame event) {
		return condition.executeBoolean(event) ? ifTrue.executeArray(event) : ifFalse.executeArray(event);
	}

	@Override
	public boolean isSingle() {
		return ifTrue.isSingle() && ifFalse.isSingle();
	}

	@Override
	public Class<?> getReturnType() {
		return superType;
	}

	@Override
	public Class<?>[] possibleReturnTypes() {
		return Arrays.copyOf(types, types.length);
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return ifTrue.toString(event, debug)
			+ " if " + condition.toString(event, debug)
			+ " otherwise " + ifFalse.toString(event, debug);
	}

}