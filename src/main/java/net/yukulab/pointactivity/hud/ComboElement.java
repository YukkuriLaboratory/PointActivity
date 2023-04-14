package net.yukulab.pointactivity.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.yukulab.pointactivity.point.ClientPointContainer;
import net.yukulab.pointactivity.point.PointReason;

import java.util.concurrent.atomic.AtomicInteger;

public class ComboElement extends HudElement {
    public static final ComboElement INSTANCE = new ComboElement();

    private ComboElement() {
        y = 21;
    }

    @Override
    public void render(MatrixStack matrixStack) {
        AtomicInteger index = new AtomicInteger(0);
        MinecraftClient.getInstance()
                .pointactivity$getPointContainer()
                .ifPresent(container -> {
                            if (container.isShadowMode()) {
                                return;
                            }
                            ((ClientPointContainer) container).getCacheDiff().forEach((key, value) -> {
                                        var combo = new Combo(key, value);
                                        combo.y = y + 11 * index.getAndIncrement();
                                        combo.render(matrixStack);
                                    }
                            );
                        }
                );
    }

    @Override
    Text getText() {
        return Text.empty();
    }

    private static class Combo extends HudElement {
        PointReason pointReason;

        int amountCombo;

        Combo(PointReason reason, int amount) {
            pointReason = reason;
            amountCombo = amount;
        }

        @Override
        Text getText() {
            if (amountCombo < 0) {
                return Text.literal(String.format("%s +%d", pointReason.displayName, -amountCombo));
            } else {
                return Text.literal(String.format("%s -%d", pointReason.displayName, amountCombo));
            }
        }
    }
}
