package source.labyrinth;
import java.util.LinkedList;

/**
 * SilkBag stores Tiles (FloorTiles and ActionTiles), to be retrieved randomly.
 * @author Erik Miller, Fillip Serov
 */
public class SilkBag {
	private static LinkedList<Tile> tiles = new LinkedList<>();

	/**
	 * Empty the SilkBag completely. Use this when starting a new game and you need a fresh bag.
	 */
	public static void emptyBag() {
		tiles.clear();
	}

	/**
	 * Gives a random Tile
	 * @return Tile A random Tile
	 */
	public static Tile getRandomTile() {
		return tiles.remove((int)(Math.random() * (tiles.size())));
	}

	/**
	 * Give a tile back to the SilkBag. If it's a FloorTile, reset it's fire / ice status as well.
	 * @param tile Tile to return back to the bag
	 */
	public static void addTile(Tile tile) {
		if (tile instanceof FloorTile) {
			FloorTile ft = (FloorTile)tile;
			ft.setIsFrozenUntil(-1);
			ft.setIsOnFireUntil(-1);
		}
		tiles.add(tile);
	}

	/**
	 * @return All the Tiles in the SilkBag in a linked list
	 */
	public static LinkedList<Tile> getEntireBag() {
		return tiles;
	}

	/**
	 * @param newBag A LinkedList of Tiles to set as the new "bag"
	 */
	public static void setEntireBag(LinkedList<Tile> newBag) {
		tiles = newBag;
	}
}
