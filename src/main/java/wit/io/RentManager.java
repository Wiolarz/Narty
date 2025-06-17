package wit.io;

import exceptions.SkiAlreadyPresent;
import exceptions.SkiNotPresent;
import wit.io.data.Ski;
import wit.io.data.Ski;
import wit.io.data.SkiType;

import java.io.DataOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOError;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;


// TODO: SWING
public class RentManager {
    private List<Ski> skis = new ArrayList<>();

    public List<Ski> getSkis() {
        return skis;
    }

    public void setSkis(List<Ski> skis) {
        if (Util.isAnyArgumentNull(skis)) {
            throw new IllegalArgumentException("skis cannot be null.");
        }
        this.skis = skis;
    }

    // + Ewidencja typów nart (lista)
    // + wprowadzanie nowego typu nart (nazwa, opis),
    // + edycja
    // + usuwanie.
    // TODO: save to file


    // TODO: is this needed?
    public RentManager() {
    }

    // TODO: is this needed?
    public RentManager(List<Ski> skis) {
        this();
        this.skis = skis;
    }

    public void addSki(SkiType type, String brand, String model, String bonds, String length) throws SkiAlreadyPresent {
        if (Util.isAnyArgumentNull(type, brand, model, bonds, length)) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        if (skiExists(model)) {
            throw new SkiAlreadyPresent("Exception occurred adding new Ski .");
        }

        Ski newSki = new Ski(type, brand, model, bonds, length);
        skis.add(newSki);
        // todo: save/override ski files here?
    }


    public void removeSki(String skiName) throws SkiNotPresent {
        if (Util.isAnyArgumentNull(skiName)) {
            throw new IllegalArgumentException("skiName cannot be null.");
        }
        if (!skiExists(skiName)) {
            throw new SkiNotPresent("Error removing ski.");
        }
        skis.removeIf(s -> s.getName().equals(skiName));
    }

    public void editSki(String model, Ski updatedSki) throws SkiNotPresent, SkiAlreadyPresent, IllegalArgumentException {
        if (Util.isAnyArgumentNull(model, updatedSki)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeSki(model);
        //addSki(updatedSki.getName(), updatedSki.getDescription());//TODO XD

    }


    private boolean skiExists(String model) {
        for (Ski ski : skis) {
            if (ski.getName().equals(model))
                return true;
        }
        return false;
    }

    public void writeSkisToFile() {
        try {
            DataOutputStream dataOutputStream = new DataOutputStream(new FileOutputStream(""));//TODO XD
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }

    }

    public List<Ski> readSkisFromFile() {
        return Collections.emptyList();
    }
}
