package io.github.axolotlclient.modules.hud.gui.component;

import io.github.axolotlclient.modules.hud.gui.layout.AnchorPoint;

/**
 * This implementation of Hud modules is based on KronHUD.
 * <a href="https://github.com/DarkKronicle/KronHUD">Github Link.</a>
 * @license GPL-3.0
 *
 * Represents an object that width/height can change and it can react accordingly
 */
public interface DynamicallyPositionable extends Positionable {

    /**
     * Get the direction that this object is anchored in
     *
     * @return {@link AnchorPoint} that represents where the object is anchored in
     */
    AnchorPoint getAnchor();

    @Override
    default int getX() {
        return getAnchor().getX(getRawX(), getWidth());
    }

    @Override
    default int getY() {
        return getAnchor().getY(getRawY(), getHeight());
    }

    @Override
    default int getTrueX() {
        return getAnchor().getX(getRawTrueX(), getTrueWidth());
    }

    @Override
    default int getTrueY() {
        return getAnchor().getY(getRawTrueY(), getTrueHeight());
    }

    @Override
    default int offsetWidth() {
        return getAnchor().offsetWidth(getWidth());
    }

    @Override
    default int offsetHeight() {
        return getAnchor().offsetHeight(getHeight());
    }

}
