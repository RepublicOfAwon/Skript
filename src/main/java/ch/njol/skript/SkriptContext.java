package ch.njol.skript;

import com.oracle.truffle.api.TruffleLanguage;

public final class SkriptContext {

    private final TruffleLanguage.Env env;
    private final SkriptLanguage language;

    public SkriptContext(SkriptLanguage language, TruffleLanguage.Env env) {
        this.language = language;
        this.env = env;
    }

    public TruffleLanguage.Env getEnv() {
        return env;
    }

    public SkriptLanguage getLanguage() {
        return language;
    }

    public void dispose() {
        // 나중에 전역 변수/함수 해제용
    }
}
