package net.yukulab.pointactivity.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.yukulab.pointactivity.point.ClientPointContainer;

public class ComboElement extends HudElement {
    public static final ComboElement INSTANCE = new ComboElement();

    private ComboElement() {
        visible = false;
        y = 21;
    }

    @Override
    Text getText() {
        return MinecraftClient.getInstance()
                .pointactivity$getPointContainer()
                .map(container -> {
                    var combo = ((ClientPointContainer) container).getCurrentCombo();
                    if (combo < 0) {
                        return Text.literal("+" + -combo);
                    } else {
                        return Text.literal("-" + combo);
                    }
                })
                .orElse(Text.empty());
    }
}
