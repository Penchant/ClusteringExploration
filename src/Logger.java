class Logger {

    public enum LoggingLevel {
        INFO,
        IMPORTANT
    }

    static LoggingLevel level = LoggingLevel.INFO;

    static void info(Object msg) {
        if (level == LoggingLevel.INFO) {
            System.out.println(msg);
        }
    }

    static void important(Object msg) {
        System.out.println(msg);
    }

    static void severe(Object msg) {
        System.err.println(msg);
    }

}
