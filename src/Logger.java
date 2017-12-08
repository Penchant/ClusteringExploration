class Logger {

    public enum LoggingLevel {
        INFO,
        IMPORTANT
    }

    static LoggingLevel level = LoggingLevel.INFO;

    static void info(String msg) {
        if (level == LoggingLevel.INFO) {
            System.out.println(msg);
        }
    }

    static void important(String msg) {
        System.out.println(msg);
    }

    static void severe(String msg) {
        System.err.println(msg);
    }

}
