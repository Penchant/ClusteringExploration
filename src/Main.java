public class Main {
    public static void main(String[] args) {
        KMeans.kmeans(DataLoader.loadData("a1_raw.csv"), 8);
    }
}
