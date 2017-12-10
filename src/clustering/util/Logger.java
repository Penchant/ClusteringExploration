package clustering.util;

public class Logger {

    public enum LoggingLevel {
        INFO,
        IMPORTANT
    }

    public static LoggingLevel level = LoggingLevel.INFO;

    public static void info(Object msg) {
        if (level == LoggingLevel.INFO) {
            System.out.println(msg);
        }
    }

    public static void important(Object msg) {
        System.out.println(msg);
    }

    public static void severe(Object msg) {
        System.err.println(msg);
    }

}
