package kmeans;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class KMeans {

    public static void main(String[] args) throws IOException {

        String pathPrefix = "D:\\KmeansClustering\\Datasets\\90_10\\";
        String inputPathPrefix = "D:\\KmeansClustering\\Datasets\\90_10\\";
        String outputPathPrefix = "D:\\KmeansClustering\\Datasets\\90_10\\";
        DistanceCalculate dc = new DistanceCalculate(pathPrefix, inputPathPrefix, outputPathPrefix);
        dc.takeData();
        dc.distanceCalculator();

        int mxuid = dc.mxuid;
        double[][] dist = dc.dist;

        ArrayList < Integer > clusterCentroids = new ArrayList < Integer > ();

        List < List < Integer >> arrayListofClusters = new ArrayList < List < Integer >> (mxuid);
        for (int i = 0; i < mxuid; i++) {
            arrayListofClusters.add(new ArrayList < Integer > ());
        }

        // Choose random 1 centroid from every 100 users orderly. 6040 users So, K = 61
        Random r = new Random();
        for (int userId = 1; userId + 100 < mxuid; userId = userId + 100) {
            int Low = userId;
            int High = userId + 100;
            int Result = r.nextInt(High - Low + 1) + Low; // rand.nextInt((max - min) + 1) + min;
            clusterCentroids.add(Result);
        }
        // As, 6001-6040 there is only 40 users
        int Low = 6001;
        int High = 6040;
        int Result = r.nextInt(High - Low + 1) + Low;
        clusterCentroids.add(Result);

        // Populate each cluster with closest users to its centroid
        for (int i = 1; i < mxuid; i++) { // i = current item
            // Check if item itself is centroid
            boolean isCentroid = false;
            for (int j = 0; j < clusterCentroids.size(); j++) {
                int centroid = clusterCentroids.get(j);
                if (i == centroid) { // If item itself is centroid
                    arrayListofClusters.get(i).add(i); // Add centroid to its own cluster
                    isCentroid = true;
                    break;
                }
            }

            if (!isCentroid) {
                double tempMax = 10000;
                int tempCentroid = 0;
                double distance = 0;

                // finding nearest centroid to i
                for (int k = 0; k < clusterCentroids.size(); k++) { // Here, clusterCentroids.size() = 61
                    int currentCentroid = clusterCentroids.get(k);
                    distance = dist[i][currentCentroid];
                    if (distance < tempMax) {
                        tempMax = distance; // tempMax will contain the closest centroid distance from a object
                        tempCentroid = currentCentroid; // The closest centroid
                    }
                }

                arrayListofClusters.get(tempCentroid).add(i);
            }
        }

        //// for debugging purpose
        // Display initial clusters
        System.out.println();
        System.out.println("initial clusters:");
        int totalUsers = 0;
        for (int i = 0; i < clusterCentroids.size(); i++) {
            int centroid = clusterCentroids.get(i);
            System.out.println((i + 1) + " | centroid: " + clusterCentroids.get(i)); // displays centroid
            for (int j = 0; j < arrayListofClusters.get(centroid).size(); j++) {
                System.out.print(arrayListofClusters.get(centroid).get(j) + ", ");
                totalUsers++;
            }
            System.out.println("\n Total users in cluster: " + arrayListofClusters.get(centroid).size()); // displays total users
            System.out.println();
            System.out.println("================================");
        }
        System.out.println("\n Total users: " + totalUsers);

        // Iterations of finding new centroids and populating
        ArrayList < Integer > newClusterCentroids;
        ArrayList < Integer > oldClusterCentroids;
        int iterator = 1;

        do {
            List < List < Integer >> arrayListofTempClusters = new ArrayList < List < Integer >> (mxuid);
            for (int i = 0; i < mxuid; i++) {
                arrayListofTempClusters.add(new ArrayList < Integer > ());
            }
            newClusterCentroids = new ArrayList < Integer > ();

            // Find new centroid from the cluster
            for (int i = 0; i < clusterCentroids.size(); i++) {
                int currentCentroid = clusterCentroids.get(i);
                double distSum = 10000;
                int newCentroid = 0;
                for (int j = 0; j < arrayListofClusters.get(currentCentroid).size(); j++) { // arrayListofClusters.get(currentCentroid).size() = cluster size of current centroid
                    int currentItem = arrayListofClusters.get(currentCentroid).get(j); // current item of the cluster
                    double distSumTemp = 0;
                    for (int k = 0; k < arrayListofClusters.get(currentCentroid).size(); k++) {
                        int nextItem = arrayListofClusters.get(currentCentroid).get(k); // next item of the cluster
                        distSumTemp += dist[currentItem][nextItem];
                    }

                    if (distSumTemp < distSum) {
                        distSum = distSumTemp; // store smallest distSumTemp
                        newCentroid = currentItem; // store the item as new centroid
                    }
                }
                newClusterCentroids.add(newCentroid);
            }

            // Storing Centroids
            oldClusterCentroids = clusterCentroids;

            // For new centroids: Again populating each cluster with closest users to its centroid
            for (int i = 1; i < mxuid; i++) { // i = current item
                // Check if item itself is centroid
                boolean isNewCentroid = false;
                for (int j = 0; j < newClusterCentroids.size(); j++) {
                    int centroid = newClusterCentroids.get(j);
                    if (i == centroid) { // If item itself is centroid
                        arrayListofTempClusters.get(i).add(i); // Add centroid to its own cluster
                        isNewCentroid = true;
                        break;
                    }
                }

                if (!isNewCentroid) {
                    double tempMax = 10000;
                    int tempCentroid = 0;
                    double distance = 0;

                    // For newCentroids, finding nearest centroid to i
                    for (int k = 0; k < newClusterCentroids.size(); k++) { // Here, newClusterCentroids.size() = 61
                        int currentCentroid = newClusterCentroids.get(k);
                        distance = dist[i][currentCentroid];
                        if (distance < tempMax) {
                            tempMax = distance; // tempMax will contain the closest centroid distance from a object
                            tempCentroid = currentCentroid; // The closest centroid
                        }
                    }

                    arrayListofTempClusters.get(tempCentroid).add(i);
                }
            }

            // Updating Clusters
            arrayListofClusters = arrayListofTempClusters;
            clusterCentroids = newClusterCentroids;
            System.out.println("Current Iteration Number : " + iterator);
            iterator++;
        } while (!newClusterCentroids.equals(oldClusterCentroids)); // k-means convergence

        System.out.println();
        System.out.println("+++++++++++++++++++++++++++++");
        System.out.println("Final Clusters after K-Means:");
        System.out.println("+++++++++++++++++++++++++++++");
        System.out.println();

        totalUsers = 0;
        for (int i = 0; i < clusterCentroids.size(); i++) {
            int centroid = clusterCentroids.get(i);
            System.out.println((i + 1) + " | centroid: " + clusterCentroids.get(i)); // displays centroid
            for (int j = 0; j < arrayListofClusters.get(centroid).size(); j++) {
                System.out.print(arrayListofClusters.get(centroid).get(j) + ", ");
                totalUsers++;
            }
            System.out.println("\n Total users in cluster: " + arrayListofClusters.get(centroid).size()); // displays total users
            System.out.println();
            System.out.println("================================");
        }
        System.out.println("\n Total users: " + totalUsers);

    }
}
