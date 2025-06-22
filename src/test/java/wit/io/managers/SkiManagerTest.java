package wit.io.managers;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.exceptions.*;
import wit.io.utils.Util;
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

    @AfterEach
    public void tearDown() throws WritingException {
        skiManager.resetEntityData();
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
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(skiManager.getEntitiesList(), listOfSkis));
    }

    @Test
    public void givenSkiExists_whenAddingNewSkiWithTheSameData_thenThrowEntityAlreadyPresentException () throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        assertThrows(EntityAlreadyPresentException.class, () -> skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f)));
    }

    @Test
    public void givenSkIsNull_whenAddingNewSki_thenThrowIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () ->  skiManager.addEntity(null));
    }

    @Test
    public void givenSkiDoesNotExist_whenAddingNewType_thenAddToSkis() throws SkiAppException {
        Ski ski = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);
        skiManager.addEntity(ski);

        Ski resultSki = skiManager.getEntitiesList().get(0);
        assertTrue(Util.compareObjectsByStringValue(ski, resultSki));
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

        assertEquals(0, skiManager.getEntities().size());
    }

    @Test
    public void givenSkiTypeExists_whenResettingSkiType_thenGetEntitiesReturnsEmptyAndClearsFile() throws SkiAppException {
        skiManager.addEntity(new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f));

        setUp();

        assertNotNull(skiManager.getEntities());
        assertEquals(0, skiManager.getEntities().size());
    }

    @Test
    public void givenSkiWithDifferentTypeButMatchingModel_whenEditingSki_thenSkiDataIsEdited() throws SkiAppException {
        Ski oldSki = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);
        Ski updatedSki = new Ski(new SkiType("name2", "description2"), "brand1", "model1", "bond1", 1f);
        skiManager.addEntity(oldSki);

        skiManager.editEntity(oldSki, updatedSki);

        assertEquals(1, skiManager.getEntities().size());
        assertEquals(updatedSki, skiManager.getEntitiesList().get(0));
    }

    @Test
    public void givenExistingSki_whenEditingWithNull_thenThrowsIllegalArgumentExceptionAndNotModify() throws SkiAppException {
        Ski oldSki = new Ski(new SkiType("name1", "description1"), "brand1", "model1", "bond1", 1f);

        skiManager.addEntity(oldSki);

        assertThrows(IllegalArgumentException.class, () -> skiManager.editEntity(oldSki, null));
        assertEquals(1, skiManager.getEntities().size());
        assertTrue(Util.compareObjectsByStringValue(oldSki, skiManager.getEntitiesList().get(0)));
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
    public void givenNoFilters_whenSearching_thenReturnsAllSkis() {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds1", 150f),
                new Ski(type1, "brand2", "model2", "bonds2", 160f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        List<Ski> results = skiManager.search(null, null, null, null, null, null);
        assertEquals(2, results.size());
        assertTrue(Util.orderAndCompareListsOfObjectsByStringValue(listOfSkis, results));
    }

    @Test
    public void givenType_whenSearching_thenReturnsMatchingSkis(){
        SkiType type1 = new SkiType("type1", "desc1");
        SkiType type2 = new SkiType("type2", "desc2");

        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand1", "model1", "bonds1", 150f),
                new Ski(type2, "brand2", "model2", "bonds2", 160f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        List<Ski> results = skiManager.search(type1, null, null, null, null, null);
        assertEquals(1, results.size());
        assertEquals(listOfSkis.get(0), results.get(0));
    }

    @Test
    public void givenBrand_whenSearching_thenReturnsMatchingSkis()  {
        SkiType type1 = new SkiType("type1", "desc1");
        List<Ski> listOfSkis = new ArrayList<>(List.of(
                new Ski(type1, "brand-test-1", "model1", "bonds1", 150f),
                new Ski(type1, "brand-other-2", "model2", "bonds2", 160f)
        ));
        skiAddingConsumer.accept(listOfSkis);

        List<Ski> results = skiManager.search(null, "TEST-1", null, null, null, null);
        assertEquals(1, results.size());
        assertEquals(listOfSkis.get(0), results.get(0));
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
                new Ski(type2, "brand2", "model-c", "bonds-3", 170f)
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
