package wit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;
import wit.io.exceptions.*;
import wit.io.managers.RentManager;
import wit.io.utils.Util;

import java.time.LocalDate;
import java.util.*;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;


public class RentManagerTest {

    private RentManager manager = null;

    private final Consumer<Set<Rent>> dataEntitySetter = (set) -> {
        try {
            // THIS OMITS CHECKS AND SETTING STATUS TO ACTIVE!
            manager.setEntities(set);
        } catch (WritingException e) {
            throw new RuntimeException(e);
        }
    };

    @BeforeEach
    public void setUp() throws Exception {
        switchToNewDay(20);
        manager.resetEntityData();
    }

    @AfterEach
    public void tearDown() throws ReadingException, WritingException {
        manager.resetEntityData();
    }


    private static LocalDate getDateForDay(int day) {
        return LocalDate.of(2025, 5, day);
    }

    public void switchToNewDay(int day) throws SkiAppException {
        manager = new RentManager("src/test/java/wit/io/datasources/Rent", getDateForDay(day));
    }

    @Test
    public void givenRentWithEndDateBeforeToday_whenReadingInData_thenRentStatusGetsUpdatedToOverdue() throws Exception {
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
        switchToNewDay(25);
        assertEquals(1, manager.getEntitiesList().size());
        assertEquals(RentStatus.OVERDUE, manager.getEntitiesList().get(0).getStatus());
    }

    @Test
    public void givenOverdueRentExistsAndScheduledRentWithCollidingStartDate_whenReadingInData_thenRentStatusGetsUpdatedToFailed() throws SkiAppException {
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(22), getDateForDay(23), null, "10", "10", "", RentStatus.OVERDUE),
                new Rent(null, getDateForDay(24), getDateForDay(25), null, "10", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);
        
        switchToNewDay(24);
        
