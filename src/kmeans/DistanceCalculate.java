package kmeans;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DistanceCalculate {

    int mxmid = 3953;   // actually 3952
    int mxuid = 6041;   // actually 6040

    List < List < Integer >> userCluster = new ArrayList < List < Integer >> (mxuid);
    List < List < Integer >> userClusterTest = new ArrayList < List < Integer >> (mxuid);

    double[][] rat = new double[mxuid][mxmid];
    double[][] Rat = new double[mxuid][mxmid];
    double[][] dist = new double[mxuid][mxuid];

    String inputPathPrefix, outputPathPrefix, pathPrefix;

    void init() {
        for (int i = 0; i < mxuid; i++) {
            userCluster.add(new ArrayList < Integer > ());
        }
        for (int i = 0; i < mxuid; i++) {
            userClusterTest.add(new ArrayList < Integer > ());
        }
    }

    DistanceCalculate(String pathPrefix, String inPrefix, String outPrefix) {
        this.pathPrefix = pathPrefix;
        this.inputPathPrefix = inPrefix;
        this.outputPathPrefix = outPrefix;
        init();
    }

    // Reading the dataset
    public void takeData() throws IOException {
        // Reading the train.csv file
        BufferedReader in = new BufferedReader(new FileReader(inputPathPrefix + "train.csv"));
        String text;
        String[] cut;
        int uid = 0, mid = 0, r = 0, t = 0;
        while ((text = in .readLine()) != null) {
            cut = text.split(",");

            uid = Integer.parseInt(cut[0]);
            mid = Integer.parseInt(cut[1]);
            r = Integer.parseInt(cut[2]);
            t = Integer.parseInt(cut[3]);
            // System.out.println(uid+" + "+ mid +" + "+ r +" + "+t);
            rat[uid][mid] = r;
            userCluster.get(uid).add(mid);
        }

        // Reading the test.csv file
        in = new BufferedReader(new FileReader(inputPathPrefix + "test.csv"));

        while ((text = in .readLine()) != null) {
            cut = text.split(",");

            uid = Integer.parseInt(cut[0]);
            mid = Integer.parseInt(cut[1]);
            r = Integer.parseInt(cut[2]);
            t = Integer.parseInt(cut[3]);
            //System.out.println(uid+" + "+ mid +" + "+ r +" + "+t);
            Rat[uid][mid] = r;
            userClusterTest.get(uid).add(mid);
        }
    }

    // Normalizing values
    double normalize(double val) {
        double new_min = 0.2;
        double new_max = 1;
        double minVal = 1;
        double maxVal = 5;
        double newVal = 0;

        newVal = ((val - minVal) * (new_max - new_min)) / (maxVal - minVal) + new_min;

        return newVal;
    }

    // Distance Calculator
    void distanceCalculator() throws IOException {
        File file = new File(inputPathPrefix + "userDist.csv");
        boolean exists = file.exists();
        if (exists) {
            System.out.println("[userDist.csv] File Exist!");

            BufferedReader in = new BufferedReader(new FileReader(inputPathPrefix + "userDist.csv"));
            String text;
            String[] cut;
            int u = 0, v = 0;
            while ((text = in .readLine()) != null) {
                cut = text.split(",");
                u = Integer.parseInt(cut[0]);
                v = Integer.parseInt(cut[1]);
                dist[u][v] = Double.parseDouble(cut[2]);
                System.out.println("Distance between " + u + " and " + v + " is " + dist[u][v]);
            }
        } else {
            System.out.println("[userDist.csv] File Does Not Exist!");

            PrintWriter out = new PrintWriter(new FileWriter(outputPathPrefix + "userDist.csv"));
            for (int u = 1; u < mxuid; u++) {
                for (int v = 1; v < mxuid; v++) {
                    List < Integer > userList = userCluster.get(u);
                    int itemSize = userList.size();
                    int commonCounter = 0;
                    for (int movieIndex = 0; movieIndex < itemSize; movieIndex++) {
                        int movieId = userList.get(movieIndex);
                        if (rat[v][movieId] != 0) {
                            commonCounter++;
                            // System.out.println("movieId: "+ movieId); // Common movies they both watched
                            dist[u][v] += Math.abs(normalize(rat[u][movieId]) - normalize(rat[v][movieId]));
                        }
                    }
                    if (commonCounter != 0) {
                        dist[u][v] = dist[u][v] / commonCounter;
                    } else {
                        dist[u][v] = 1;
                    }

                    // Save distances in file
                    out.println(u + "," + v + "," + dist[u][v]);
                    out.flush();

                    System.out.println("Distance between " + u + " and " + v + " is " + dist[u][v]);
                }
            }
            out.close();
        }
    }
}
