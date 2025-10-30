package ch.njol.skript;

import ch.njol.skript.lang.SkriptParser;
import com.oracle.truffle.api.TruffleLanguage;
import com.oracle.truffle.api.nodes.Node;
import com.oracle.truffle.api.nodes.RootNode;
import com.oracle.truffle.api.CallTarget;
import com.oracle.truffle.api.frame.VirtualFrame;
import com.oracle.truffle.api.source.Source;

@TruffleLanguage.Registration(
	id = SkriptLanguage.ID,
	name = "Skript",
	version = "1.0",
	defaultMimeType = SkriptLanguage.MIME,
	characterMimeTypes = { SkriptLanguage.MIME }
)
public final class SkriptLanguage extends TruffleLanguage<SkriptContext> {

	public static final String ID = "skript";
	public static final String MIME = "application/x-skript";

	@Override
	protected SkriptContext createContext(Env env) {
		return new SkriptContext(this, env);
	}

}