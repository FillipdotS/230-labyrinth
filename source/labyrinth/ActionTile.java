package source.labyrinth;

public class ActionTile extends Tile {
    public enum ActionType {
        FIRE("source/resources/img/action_tile_fire2.png"),
        ICE("source/resources/img/action_tile_ice.png"),
        DOUBLEMOVE("source/resources/img/action_tile_double_move.png"),
        BACKTRACK("source/resources/img/action_tile_back_track.png");

        public final String imageURL;

        ActionType(String imageURL) {
            this.imageURL = imageURL;
        }
    }

    private final ActionType actionType;

    public ActionTile(ActionType actionType) {
        this.actionType = actionType;
    }

    public ActionType getType() {
        return this.actionType;
    }
}
