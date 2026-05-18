package irislgtm.mto;

import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

public class MTOConfigScreen extends Screen {
    private static final Component TITLE = Component.literal("Mekanism Tiered Outlines");
    private final Screen parent;

    public MTOConfigScreen(Screen parent) {
        super(TITLE);
        this.parent = parent;
    }

    @Override
    protected void init() {
        int centerX = width / 2;
        int centerY = height / 2;
        addRenderableWidget(new OpacitySlider(centerX - 100, centerY - 10, 200, 20));
        addRenderableWidget(Button.builder(Component.literal("Done"), button -> onClose())
              .bounds(centerX - 100, centerY + 24, 200, 20)
              .build());
    }

    @Override
    public void onClose() {
        minecraft.setScreen(parent);
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        guiGraphics.drawCenteredString(font, title, width / 2, height / 2 - 42, 0xFFFFFF);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
    }

    private static class OpacitySlider extends AbstractSliderButton {
        private OpacitySlider(int x, int y, int width, int height) {
            super(x, y, width, height, Component.empty(), MTOConfig.OUTLINE_OPACITY.get());
            updateMessage();
        }

        @Override
        protected void updateMessage() {
            setMessage(Component.literal("Opacity: " + String.format("%.2f", value)));
        }

        @Override
        protected void applyValue() {
            MTOConfig.OUTLINE_OPACITY.set(value);
            MTOConfig.CLIENT_SPEC.save();
        }
    }
}