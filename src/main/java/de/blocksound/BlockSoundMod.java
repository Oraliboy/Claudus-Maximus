package de.blocksound;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import org.lwjgl.glfw.GLFW;

public class BlockSoundMod implements ClientModInitializer {
    // Taste ist im Steuerungs-Menü (Optionen > Steuerung > Tastenbelegung) frei änderbar.
    public static KeyMapping OPEN_MENU;

    @Override
    public void onInitializeClient() {
        BlockSoundConfig.load();

        OPEN_MENU = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.blocksound.open",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_B,
                "key.categories.blocksound"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (OPEN_MENU.consumeClick()) {
                if (client.screen == null) {
                    client.setScreen(new BlockSelectScreen());
                }
            }
        });
    }
}
