package net.yukulab.pointactivity.hud;

import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Style;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.time.Duration;
import java.time.Instant;
import java.util.Optional;

public class PointElement extends HudElement {
    public static final PointElement INSTANCE = new PointElement();

    private float displayedPoint = -1;
    private int targetPoint;
    private float addPointPerMillis;
    private Instant startAnimTime = Instant.now();
    private Instant lastAddedTime = Instant.now();


    private PointElement() {
    }

    @Override
    Text getText() {
        return getDisplayPoint()
                .map(point -> {
                    var number = Text.literal(Integer.toString(point));
                    Formatting color;
                    if (point > 500) {
                        color = Formatting.GREEN;
                    } else if (point > 100) {
                        color = Formatting.YELLOW;
                    } else {
                        color = Formatting.RED;
                    }
                    number.setStyle(Style.EMPTY.withColor(color));
                    return Text.literal("Point: ").append(number);
                })
                .orElse(Text
                        .literal("Point: ")
                        .append(Text
                                .literal("Error")
                                .setStyle(Style.EMPTY.withColor(Formatting.RED))
                        )
                );
    }

    private Optional<Integer> getDisplayPoint() {
        MinecraftClient client = MinecraftClient.getInstance();
        return client
                .pointactivity$getPointContainer()
                .map(container -> {
                    var point = container.getPoint();
                    if (displayedPoint == -1) {
                        displayedPoint = point;
                        return Math.round(displayedPoint);
                    } else if (displayedPoint != point) {
                        int animationMillis = client.pointactivity$getClientConfig().pointAnimationTimeMillis();
                        Instant now = Instant.now();
                        if (targetPoint != point) {
                            var diff = point - displayedPoint;
                            addPointPerMillis = diff / (float) animationMillis;
                            targetPoint = point;
                            startAnimTime = now;
                            lastAddedTime = now;
                        }
                        var betweenMillis = Duration.between(startAnimTime, now).toMillis();
                        if (betweenMillis >= animationMillis) {
                            displayedPoint = targetPoint;
                        } else {
                            displayedPoint += addPointPerMillis * Duration.between(lastAddedTime, now).toMillis();
                            lastAddedTime = now;
                        }
                    }
                    return Math.round(displayedPoint);
                });
    }
}