        assertEquals(1, manager.getEntitiesList().stream().filter((rent) -> rent.getStatus().equals(RentStatus.FAILED)).count());
        assertEquals(1, manager.getEntitiesList().stream().filter((rent) -> rent.getStatus().equals(RentStatus.OVERDUE)).count());
    }

    @Test
    public void givenOverdueRentExistsAndScheduledRentWithNotCollidingStartDate_whenReadingInData_thenRentStatusDoesNotGetUpdated() throws SkiAppException {
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(Util.stringToUUID("test"), getDateForDay(22), getDateForDay(23), getDateForDay(25), "10", "10", "", RentStatus.RETURNED),
                new Rent(Util.stringToUUID("test1"), getDateForDay(28), getDateForDay(29), null, "10", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(28);
        assertTrue(Util.orderAndCompareSetsOfObjectsByStringValue(setOfRentals, manager.getEntities()));
    }


    @Test
    public void givenRentalsDataExist_whenCreatingNewManager_thenRentalDataIsCorrectlyLoaded() throws SkiAppException {
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(22), getDateForDay(23), null, "10", "10", "", RentStatus.ACTIVE),
                new Rent(null, getDateForDay(24), getDateForDay(25), null, "10", "10", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        switchToNewDay(22);
        assertEquals(2, manager.getEntitiesList().size());
        assertTrue(Util.orderAndCompareSetsOfObjectsByStringValue(setOfRentals, manager.getEntities()));
    }

    @Test
    public void givenNewRentWithEndDateBeforeStartDate_whenAddingRent_thenThrowInvalidRentDateException() throws SkiAppException {
        Rent test1 = new Rent(null, getDateForDay(25), getDateForDay(22), null, "model1", "client1", "", null);
        assertThrows(InvalidRentDateException.class, () -> manager.addEntity(test1));
    }

    @Test
    public void givenExistingRent_whenAddingRentWithOverlappingDateFromRight_thenThrowOverlappingRentDateException() throws SkiAppException {
        Rent test1 = new Rent(null, getDateForDay(22), getDateForDay(25), null, "model1", "client1", "", null);
        manager.addEntity(test1);

        Rent test2 = new Rent(null, getDateForDay(24), getDateForDay(26), null, "model1", "client2", "", null);

        assertThrows(OverlappingRentDateException.class, () -> manager.addEntity(test2));
        assertEquals(1, manager.getEntities().size());
    }

    @Test
    public void givenExistingRent_whenAddingRentWithOverlappingDateFromLeft_thenThrowOverlappingRentDateException() throws SkiAppException {
        Rent test1 = new Rent(null, getDateForDay(22), getDateForDay(25), null, "model1", "client1", "", null);
        manager.addEntity(test1);

        Rent test2 = new Rent(null, getDateForDay(21), getDateForDay(23), null, "model1", "client2", "", null);

        assertThrows(OverlappingRentDateException.class, () -> manager.addEntity(test2));
        assertEquals(1, manager.getEntities().size());
    }

    @Test
    public void givenExistingRent_whenAddingRentInCenter_thenThrowOverlappingRentDateException() throws SkiAppException {
        Rent test1 = new Rent(null, getDateForDay(22), getDateForDay(25), null, "model1", "client1", "", null);
        manager.addEntity(test1);

        Rent test2 = new Rent(null, getDateForDay(23), getDateForDay(24), null, "model1", "client2", "", null);

        assertThrows(OverlappingRentDateException.class, () -> manager.addEntity(test2));
        assertEquals(1, manager.getEntities().size());
    }

    @Test
    public void whenAddingRentInThePast_thenThrowInvalidRentDateException() {
        Rent test1 = new Rent(null, getDateForDay(15), getDateForDay(16), null, "model1", "client1", "", null);
        assertThrows(InvalidRentDateException.class, () -> manager.addEntity(test1));
    }

    @Test
    public void givenExistingRent_whenAddingNotOverlappingRent_noExceptionThrown() throws SkiAppException {
        Rent test1 = new Rent(null, getDateForDay(22), getDateForDay(24), null, "model1", "client1", "", null);
        Rent test2 = new Rent(null, getDateForDay(25), getDateForDay(26), null, "model2", "client1", "", null);
        Rent test3 = new Rent(null, getDateForDay(27), getDateForDay(28), null, "model2", "client2", "", null);
        manager.addEntity(test1);
        assertDoesNotThrow(() -> manager.addEntity(test2));
        assertDoesNotThrow(() -> manager.addEntity(test3));
    }

    @Test
    public void givenExistingRent_whenAddingRentForDifferentSkiOnSameDay_thenDoesNotThrow() throws SkiAppException {
        Rent test1 = new Rent(null, getDateForDay(22), getDateForDay(25), null, "model1", "client1", "", null);
        manager.addEntity(test1);

        Rent test2 = new Rent(null, getDateForDay(24), getDateForDay(26), null, "model2", "client2", "", null);

        assertDoesNotThrow(() -> manager.addEntity(test2));
        assertEquals(2, manager.getEntities().size());
    }

    @Test
    public void givenExistingRent_whenEditingRentWithCorrectData_thenDataIsUpdated() throws SkiAppException {
        Rent test1 = new Rent(Util.stringToUUID("rent1"), getDateForDay(22), getDateForDay(23), null, "10", "10", "old comment", RentStatus.ACTIVE);
        manager.addEntity(test1);

        Rent test2 = new Rent(Util.stringToUUID("rent1"), getDateForDay(22), getDateForDay(23), null, "10", "10", "new comment", RentStatus.ACTIVE);
        manager.editEntity(test1, test2);

        assertEquals(1, manager.getEntities().size());
        Rent result = manager.getEntitiesList().get(0);
        assertEquals("new comment", result.getComment());
    }

    @Test
    public void givenNoExistingRents_whenEditing_thenThrowEntityNotPresentException() {
        Rent test1 = new Rent(null, getDateForDay(22), getDateForDay(23), null, "model1", "client1", "", null);
        Rent test2 = new Rent(null, getDateForDay(24), getDateForDay(25), null, "model1", "client1", "", null);

        assertThrows(EntityNotPresentException.class, () -> manager.editEntity(test1, test2));
    }

    @Test
    public void givenExistingRents_whenSearchingByStatus_thenReturnsMatchingRents() throws SkiAppException {
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(15), getDateForDay(16), null, "model1", "client1", "", RentStatus.RETURNED),
                new Rent(null, getDateForDay(17), getDateForDay(18), null, "model1", "client1", "", RentStatus.ACTIVE),
                new Rent(null, getDateForDay(23), getDateForDay(24), null, "model2", "client2", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);
        switchToNewDay(23);

        List<Rent> results = manager.search(null, null, null, null, null, null, RentStatus.OVERDUE);
        assertEquals(1, results.size());
        assertEquals("model1", results.get(0).getSkiModel());
    }

    @Test
    public void givenExistingRents_whenSearchingByClientID_thenReturnsMatchingRents() throws SkiAppException {
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(22), getDateForDay(23), null, "model1", "client1", "", RentStatus.ACTIVE),
                new Rent(null, getDateForDay(24), getDateForDay(25), null, "model2", "client2", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        List<Rent> results = manager.search(null, "client1", null, null, null, null, null);
        assertEquals(1, results.size());
        assertEquals("client1", results.get(0).getClientID());
    }

    @Test
    public void givenExistingRent_whenSearchingWithNoMatchingCriteria_thenReturnsEmptyList() throws SkiAppException {
        Set<Rent> setOfRentals = new HashSet<>(List.of(
                new Rent(null, getDateForDay(22), getDateForDay(23), null, "model1", "client1", "", RentStatus.ACTIVE)
        ));
        dataEntitySetter.accept(setOfRentals);

        List<Rent> results = manager.search("non-existent-model", null, null, null, null, null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void givenExistingRents_whenEndDateHasNotPassed_thenStatusRemainsActive() throws SkiAppException {
        manager.addEntity(new Rent(null, getDateForDay(22), getDateForDay(23), null, "model1", "client1", "", RentStatus.ACTIVE));
        switchToNewDay(21);
        assertEquals(1, manager.getEntities().size());
        assertEquals(RentStatus.ACTIVE, manager.getEntitiesList().get(0).getStatus());
    }

}
