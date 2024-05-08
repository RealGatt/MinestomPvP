package io.github.togar2.pvp.feature.tracking;

import io.github.togar2.pvp.damage.combat.CombatManager;
import io.github.togar2.pvp.feature.IndependentFeature;
import io.github.togar2.pvp.feature.RegistrableFeature;
import net.kyori.adventure.text.Component;
import net.minestom.server.entity.Entity;
import net.minestom.server.entity.Player;
import net.minestom.server.entity.damage.Damage;
import net.minestom.server.event.Event;
import net.minestom.server.event.EventNode;
import net.minestom.server.event.player.AsyncPlayerConfigurationEvent;
import net.minestom.server.event.player.PlayerDeathEvent;
import net.minestom.server.event.player.PlayerSpawnEvent;
import net.minestom.server.event.player.PlayerTickEvent;
import net.minestom.server.tag.Tag;
import org.jetbrains.annotations.Nullable;

public class DeathMessageFeatureImpl implements TrackingFeature,
		DeathMessageFeature, RegistrableFeature, IndependentFeature {
	public static final Tag<CombatManager> COMBAT_MANAGER = Tag.Transient("combatManager");
	
	@Override
	public void init(EventNode<Event> node) {
		node.addListener(AsyncPlayerConfigurationEvent.class, event -> {
			event.getPlayer().setTag(COMBAT_MANAGER, new CombatManager(event.getPlayer()));
			System.out.println(event.getPlayer().getTag(COMBAT_MANAGER));
		});
		
		node.addListener(PlayerSpawnEvent.class, event -> event.getPlayer().getTag(COMBAT_MANAGER).reset());
		
		node.addListener(PlayerTickEvent.class, event -> event.getPlayer().getTag(COMBAT_MANAGER).tick());
		
		node.addListener(PlayerDeathEvent.class, event -> {
			Component message = getDeathMessage(event.getPlayer());
			event.setChatMessage(message);
			event.setDeathText(message);
		});
	}
	
	@Override
	public void recordDamage(Player player, @Nullable Entity attacker, Damage damage) {
		int id = attacker == null ? -1 : attacker.getEntityId();
		player.getTag(COMBAT_MANAGER).recordDamage(id, damage);
	}
	
	@Override
	public @Nullable Component getDeathMessage(Player player) {
		return player.getTag(COMBAT_MANAGER).getDeathMessage();
	}
}
