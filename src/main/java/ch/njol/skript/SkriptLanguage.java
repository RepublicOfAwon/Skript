package ch.njol.skript;

import com.oracle.truffle.api.TruffleLanguage;

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