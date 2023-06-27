package net.yukulab.pointactivity.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;


public abstract class HudElement {
    public int x = 5;
    public int y = 10;

    public int backgroundColor = -0x6FAFAFB0;

    public boolean visible = true;

    public int getHeight() {
        return MinecraftClient.getInstance().inGameHud.getTextRenderer().fontHeight + 2;
    }

    public int getWidth() {
        return MinecraftClient.getInstance().inGameHud.getTextRenderer().getWidth(getText());
    }

    public void render(DrawContext context) {
        context.fill(x, y, x + getWidth() + 3, y + getHeight(), backgroundColor);
        context.drawText(MinecraftClient.getInstance().inGameHud.getTextRenderer(), getText(), x + 2, y + 2, -1, false);
    }

    abstract Text getText();
}
