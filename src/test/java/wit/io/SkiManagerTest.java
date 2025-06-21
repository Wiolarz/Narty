package wit.io;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.exceptions.*;
import wit.io.managers.SkiManager;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

import static org.junit.jupiter.api.Assertions.*;

public class SkiManagerTest {

    private SkiManager skiManager = null;

    private final Consumer<List<Ski>> skiAddingConsumer = (list) -> list.forEach(ski -> {
        try {
            skiManager.addEntity(ski);
        } catch (Exception e) {
            fail(e.getMessage());
        }
    });


    @BeforeEach
    public void setUp() {
        try {
            skiManager = new SkiManager("src/test/java/wit/io/datasources/ski");
            skiManager.resetEntityData();
        } catch (WritingException | ReadingException e) {
            fail(e.getMessage());
        }
    }
    
    @AfterAll
    public static void tearDown() throws ReadingException, WritingException {
        new SkiManager("src/test/java/wit/io/datasources/ski").resetEntityData();
    }

    @Test
    public void givenSkiDataExists_whenCreatingNewSkiManager_thenSkiDataIsLoadedSuccessfully() throws ReadingException {
        // given
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f),
                new Ski(new SkiType("name2", "description2"), "brand2", "model2", "bond2", 2f),
                new Ski(new SkiType("name3", "description3"), "brand3", "model3", "bond3", 3f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        // when
        skiManager = new SkiManager("src/test/java/wit/io/datasources/ski");

        // then
        assertEquals(3, skiManager.getEntities().size());
        for (int i = 0; i<skiManager.getEntities().size(); i++) {
            assertEquals(listOfSkis.get(i), skiManager.getEntitiesList().get(i));
        }
    }

