package wit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Rent;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.SkiAppException;
import wit.io.exceptions.WritingException;
import wit.io.managers.RentManager;
import wit.io.managers.ReportManager;
import wit.io.managers.SkiManager;
import wit.io.utils.Util;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import static org.junit.jupiter.api.Assertions.*;

public class ReportManagerTest {
    private RentManager rentManager = null;

    private final Consumer<Set<Rent>> dataEntitySetter = (set) -> {
        try {
            // THIS OMITS CHECKS AND SETTING STATUS TO ACTIVE!
            rentManager.setEntities(set);
        } catch (WritingException e) {
            throw new RuntimeException(e);
        }
    };

    private SkiManager skiManager = null;

    private final Consumer<List<Ski>> skiAddingConsumer = (list) -> list.forEach(ski -> {
        try {
            skiManager.addEntity(ski);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    });

    private ReportManager reportManager = null;

    private LocalDate now = null;

    @BeforeEach
    public void setUp() {
        try {
            skiManager = new SkiManager("src/test/java/wit/io/datasources/ski");
            rentManager = new RentManager("src/test/java/wit/io/datasources/Rent");
            skiManager.resetEntityData();
            rentManager.resetEntityData();

            reportManager = new ReportManager(rentManager, skiManager);
            now = getDateForDay(20);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() throws WritingException {
        rentManager.resetEntityData();
        skiManager.resetEntityData();
    }

    private static LocalDate getDateForDay(int day) {
        return LocalDate.of(2025, 5, day);
    }

    public void switchToNewDay(int day) throws SkiAppException {
        LocalDate now = getDateForDay(day);
        rentManager = new RentManager("src/test/java/wit/io/datasources/Rent", now);
        reportManager = new ReportManager(rentManager, skiManager);
        this.now = now;
    }

    @Test
    public void givenExistingNotRentedSkis_whenAvailableSkisCalled_thenReturnCorrectSkis() {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis, new ArrayList<>(reportManager.availableSkis(now))));
    }

    @Test
    public void givenNoSkis_whenAvailableSkisCalled_thenReturnEmpty() {
        assertTrue(reportManager.availableSkis(now).isEmpty());
    }

    @Test
    public void givenScheduledRentAndOneCurrentlyOngoing_whenAvailableSkisCalled_thenReturnCurrentlyAvailableOnes() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(19), getDateForDay(23), null, "model1", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(28), getDateForDay(29), null, "model2", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis.subList(1,3), new ArrayList<>(reportManager.availableSkis(now))));
    }

    @Test
    public void givenAllCurrentlyOngoingRents_whenAvailableSkisCalled_thenReturnNoAvailableSkis() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(19), getDateForDay(25), null, "model1", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(18), getDateForDay(29), null, "model2", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test2"), getDateForDay(15), getDateForDay(30), null, "model3", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(20);
        assertEquals(0, reportManager.availableSkis(now).size());
    }

    @Test
    public void givenAllSkisReturned_whenAvailableSkisCalled_thenReturnAllSkis() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(19), getDateForDay(25), null, "model1", "10", "", RentStatus.RETURNED),
                new Rent(Util.stringToUUID("test1"), getDateForDay(18), getDateForDay(29), null, "model2", "10", "", RentStatus.RETURNED),
                new Rent(Util.stringToUUID("test2"), getDateForDay(15), getDateForDay(29), null, "model3", "10", "", RentStatus.RETURNED)
        ));
        dataEntitySetter.accept(setOfRentals);
        switchToNewDay(30);

        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis, new ArrayList<>(reportManager.availableSkis(now))));
    }

    @Test
    public void givenNullLocalDate_whenAvailableSkisCalled_thenNoErrorIsThrown() {
        assertDoesNotThrow(() -> reportManager.availableSkis(null));
    }

    @Test
    public void givenOverdueSkisExist_whenOverdueSkisCalled_thenReturnAllOverdueSkis() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(19), getDateForDay(25), getDateForDay(27), "model1", "10", "", RentStatus.OVERDUE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(18), getDateForDay(29), getDateForDay(30), "model2", "10", "", RentStatus.OVERDUE),
                new Rent(Util.stringToUUID("test2"), getDateForDay(15), getDateForDay(29), getDateForDay(30), "model3", "10", "", RentStatus.OVERDUE),
                new Rent(Util.stringToUUID("test"), getDateForDay(5), getDateForDay(10), getDateForDay(15), "model1", "10", "", RentStatus.RETURNED)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(30);

        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis, new ArrayList<>(reportManager.overdueSkis())));

    }

    @Test
    public void givenNoOverdueSkisExist_whenOverdueSkisIsCalled_thenReturnEmpty() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(19), getDateForDay(25), null, "model1", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(18), getDateForDay(29), null, "model2", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test2"), getDateForDay(15), getDateForDay(30), null, "model3", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(20);
        assertTrue(reportManager.overdueSkis().isEmpty());
    }

    @Test
    public void givenNoSkisAndNoRents_whenOverdueSkisIsCalled_thenReturnEmpty() {
        assertTrue(reportManager.overdueSkis().isEmpty());
    }

    @Test
    public void givenRentedSkisExist_whenRentedSkisIsCalled_thenReturnAllRentedSkis() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(19), getDateForDay(25), null, "model1", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(18), getDateForDay(29), null, "model2", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test2"), getDateForDay(15), getDateForDay(30), null, "model3", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test2"), getDateForDay(5), getDateForDay(10), null, "model4", "10", "", RentStatus.RETURNED)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(20);
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis.subList(0, 3), new ArrayList<>(reportManager.rentedSkis(now))));
    }

    @Test
    public void givenNoSkis_whenRentedSkisIsCalled_thenReturnEmpty()  {
        assertTrue(reportManager.rentedSkis(now).isEmpty());
    }

    @Test
    public void givenNoRentedSkis_whenRentedSkisIsCalled_thenReturnEmpty() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f)
        ));
        skiAddingConsumer.accept(listOfSkis);
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(10), getDateForDay(15), null, "model1", "10", "", RentStatus.RETURNED),
                new Rent(Util.stringToUUID("test1"), getDateForDay(12), getDateForDay(17), null, "model2", "10", "", RentStatus.RETURNED)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(20);
        assertTrue(reportManager.rentedSkis(now).isEmpty());
    }

    @Test
    public void givenScheduledRentsForFuture_whenRentedSkisIsCalled_thenReturnEmpty() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f)
        ));
        skiAddingConsumer.accept(listOfSkis);
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(25), getDateForDay(30), null, "model1", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(28), getDateForDay(30), null, "model2", "10", "", RentStatus.ACTIVE)        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(20);
        assertTrue(reportManager.rentedSkis(now).isEmpty());
    }

    @Test
    public void givenMixedRents_whenRentedSkisIsCalled_thenReturnOnlyCurrentlyRentedSkis() throws SkiAppException {
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f),
                new Ski(new SkiType("name4", "description4"), "brand4", "model4", "bond4", 4f)
        ));
        skiAddingConsumer.accept(listOfSkis);
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(18), getDateForDay(22), null, "model1", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test1"), getDateForDay(19), getDateForDay(21), null, "model2", "10", "", RentStatus.ACTIVE),
                new Rent(Util.stringToUUID("test2"), getDateForDay(10), getDateForDay(15), null, "model3", "10", "", RentStatus.RETURNED),
                new Rent(Util.stringToUUID("test3"), getDateForDay(25), getDateForDay(27), null, "model4", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(20);

        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis.subList(0,2), new ArrayList<>(reportManager.rentedSkis(now))));
    }

    @Test
    public void givenNullLocalDate_whenRentedSkisCalled_thenNoErrorIsThrown() {
        assertDoesNotThrow(() -> reportManager.rentedSkis(null));
    }

}
