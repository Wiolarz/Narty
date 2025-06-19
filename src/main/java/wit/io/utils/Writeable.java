package wit.io.utils;

import java.io.DataOutputStream;
import java.io.IOException;

public interface Writeable {
    void writeData(DataOutputStream stream) throws IOException;
}
