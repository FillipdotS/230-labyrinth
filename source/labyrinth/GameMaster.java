package source.labyrinth;

import java.net.Inet4Address;

public class GameMaster {
    private Profile exact;
    public String getBoard(){
        return null;
    }
    public static void getPlayer(Profile prof){
        String username = prof.getName();
        Integer userid = prof.getID();
        Integer totalPlayed = prof.getTotalPlayed();
        Integer wins = prof.getWins();
        Integer loses = prof.getLosses();
        System.out.println("Player: "+username+"\n" +"ID: "+userid+ "\n"+ "TotalPlay Count: "+totalPlayed+ "\n"+ "Win/Lose: "+wins+"/"+loses);
    }
    public static void main(String arg[]) {
        ProfileManager pm = new ProfileManager();
        getPlayer(pm.getProfileByName("Fillip"));
    }
}
