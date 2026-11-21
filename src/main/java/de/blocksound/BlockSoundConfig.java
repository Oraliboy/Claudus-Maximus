package de.blocksound;

import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

public class BlockSoundConfig {
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("blocksound.properties");

    private static Block selected = null;
    private static SoundEvent selectedPlaceSound = null;
    private static Set<ResourceLocation> allPlaceSounds = null;

    public static Block getSelected() { return selected; }
    public static SoundEvent getSelectedPlaceSound() { return selectedPlaceSound; }

    public static void setSelected(Block block) {
        selected = block;
        selectedPlaceSound = block == null ? null : block.defaultBlockState().getSoundType().getPlaceSound();
        save();
    }

    /** Alle Platzier-Sounds, die irgendein Blockzustand im Spiel verwendet. */
    public static Set<ResourceLocation> getAllPlaceSounds() {
        if (allPlaceSounds == null) {
            Set<ResourceLocation> set = new HashSet<>();
            for (Block b : BuiltInRegistries.BLOCK) {
                for (BlockState s : b.getStateDefinition().getPossibleStates()) {
                    set.add(s.getSoundType().getPlaceSound().getLocation());
                }
            }
            allPlaceSounds = set;
        }
        return allPlaceSounds;
    }

    public static void load() {
        if (!Files.exists(FILE)) return;
        try (Reader r = Files.newBufferedReader(FILE)) {
            Properties p = new Properties();
            p.load(r);
            String id = p.getProperty("block");
            if (id != null && !id.isBlank()) {
                ResourceLocation rl = ResourceLocation.tryParse(id);
                if (rl != null) {
                    BuiltInRegistries.BLOCK.getOptional(rl).ifPresent(b -> {
                        selected = b;
                        selectedPlaceSound = b.defaultBlockState().getSoundType().getPlaceSound();
                    });
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void save() {
        Properties p = new Properties();
        p.setProperty("block", selected == null ? "" : BuiltInRegistries.BLOCK.getKey(selected).toString());
        try (Writer w = Files.newBufferedWriter(FILE)) {
            p.store(w, "Block Sound Changer");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
