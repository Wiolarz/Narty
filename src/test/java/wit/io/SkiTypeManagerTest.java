package wit.io;

import exceptions.SkiTypeAlreadyPresent;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Ski;
import wit.io.data.SkiType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class SkiTypeManagerTest
{
    private SkiTypeManager manager = null;

    @BeforeEach
    public void setUp() {
        manager = new SkiTypeManager("");
    }

    public void populateSkiTypes() {
        manager.setSkiTypes(
                List.of(
                        new SkiType("name1", "a"),
                        new SkiType("name1", "b")
                )
        );
    }

    @Test
    public void givenSkiTypeExists_whenAddingNewType_thenThrowSkiTypeAlreadyPresentException() {
        populateSkiTypes();

        assertThrows(
                SkiTypeAlreadyPresent.class,
                () -> manager.addSkiType("name1", "a")
        );
    }

    @Test
    public void givenSkiTypeDoesNotExist_whenAddingNewType_thenAddToSkiTypes() throws Exception {
        populateSkiTypes();

        manager.addSkiType("newName", "aaa");

        assertEquals(1, manager.getSkiTypes().size());
        assertEquals("newName", manager.getSkiTypes().get(0).getName());
        assertEquals("aaa", manager.getSkiTypes().get(0).getDescription());
    }
}