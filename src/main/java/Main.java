
import wit.io.data.Rent;
import wit.io.data.Ski;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.*;
import wit.io.managers.RentManager;
import wit.io.managers.SkiManager;
import wit.io.utils.Const;
import wit.io.managers.SkiTypeManager;
import wit.io.data.SkiType;

import java.util.Calendar;
import java.util.Date;


public class Main {
    static SkiTypeManager skiTypeManager;
    static SkiManager skiManager;
    private static boolean managersSetup() throws SkiAppException {
        try {
            skiTypeManager = new SkiTypeManager(Const.SkiTypeFilePath);
            System.out.println("pierwszy git");
            skiManager = new SkiManager(Const.SkiFilePath);
        } catch (ReadingException e)
        {
            System.out.println("Failed to create Manager");
            return false;
        }

        skiManager.resetEntityData();
        skiTypeManager.resetEntityData();

        populateData();
        return true;
    }

    private static void populateData() throws SkiAppException {
        skiManager.resetEntityData();
        skiTypeManager.resetEntityData();

        SkiType skiType1 = new SkiType("hello", "world");
        SkiType skiType2 = new SkiType("kill", "mee");
        skiTypeManager.addEntity(skiType1);
        skiTypeManager.addEntity(skiType2);


        Ski ski1 = new Ski(skiType1, "marka_a", "super", "ekstra", 10f);
        Ski ski2 = new Ski(skiType2, "marka_b", "kiepski", "zwykle", 20f);
        Ski ski3 = new Ski(skiType1, "marka_c", "kiepski", "zwykle", 5f);
        Ski ski4 = new Ski(skiType2, "marka_d", "kiepski", "zwykle", 3f);
        Ski ski5 = new Ski(skiType1, "marka_e", "kiepski", "ekstra", 50f);
        skiManager.addEntity(ski1);
        skiManager.addEntity(ski2);
        skiManager.addEntity(ski3);
        skiManager.addEntity(ski4);
        skiManager.addEntity(ski5);

    }


    public static void main(String[] args) throws EntityAlreadyPresentException, ReadingException, WritingException, EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        RentManager rentManager = new RentManager("src/main/java/wit/io/datasources/rent");
        rentManager.resetEntityData();
        rentManager.addEntity(
                new Rent(null, new Date(125, 5, 19), new Date(125, 5, 24), null, "10", "10", "", RentStatus.ACTIVE)
        );
        // todo: +1
        // Wed Jul 15 00:00:00 CEST 3925
        // Wed Jul 13 00:00:00 CEST 3925
        System.out.println(rentManager.getEntitiesList());
    }
}