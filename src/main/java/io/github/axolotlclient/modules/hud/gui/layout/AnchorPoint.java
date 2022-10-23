package io.github.axolotlclient.modules.hud.gui.layout;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@AllArgsConstructor
public enum AnchorPoint {
    TOP_LEFT("topleft", -1, 1),
    TOP_MIDDLE("topmiddle", 0, 1),
    TOP_RIGHT("topright", 1, 1),
    MIDDLE_LEFT("middleleft", -1, 0),
    MIDDLE_MIDDLE("middlemiddle", 0, 0),
    MIDDLE_RIGHT("middleright", 1, 0),
    BOTTOM_LEFT("bottomleft", -1, -1),
    BOTTOM_MIDDLE("bottommiddle", 0, -1),
    BOTTOM_RIGHT("bottomright", 1, -1),
    ;

    private final String key;

    @Getter
    private final int xComponent;

    @Getter
    private final int yComponent;

    public int getX(int anchorX, int width) {
        return switch (xComponent) {
            case 0 -> anchorX - (width / 2);
            case 1 -> anchorX - width;
            default -> anchorX;
        };
    }

    public int getY(int anchorY, int height) {
        return switch (yComponent) {
            case 0 -> anchorY - (height / 2);
            case 1 -> anchorY - height;
            default -> anchorY;
        };
    }

    public int offsetWidth(int width) {
        return switch (xComponent) {
            case 0 -> width / 2;
            case 1 -> width;
            default -> 0;
        };
    }

    public int offsetHeight(int height) {
        return switch (yComponent) {
            case 0 -> (height / 2);
            case 1 -> 0;
            default -> height;
        };
    }
}