    @Test
    public void givenSkiExists_whenAddingNewSki_thenThrowEntityAlreadyPresentException () throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        assertThrows(EntityAlreadyPresentException.class, () -> skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f)));
    }

    @Test
    public void givenSkiEqualsNull_whenAddingNewSki_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->  skiManager.addEntity(null));
    }

    @Test
    public void givenSkiDoesNotExist_whenAddingNewType_thenAddToSkis() throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        Ski ski = skiManager.getEntitiesList().get(0);
        assertEquals(1, skiManager.getEntities().size());
        assertEquals("name1", ski.getType().getName());
        assertEquals("description1", ski.getType().getDescription());
        assertEquals("brand1", ski.getBrand());
        assertEquals("model1", ski.getModel());
        assertEquals("bond1", ski.getBonds());
        assertEquals(1f, ski.getLength());
    }

    @Test
    public void givenSkiNotPresentInManager_whenRemovingSki_thenThrowEntityNotPresentException() {
        assertThrows(EntityNotPresentException.class, () -> skiManager.removeEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f)));
    }

    @Test
    public void givenSkiTypeEqualsNull_whenRemovingSkiTypes_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> skiManager.removeEntity(null));
    }

    @Test
    public void givenSkiExists_whenRemovingSki_thenSkiIsRemoved() throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        skiManager.removeEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        assertFalse(skiManager.getEntities().contains(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f)));
        assertEquals(0, skiManager.getEntities().size());
    }

    @Test
    public void givenExistingSki_whenRemovingSkiWithDifferentType_thenThrowEntityNotPresentException() throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        assertThrows(EntityNotPresentException.class, () -> skiManager.removeEntity(new Ski(new SkiType("name2", "description2"), "brand1", "model1", "bond1", 1f)));
    }

    @Test
    public void givenSkiTypeExists_whenResettingSkiType_thenGetEntitiesReturnsEmptyAndClearsFile() throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        setUp();

        assertNotNull(skiManager.getEntities());
        assertEquals(0, skiManager.getEntities().size());
    }

    @Test
    public void givenSkiWithDifferentType_whenEditingSki_thenSkiDataIsEdited() throws SkiAppException {
        Ski oldSki = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);
        Ski updatedSki = new Ski(new SkiType("name2", "description2"), "brand1", "model1", "bond1", 1f);

        skiManager.addEntity(oldSki);

        skiManager.editEntity(oldSki, updatedSki);

        assertEquals(updatedSki, skiManager.getEntitiesList().get(0));
        assertEquals(1, skiManager.getEntities().size());
    }

    @Test
    public void givenExistingSki_whenEditingWithNull_thenThrowsIllegalArgumentExceptionAndNotModify() throws SkiAppException {
        Ski oldSki = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);

        skiManager.addEntity(oldSki);

        assertThrows(IllegalArgumentException.class, () -> skiManager.editEntity(oldSki, null));
        assertEquals(1, skiManager.getEntities().size());
        assertEquals(oldSki, skiManager.getEntitiesList().get(0));
    }

    @Test
    public void givenNullSki_whenEditingWithNewSKi_thenThrowsIllegalArgumentExceptionAndNotEdit() {
        Ski newSki = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);

        assertThrows(IllegalArgumentException.class, () -> skiManager.editEntity(null, newSki));
        assertEquals(0, skiManager.getEntities().size());
    }

    @Test
    public void givenNewSki_whenEditingNotExistingSki_thenThrowEntityNotPresentException() {
        Ski oldSki = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);
        Ski updatedSki = new Ski(new SkiType("name2", "description2"), "brand1", "model1", "bond1", 1f);

        assertThrows(EntityNotPresentException.class, () -> skiManager.editEntity(oldSki, updatedSki));
        assertEquals(0, skiManager.getEntities().size());
    }


    @Test
    public void givenNoFilters_whenSearching_thenReturnsAllSkis() throws SkiAppException {
        SkiType type1 = new SkiType("type1", "desc1");
        Ski ski1 = new Ski(type1, "brand1", "model1", "bonds1", 150f);
        Ski ski2 = new Ski(type1, "brand2", "model2", "bonds2", 160f);
        skiManager.addEntity(ski1);
        skiManager.addEntity(ski2);

        List<Ski> results = skiManager.search(null, null, null, null, null, null);
        assertEquals(2, results.size());
    }

    @Test
    public void givenType_whenSearching_thenReturnsMatchingSkis() throws SkiAppException {
        SkiType type1 = new SkiType("type1", "desc1");
        SkiType type2 = new SkiType("type2", "desc2");
        Ski ski1 = new Ski(type1, "brand1", "model1", "bonds1", 150f);
        Ski ski2 = new Ski(type2, "brand2", "model2", "bonds2", 160f);
        skiManager.addEntity(ski1);
        skiManager.addEntity(ski2);

        List<Ski> results = skiManager.search(type1, null, null, null, null, null);
        assertEquals(1, results.size());
        assertEquals(ski1, results.get(0));
    }

    @Test
    public void givenBrand_whenSearching_thenReturnsMatchingSkis() throws SkiAppException {
        SkiType type1 = new SkiType("type1", "desc1");
        Ski ski1 = new Ski(type1, "brand-test-1", "model1", "bonds1", 150f);
        Ski ski2 = new Ski(type1, "brand-other-2", "model2", "bonds2", 160f);
        skiManager.addEntity(ski1);
        skiManager.addEntity(ski2);

        List<Ski> results = skiManager.search(null, "TEST-1", null, null, null, null);
        assertEquals(1, results.size());
        assertEquals(ski1, results.get(0));
    }

    @Test
    public void givenModel_whenSearching_thenReturnsMatchingSkis()  {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model-epic", "bonds1", 150f),
                new Ski(type1, "brand2", "model", "bonds2", 160f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        List<Ski> results = skiManager.search(null, null, "Epic", null, null, null);
        assertEquals(1, results.size());
        assertEquals(listOfSkis.get(0), results.get(0));
    }

    @Test
    public void givenBonds_whenSearching_thenReturnsMatchingSkis()  {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds-epic-x", 150f),
                new Ski(type1, "brand2", "model2", "bonds-y", 160f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        List<Ski> results = skiManager.search(null, null, null, "epic-x", null, null);
        assertEquals(1, results.size());
        assertEquals(listOfSkis.get(0), results.get(0));
    }

    @Test
    public void givenMinLength_whenSearching_thenReturnsLongerOrEqualSkis() {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds1", 150f),
                new Ski(type1, "brand2", "model2", "bonds2", 160f),
                new Ski(type1, "brand3", "model3", "bonds3", 170f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        assertEquals(2, skiManager.search(null, null, null, null, 160f, null).size());
        assertTrue(skiManager.search(null, null, null, null, 160f, null).contains(listOfSkis.get(1)));
        assertTrue(skiManager.search(null, null, null, null, 160f, null).contains(listOfSkis.get(2)));
    }

    @Test
    public void givenMaxLength_whenSearching_thenReturnsShorterOrEqualSkis() {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds1", 150f),
                new Ski(type1, "brand2", "model2", "bonds2", 160f),
                new Ski(type1, "brand3", "model3", "bonds3", 170f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        assertEquals(2, skiManager.search(null, null, null, null, null, 160f).size());
        assertTrue(skiManager.search(null, null, null, null, null, 160f).contains(listOfSkis.get(0)));
        assertTrue(skiManager.search(null, null, null, null, null, 160f).contains(listOfSkis.get(1)));
    }

    @Test
    public void givenMinMaxLength_whenSearching_thenReturnsMatchingSkis() {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds1", 150f),
                new Ski(type1, "brand2", "model2", "bonds2", 160f),
                new Ski(type1, "brand3", "model3", "bonds3", 170f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        assertEquals(1, skiManager.search(null, null, null, null, 155f, 165f).size());
        assertEquals(listOfSkis.get(1), skiManager.search(null, null, null, null, 155f, 165f).get(0));
    }

    @Test
    public void givenBrandTypeMinMaxLength_whenSearching_thenReturnsCorrectResults() {
        SkiType type1 = new SkiType("type1", "desc1");
        SkiType type2 = new SkiType("type2", "desc2");

        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model-a", "bonds-1", 150f),
                new Ski(type2, "brand1", "model-b", "bonds-2", 160f),
                new Ski(type2, "brand2", "model-b", "bonds-3", 170f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        List<Ski> results = skiManager.search(type2, "brand1", null, null, 155f, 165f);
        assertEquals(1, results.size());
        assertEquals(listOfSkis.get(1), results.get(0));
    }

    @Test
    public void givenBrandModelBondsMinMaxLengthFilters_whenSearching_thenReturnsSingleSki() {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds1", 150f),
                new Ski(type1, "brand2", "model2", "bonds2", 160f)
        ));
        skiAddingConsumer.accept(listOfSkis);
        List<Ski> results = skiManager.search(type1, "brand1", "model1", "bonds1", 140f, 160f);
        assertEquals(1, results.size());
        assertEquals(listOfSkis.get(0), results.get(0));
    }

    @Test
    public void givenWrongFiltersThatMatchNothing_whenSearching_thenReturnsEmptyArrayList() throws SkiAppException {
        SkiType type1 = new SkiType("type1", "desc1");
        Ski ski1 = new Ski(type1, "brand1", "model1", "bonds1", 150f);
        skiManager.addEntity(ski1);

        List<Ski> results = skiManager.search(type1, "unknownBrand", null, null, null, null);
        assertTrue(results.isEmpty());
    }

    @Test
    public void givenNonExistentSkiType_whenSearching_thenReturnsEmptyArrayList() throws SkiAppException {
        SkiType type1 = new SkiType("type1", "desc1");
        Ski ski1 = new Ski(type1, "brand1", "model1", "bonds1", 150f);
        skiManager.addEntity(ski1);

        List<Ski> results = skiManager.search(new SkiType("type-x", "desc-x"), null, null, null, null, null);
        assertTrue(results.isEmpty());
    }


}
