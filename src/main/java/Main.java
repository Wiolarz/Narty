
import wit.io.Const;
import wit.io.SkiTypeManager;
import wit.io.data.SkiType;

import java.util.ArrayList;
import java.util.List;


public class Main
{
    public static void main(String[] args)
    {
        SkiTypeManager skiTypeManager = new SkiTypeManager(Const.SkiTypeFilePath);
        SkiType skiType = new SkiType("hello", "world");
        SkiType skiType2 = new SkiType("kill", "mee");
        skiTypeManager.setSkiTypes(new ArrayList<>(List.of(skiType, skiType2)));
        System.out.println("elo przed writem");
        skiTypeManager.writeToFile();
        System.out.println("elo po writiece");
        skiTypeManager.readFromFile();
        System.out.println("elo na koncu");


//        try
//        {
//            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(""));
//        } catch (FileNotFoundException e)
//        {
//            throw new RuntimeException(e);
//        }
//
//
//        try
//        {
//            DataInputStream dataInputStream = new DataInputStream(new FileInputStream(""));
//        } catch (FileNotFoundException e)
//        {
//            throw new RuntimeException(e);
//        }

    }
}