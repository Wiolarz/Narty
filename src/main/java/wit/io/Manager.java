package wit.io;

import exceptions.EntityAlreadyPresent;
import exceptions.EntityNotPresent;

import java.util.ArrayList;
import java.util.List;

// TODO: SWING
public abstract class Manager<T> {
    private List<T> dataEntity;
    private String filePath;

    public abstract void writeToFile();
    public abstract void readFromFile();

    public Manager(String filePath) {
        dataEntity = new ArrayList<>();
        this.filePath = filePath;
        readFromFile();
    }

    public void addEntity(T newEntity) throws EntityAlreadyPresent {
        if (Util.isAnyArgumentNull(newEntity)) {
            throw new IllegalArgumentException("newEntity cannot be null.");
        }
        if (entityExists(newEntity)) {
            throw new EntityAlreadyPresent("Exception occurred adding new newEntity Type."); //TODO  + newEntity.getClassName()
        }

        dataEntity.add(newEntity);
        writeToFile();
    }


    public void removeEntity(T entity) throws EntityNotPresent {
        if (Util.isAnyArgumentNull(entity)) {
            throw new IllegalArgumentException("entity cannot be null.");
        }
        if (!entityExists(entity)) {
            throw new EntityNotPresent("Error removing ski.");
        }
        dataEntity.removeIf(s -> s.equals(entity));
        writeToFile();
    }

    public void editEntity(T oldEntity, T newEntity)
            throws EntityNotPresent, EntityAlreadyPresent, IllegalArgumentException {
        if (Util.isAnyArgumentNull(oldEntity, newEntity)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeEntity(oldEntity);
        addEntity(newEntity);
        writeToFile();

    }

    public List<T> getEntities() {
        return dataEntity;
    }


    private boolean entityExists(T entity) {
        for (T e : dataEntity) {
            if (e == entity)
                return true;
        }
        return false;
    }


}
