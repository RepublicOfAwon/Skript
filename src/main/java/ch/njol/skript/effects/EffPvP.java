package ch.njol.skript.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.GameRule;
import org.bukkit.World;
import org.jetbrains.annotations.Nullable;

@Name("PvP")
@Description("Set the PvP state for a given world.")
@Examples({"enable PvP #(current world only)",
		"disable PvP in all worlds"})
@Since("1.3.4")
public class EffPvP extends Effect {

	private static final boolean PVP_GAME_RULE_EXISTS = Skript.fieldExists(GameRule.class, "PVP");

	static {
		Skript.registerEffect(EffPvP.class, "enable PvP [in %worlds%]", "disable PVP [in %worlds%]");
	}
	
	@SuppressWarnings("null")
	private Expression<World> worlds;
	private boolean enable;
	
	@SuppressWarnings({"unchecked", "null"})
	@Override
	public SyntaxElement init(final Expression<?>[] exprs, final int matchedPattern, final Kleenean isDelayed, final ParseResult parseResult) {
		worlds = (Expression<World>) exprs[0];
		enable = matchedPattern == 0;
		return this;
	}
	
	@Override
	protected void executeVoid(VirtualFrame event) {
		if (PVP_GAME_RULE_EXISTS) {
			for (World world : worlds.executeArray(event))
				world.setGameRule(GameRule.PVP, enable);
		} else {
			for (World world : worlds.executeArray(event))
				world.setPVP(enable);
		}
	}
	
	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return (enable ? "enable" : "disable") + " PvP in " + worlds.toString(event, debug);
	}
	
}