package ch.njol.skript.sections;

import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.*;
import ch.njol.skript.lang.parser.ParserInstance;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.nodes.UnexpectedResultException;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.rmi.UnexpectedException;
import java.util.ArrayList;
import java.util.List;

@Name("If Chain")
public class SecIfChain extends Section {

    private final List<TriggerItem> conditionals = new ArrayList<>();
	private boolean init;

    public void addConditional(SecConditional cond) {
        conditionals.add(cond);
    }

    @Override
    public SyntaxElement init(Expression<?>[] exprs, int matchedPattern,
							  Kleenean delayed, SkriptParser.ParseResult result,
							  SectionNode node, List<TriggerItem> items) {
        throw new RuntimeException();
    }

    @Override
	public Object walk(Event event) {
		if (!init) init();
		try {
			super.walk(event);
		} catch (SecConditional.SkipException e) {

		}
		return null;
    }

	private void init() {
		setTriggerItems(conditionals);
		init = true;
	}

	@Override
	public String toString(@Nullable Event event, boolean debug) {
		return "if chain";
	}

	public List<TriggerItem> getConditionals() {
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