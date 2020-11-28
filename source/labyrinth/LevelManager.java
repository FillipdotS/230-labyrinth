package source.labyrinth;

import java.util.ArrayList;
import java.io.File;

public class LevelManager {
    public ArrayList<String> getLevels(){
        File actual = new File("./source/resources/levels");
        ArrayList<String> levels=new ArrayList<String>();
        for (File f : actual.listFiles()){
            levels.add(f.getName());
        }
        return levels;
    }
}
