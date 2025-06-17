package wit.io;

import exceptions.EntityAlreadyPresent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.SkiType;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkiTypeManagerTest
{
    private SkiTypeManager manager = null;

    @BeforeEach
    public void setUp() {
        manager = new SkiTypeManager("src/test/java/wit/io/datasources/SkiType");
        manager.resetEntityData();
    }

    public void populateSkiTypes() throws EntityAlreadyPresent {
        manager.addEntity(new SkiType("hello1", "world1"));
    }

    @Test
    public void givenSkiTypeExists_whenAddingNewType_thenThrowSkiTypeAlreadyPresentException() throws EntityAlreadyPresent{
        populateSkiTypes();

        assertThrows(
                EntityAlreadyPresent.class,
                () -> manager.addEntity(new SkiType("hello1", "world1"))
        );
    }

    @Test
    public void givenSkiTypeDoesNotExist_whenAddingNewType_thenAddToSkiTypes() throws Exception {
        manager.addEntity(new SkiType("newName", "aaa"));

        assertEquals(1, manager.getEntities().size());
        assertEquals("newName", manager.getEntities().get(0).getName());
        assertEquals("aaa", manager.getEntities().get(0).getDescription());
    }
}