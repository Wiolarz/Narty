package wit.io.managers;

import org.junit.jupiter.api.AfterEach;
import wit.io.exceptions.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.SkiType;
import wit.io.utils.Util;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

class SkiTypeManagerTest
{
    private SkiTypeManager manager = null;

    private final Consumer<List<SkiType>> skiTypeAddingConsumer = (list) -> list.forEach(ski -> {
        try {
            manager.addEntity(ski);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    });



    @BeforeEach
    public void setUp() {
        try {
            manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");
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
    public void givenSkiTypeExists_whenAddingNewType_thenThrowSkiTypeAlreadyPresentException() throws SkiAppException {
        manager.addEntity(new SkiType("hello1", "world1"));

        assertThrows(
                EntityAlreadyPresentException.class,
                () -> manager.addEntity(new SkiType("hello1", "world1"))
        );
    }

    @Test
    public void givenSkiTypeEqualsNull_whenAddingNewType_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.addEntity(null));
    }

    @Test
    public void givenSkiTypeDoesNotExist_whenAddingNewType_thenAddToSkiTypes() throws Exception {
        manager.addEntity(new SkiType("newName", "aaa"));

        assertEquals(1, manager.getEntities().size());
        assertEquals("newName", manager.getEntitiesList().get(0).getName());
        assertEquals("aaa", manager.getEntitiesList().get(0).getDescription());
    }

    @Test
    public void givenSkiTypeNotPresentInManager_whenRemovingSkiTypes_thenThrowEntityNotPresentException() {
        assertThrows(EntityNotPresentException.class, () -> manager.removeEntity(new SkiType("", "")));
    }

    @Test
    public void givenSkiTypeEqualsNull_whenRemovingSkiTypes_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.removeEntity(null));
    }

    @Test
    public void givenSkiTypeExists_whenRemovingSkiType_thenSkiTypeIsRemoved() throws SkiAppException {
        SkiType ski = new SkiType("hello1", "world1");
        manager.addEntity(ski);

        manager.removeEntity(ski);

        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void givenSkiTypeExists_whenRemovingSkiTypeWithSimilarName_thenThrowEntityNotPresentException() throws SkiAppException {
        manager.addEntity(new SkiType("hello1", "world1"));

        assertThrows(
                EntityNotPresentException.class,
                () -> manager.removeEntity(new SkiType("hello11", "world1"))
        );
    }

    @Test
    public void givenSkiTypeExists_whenResettingSkiType_thenGetEntitiesReturnsEmptyAndClearsFile() throws Exception {
        // given
        manager.addEntity(new SkiType("newName", "aaa"));

        // when
        setUp();

        // then
        assertNotNull(manager.getEntities());
        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void whenResettingSkiTypeData_thenAppropriateFileIsEmpty() throws Exception {
        // given
        manager.addEntity(new SkiType("newName", "aaa"));
        manager.resetEntityData();

        // when
        manager.readFromFile();

        // then
        assertNotNull(manager.getEntities());
        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void givenSkiTypeDataExists_whenCreatingNewSkiTypeManager_thenSkiTypeDataIsLoadedSuccessfully() throws ReadingException {
        // given
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("newName1", "aaa1"),
                new SkiType("newName2", "aaa2"),
                new SkiType("newName3", "aaa3")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        // when
        manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");

        // then
        assertEquals(3, manager.getEntities().size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis, manager.getEntitiesList()));
    }

    @Test
    public void givenSkiTypeWithDifferentDescription_whenEditingSkiType_thenSkiDataIsEdited() throws SkiAppException {
        // given
        SkiType oldSkiType = new SkiType("lorem", "ip");
        SkiType newSkiType = new SkiType("lorem", "ipsum");
        manager.addEntity(oldSkiType);

        // when
        manager.editEntity(oldSkiType, newSkiType);

        // then
        assertEquals(1, manager.getEntitiesList().size());
        assertEquals(newSkiType, manager.getEntitiesList().get(0));
    }

    @Test
    public void givenSkiTypeWithDifferentName_whenEditingSkiType_thenSkiDataIsEdited() throws SkiAppException {
        // given
        SkiType oldSkiType = new SkiType("lor", "ipsum");
        SkiType newSkiType = new SkiType("lorem", "ipsum");
        manager.addEntity(oldSkiType);

        // when
        manager.editEntity(oldSkiType, newSkiType);

        // then
        assertEquals(1, manager.getEntitiesList().size());
        assertEquals(newSkiType, manager.getEntitiesList().get(0));
    }

    @Test
    public void givenNullSkiType_whenEditingExistingSkiType_thenThrowIllegalArgumentException() throws SkiAppException {
        // given
        SkiType oldSkiType = new SkiType("lorem", "ipsum");
        manager.addEntity(oldSkiType);

        assertThrows(IllegalArgumentException.class, () -> manager.editEntity(oldSkiType, null));
    }

    @Test
    public void givenNewSkiType_whenEditingWithNull_thenThrowIllegalArgumentException() {
        SkiType newSkiType = new SkiType("lorem", "ipsum");

        assertThrows(IllegalArgumentException.class, () -> manager.editEntity(null, newSkiType));
    }

    @Test
    public void givenNullSkiType_whenEditingWithNull_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.editEntity(null, null));
    }

    @Test
    public void givenNewSkiType_whenEditingNotExistingSkiType_thenThrowEntityNotPresentException() {
        SkiType oldSkiType = new SkiType("lor", "ipsum");
        SkiType newSkiType = new SkiType("lorem", "ipsum");
        assertThrows(EntityNotPresentException.class, () -> manager.editEntity(oldSkiType, newSkiType));
    }

    @Test
    public void givenKnownNameSuffix_whenSearching_returnSkiTypesWithNamesStartingWithTheSuffix() {
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ipsum"),
                new SkiType("LOREM", "ipsum"),
                new SkiType("l", "ipsum"),
                new SkiType("rem", "ipsum"),
                new SkiType("bart", "ipsum"),
                new SkiType("test", "ipsum")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertEquals(2, manager.search("lo", "").size());
        assertTrue(Util.isInListOfObjectsByString(manager.search("lo", ""), listOfSkis.get(0)));
        assertTrue(Util.isInListOfObjectsByString(manager.search("lo", ""), listOfSkis.get(1)));
    }

    @Test
    public void givenUnknownNameSuffix_whenSearching_returnEmptyArrayList() {
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ipsum"),
                new SkiType("LOREM", "ipsum"),
                new SkiType("l", "ipsum"),
                new SkiType("rem", "ipsum"),
                new SkiType("bart", "ipsum"),
                new SkiType("test", "ipsum")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertEquals(0, manager.search("Dawid", "").size());
    }

    @Test
    public void givenKnownDescription_whenSearching_returnCorrectSkiTypes(){
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ip"),
                new SkiType("LOREM", "Ips"),
                new SkiType("l", "idk"),
                new SkiType("rem", "hello"),
                new SkiType("bart", "type"),
                new SkiType("test", "???")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertEquals(3, manager.search("", "i").size());
        assertTrue(Util.isInListOfObjectsByString(manager.search("", "i"), listOfSkis.get(0)));
        assertTrue(Util.isInListOfObjectsByString(manager.search("", "i"), listOfSkis.get(1)));
        assertTrue(Util.isInListOfObjectsByString(manager.search("", "i"), listOfSkis.get(2)));
    }

    @Test
    public void givenUnknownDescription_whenSearching_returnEmptyArrayList() {
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ip"),
                new SkiType("LOREM", "Ips"),
                new SkiType("l", "idk"),
                new SkiType("rem", "hello"),
                new SkiType("bart", "type"),
                new SkiType("test", "???")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertEquals(0, manager.search("", "unknown description!").size());
    }

    @Test
    public void givenKnownNameSuffixAndKnownDescription_whenSearching_thenReturnCorrectSkiTypes() {
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ip"),
                new SkiType("LOREM", "Ips"),
                new SkiType("l", "idk"),
                new SkiType("lo", "test"),
                new SkiType("rem", "hello"),
                new SkiType("bart", "type"),
                new SkiType("test", "???")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertEquals(3, manager.search("l", "i").size());
        assertTrue(Util.isInListOfObjectsByString(manager.search("l", "i"), listOfSkis.get(0)));
        assertTrue(Util.isInListOfObjectsByString(manager.search("l", "i"), listOfSkis.get(1)));
        assertTrue(Util.isInListOfObjectsByString(manager.search("l", "i"), listOfSkis.get(2)));

    }

    @Test
    public void givenKnownNameSuffixAndUnknownDescription_whenSearching_thenReturnEmptyArrayList() {
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ip"),
                new SkiType("LOREM", "Ips"),
                new SkiType("l", "idk"),
                new SkiType("lo", "test"),
                new SkiType("rem", "hello"),
                new SkiType("bart", "type"),
                new SkiType("test", "???")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertEquals(0, manager.search("l", "unknown!").size());
    }

    @Test
    public void givenNullNameSuffixAndDescription_whenSearching_thenReturnUnchangedList() {
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("lorem", "ip"),
                new SkiType("LOREM", "Ips"),
                new SkiType("l", "idk"),
                new SkiType("lo", "test"),
                new SkiType("rem", "hello"),
                new SkiType("bart", "type"),
                new SkiType("test", "???")
        ));
        skiTypeAddingConsumer.accept(listOfSkis);

        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis, manager.search(null, null)));
    }

    @Test
    public void givenDatasourceFileIsMissing_whenInitializing_thenSkiTypeReadsNoData() throws ReadingException {
        File file = new File("src/test/java/wit/io/datasources/Ski");
        file.delete();

        manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");

        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void givenDatasourceFileIsMissing_whenWritingNewSkiType_thenFileIsCreated() throws SkiAppException {
        File file = new File("src/test/java/wit/io/datasources/Ski");
        file.delete();
        manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");

        manager.addEntity(new SkiType("name", "description"));

        file = new File("src/test/java/wit/io/datasources/SkiType");
        assertEquals(1, manager.getEntities().size());
        assertTrue(file.exists());
    }




}