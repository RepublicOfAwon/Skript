package ch.njol.skript.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer.ChangeMode;
import ch.njol.skript.doc.*;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.sections.EffSecShoot;
import ch.njol.util.Kleenean;
import com.oracle.truffle.api.frame.VirtualFrame;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Projectile;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.projectiles.ProjectileSource;
import org.jetbrains.annotations.Nullable;

@Name("Shooter")
@Description("The shooter of a projectile.")
@Example("shooter is a skeleton")
@Since("1.3.7, 2.11 (entity shoot bow event)")
public class ExprShooter extends PropertyExpression<Projectile, LivingEntity> {
	static {
		Skript.registerExpression(ExprShooter.class, LivingEntity.class, ExpressionType.SIMPLE, "[the] shooter [of %projectile%]");
	}

	@Override
	public SyntaxElement init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, ParseResult parseResult) {
		//noinspection unchecked
		setExpr((Expression<? extends Projectile>) exprs[0]);
		return this;
	}
	
	@Override
	protected LivingEntity @Nullable [] get(VirtualFrame event, Projectile[] source) {
		if (event instanceof EffSecShoot.ShootEvent shootEvent && getExpr().isDefault() && !(shootEvent.getProjectile() instanceof Projectile)) {
			return new LivingEntity[]{shootEvent.getShooter()};
		} else if (event instanceof EntityShootBowEvent shootBowEvent && getExpr().isDefault()) {
			return new LivingEntity[]{shootBowEvent.getEntity()};
		}

		return get(source, projectile -> {
			Object shooter = projectile != null ? projectile.getShooter() : null;
			if (shooter instanceof LivingEntity livingShooter)
				return livingShooter;
			return null;
		});
	}
	
	@Override
	public @Nullable Class<?>[] acceptChange(ChangeMode mode) {
		if (mode == ChangeMode.SET)
			return new Class[] {LivingEntity.class};
		return super.acceptChange(mode);
	}
	
	@Override
	public void change(VirtualFrame event, Object @Nullable [] delta, ChangeMode mode) {
		if (mode == ChangeMode.SET) {
			assert delta != null;
			ProjectileSource source = (ProjectileSource) delta[0];
			for (Projectile projectile : getExpr().executeArray(event)) {
				assert projectile != null : getExpr();
				projectile.setShooter(source);
			}
		} else {
			super.change(event, delta, mode);
		}
	}
	
	@Override
	public Class<LivingEntity> getReturnType() {
		return LivingEntity.class;
	}
	
	@Override
	public String toString(@Nullable VirtualFrame event, boolean debug) {
		return "the shooter" + (getExpr().isDefault() ? "" : " of " + getExpr().toString(event, debug));
	}
	
}