public class Logger {

    public enum LoggingLevel {
        INFO,
        IMPORTANT
    }

    public static LoggingLevel level = LoggingLevel.INFO;

    public static void info(String msg) {
        if (level == LoggingLevel.INFO) {
            System.out.println(msg);
        }
    }

    public static void important(String msg) {
        System.out.println(msg);
    }

    public static void severe(String msg) {
        System.err.println(msg);
    }

}
