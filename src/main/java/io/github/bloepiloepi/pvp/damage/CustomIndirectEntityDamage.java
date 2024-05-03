package io.github.bloepiloepi.pvp.damage;

import io.github.bloepiloepi.pvp.entity.EntityUtils;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.LivingEntity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.DamageType;
import net.minestom.server.item.ItemComponent;
import net.minestom.server.item.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public class CustomIndirectEntityDamage extends CustomEntityDamage {
	private final @Nullable Entity owner;
	
	public CustomIndirectEntityDamage(DamageType origintype, String name, @NotNull Entity projectile, @Nullable Entity owner) {
		super(origintype, name, projectile);
		this.owner = owner;
	}
	
	@Override
	@Nullable
	public Entity getDirectEntity() {
		return this.entity;
	}
	
	@Override
	@Nullable
	public Entity getEntity() {
		return owner;
	}
	
	@Nullable
	public Entity getOwner() {
		return owner;
	}
	
	@Override
	public @Nullable Component getDeathMessage(@NotNull Player killed) {
		Component ownerName = owner == null ? EntityUtils.getName(entity) : EntityUtils.getName(owner);
		ItemStack weapon = entity instanceof LivingEntity ? ((LivingEntity) entity).getItemInMainHand() : ItemStack.AIR;
		String id = "death.attack." + originDamageType.messageId();
		if (!weapon.isAir() && weapon.get(ItemComponent.ITEM_NAME) != null) {
			return Component.translatable(id + ".item", EntityUtils.getName(killed), ownerName, Objects.requireNonNull(weapon.get(ItemComponent.ITEM_NAME)));
		} else {
			return Component.translatable(id, EntityUtils.getName(killed), ownerName);
		}
	}
}
