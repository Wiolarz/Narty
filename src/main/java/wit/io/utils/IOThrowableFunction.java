package wit.io.utils;

import java.io.IOException;

/**
 * A functional interface representing a function that takes an input of type T and
 * returns a result of type R, potentially throwing an IOException during execution.
 * This interface is designed to handle I/O operations that may fail, allowing
 * proper exception handling in the calling code.
 *
 * @param <T> The type of the input parameter
 * @param <R> The type of the return value
 */
public interface IOThrowableFunction<T, R> {
    R apply(T t) throws IOException;
}
