import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Main {

    private static final int kmeans = 1;
    private static final int db = 2;
    private static final int cl = 3;
    private static final int pso = 4;
    private static final int aco = 5;

    /**
     * Polls the user for which algorithm they would like to use and returns it's id
     * @param scanner The scanner to use to poll for input
     * @return The id of the chosen algorithm
     */
    private static int getMenuSelection(Scanner scanner) {
        Arrays.stream(new String[]{
                "Enter which algorithm you would like to use: ",
                "\t" + kmeans + ". K Means",
                "\t" + db + ". DB Scan",
                "\t" + cl + ". Competitive Learning",
                "\t" + pso + ". Particle Swarm Optimization",
                "\t" + aco + ". Ant Colony Optimization"
        }).forEach(System.out::println);

        return Integer.parseInt(scanner.nextLine());
    }

    /**
     * Generates data for the algorithms to use based on what the user selects
     * @param scanner The scanner to use to poll for input
     * @return The generated data
     */
    private static List<AttributeSet> getDataSelection(Scanner scanner) {
        System.out.print("Enter the path to the file you would like to use (Use \"sample\" for a list of sample sets): ");

        String filePath = scanner.nextLine();

        List<AttributeSet> data;

        if (filePath.equalsIgnoreCase("sample")) {
            Arrays.stream(new String[]{
                    "Enter which sample you would like to use: ",
                    "\t1. Two Squares (0.5-10.5 on x and y, 50.5-60.5 on x and y",
                    "\t2. Circle (n circles with 360 points each of given radii)",
            }).forEach(System.out::println);

            int sample = Integer.parseInt(scanner.nextLine());

            switch (sample) {
                case 1:
                    data = DataLoader.genTwoSquaresSample();
                    break;
                case 2:
                    System.out.print("Enter a comma separated list of radii: ");

                    String[] radii = scanner.nextLine().split(",");

                    data = DataLoader.genCircleSample(Arrays.stream(radii).mapToDouble(i -> Double.parseDouble(i)).toArray());
                    break;
                default: data = DataLoader.genTwoSquaresSample();
            }
        } else {
            data = DataLoader.loadData(filePath);
        }

        return data;
    }

    /**
     * Executes the given algorithm (input) with the given data (data)
     * @param scanner The scanner to use for polling input for parameters
     * @param input The algorithm id
     * @param data The List<Attribute> data set
     */
    private static void executeAlgorithm(Scanner scanner, int input, List<AttributeSet> data) {
        switch(input) {
            case kmeans:
                System.out.print("Enter k (How many clusters, integer): ");

                int k = Integer.parseInt(scanner.nextLine());
                List<Centroid> centroids = KMeans.kmeans(data, k);

                centroids.stream().forEach(System.out::println);
                break;
            case db:
                System.out.print("Enter epsilon (Max distance between points, double): ");
                double epsilon = Double.parseDouble(scanner.nextLine());

                System.out.print("Enter minPts (Min amount of points required, integer): ");
                int minPts = Integer.parseInt(scanner.nextLine());

                Map<Integer, List<AttributeSet>> clusters = DBScan.dbScan(data, epsilon, minPts);
                clusters.keySet().forEach(s -> Logger.info(clusters.get(s)));
                break;
            case cl: break;
            case pso: break;
            case aco: break;
        }
    }

    public static void main(String[] args) {
        Logger.level = Logger.LoggingLevel.IMPORTANT;

        Scanner scanner = new Scanner(System.in);

        // Get which algorithm we want to use
        int menuSelection = getMenuSelection(scanner);
        // Get what data set to use
        List<AttributeSet> data = getDataSelection(scanner);

        // Execute the algoirthm
        executeAlgorithm(scanner, menuSelection, data);
    }
}
