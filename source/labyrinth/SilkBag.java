package source.labyrinth;
import java.util.LinkedList;

public class SilkBag {
    private  LinkedList<Tile> tiles;

    public Tile getRandomTile() {
        return tiles.remove((int)Math.random() * (tiles.size()));
    }
    public void returnTile(FloorTile tile){
        tile.setIsFrozenUntil(-1);
        tile.setIsOnFireUntil(-1);
        this.tiles.add(tile);
    }
}
