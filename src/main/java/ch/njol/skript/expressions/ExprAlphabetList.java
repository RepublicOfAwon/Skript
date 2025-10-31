package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;
import ch.njol.skript.lang.simplification.SimplifiedLiteral;

import java.util.Arrays;

@Name("Alphabetical Sort")
@Description("Sorts given strings in alphabetical order.")
@Examples({"set {_list::*} to alphabetically sorted {_strings::*}"})
@Since("2.2-dev18b")
public class ExprAlphabetList extends SimpleExpression<String>{
	
	static{
		Skript.registerExpression(ExprAlphabetList.class, String.class, ExpressionType.COMBINED, "alphabetically sorted %strings%");
	}
	
	@SuppressWarnings("null")
	private Expression<String> texts;
	
	@SuppressWarnings({"null", "unchecked"})
	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		texts = (Expression<String>) exprs[0];
		return this;
	}
	
	@Override
	@Nullable
	protected String[] execute(VirtualFrame event) {
		String[] sorted = texts.executeAll(event).clone(); // Not yet sorted
		Arrays.sort(sorted); // Now sorted
		return sorted;
	}
	
	@Override
	public Class<? extends String> getReturnType() {
		return String.class;
	}
	
	@Override
	public boolean isSingle() {
		return false;
	}

	@Override
	public Expression<? extends String> simplify() {
		if (texts instanceof Literal)
			return SimplifiedLiteral.fromExpression(this);
		return this;
	}

	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "alphabetically sorted " + texts.toString(event, debug);
	}
	
}