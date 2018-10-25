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
}
