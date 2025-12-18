package core.game.world.map.path;

/**
 * The interface Clip mask supplier.
 */
@FunctionalInterface
public interface ClipMaskSupplier {

    /**
     * Gets the clipping flag at a given coordinate.
     *
     * @param z the plane
     * @param x the X coordinate
     * @param y the Y coordinate
     * @return the clipping flag for the tile
     */
    int getClippingFlag(int z, int x, int y);
}
