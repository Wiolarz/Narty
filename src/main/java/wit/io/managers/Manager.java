package wit.io.managers;

import wit.io.exceptions.*;
import wit.io.utils.IOThrowableFunction;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

// TODO: SWING
public abstract class Manager<T extends Writeable> {
    protected List<T> dataEntities;
    protected File file;

    public abstract void readFromFile() throws ReadingException;

    public Manager(String filePath) throws ReadingException{
        if(Util.isAnyArgumentNull(filePath)) {
            throw new IllegalArgumentException("filePath cannot be null");
        }

        file = new File(filePath);
        dataEntities = new ArrayList<>();

        if(!file.exists()) {
            return;
        }
        readFromFile();
    }

    public void writeToFile() throws WritingException {
        try (DataOutputStream output =
                     new DataOutputStream(new FileOutputStream(file))) {
            output.writeInt(dataEntities.size());
            for (T entity : dataEntities) {
                entity.writeData(output);
            }
        } catch (IOException e) {
            throw new WritingException(e);
        }

    }

    // TODO: ran only once per manager, at the start of the program
    protected void readFromFile(IOThrowableFunction<DataInputStream, T> readFunc) throws ReadingException{
        if(!file.exists()) {
            return;
        }

        try (DataInputStream input =
                     new DataInputStream(new FileInputStream(file))) {
            int dataLength = input.readInt();
            for (int i = 0; i < dataLength; i++) {
                dataEntities.add(readFunc.apply(input));
            }
        } catch(IOException e){
            throw new ReadingException(e);
        }
    }

    public void resetEntityData() throws WritingException{
        dataEntities = new ArrayList<>();
        writeToFile();
    }

    public void addEntity(T newEntity) throws EntityAlreadyPresentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(newEntity)) {
            throw new IllegalArgumentException("newEntity cannot be null.");
        }
        if (entityExists(newEntity)) {
            throw new EntityAlreadyPresentException("Exception occurred adding new newEntity Type." + newEntity.toString());
        }

        dataEntities.add(newEntity);
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
        dataEntities.removeIf(s -> s.equals(entity));
        writeToFile();
    }

    public void editEntity(T oldEntity, T newEntity)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(oldEntity, newEntity)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeEntity(oldEntity);
        addEntity(newEntity);

    }

    public List<T> getEntities() {
        return dataEntities;
    }


    private boolean entityExists(T entity) {
        for (T e : dataEntities) {
            if (e.equals(entity))
                return true;
        }
        return false;
    }
}
