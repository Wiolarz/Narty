package wit.io.utils;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * Interface defining the contract for objects that can be written to a binary stream.
 * Implementing classes must provide a way to serialize their data to a DataOutputStream.
 * This interface is used throughout the application for persistent storage of objects.
 */
public interface Writeable {
    void writeData(DataOutputStream stream) throws IOException;
}
