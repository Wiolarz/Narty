package wit.io.data;

import java.util.List;

// (typ, marka, model, wiązania, długość)
public class Ski {
    /*
    Ewidencja typów nart (lista) i wprowadzanie nowego typu nart (nazwa, opis), edycja, usuwanie.

     */
    SkiType type;
    String brand;
    String model;
    String bonds;
    String length;

    public Ski(SkiType type, String brand, String model, String bonds, String length) {
        this.type = type;
        this.brand = brand;
        this.model = model;
        this.bonds = bonds;
        this.length = length;
    }


    public String getName() {
        return model;
    }

    //TODO implement tostring and equals
}