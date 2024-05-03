package io.github.bloepiloepi.pvp.projectile;

import io.github.bloepiloepi.pvp.potion.PotionListener;
import io.github.bloepiloepi.pvp.potion.effect.CustomPotionEffect;
import io.github.bloepiloepi.pvp.potion.effect.CustomPotionEffects;
import io.github.bloepiloepi.pvp.potion.item.CustomPotionType;
import io.github.bloepiloepi.pvp.potion.item.CustomPotionTypes;
import net.minestom.server.color.Color;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.EntityType;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.metadata.projectile.ArrowMeta;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import net.minestom.server.item.Material;
import net.minestom.server.item.component.PotionContents;
import net.minestom.server.potion.Potion;
import net.minestom.server.potion.PotionEffect;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Arrow extends AbstractArrow {
	public static final ItemStack DEFAULT_ARROW = ItemStack.of(Material.ARROW);
	public static final Predicate<ItemStack> ARROW_PREDICATE = stack ->
			stack.material() == Material.ARROW
					|| stack.material() == Material.SPECTRAL_ARROW
					|| stack.material() == Material.TIPPED_ARROW;
	public static final Predicate<ItemStack> ARROW_OR_FIREWORK_PREDICATE = ARROW_PREDICATE.or(stack ->
			stack.material() == Material.FIREWORK_ROCKET);

	private final boolean legacy;
	private PotionContents potionContents;
	private boolean fixedColor;

	public Arrow(@Nullable Entity shooter, boolean legacy) {
		super(shooter, EntityType.ARROW);
		this.legacy = legacy;
		this.potionContents = new PotionContents(
				null,
				new Color(0, 0, 0),
				Collections.emptyList()
		);
	}
	public void inheritEffects(ItemStack stack) {
		PotionContents potionMeta = stack.get(ItemComponent.POTION_CONTENTS);
		if (potionMeta != null) {
			PotionEffect potionEffect = potionMeta.potion();
			@NotNull List<net.minestom.server.potion.CustomPotionEffect> customEffects = potionMeta.customEffects();
			Color color = potionMeta.customColor();

			if (color == new Color(0, 0, 0)) {
				fixedColor = false;
				if (potionEffect == null && customEffects.isEmpty()) {
					setColor(-1);
				} else {
					setColor(PotionListener.getPotionColor(
							PotionListener.getAllPotions(potionEffect, customEffects, legacy)));
				}
			} else {
				fixedColor = true;
				setColor(color.asRGB());
			}

			this.potionContents = potionMeta;
		} else if (stack.material() == Material.ARROW) {
			fixedColor = false;
			setColor(-1);

			this.potionContents = new PotionContents(
					null,
					new Color(0, 1, 0),
					Collections.emptyList()
			);
		}
	}

	@Override
	public void update(long time) {
		super.update(time);

		if (onGround && stuckTime >= 600 && !potionContents.customEffects().isEmpty()) {
			triggerStatus((byte) 0);

			fixedColor = false;
			setColor(-1);

			this.potionContents = new PotionContents(
					null,
					new Color(0, 1, 0),
					Collections.emptyList()
			);
		}
	}

	@Override
	protected void onHurt(LivingEntity entity) {
		PotionEffect potionEffect = potionContents.potion();
		@NotNull List<net.minestom.server.potion.CustomPotionEffect> customPotionEffects = potionContents.customEffects();
		CustomPotionType customPotionType = CustomPotionTypes.get(potionEffect);
		if (customPotionType != null) {
			for (Potion potion : legacy ? customPotionType.getLegacyEffects() : customPotionType.getEffects()) {
				CustomPotionEffect customPotionEffect = CustomPotionEffects.get(potion.effect());
				if (customPotionEffect.isInstant()) {
					customPotionEffect.applyInstantEffect(this, null,
							entity, potion.amplifier(), 1.0D, legacy);
				} else {
					int duration = Math.max(potion.duration() / 8, 1);
					entity.addEffect(new Potion(potion.effect(), potion.amplifier(), duration, potion.flags()));
				}
			}
		}

		if (customPotionEffects.isEmpty()) return;

		customPotionEffects.stream().map(customPotion ->
						new Potion(Objects.requireNonNull(PotionEffect.fromId(customPotion.id().id())),
								customPotion.amplifier(), customPotion.duration(),
								PotionListener.createFlags(
										customPotion.isAmbient(),
										customPotion.showParticles(),
										customPotion.showIcon()
								)))
				.forEach(potion -> {
					CustomPotionEffect customPotionEffect = CustomPotionEffects.get(potion.effect());
					if (customPotionEffect.isInstant()) {
						customPotionEffect.applyInstantEffect(this, null,
								entity, potion.amplifier(), 1.0D, legacy);
					} else {
						entity.addEffect(new Potion(potion.effect(), potion.amplifier(),
								potion.duration(), potion.flags()));
					}
				});
	}

	@Override
	protected ItemStack getPickupItem() {
		if (potionContents.potion() == null && potionContents.customEffects().isEmpty()) {
			return DEFAULT_ARROW;
		}

		return ItemStack.builder(Material.TIPPED_ARROW).set(ItemComponent.POTION_CONTENTS, potionContents).build();
	}

	public void addPotion(net.minestom.server.potion.CustomPotionEffect effect) {
		potionContents.customEffects().add(effect);
		setColor(PotionListener.getPotionColor(
				PotionListener.getAllPotions(null, potionContents.customEffects(), legacy)));
	}

	private void setColor(int color) {
		((ArrowMeta) getEntityMeta()).setColor(color);
	}

	private int getColor() {
		return ((ArrowMeta) getEntityMeta()).getColor();
	}
}