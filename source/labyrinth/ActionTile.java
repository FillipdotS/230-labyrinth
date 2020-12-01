package source.labyrinth;

//Action Tiles (Likely stored as an enum similar to our FloorTile class, should contain a file url to an icon of the Action Tile).

public class ActionTile extends Tile {
    public enum ActionType{
        FIRE("../resources/img/action_tile_fire2.png"),
        ICE("../resources/img/action_tile_ice2.png"),
        DOUBLEMOVE("../resources/img/action_tile_double_move.png"),
        BACKTRACK("../resources/img/action_tile_back_track.png");


        private final String imageURL;

        ActionType(String imageURL) {
            this.imageURL = imageURL;
        }
    }


}
