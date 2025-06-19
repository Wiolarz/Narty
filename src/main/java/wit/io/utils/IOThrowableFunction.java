package wit.io.utils;

import java.io.IOException;

public interface IOThrowableFunction<T, R> {
    R apply(T t) throws IOException;
}
