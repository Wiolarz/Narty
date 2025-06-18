
import exceptions.EntityAlreadyPresent;
import exceptions.EntityNotPresent;
import exceptions.ReadingException;
import exceptions.WritingException;
import wit.io.utils.Const;
import wit.io.SkiTypeManager;
import wit.io.data.SkiType;


public class Main
{
    public static void main(String[] args) throws EntityAlreadyPresent, EntityNotPresent, WritingException {
        System.out.println("Main Start");
        SkiTypeManager skiTypeManager;
        try {
            skiTypeManager = new SkiTypeManager(Const.SkiTypeFilePath);
        } catch (ReadingException e)
        {
            System.out.println("Failed to create Manager");
            return;
        }

        SkiType skiType = new SkiType("hello", "world");
        SkiType skiType2 = new SkiType("kill", "mee");
        // skiTypeManager.addEntity(skiType);
        // skiTypeManager.addEntity(skiType2);
        skiTypeManager.removeEntity(skiType);
        skiTypeManager.removeEntity(skiType2);
        System.out.println("elo przed writem");
        try {
            skiTypeManager.writeToFile();    
        } catch (WritingException e)
        {
            System.out.println("Problem z czytaniem");
            return;
        }
        
        
        System.out.println("elo po writiece");
        try {
            skiTypeManager.readFromFile();
        } catch (ReadingException e)
        {
            System.out.println("Problem z pisaniem");
            return;
        }

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
        System.out.println("Main End");
    }
}