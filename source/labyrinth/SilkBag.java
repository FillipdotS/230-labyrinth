package source.labyrinth;
import java.util.LinkedList;

/**
 * SilkBag
 * Stores floor and action tiles
 * Gives out random tile and returns tile
 * @author Erik Miller
 */
public class SilkBag {
    private LinkedList<Tile> tiles = new LinkedList<>();

    /**
     * gives random Tile
     * @return Tile
     */
    public Tile getRandomTile() {
        return tiles.remove((int)(Math.random() * (tiles.size())));
    }

    /**
     * returns tile from board to silkBag
     * @param tile
     */
    public void returnTile(FloorTile tile) {
        tile.setIsFrozenUntil(-1);
        tile.setIsOnFireUntil(-1);
        this.tiles.add(tile);
    }

}
