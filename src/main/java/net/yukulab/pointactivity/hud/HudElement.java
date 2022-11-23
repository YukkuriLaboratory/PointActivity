package net.yukulab.pointactivity.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawableHelper;
import net.minecraft.client.util.math.MatrixStack;
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

    public void render(MatrixStack matrixStack) {
        DrawableHelper.fill(matrixStack, x, y, x + getWidth() + 3, y + getHeight(), backgroundColor);
        MinecraftClient.getInstance().inGameHud.getTextRenderer().draw(matrixStack, getText(), x + 2, y + 2, -1);
    }

    abstract Text getText();
}
