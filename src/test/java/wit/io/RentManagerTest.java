package wit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.SkiAppException;
import wit.io.exceptions.WritingException;
import wit.io.managers.RentManager;
import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;


public class RentManagerTest {

    private RentManager manager = null;

    private final Consumer<Set<Rent>> consumer = (set) -> {
        try {
            manager.setEntities(set);
        } catch (WritingException e) {
            throw new RuntimeException(e);
        }
    };

    @BeforeEach
    public void setUp() throws Exception {
        manager = new RentManager("src/test/java/wit/io/datasources/Rent");
        manager.resetEntityData();
    }

    @AfterEach
    public void tearDown() throws ReadingException, WritingException {
        manager.resetEntityData();
    }


    private static LocalDate getDateForDay(int day) {
        return LocalDate.of(2025, 6, day);
    }

    public void switchToNewTime(LocalDate date) throws SkiAppException {
        manager = new RentManager("src/test/java/wit/io/datasources/Rent", date);
    }

    @Test
    public void givenRentWithEndDateBeforeToday_whenReadingInData_thenRentStatusGetsUpdatedToOverdue() throws Exception {
        switchToNewTime(getDateForDay(20));
        manager.addEntity(
                new Rent(
                        null,
                        getDateForDay(22),
                        getDateForDay(23),
                        null,
                        "10",
                        "10",
                        "",
                        RentStatus.ACTIVE
                )
        );
        switchToNewTime(getDateForDay(25));
        assertEquals(RentStatus.OVERDUE, manager.getEntitiesList().get(0).getStatus());
    }

    @Test
    public void givenOverdueRentExistsAndScheduledRentWithCollidingStartDate_whenReadingInData_thenRentStatusGetsUpdatedToFailed() throws SkiAppException {
        switchToNewTime(getDateForDay(21));
        // this entity is not returned on time
        Set<Rent> listOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(22), getDateForDay(23), null, "10", "10", "", RentStatus.OVERDUE),
                new Rent(null, getDateForDay(24), getDateForDay(25), null, "10", "10", "", RentStatus.ACTIVE)
        ));
        consumer.accept(listOfRentals);
        
        switchToNewTime(getDateForDay(24));
        
        assertEquals(RentStatus.FAILED, manager.getEntitiesList().get(1).getStatus());
    }

    @Test
    public void givenOverdueRentExistsAndScheduledRentWithNotCollidingStartDate_whenReadingInData_thenRentStatusDoesNotGetUpdated() throws SkiAppException {
        switchToNewTime(getDateForDay(21));
        // this entity is not returned on time
        Set<Rent> listOfRentals = new HashSet<>(List.of(
                new Rent(UUID.nameUUIDFromBytes("test".getBytes()), getDateForDay(22), getDateForDay(23), getDateForDay(25), "10", "10", "", RentStatus.OVERDUE),
                new Rent(UUID.nameUUIDFromBytes("test1".getBytes()), getDateForDay(28), getDateForDay(29), null, "10", "10", "", RentStatus.ACTIVE)
        ));
        consumer.accept(listOfRentals);

        switchToNewTime(getDateForDay(28));
        //assertEquals(RentStatus.ACTIVE, manager.getEntities().stream().filter((rent) -> rent.getRentID().equals()));
    }


    @Test
    public void givenRentalsDataExist_whenCreatingNewManager_thenRentalDataIsCorrectlyLoaded() throws SkiAppException {
        switchToNewTime(getDateForDay(21));
        Set<Rent> listOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(22), getDateForDay(23), null, "10", "10", "", RentStatus.ACTIVE),
                new Rent(null, getDateForDay(24), getDateForDay(25), null, "10", "10", "", RentStatus.ACTIVE)
        ));
        consumer.accept(listOfRentals);

        manager = new RentManager("src/test/java/wit/io/datasources/Rent");
        assertEquals(2, manager.getEntitiesList().size());
    }


    

}
