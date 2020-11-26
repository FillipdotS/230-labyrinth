package source.labyrinth;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class GameMaster {
    public String getBoard(){

        return null;
    }
    /**
     * ToString?
     * **/
    public static void getPlayerInfo(Profile prof){
        String username = prof.getName();
        Integer userid = prof.getID();
        Integer totalPlayed = prof.getTotalPlayed();
        Integer wins = prof.getWins();
        Integer loses = prof.getLosses();
        System.out.println("Player: "+username+"\n" +"ID: "+userid+ "\n"+ "TotalPlay Count: "+totalPlayed+ "\n"+ "Win/Lose: "+wins+"/"+loses);
    }
    /**
     * Basic leaderboard for win
     * **/
    public static void leaderboard(ArrayList<Profile> x){
        Profile[] appending = new Profile[x.size()];
        for(int i = 0; i < x.size() ; i++){
             appending = addX(appending,x.get(i));
            }
        for(int i = 0; i < appending.length-(x.size()-1);i++){
            getPlayerInfo(appending[i]);
            }
        }

    public static Profile[] addX(Profile[] array,Profile add){
        Profile[] dest_Array = new Profile[array.length+1];
        int val = add.getWins();
        if(array[0]==null){
            array[0]= add;
            return array;
        }
        int index = 0;
        int j = 0;
        boolean checker= false;
        if(array[index].getWins() < val){
            checker=true;
        }
        while(!checker){
            if (array[index].getWins() > val){
                checker=true;
            }else if (array[index].getWins() == val){
                if(array[index].getLosses()<add.getLosses()){
                    index++;
                    checker = true;
                }else checker = true;
            }
            else index++;
        }
        for(int i = 0; i < dest_Array.length; i++) {
            if(i == index) {
                dest_Array[i] = add;
            }
            else {
                dest_Array[i] = array[j];
                j++;
            }
        }
        return dest_Array;
    }
    public static void main(String arg[]) {
        ProfileManager pm = new ProfileManager();
        //getPlayerInfo(pm.getProfileByName("Fillip"));
        leaderboard(pm.getProfiles());
    }
}
