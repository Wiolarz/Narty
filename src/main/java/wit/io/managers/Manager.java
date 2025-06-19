package wit.io.managers;

import exceptions.*;
import wit.io.utils.Util;

import java.util.ArrayList;
import java.util.List;


// TODO: SWING
public abstract class Manager<T> {
    protected List<T> dataEntity;
    protected String filePath;

    public abstract void writeToFile() throws WritingException;

    // ran only once per manager, at the start of the program
    public abstract void readFromFile() throws ReadingException;

    public Manager(String filePath) throws ReadingException{
        dataEntity = new ArrayList<>();
        this.filePath = filePath;
        readFromFile();
    }
    public void resetEntityData() throws WritingException{
        dataEntity = new ArrayList<>();
        writeToFile();
    }

    public void addEntity(T newEntity) throws EntityAlreadyPresentException, WritingException {
        if (Util.isAnyArgumentNull(newEntity)) {
            throw new IllegalArgumentException("newEntity cannot be null.");
        }
        if (entityExists(newEntity)) {
            throw new EntityAlreadyPresentException("Exception occurred adding new newEntity Type." + newEntity.toString());
        }

        dataEntity.add(newEntity);
        writeToFile();
    }


    public void removeEntity(T entity) throws EntityNotPresentException, WritingException {
        if (Util.isAnyArgumentNull(entity)) {
            throw new IllegalArgumentException("entity cannot be null.");
        }
        if (!entityExists(entity)) {
            throw new EntityNotPresentException("Error removing e.");
        }
        // TODO: custom equals
        dataEntity.removeIf(s -> s.equals(entity));
        writeToFile();
    }

    public void editEntity(T oldEntity, T newEntity)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException {
        if (Util.isAnyArgumentNull(oldEntity, newEntity)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeEntity(oldEntity);
        addEntity(newEntity);

    }

    public List<T> getEntities() {
        return dataEntity;
    }


    private boolean entityExists(T entity) {
        for (T e : dataEntity) {
            if (e.equals(entity))
                return true;
        }
        return false;
    }


}
