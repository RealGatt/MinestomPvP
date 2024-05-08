package io.github.togar2.pvp.feature.attack;

import io.github.togar2.pvp.entity.EntityUtils;
import io.github.togar2.pvp.feature.CombatVersion;
import io.github.togar2.pvp.feature.IndependentFeature;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.potion.PotionEffect;

import java.util.concurrent.ThreadLocalRandom;

public class CriticalFeatureImpl implements CriticalFeature, IndependentFeature {
	private final CombatVersion version;
	
	public CriticalFeatureImpl(CombatVersion version) {
		this.version = version;
	}
	
	@Override
	public boolean shouldCrit(LivingEntity attacker, AttackValues.PreCritical values) {
		boolean critical = values.strong() && !EntityUtils.isClimbing(attacker)
				&& attacker.getVelocity().y() < 0 && !attacker.isOnGround()
				&& !attacker.hasEffect(PotionEffect.BLINDNESS)
				&& attacker.getVehicle() == null;
		if (version.legacy()) return critical;
		
		// Not sprinting required for critical in 1.9+
		return critical && !attacker.isSprinting();
	}
	
	@Override
	public float applyToDamage(float damage) {
		if (version.legacy()) {
			return damage + ThreadLocalRandom.current().nextInt((int) (damage / 2 + 2));
		} else {
			return damage * 1.5f;
		}
	}
}
