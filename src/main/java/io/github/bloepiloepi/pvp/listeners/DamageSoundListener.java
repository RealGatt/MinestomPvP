package io.github.bloepiloepi.pvp.listeners;

import io.github.bloepiloepi.pvp.events.FinalAttackEvent;
import net.kyori.adventure.sound.Sound;
import net.minestom.server.MinecraftServer;
import net.minestom.server.entity.Player;
import net.minestom.server.network.packet.server.play.SoundEffectPacket;
import net.minestom.server.sound.SoundEvent;

import java.util.List;

public class DamageSoundListener {

    private final static List<Long> HIT_SOUND_SEEDS = List.of(4769332479410481736L, 8946315698180586284L, 3236002118904976264L);

    public DamageSoundListener() {
        MinecraftServer.getGlobalEventHandler().addListener(FinalAttackEvent.class, event -> {
            if (event.isCancelled()) return;
            if (!(event.getTarget() instanceof Player) || !(event.getEntity() instanceof Player)) return;
            Player target = (Player) event.getTarget();

            // Play hit sound
            float pitch = (float) (Math.random() - Math.random()) * 0.2f + 1.0f;
            long seed = HIT_SOUND_SEEDS.get((int) (Math.random() * HIT_SOUND_SEEDS.size()));

            target.sendPacketToViewersAndSelf(new SoundEffectPacket(
                    SoundEvent.ENTITY_PLAYER_HURT,
                    Sound.Source.PLAYER,
                    target.getPosition().blockX(),
                    target.getPosition().blockY(),
                    target.getPosition().blockZ(),
                    1.0f,
                    pitch,
                    seed
            ));
        });
    }
}
