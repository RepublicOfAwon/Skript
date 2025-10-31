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
import ch.njol.skript.util.Date;
import ch.njol.skript.util.Timespan;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.jetbrains.annotations.Nullable;
import ch.njol.skript.lang.simplification.SimplifiedLiteral;

@Name("Date Ago/Later")
@Description("A date the specified timespan before/after another date.")
@Examples({"set {_yesterday} to 1 day ago",
			"set {_hourAfter} to 1 hour after {someOtherDate}",
			"set {_hoursBefore} to 5 hours before {someOtherDate}"})
@Since("2.2-dev33")
public class ExprDateAgoLater extends SimpleExpression<Date> {

    static {
        Skript.registerExpression(ExprDateAgoLater.class, Date.class, ExpressionType.COMBINED,
                "%timespan% (ago|in the past|before [the] [date] %-date%)",
                "%timespan% (later|(from|after) [the] [date] %-date%)");
    }

    @SuppressWarnings("null")
    private Expression<Timespan> timespan;
    @Nullable
    private Expression<Date> date;
    private boolean ago;

    @Override
    @SuppressWarnings({"unchecked", "null"})
    public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
        timespan = (Expression<Timespan>) exprs[0];
        date = (Expression<Date>) exprs[1];
        ago = matchedPattern == 0;
        return this;
    }

    @Override
    @Nullable
    @SuppressWarnings("null")
    protected Date[] execute(VirtualFrame e) {
        Timespan timespan = this.timespan.executeSingle(e);
		Date date = this.date != null ? this.date.executeSingle(e) : new Date();
		if (timespan == null || date == null)
			return null;

        return new Date[] { ago ? date.minus(timespan) : date.plus(timespan) };
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends Date> getReturnType() {
        return Date.class;
    }

	@Override
	public Expression<? extends Date> simplify() {
		if (date instanceof Literal && timespan instanceof Literal)
			return SimplifiedLiteral.fromExpression(this);
		return this;
	}

	@Override
    public String toString(@Nullable VirtualFrame e, boolean debug) {
        return timespan.toString(e, debug) + " " + (ago ? (date != null ? "before " + date.toString(e, debug) : "ago")
			: (date != null ? "after " + date.toString(e, debug) : "later"));
    }
}