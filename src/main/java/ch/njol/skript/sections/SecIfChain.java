package ch.njol.skript.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@Name("If Chain")
public class SecIfChain extends Section {

    private final List<SecConditional> conditionals = new ArrayList<>();

    public void addConditional(SecConditional cond) {
        conditionals.add(cond);
    }

    @Override
    public SyntaxElement init(Expression<?>[] exprs, int matchedPattern,
							  Kleenean delayed, SkriptParser.ParseResult result,
							  SectionNode node, List<TriggerItem> items) {
        // code not parsed here, handled by child SecConditionals
        return this;
    }

    @Override
    protected @Nullable Object walk(Event event) {
        for (SecConditional cond : conditionals) {
            Object result = cond.walk(event);
            if (result instanceof SecConditional.SkipException)
                break;
        }
        return null;
    }

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "if chain";
	}

	public List<SecConditional> getConditionals() {
		return conditionals;
	}

	public static class IfChainData extends ParserInstance.Data {

		SecIfChain chainNode;

		public IfChainData(ParserInstance parserInstance) {
			super(parserInstance);
			this.chainNode = null;
		}
	}
}