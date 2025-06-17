package wit.io;

import exceptions.SkiTypeAlreadyPresent;
import exceptions.SkiTypeNotPresent;
import wit.io.data.SkiType;

import java.util.ArrayList;
import java.util.List;

// TODO: SWING
public class SkiTypeManager
{
    // + Ewidencja typów nart (lista)
    // wprowadzanie nowego typu nart (nazwa, opis),
    // edycja,usuwanie.
    // TODO: save to file
    List<SkiType> skiTypes = new ArrayList<>();

    public void addSkiType(String name, String description) throws SkiTypeAlreadyPresent
    {
        if (skiTypeExists(name)) {
            throw new SkiTypeAlreadyPresent("Exception occurred adding new Ski Type.");
        }

        SkiType newSkiType = new SkiType(name, description);
        skiTypes.add(newSkiType);
        // todo: save/override ski files here?
    }

    public void removeSkiType(String skiTypeName) throws SkiTypeNotPresent
    {
        if (!skiTypeExists(skiTypeName)) {
            throw new SkiTypeNotPresent();
        }
    }

    public void editSkiType(String skiTypeName) throws SkiTypeAlreadyPresent {
        if (skiTypeExists(skiTypeName)) {
            throw new SkiTypeAlreadyPresent();
        }

    }


    private boolean skiTypeExists(String skiTypeName) {
        for (SkiType type : skiTypes) {
            if (type.getName().equals(skiTypeName))
                return true;
        }
        return false;
    }
}
