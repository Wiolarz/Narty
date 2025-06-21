package wit.io.managers;

import wit.io.data.Ski;
import wit.io.data.SkiType;
import wit.io.data.Rent;
import wit.io.data.enums.RentStatus;

import java.lang.reflect.Array;
import java.util.Date;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.stream.Collectors;

public class ReportManager {
    private RentManager rentManager;
    private SkiManager skiManager;

    public ReportManager(RentManager rentManager, SkiManager skiManager) {
        this.rentManager = rentManager;
        this.skiManager = skiManager;
    }

    public ArrayList<Ski> availableSkis(Date startDate, Date endDate) {
        // wszystkie - na wypożyczeniu
        return null;
    }

    public ArrayList<Ski> overdueSkis() {
        return null;
    }

    public ArrayList<Ski> rentedSkis(Date startDate) {
        // startDate now or before now
        Date startDateFilter = (startDate == null) ? new Date() : startDate;
        var skiModels = rentManager.getEntities()
                .stream()
                .filter(e -> e.getStatus() == RentStatus.ACTIVE || e.getStatus() == RentStatus.OVERDUE)
                .filter(e -> !e.getStartDate().after(startDateFilter))
                .map(Rent::getSkiModel)
                .map(e -> new Ski(new SkiType("", ""), "", e, "", 0.0f))
                .collect(Collectors.toCollection(HashSet::new));

        HashSet<Ski> skiModelsSet = new HashSet<>(skiManager.getEntities());
        // TODO: return list or set
        // TODO: consider using linkedhashset
        skiModelsSet.retainAll(skiModels);
        return null;

        // TODO: test if empty
    }
}
