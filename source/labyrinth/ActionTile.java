package source.labyrinth;

public class ActionTile extends Tile {
    public enum ActionType {
        FIRE("source/resources/img/action_tile_fire2.png"),
        ICE("source/resources/img/action_tile_ice2.png"),
        DOUBLEMOVE("source/resources/img/action_tile_double_move.png"),
        BACKTRACK("source/resources/img/action_tile_back_track.png");

        private final String imageURL;

        ActionType(String imageURL) {
            this.imageURL = imageURL;
        }
    }
}
