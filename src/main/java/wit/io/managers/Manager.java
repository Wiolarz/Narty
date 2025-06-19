package wit.io.managers;

import wit.io.data.Client;
import wit.io.exceptions.EntityAlreadyPresentException;
import wit.io.exceptions.EntityNotPresentException;
import wit.io.exceptions.ReadingException;
import wit.io.exceptions.WritingException;
import wit.io.utils.IOThrowableFunction;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// TODO: SWING
public abstract class Manager<T extends Writeable> {
    protected List<T> dataEntity;
    protected String filePath;

    public abstract void readFromFile() throws ReadingException;

    public Manager(String filePath) throws ReadingException{
        dataEntity = new ArrayList<>();
        this.filePath = filePath;
        readFromFile();
    }

    public void writeToFile() throws WritingException {
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(filePath))) {
            output.writeInt(dataEntity.size());
            for (T entity : dataEntity) {
                entity.writeData(output);
            }
        } catch (IOException e) {
            throw new WritingException(e);
        }

    }

    // TODO: ran only once per manager, at the start of the program
    protected void readFromFile(IOThrowableFunction<DataInputStream, T> readFunc) throws ReadingException{
        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(filePath))) {
            int dataLength = input.readInt();
            for (int i = 0; i < dataLength; i++) {
                dataEntity.add(readFunc.apply(input));
            }
        } catch(IOException e){
            throw new ReadingException(e);
        }
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
