package ch.njol.skript.entity;

import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser.ParseResult;
import ch.njol.skript.lang.SyntaxElement;
import ch.njol.skript.util.Patterns;
import ch.njol.skript.variables.Variables;
import ch.njol.util.coll.CollectionUtils;
import org.bukkit.entity.MushroomCow;
import org.bukkit.entity.MushroomCow.Variant;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class MooshroomData extends EntityData<MushroomCow> {

	private static final Patterns<Variant> PATTERNS = new Patterns<>(new Object[][]{
		{"mooshroom", null},
		{"red mooshroom", Variant.RED},
		{"brown mooshroom", Variant.BROWN}
	});
	private static final Variant[] VARIANTS = Variant.values();

	static {
		EntityData.register(MooshroomData.class, "mooshroom", MushroomCow.class, 0, PATTERNS.getPatterns());

		Variables.yggdrasil.registerSingleClass(Variant.class, "MushroomCow.Variant");
	}

	private @Nullable Variant variant = null;
	
	public MooshroomData() {}
	
	public MooshroomData(@Nullable Variant variant) {
		this.variant = variant;
		super.codeNameIndex = PATTERNS.getMatchedPattern(variant, 0).orElse(0);
	}
	
	@Override
	protected SyntaxElement init(Literal<?>[] exprs, int matchedCodeName, int matchedPattern, ParseResult parseResult) {
		variant = PATTERNS.getInfo(matchedCodeName);
		return this;
	}
	
	@Override
	protected SyntaxElement init(@Nullable Class<? extends MushroomCow> entityClass, @Nullable MushroomCow mushroomCow) {
		if (mushroomCow != null) {
			variant = mushroomCow.getVariant();
			super.codeNameIndex = PATTERNS.getMatchedPattern(variant, 0).orElse(0);
		}
		return this;
	}
	
	@Override
	public void set(MushroomCow mushroomCow) {
		Variant variant = this.variant;
		if (variant == null)
			variant = CollectionUtils.getRandom(VARIANTS);
		assert variant != null;
		mushroomCow.setVariant(variant);
	}
	
	@Override
	protected boolean match(MushroomCow mushroomCow) {
		return dataMatch(variant, mushroomCow.getVariant());
	}
	
	@Override
	public Class<? extends MushroomCow> getType() {
		return MushroomCow.class;
	}
	
	@Override
	public @NotNull EntityData<?> getSuperType() {
		return new MooshroomData();
	}
	
	@Override
	protected int hashCode_i() {
		return Objects.hashCode(variant);
	}
	
	@Override
	protected boolean equals_i(EntityData<?> entityData) {
		if (!(entityData instanceof MooshroomData other))
			return false;
		return variant == other.variant;
	}
	
	@Override
	public boolean isSupertypeOf(EntityData<?> entityData) {
		if (!(entityData instanceof MooshroomData other))
			return false;
		return dataMatch(variant, other.variant);
	}

}