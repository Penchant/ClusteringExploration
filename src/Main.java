public class Main {
    public static void main(String[] args) {
        Logger.level = Logger.LoggingLevel.IMPORTANT;
        KMeans.kmeans(DataLoader.loadData("a1_raw.csv"), 8);
    }
}
