package fit.wenchao.mycrawler.utils;

import java.io.FileNotFoundException;

public class ExceptionUtils {

    public static Throwable getRootCause(Throwable ex) {
        Throwable cause = ex.getCause();

        Throwable result = null;
        while (cause != null) {
            result = cause;
            cause = cause.getCause();
        }

        return result;
    }

    public static void main(String[] args) {
        RuntimeException runtimeException = new RuntimeException(new FileNotFoundException());
        System.out.println(getRootCause(runtimeException));

        runtimeException = new RuntimeException();
        System.out.println(getRootCause(runtimeException));
    }
}
