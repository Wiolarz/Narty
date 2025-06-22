package wit.io;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Client;
import wit.io.exceptions.EntityAlreadyPresentException;
import wit.io.exceptions.EntityNotPresentException;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
import wit.io.managers.ClientManager;
import wit.io.utils.Util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class ClientManagerTest {

    private ClientManager manager = null;
    private final String testFilePath = "src/test/java/wit/io/datasources/ClientTest";

    private final Consumer<List<Client>> clientAddingConsumer = (list) -> list.forEach(client -> {
        try {
            manager.addEntity(client);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    });

    @BeforeEach
    public void setUp() {
        try {
            manager = new ClientManager(testFilePath);
            manager.resetEntityData();
        } catch (WritingException | ReadingException e) {
            fail(e.getMessage());
        }
    }

    @AfterEach
    public void tearDown() throws WritingException {
        manager.resetEntityData();
    }

    @Test
    public void givenClientDataExists_whenCreatingNewManager_thenDataIsLoaded() throws Exception {
        List<Client> clients = List.of(
                new Client("0000", "first", "last", "d1"),
                new Client("0011", "first", "last", "d2"),
                new Client("1100", "first", "last", "d3")
        );
        clientAddingConsumer.accept(clients);

        manager = new ClientManager(testFilePath);

        assertEquals(3, manager.getEntities().size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(clients, manager.getEntitiesList()));
    }

    @Test
    public void givenClientExists_whenAddingClientWithMatchingDocID_thenThrowEntityAlreadyPresentException() throws Exception {
        manager.addEntity(new Client("12345", "first", "last", "desc"));

        assertThrows(
                EntityAlreadyPresentException.class,
                () -> manager.addEntity(new Client("12345", "first", "last", "desc"))
        );
    }

    @Test
    public void givenClientExists_whenAddingClientWithMatchingDataButDifferentDocID_thenClientAddedCorrectly() throws Exception {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", "desc"),
                new Client("123457", "first", "last", "desc")
        );
        manager.addEntity(clients.get(0));

        assertDoesNotThrow(
                () -> manager.addEntity(clients.get(1))
        );
        assertEquals(2, manager.getEntities().size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(clients, manager.getEntitiesList()));
    }

    @Test
    public void givenClientIsNull_whenAddingNewClient_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.addEntity(null));
    }

    @Test
    public void givenClientDoesNotExist_whenAddingNewClient_thenClientIsAdded() throws Exception {
        Client newClient = new Client("67890", "first", "last", "description");

        manager.addEntity(newClient);

        assertEquals(1, manager.getEntities().size());
        assertTrue(Util.compareObjectsByStringValue(newClient, manager.getEntitiesList().get(0)));
    }

    @Test
    public void givenClientExists_whenAddingClientWithMatchingDocIdButDifferentData_thenThrowEntityAlreadyPresent() throws Exception {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", "desc"),
                new Client("12345", "last", "first", "lol")
        );
        manager.addEntity(clients.get(0));

        assertThrows(EntityAlreadyPresentException.class, () -> manager.addEntity(clients.get(1)));
        assertTrue(Util.compareObjectsByStringValue(clients.get(0), manager.getEntitiesList().get(0)));
    }


    @Test
    public void givenClientNotPresent_whenRemovingClient_thenThrowEntityNotPresentException() {
        assertThrows(EntityNotPresentException.class, () -> manager.removeEntity(new Client("00000", "No", "Nope", "")));
    }

    @Test
    public void givenClientIsNull_whenRemovingClient_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.removeEntity(null));
    }

    @Test
    public void givenClientExists_whenRemovingExactClient_thenClientIsRemoved() throws Exception {
        Client client = new Client("4", "firstName", "lastName", "description");
        manager.addEntity(client);

        manager.removeEntity(client);

        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void givenMultipleClientsWithSimilarDataExist_whenRemovingOne_thenRemoveOnlyOneWithMatchingDocID() throws Exception {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", ""),
                new Client("5678", "first", "last", ""),
                new Client("91011", "first", "last", ""),
                new Client("12134", "first", "last", "")
        );
        clientAddingConsumer.accept(clients);

        manager.removeEntity(clients.get(0));

        assertEquals(3, manager.getEntities().size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(clients.subList(1, 4), manager.getEntitiesList()));
    }

    @Test
    public void givenClientExists_whenEditingClient_thenClientDataIsUpdated() throws Exception {
        Client oldClient = new Client("1241", "firstName", "lastName", "description");
        manager.addEntity(oldClient);
        Client newClient = new Client("1241", "newFirstName", "newLastName", "newDescription");

        manager.editEntity(oldClient, newClient);

        assertEquals(1, manager.getEntitiesList().size());
        assertTrue(Util.compareObjectsByStringValue(manager.getEntitiesList().get(0), newClient));
    }

    @Test
    public void givenClientExists_whenEditingClientWithNull_thenIllegalArgumentExceptionIsThrown() throws Exception {
        Client oldClient = new Client("1254", "firstName", "lastName", "description");
        manager.addEntity(oldClient);

        assertThrows(IllegalArgumentException.class, () -> manager.editEntity(oldClient, null));
        assertEquals(1, manager.getEntitiesList().size());
        assertTrue(Util.compareObjectsByStringValue(manager.getEntitiesList().get(0), oldClient));
    }

    @Test
    public void givenNullClient_whenEditing_thenThrowIllegalArgumentException() {
        Client client = new Client("234562", "firstName", "lastName", "description");
        assertThrows(IllegalArgumentException.class, () -> manager.editEntity(null, client));
    }

    @Test
    public void givenClientDoesNotExist_whenEditing_thenThrowEntityNotPresentException() {
        Client oldClient = new Client("135", "firstName", "lastName", "description");
        Client newClient = new Client("246", "a", "b", "c");

        assertThrows(EntityNotPresentException.class, () -> manager.editEntity(oldClient, newClient));
    }

    @Test
    public void whenResettingData_thenManagerAndFileAreCleared() throws Exception {
        manager.addEntity(new Client("1352", "firstName", "lastName", "description"));
        manager.resetEntityData();

        manager.readFromFile();

        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void givenNoFilters_whenSearching_thenReturnOriginalList() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", ""),
                new Client("5678", "first", "last", ""),
                new Client("91011", "first", "last", ""),
                new Client("12134", "first", "last", "")
        );
        clientAddingConsumer.accept(clients);

        ArrayList<Client> results = manager.search(null, null, null, null);

        assertEquals(4, results.size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(clients, results));
    }


    @Test
    public void givenKnownDocId_whenSearching_thenReturnCorrectClient() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", ""),
                new Client("5678", "first", "last", ""),
                new Client("91011", "first", "last", ""),
                new Client("12134", "first", "last", "")
        );
        clientAddingConsumer.accept(clients);

        // When: searching by a unique document ID
        ArrayList<Client> results = manager.search("91011", null, null, null);

        assertEquals(1, results.size());
        assertTrue(Util.compareObjectsByStringValue(clients.get(2), results.get(0)));
    }

    @Test
    public void givenPartialKnownFirstNamePrefix_whenSearching_thenReturnMatchingClients() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", ""),
                new Client("5678", "no", "destr", ""),
                new Client("91011", "nah", "temp", ""),
                new Client("12134", "hell", "ni", "")
        );
        clientAddingConsumer.accept(clients);

        ArrayList<Client> results = manager.search(null, "fir", null, null);

        assertEquals(1, results.size());
        assertTrue(Util.compareObjectsByStringValue(clients.get(0), results.get(0)));
    }

    @Test
    public void givenPartialKnownLastNamePrefix_whenSearching_thenReturnMatchingClients() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", ""),
                new Client("5678", "no", "destr", ""),
                new Client("91011", "nah", "temp", ""),
                new Client("12134", "hell", "ni", "")
        );
        clientAddingConsumer.accept(clients);

        ArrayList<Client> results = manager.search(null, null, "te", null);

        assertEquals(1, results.size());
        assertTrue(Util.compareObjectsByStringValue(clients.get(2), results.get(0)));
    }

    @Test
    public void givenKnownDescriptionSubstring_whenSearching_thenReturnMatchingClients() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", "de"),
                new Client("5678", "no", "destr", "desc"),
                new Client("91011", "nah", "temp", "tem"),
                new Client("12134", "hell", "ni", "xxxxxxxx")
        );
        clientAddingConsumer.accept(clients);

        ArrayList<Client> results = manager.search(null, null, null, "de");

        assertEquals(2, results.size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(clients.subList(0, 2), results));
    }

    @Test
    public void givenMultipleCriteria_whenSearching_thenReturnCorrectClients() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", "de"),
                new Client("5678", "fi", "la", "desc"),
                new Client("91011", "fir", "temp", "tem"),
                new Client("12134", "hell", "ni", "xxxxxxxx")
        );
        clientAddingConsumer.accept(clients);

        ArrayList<Client> results = manager.search(null, "fi", "l", "d");

        assertEquals(2, results.size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(clients.subList(0,2), results));
    }

    @Test
    public void givenUnknownCriteria_whenSearching_thenReturnEmptyList() {
        List<Client> clients = List.of(
                new Client("12345", "first", "last", "de"),
                new Client("5678", "fi", "la", "desc"),
                new Client("91011", "fir", "temp", "tem"),
                new Client("12134", "hell", "ni", "xxxxxxxx")
        );
        clientAddingConsumer.accept(clients);

        ArrayList<Client> results = manager.search("99999", "Nie", null, null);

        assertEquals(0, results.size());
    }


    @Test
    public void givenDatasourceFileIsMissing_whenWritingNewClient_thenFileIsCreated() throws Exception {
        File file = new File(testFilePath);
        file.delete();
        manager = new ClientManager(testFilePath);

        manager.addEntity(new Client("1241", "f", "l", "d"));

        file = new File(testFilePath);
        assertTrue(file.exists());
        assertEquals(1, manager.getEntities().size());
    }

}
