package wit.io;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.SkiAppException;
import wit.io.exceptions.WritingException;
import wit.io.managers.RentManager;
import wit.io.managers.SkiManager;

import java.util.Calendar;
import java.util.Date;
import static org.junit.jupiter.api.Assertions.*;


public class RentManagerTest {
    // przetestuj OVERDO i failed

    private RentManager manager = null;


    @BeforeEach
    public void setUp() throws Exception {
        manager = new RentManager("src/test/java/wit/io/datasources/Rent");
        manager.resetEntityData();
    }

    @AfterEach
    public void tearDown() throws ReadingException, WritingException {
        manager.resetEntityData();
    }


    private static Date getDateForDay(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JUNE, day);
        return new Date(calendar.getTimeInMillis());
    }

    public void switchToNewTime(Date date) throws SkiAppException {
        manager = new RentManager("src/test/java/wit/io/datasources/Rent", date);
    }

    @Test
    public void givenRentWithEndDateBeforeToday_whenReadingInData_thenRentStatusGetsUpdatedToOverdue() throws Exception {
        switchToNewTime(getDateForDay(20));
        manager.addEntity(
                new Rent(
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
        assertEquals(RentStatus.OVERDUE, manager.getEntitiesList().get(1).getStatus());
    }

    @Test
    public void givenOverdueRentExistsAndScheduledRentWithCollidingStartDate_whenReadingInData_thenRentStatusGetsUpdatedToFailed() throws SkiAppException {
        switchToNewTime(getDateForDay(21));
        // this entity is not returned on time
        manager.addEntity(
                new Rent(
                        getDateForDay(22),
                        getDateForDay(23),
                        null,
                        "10",
                        "10",
                        "",
                        RentStatus.OVERDUE
                )
        );
        // this entity REQUIRES the first entity to return on time
        manager.addEntity(
                new Rent(
                        getDateForDay(24),
                        getDateForDay(25),
                        null,
                        "10",
                        "10",
                        "",
                        RentStatus.ACTIVE
                )
        );
        
        switchToNewTime(getDateForDay(24));
        
        assertEquals(RentStatus.FAILED, manager.getEntitiesList().get(1).getStatus());
    }

    

}
