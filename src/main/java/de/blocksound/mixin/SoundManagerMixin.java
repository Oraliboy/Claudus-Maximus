package de.blocksound.mixin;

import de.blocksound.BlockSoundConfig;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.client.resources.sounds.SoundInstance;
import net.minecraft.client.sounds.SoundManager;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(SoundManager.class)
public class SoundManagerMixin {

    /**
     * Fängt JEDEN Sound ab, der abgespielt wird (eigene Platzierungen und die anderer Spieler).
     * Ist es ein Block-Platzier-Sound, wird er durch den Platzier-Sound des gewählten Blocks ersetzt.
     */
    @ModifyVariable(
            method = "play(Lnet/minecraft/client/resources/sounds/SoundInstance;)",
            at = @At("HEAD"),
            argsOnly = true)
    private SoundInstance blocksound$replacePlaceSound(SoundInstance original) {
        SoundEvent replacement = BlockSoundConfig.getSelectedPlaceSound();
        if (replacement == null) return original;

        if (!BlockSoundConfig.getAllPlaceSounds().contains(original.getLocation())) return original;
        if (original.getLocation().equals(replacement.getLocation())) return original;

        return new SimpleSoundInstance(
                replacement,
                original.getSource(),
                original.getVolume(),
                original.getPitch(),
                RandomSource.create(),
                original.getX(), original.getY(), original.getZ());
    }
}
