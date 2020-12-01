package source.labyrinth;

//Action Tiles (Likely stored as an enum similar to our FloorTile class, should contain a file url to an icon of the Action Tile).

public class ActionTile extends Tile {
    public enum TileType{
        FIRE,
        FREEZE,
        DOUBLEMOVE,
        BACKTRACK
    }
}
