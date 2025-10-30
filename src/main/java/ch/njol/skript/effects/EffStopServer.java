package ch.njol.skript.effects;

import ch.njol.skript.lang.SyntaxElement;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.Bukkit;
import org.jetbrains.annotations.Nullable;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.util.Kleenean;


@Name("Stop Server")
@Description("Stops or restarts the server. If restart is used when the restart-script spigot.yml option isn't defined, the server will stop instead.")
@Examples({"stop the server", "restart server"})
@Since("2.5")
public class EffStopServer extends Effect {
	
	static {
		Skript.registerEffect(EffStopServer.class,
			"(stop|shut[ ]down) [the] server",
			"restart [the] server");
	}
	
	private boolean restart;
	
	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		restart = matchedPattern == 1;
		return this;
	}
	
	@Override
	protected void executeVoid(VirtualFrame e) {
		if (restart)
			Bukkit.spigot().restart();
		else
			Bukkit.shutdown();
	}
	
	
	@Override
	public String toString(@Nullable VirtualFrame e, boolean debug) {
		return (restart ? "restart" : "stop") + " the server";
	}
	
}