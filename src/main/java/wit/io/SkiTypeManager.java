package wit.io;

import exceptions.EntityAlreadyPresent;
import exceptions.EntityNotPresent;
import wit.io.data.SkiType;

import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

// TODO: SWING
public class SkiTypeManager {
    private List<SkiType> skiTypes;
    private String filePath;

    public List<SkiType> getSkiTypes() {
        return skiTypes;
    }

    public void setSkiTypes(List<SkiType> skiTypes) {
        if (Util.isAnyArgumentNull(skiTypes)) {
            throw new IllegalArgumentException("skiTypes cannot be null.");
        }
        this.skiTypes = skiTypes;
    }

    // + Ewidencja typów nart (lista)
    // + wprowadzanie nowego typu nart (nazwa, opis),
    // + edycja
    // + usuwanie.
    // TODO: save to file


    public SkiTypeManager(String filePath) {
        skiTypes = new ArrayList<>();
        this.filePath = filePath;
        readFromFile();
    }

    public void addSkiType(String name, String description) throws EntityAlreadyPresent {
        if (Util.isAnyArgumentNull(name)) {
            throw new IllegalArgumentException("name cannot be null.");
        }
        if (skiTypeExists(name)) {
            throw new EntityAlreadyPresent("Exception occurred adding new Ski Type.");
        }

        SkiType newSkiType = new SkiType(name, description == null ? "" : description);
        skiTypes.add(newSkiType);
        // todo: save/override ski files here?
    }


    public void removeSkiType(String skiTypeName) throws EntityNotPresent {
        if (Util.isAnyArgumentNull(skiTypeName)) {
            throw new IllegalArgumentException("skiTypeName cannot be null.");
        }
        if (!skiTypeExists(skiTypeName)) {
            throw new EntityNotPresent("Error removing ski.");
        }
        skiTypes.removeIf(s -> s.getName().equals(skiTypeName));
    }

    public void editSkiType(String skiTypeName, SkiType updatedSki)
        throws EntityNotPresent, EntityAlreadyPresent, IllegalArgumentException {
        if (Util.isAnyArgumentNull(skiTypeName, updatedSki)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeSkiType(skiTypeName);
        addSkiType(updatedSki.getName(), updatedSki.getDescription());

    }


    private boolean skiTypeExists(String skiTypeName) {
        for (SkiType type : skiTypes) {
            if (type.getName().equals(skiTypeName))
                return true;
        }
        return false;
    }

    public void writeToFile() {
        // writes skiTypes to file (override)
        try (DataOutputStream output =
                 new DataOutputStream(new FileOutputStream(filePath))) {

            output.writeInt(skiTypes.size());
            for (SkiType type : skiTypes) {
                output.writeUTF(type.getName());
                output.writeUTF(type.getDescription());
            }

        } catch (IOException e) {

        }

    }

    public void readFromFile() {
        // at the beggining
        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(filePath))) {
            int length = input.readInt();
            for (int i = 0; i < length; i++) {
                String name = input.readUTF();
                String description = input.readUTF();
                SkiType skiType = new SkiType(name, description);
                System.out.println(skiType.getName());
                System.out.println(skiType.getDescription());
                skiTypes.add(skiType);
            }

        }catch(IOException e){

        }
    }

    public List<SkiType> readSkiTypesFromFile() {
        return Collections.emptyList();
    }
}
