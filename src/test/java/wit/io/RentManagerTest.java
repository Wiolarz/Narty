package wit.io;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.SkiAppException;
import wit.io.exceptions.WritingException;
import wit.io.managers.RentManager;

import java.util.Calendar;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.*;

public class RentManagerTest {
    // przetestuj OVERDO i failed

    private RentManager manager = null;

    private static final long past = getTimestamp(10);
    private static final long now = getTimestamp(15);
    private static final long future = getTimestamp(20);

    private static MockedConstruction<Date> dateMock;


    @BeforeEach
    public void setUp() throws SkiAppException {
        try {
            createNewManager();
            manager.resetEntityData();
        } catch (WritingException | ReadingException e) {
            fail(e.getMessage());
        }
    }

    private void createNewManager() throws SkiAppException {
        manager = new RentManager("src/test/java/wit/io/datasources/Rent");
    }

    private static long getTimestamp(int day) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(2025, Calendar.JUNE, day, 0, 0, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        return calendar.getTimeInMillis();
    }

    @AfterAll
    public static void tearDown() {
        dateMock.close();
    }

    public void switchToNewTime(Long time) {
        if(dateMock != null) {
            dateMock.close();
        }
        dateMock = Mockito.mockConstruction(Date.class, (mock, context) -> {
            Mockito.when(mock.getTime()).thenReturn(time);
        });
    }

    @Test
    public void backToThePast() throws SkiAppException {
        switchToNewTime(now);
        manager.addEntity(
                new Rent(
                        new Date(2025, 6, 16),
                        new Date(2025, 6, 19),
                        null,
                        "10",
                        "10",
                        "",
                        RentStatus.ACTIVE
                )
        );
        switchToNewTime(future);
        createNewManager();
        assertEquals(RentStatus.OVERDUE, manager.getEntities().get(0).getStatus());
    }

    @Test
    public void givenRentsInThePast_whenEndDateIsBeforeNow_thenChangeRentStatus() {

    }
}
