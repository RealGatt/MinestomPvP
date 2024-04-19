package io.github.bloepiloepi.pvp.explosion;

import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.entity.metadata.other.EndCrystalMeta;
import net.minestom.server.instance.Instance;
import net.minestom.server.instance.block.Block;
import org.jetbrains.annotations.NotNull;

public class CrystalEntity extends LivingEntity {
	private final boolean fire;
	private final Instance instance;

	public CrystalEntity(boolean fire, boolean showingBottom, Instance instance) {
		super(EntityType.END_CRYSTAL);
		this.fire = fire;
		this.instance = instance;
		setNoGravity(true);
		hasPhysics = false;
		((EndCrystalMeta) getEntityMeta()).setShowingBottom(showingBottom);
	}

	public CrystalEntity(Instance instance) {
		this(false, false, instance);
	}

	@Override
	public void update(long time) {
		if (fire && !instance.getBlock(position).compare(Block.FIRE))
			instance.setBlock(position, Block.FIRE);
	}

	@Override
	public boolean damage(@NotNull DamageType type, float value) {
		if (isDead() || isRemoved())
			return false;
		if (isInvulnerable() || isImmune(type)) {
			return false;
		}

		remove();

		if (instance.getExplosionSupplier() != null) {
			instance.explode((float) position.x(), (float) position.y(), (float) position.z(), 6.0f);
		}

		return true;
	}
}
