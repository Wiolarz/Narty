package wit.io;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.Mockito;
import wit.io.data.Rent;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.SkiAppException;
import wit.io.exceptions.WritingException;
import wit.io.managers.RentManager;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.fail;

public class RentManagerTest {
    // przetestuj OVERDO i failed

    private RentManager manager = null;

    @Mock
    private Date date;

    @BeforeEach
    public void setUp() throws SkiAppException {
        try {
            manager = new RentManager("src/test/java/wit/io/datasources/Rent");
            manager.resetEntityData();
        } catch (WritingException | ReadingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void backToThePast() {

        long fiveDaysAgo = Instant.now().minus(5, ChronoUnit.DAYS).toEpochMilli();
        try(MockedConstruction<Date> mockedDate = Mockito.mockConstruction(Date.class, (mock, context) -> {
            Mockito.when(mock.getTime()).thenReturn(fiveDaysAgo);
        })) {
            mockedDate.constructed().get(0);
            //not uwu
            // 2 mocki? NOW w jakiejś tam dacie
            // past w innej, w przeszłości w porównaniu do pierwszego?
            setUp();
            manager.addEntity(new Rent(new Date(2025, 6, 16), new Date(2025, 6, 22), null, 10, 10, "", null));
        } catch (Exception e) {
            fail(e.getLocalizedMessage());
        };
    }
}
