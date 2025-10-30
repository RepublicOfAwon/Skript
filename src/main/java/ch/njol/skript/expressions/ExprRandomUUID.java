package ch.njol.skript.expressions;

import java.util.UUID;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;

@Name("Random UUID")
@Description("Returns a random UUID.")
@Examples("set {_uuid} to random uuid")
@Since("2.5.1, 2.11 (return UUIDs)")
public class ExprRandomUUID extends SimpleExpression<UUID> {

	static {
		Skript.registerExpression(ExprRandomUUID.class, UUID.class, ExpressionType.SIMPLE, "[a] random uuid");
	}

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		return this;
	}

	@Override
	protected UUID @Nullable [] execute(VirtualFrame e) {
		return new UUID[]{ UUID.randomUUID() };
	}

	@Override
	public boolean isSingle() {
		return true;
	}

	@Override
	public Class<? extends UUID> getReturnType() {
		return UUID.class;
	}

	@Override
	public String toString(@Nullable VirtualFrame e, boolean debug) {
		return "random uuid";
	}

}