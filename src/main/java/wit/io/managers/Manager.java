package wit.io.managers;

import wit.io.exceptions.*;
import wit.io.utils.IOThrowableFunction;
import wit.io.utils.Util;
import wit.io.utils.Writeable;

import java.io.*;
import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.List;

/**
 * An abstract generic manager class for handling collections of {@link Writeable} entities.
 * This class provides essential functionalities for reading from and writing to a file,
 * as well as managing a set of entities including adding, removing, and editing them
 * @param <T> The type of entities managed by this class, which MUST implement {@link Writeable}.
 */
public abstract class Manager<T extends Writeable> {
    /**
     * A Set containing all managed entities of type T. This collection is used to store
     * the in-memory representation of the data.
     */
    protected Set<T> dataEntities;

    /**
     * The file the managed entities should be saved/read from. This file serves as
     * the permanent storage for all data managed by this class.
     */
    protected File file;

    /**
     * Abstract method to be implemented by subclasses for reading data from the file.
     * Subclasses should specify how their data class is read.
     * @throws ReadingException If an error occurs during the reading process.
     */
    public abstract void readFromFile() throws ReadingException;

    /**
     * Constructor for Manager.
     * Initializes the file and the data entities set.
     * If the file already exists it attempts to read the existing data into memory.
     *
     * @param filePath The path to the file where entities are/will be stored.
     * @throws IllegalArgumentException If the {@code filePath} is null.
     * @throws ReadingException If an error occurs during the attempt to read from the file.
     */
    public Manager(String filePath) throws ReadingException{
        if(Util.isAnyArgumentNull(filePath)) {
            throw new IllegalArgumentException("filePath cannot be null");
        }

        file = new File(filePath);
        dataEntities = new LinkedHashSet<>();

        if(!file.exists()) {
            return;
        }
        readFromFile();
    }

    /**
     * Writes the current set of data entities to the file.
     * The number of entities is written first, followed by each entity's data
     * using its writeData method.
     * @throws WritingException If an I/O error occurs during the writing process.
     */
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

    /**
     * Reads data from the file using a provided function to load in each entity.
     * This method is protected and intended for use by subclasses to implement their
     * specific readFromFile logic.
     * should be run only once per manager, at the start of the program.
     *
     * @param readFunc A functional interface that takes a {@link DataInputStream}
     * and returns an entity of type T, handling potential {@link IOException}.
     * @throws ReadingException If an I/O error occurs during reading from the file.
     */
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

    /**
     * Resets entity data by clearing the current set and writing an empty set to the file.
     * @throws WritingException If an error occurs while writing to the file.
     */
    public void resetEntityData() throws WritingException{
        dataEntities = new LinkedHashSet<>();
        writeToFile();
    }

    /**
     * Adds a new entity to the manager's collection and saves the updated collection to a file.
     * @param newEntity The entity to be added.
     * @throws IllegalArgumentException if newEntity is null.
     * @throws EntityAlreadyPresentException If the entity to be added already exists in the collection.
     * @throws WritingException If an error occurs while writing the updated data to the file.
     * @throws SkiAppException If a general application error occurs
     */
    public void addEntity(T newEntity) throws EntityAlreadyPresentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(newEntity)) {
            throw new IllegalArgumentException("newEntity cannot be null.");
        }

        if (dataEntities.contains(newEntity)) {
            throw new EntityAlreadyPresentException("Exception occurred adding new newEntity Type." + newEntity.toString());
        }

        dataEntities.add(newEntity);
        writeToFile();
    }

    /**
     * Removes an existing entity from the manager's collection and saves the updated collection to a file.
     * @param entity The entity to be removed.
     * @throws IllegalArgumentException If entity is null.
     * @throws EntityNotPresentException If the entity to be removed is not found in the collection.
     * @throws WritingException If an error occurs while writing the updated data to the file.
     */
    public void removeEntity(T entity) throws EntityNotPresentException, WritingException {
        if (Util.isAnyArgumentNull(entity)) {
            throw new IllegalArgumentException("entity cannot be null.");
        }
        if (!dataEntities.contains(entity)) {
            throw new EntityNotPresentException("Error removing e.");
        }
        // TODO: custom equals
        dataEntities.remove(entity);
        writeToFile();
    }

    /**
     * Edits an existing entity by replacing it with a new one. This is achieved by
     * first removing the old entity and then adding the new entity. The changes are
     * then saved to the file.
     *
     * @param oldEntity The entity to be replaced.
     * @param newEntity The new entity that will replace the old one.
     * @throws IllegalArgumentException If either oldEntity or newEntity is null.
     * @throws EntityNotPresentException If the oldEntity is not found in the collection.
     * @throws EntityAlreadyPresentException If the newEntity (after removal of old still persists).
     * @throws WritingException If an error occurs while writing the updated data to the file.
     * @throws SkiAppException If a general application error occurs.
     */
    public void editEntity(T oldEntity, T newEntity)
            throws EntityNotPresentException, EntityAlreadyPresentException, IllegalArgumentException, WritingException, SkiAppException {
        if (Util.isAnyArgumentNull(oldEntity, newEntity)) {
            throw new IllegalArgumentException("One or more of given arguments were null.");
        }
        removeEntity(oldEntity);
        addEntity(newEntity);
    }

    /**
     * Gets the current set of managed enti
     * @return A Set containing all managed entities.
     */
    public Set<T> getEntities() {
        return dataEntities;
    }

    /**
     * Sets the managed entities to a new provided set and saves these changes to the file.
     * @param setDataEntities The new Set of entities to manage.
     * @throws WritingException If an error occurs while writing the new data to the file.
     */
    public void setEntities(Set<T> setDataEntities) throws WritingException {
        dataEntities = setDataEntities;
        writeToFile();
    }

    /**
     * Gets the current set of managed entities as an ArrayList.
     * This provides a convenient way to access the entities when a List interface is preferred.
     * @return An ArrayList containing all managed entities.
     */
    public ArrayList<T> getEntitiesList() {
        return new ArrayList<>(dataEntities);
    }
}
