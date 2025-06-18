package wit.io;

import exceptions.EntityAlreadyPresentException;
import exceptions.EntityNotPresentException;
import exceptions.ReadingException;
import exceptions.WritingException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.SkiType;
import wit.io.managers.SkiTypeManager;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkiTypeManagerTest
{
    private SkiTypeManager manager = null;

    @BeforeEach
    public void setUp() {
        try {
            manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");
            manager.resetEntityData();
        } catch (WritingException | ReadingException e) {
            fail(e.getMessage());
        }
    }

    @Test
    public void givenSkiTypeExists_whenAddingNewType_thenThrowSkiTypeAlreadyPresentException() throws EntityAlreadyPresentException, WritingException{
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
    public void givenSkiTypeNotPresentInManager_whenRemovingSki_thenThrowEntityNotPresentException() {
        assertThrows(EntityNotPresentException.class, () -> manager.removeEntity(new SkiType("", "")));
    }

    @Test
    public void givenSkiTypeEqualsNull_whenRemovingSki_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> manager.removeEntity(null));
    }

    @Test
    public void givenSkiTypeExists_whenRemovingSki_thenSkiIsRemoved() throws WritingException, EntityAlreadyPresentException, EntityNotPresentException{
        SkiType ski = new SkiType("hello1", "world1");
        manager.addEntity(ski);

        manager.removeEntity(ski);

        assertFalse(manager.getEntities().contains(ski));
        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void givenSkiWithSimilarName_whenRemovingSki_thenThrowEntityNotPresentException() throws WritingException, EntityAlreadyPresentException, EntityNotPresentException {
        manager.addEntity(new SkiType("hello1", "world1"));

        assertThrows(
                EntityNotPresentException.class,
                () -> manager.removeEntity(new SkiType("hello11", "world1"))
        );
    }

    @Test
    public void givenSkiTypeDoesNotExist_whenAddingNewType_thenAddToSkiTypes() throws Exception {
        manager.addEntity(new SkiType("newName", "aaa"));

        assertEquals(1, manager.getEntities().size());
        assertEquals("newName", manager.getEntities().get(0).getName());
        assertEquals("aaa", manager.getEntities().get(0).getDescription());
    }

    @Test
    public void givenSkiTypeExists_whenResettingEntityData_thenGetEntitiesReturnsEmptyAndClearsFile() throws Exception {
        // given
        manager.addEntity(new SkiType("newName", "aaa"));

        // when
        manager.resetEntityData();

        // then
        assertNotNull(manager.getEntities());
        assertEquals(0, manager.getEntities().size());
    }

    @Test
    public void whenResettingEntityData_thenAppropriateFileIsEmpty() throws Exception {
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
    public void givenEntityDataExists_whenCreatingNewSkiTypeManager_thenSkiDataIsLoadedSuccessfully() throws ReadingException {
        // given
        List<SkiType> listOfSkis = new ArrayList<>(List.of(
                new SkiType("newName1", "aaa1"),
                new SkiType("newName2", "aaa2"),
                new SkiType("newName3", "aaa3")
        ));
        listOfSkis.forEach((ski) -> {try {manager.addEntity(ski);} catch (Exception e) {fail(e.getMessage());}});

        // when
        manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");

        // then
        assertEquals(3, manager.getEntities().size());
        listOfSkis.removeAll(manager.getEntities());
        assertTrue(listOfSkis.isEmpty());
    }


}