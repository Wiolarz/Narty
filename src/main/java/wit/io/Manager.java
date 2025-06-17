package wit.io;

import exceptions.SkiTypeAlreadyPresent;
import exceptions.SkiTypeNotPresent;

import java.io.DataOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Manager<ItemType> {
    private String filePath;
    private List<ItemType> itemList;
    
    public Manager(String filePath){
        itemList = new ArrayList<>();
        this.filePath = filePath;
        // todo: implement this.itemList = readSkiTypesFromFile(filePath);
    }

    
    public List<ItemType> getSkiTypes() {
        return itemList;
    }

    public void setSkiTypes(List<ItemType> itemList) {
        if (Util.isAnyArgumentNull(itemList)) {
            throw new IllegalArgumentException("itemList cannot be null.");
        }
        this.itemList = itemList;
    }

    // + Ewidencja typów nart (lista)
    // + wprowadzanie nowego typu nart (nazwa, opis),
    // + edycja
    // + usuwanie.
    // TODO: save to file




    public void addItem(ItemType item) throws SkiTypeAlreadyPresent {
        if (Util.isAnyArgumentNull(item)) {
            throw new IllegalArgumentException("added item cannot be null.");
        }
        if (itemExists(item)) {
            throw new SkiTypeAlreadyPresent("Exception occurred adding new Ski Type.");
        }

        itemList.add(item);
        // todo: save/override ski files here?
    }


    public void removeSkiType(ItemType item) throws SkiTypeNotPresent {
        if (Util.isAnyArgumentNull(item)) {
            throw new IllegalArgumentException("skiTypeName cannot be null.");
        }
        if (!itemExists(item)) {
            throw new SkiTypeNotPresent("Error removing ski.");
        }
        itemList.removeIf(s -> s.equals(item));
    }

    public void editSkiType(ItemType old_item, ItemType new_item)
            throws SkiTypeNotPresent, SkiTypeAlreadyPresent, IllegalArgumentException {
        if (Util.isAnyArgumentNull(old_item, new_item)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeSkiType(old_item);
        addItem(new_item);

    }


    private boolean itemExists(ItemType checked_item) {
        for (ItemType item : itemList) {
            if (item.equals(checked_item))
                return true;
        }
        return false;
    }

    public void writeToFile() {
        // writes itemList to file (override)
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(filePath))) {

            for (ItemType type : itemList) {
                //byte[] bytes = itemList.toBytes(); //TODO XD
                //output.write(bytes);
            }

        } catch (IOException e) {

        }

    }

    public List<ItemType> readSkiTypesFromFile() {
        return Collections.emptyList();
    }

}
