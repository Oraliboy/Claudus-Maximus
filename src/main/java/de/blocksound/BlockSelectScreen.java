package de.blocksound;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.resources.sounds.SimpleSoundInstance;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.Block;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class BlockSelectScreen extends Screen {
    private static final int SLOT = 18;
    private static final int GRID_TOP = 50;

    private final List<Block> allBlocks = new ArrayList<>();
    private List<Block> filtered = new ArrayList<>();
    private EditBox search;
    private int scrollRows = 0;
    private int cols, visibleRows, gridLeft;

    public BlockSelectScreen() {
        super(Component.translatable("gui.blocksound.title"));
        for (Block b : BuiltInRegistries.BLOCK) {
            if (b.asItem() != Items.AIR) allBlocks.add(b); // nur Blöcke mit Item-Darstellung
        }
        filtered = new ArrayList<>(allBlocks);
    }

    @Override
    protected void init() {
        cols = Math.max(1, Math.min(18, (width - 40) / SLOT));
        gridLeft = (width - cols * SLOT) / 2;
        visibleRows = Math.max(1, (height - GRID_TOP - 36) / SLOT);

        search = new EditBox(font, width / 2 - 100, 24, 200, 18, Component.translatable("gui.blocksound.search"));
        search.setHint(Component.translatable("gui.blocksound.search"));
        search.setResponder(this::applyFilter);
        addRenderableWidget(search);
        setInitialFocus(search);

        addRenderableWidget(Button.builder(Component.translatable("gui.blocksound.reset"), b -> {
            BlockSoundConfig.setSelected(null);
        }).bounds(width / 2 - 154, height - 28, 150, 20).build());

        addRenderableWidget(Button.builder(Component.translatable("gui.blocksound.done"), b -> onClose())
                .bounds(width / 2 + 4, height - 28, 150, 20).build());
    }

    private void applyFilter(String text) {
        String q = text.toLowerCase(Locale.ROOT).trim();
        filtered = new ArrayList<>();
        for (Block b : allBlocks) {
            String id = BuiltInRegistries.BLOCK.getKey(b).getPath();
            if (q.isEmpty() || id.contains(q) || b.getName().getString().toLowerCase(Locale.ROOT).contains(q)) {
                filtered.add(b);
            }
        }
        scrollRows = 0;
    }

    private int totalRows() { return (filtered.size() + cols - 1) / cols; }

    private int maxScroll() { return Math.max(0, totalRows() - visibleRows); }

    private Block blockAt(double mx, double my) {
        int col = (int) Math.floor((mx - gridLeft) / SLOT);
        int row = (int) Math.floor((my - GRID_TOP) / SLOT);
        if (mx < gridLeft || my < GRID_TOP || col < 0 || col >= cols || row < 0 || row >= visibleRows) return null;
        int idx = (row + scrollRows) * cols + col;
        return idx >= 0 && idx < filtered.size() ? filtered.get(idx) : null;
    }

    @Override
    public void render(GuiGraphics g, int mouseX, int mouseY, float delta) {
        renderBackground(g, mouseX, mouseY, delta);
        g.drawCenteredString(font, title, width / 2, 8, 0xFFFFFF);

        Block current = BlockSoundConfig.getSelected();
        Component cur = Component.translatable("gui.blocksound.current",
                current == null ? Component.translatable("gui.blocksound.none").getString() : current.getName().getString());
        g.drawCenteredString(font, cur, width / 2, 38, 0xAAAAAA);

        g.fill(gridLeft - 2, GRID_TOP - 2, gridLeft + cols * SLOT + 2, GRID_TOP + visibleRows * SLOT + 2, 0xAA000000);

        Block hovered = null;
        for (int row = 0; row < visibleRows; row++) {
            for (int col = 0; col < cols; col++) {
                int idx = (row + scrollRows) * cols + col;
                if (idx >= filtered.size()) break;
                Block b = filtered.get(idx);
                int x = gridLeft + col * SLOT;
                int y = GRID_TOP + row * SLOT;
                if (b == current) g.fill(x, y, x + SLOT, y + SLOT, 0xFF3AA33A);
                boolean over = mouseX >= x && mouseX < x + SLOT && mouseY >= y && mouseY < y + SLOT;
                if (over) { g.fill(x, y, x + SLOT, y + SLOT, 0x80FFFFFF); hovered = b; }
                g.renderItem(new ItemStack(b.asItem()), x + 1, y + 1);
            }
        }

        super.render(g, mouseX, mouseY, delta);

        if (hovered != null) {
            g.renderTooltip(font, new ItemStack(hovered.asItem()), mouseX, mouseY);
        }
    }

    @Override
    public boolean mouseClicked(double mx, double my, int button) {
        if (super.mouseClicked(mx, my, button)) return true;
        Block b = blockAt(mx, my);
        if (b != null && button == 0) {
            BlockSoundConfig.setSelected(b);
            // Vorschau des gewählten Platzier-Sounds
            Minecraft.getInstance().getSoundManager()
                    .play(SimpleSoundInstance.forUI(BlockSoundConfig.getSelectedPlaceSound(), 1.0F));
            return true;
        }
        return false;
    }

    @Override
    public boolean mouseScrolled(double mx, double my, double scrollX, double scrollY) {
        scrollRows = Math.max(0, Math.min(maxScroll(), scrollRows - (int) Math.signum(scrollY)));
        return true;
    }

    @Override
    public boolean isPauseScreen() { return false; }
}
