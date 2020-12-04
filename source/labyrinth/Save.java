package source.labyrinth;

import javafx.beans.property.SimpleStringProperty;
/**
 * 
 * @author Max
 * TODO: change all attributes and get set methods that suits the save.txt format 
 */
public class Save {
   SimpleStringProperty fileName;
   SimpleStringProperty descript;
   SimpleStringProperty tiles;
   SimpleStringProperty dateModified;
   Save(String fileName, String descript, String tiles, String dateModified) {
      this.fileName = new SimpleStringProperty(fileName);
      this.descript = new SimpleStringProperty(descript);
      this.tiles = new SimpleStringProperty(tiles);
      this.dateModified = new SimpleStringProperty(dateModified);
   }
   public String getFileName(){
      return fileName.get();
   }
   public void setFileName(String fname){
      fileName.set(fname);
   }
   public String getDescript(){
      return descript.get();
   }
   public void setDescript(String fpath){
      descript.set(fpath);
   }
   public String getTiles(){
      return tiles.get();
   }
   public void setTiles(String fsize){
      tiles.set(fsize);
   }
   public String getDateModified(){
      return dateModified.get();
   }
   public void setModified(String fmodified){
      dateModified.set(fmodified);
   }
}
