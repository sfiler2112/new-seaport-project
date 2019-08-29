import java.util.ArrayList;

public class World extends Thing{

    public World(ArrayList<String> fileLines){
        for(String currentLine: fileLines){
            readLine(currentLine);
        }
    }

    public void readLine(String line){

    }
}
