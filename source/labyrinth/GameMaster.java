package source.labyrinth;

public class GameMaster {
    public String getBoard(){
        return null;
    }
    public static void getPlayer(String username, Integer userid){
        Profile x = new Profile(username, userid);
        Integer totalPlayed = x.getTotalPlayed();
        Integer wins = x.getWins();
        Integer loses = x.getLosses();
        System.out.println("Player: "+username+"\n" +"ID: "+userid+ "\n"+ "TotalPlay Count: "+totalPlayed+ "\n"+ "Win/Lose: "+wins+"/"+loses);
    }
    public static void main(String arg[]) {

        getPlayer("name", 1);
    }
}
