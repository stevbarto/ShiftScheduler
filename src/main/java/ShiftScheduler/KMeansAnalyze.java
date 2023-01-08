package ShiftScheduler;

import com.opencsv.exceptions.CsvException;
import net.sf.javaml.clustering.Clusterer;
import net.sf.javaml.clustering.KMeans;
import net.sf.javaml.core.Dataset;
import net.sf.javaml.core.DefaultDataset;
import net.sf.javaml.core.DenseInstance;
import net.sf.javaml.core.Instance;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.SortedSet;

public class KMeansAnalyze {


    private final ShiftScheduler scheduler;
    private int clusters;
    private int iterations;
    private MetricsTool metricsTool;

    public KMeansAnalyze(ShiftScheduler scheduler) {

        this.scheduler = scheduler;
        metricsTool = new MetricsTool(scheduler);
        this.clusters = 3;
        this.iterations = 100;

    }

    public void setClusters(int clusters) {
        this.clusters = clusters;
    }

    public int getClusters() {
        return this.clusters;
    }

    public void setIterations(int iterations) {
        this.iterations = iterations;
    }

    public int getIterations() {
        return this.iterations;
    }

    public KMeansResults classifyData(ArrayList<ArrayList<Integer>> metrics)
            throws IOException, CsvException {

        Dataset newData = new DefaultDataset();

        ArrayList<ArrayList<Double>> transformed = new ArrayList<ArrayList<Double>>();
        transformed.add(new ArrayList<Double>());
        transformed.add(new ArrayList<Double>());

        /*
        for (Integer a:metrics) {
            transformed.add((double) a);
        }

         */

        for (int i = 0; i < metrics.size(); i++) {
            for (int j = 0; j < metrics.get(i).size(); j++) {
                transformed.get(i).add((double) metrics.get(i).get(j));
            }
        }

        // Build an instance for each line
        int set = 0;
        for (int i = 0; i < transformed.get(0).size(); i++) {
            double a = transformed.get(0).get(i);
            double b = transformed.get(1).get(i);
            double[] arr = {a,b};
            Instance lineInst = new DenseInstance(arr);
            lineInst.setClassValue(String.valueOf(set));
            set++;

            newData.add(lineInst);

        }


        Clusterer km = new KMeans(this.clusters, this.iterations);

        Dataset[] clust = km.cluster(newData);

        ArrayList<Dataset> clusters = new ArrayList<Dataset>();

        for (Dataset d:clust) {
            clusters.add(d);
        }

        ArrayList<Dataset> rankedDatasets = rankDataClusters(clusters);

        KMeansResults results = new KMeansResults(this.clusters);

        for (int i = 0; i < rankedDatasets.size(); i++) {
            for (int j = 0; j < rankedDatasets.get(i).size(); j++) {
                Instance inst = rankedDatasets.get(i).instance(j);
                SortedSet<Integer> key = inst.keySet();
                for (Integer intg:key) {

                    double dubz = inst.get(intg);

                    results.addPair(i, Integer.parseInt((String) inst.classValue()), dubz);

                }
            }
        }

        return results;

    }

    private ArrayList<Dataset> rankDataClusters(ArrayList<Dataset> clusters) {

        if (clusters.size() <= 1) {
            return clusters;
        }

        int bestIndex = 0;
        ArrayList<Dataset> values = new ArrayList<Dataset>();
        double avg = 0;
        for (int i = 0; i < clusters.size(); i++) {
            Dataset cluster = clusters.get(i);

            ArrayList<Double> instAverage = new ArrayList<Double>();

            for (int j = 0; j < cluster.size(); j++) {

                Instance instance = cluster.instance(j);

                ArrayList<Double> instVal = new ArrayList<Double>();

                for (int k = 0; k < instance.size(); k++) {
                    instVal.add(instance.value(k));
                }

                double instAvg = average(instVal);

                instAverage.add(instAvg);

            }

            double clusterAvg = average(instAverage);

            if (clusterAvg > avg) {
                avg = clusterAvg;
                bestIndex = i;
            }
        }

        values.add(clusters.remove(bestIndex));

        ArrayList<Dataset> returns = rankDataClusters(clusters);

        for (int i = 0; i < returns.size(); i++) {
            values.add(returns.get(i));
        }

        return values;

    }

    private double average(ArrayList<Double> values) {

        int size = values.size();

        if (size == 0) {
            return 0.0;
        }

        double sum = 0.0;

        for (double d:values) {
            sum = sum + d;
        }

        return sum / size;

    }

    public ArrayList<double[]> convertStringArray(List<String[]> dataIn) {

        ArrayList<double[]> transformed = new ArrayList<double[]>();

        int index = 0;
        for (String[] a:dataIn) {
            transformed.add(new double[a.length]);
            for (int i = 0; i < a.length; i++) {
                transformed.get(index)[i] = Double.parseDouble(a[i]);
            }
            index++;
        }


        return transformed;
    }

    public ArrayList<double[]> convertStringArrayList(List<String[]> dataIn) {

        ArrayList<double[]> transformed = new ArrayList<double[]>();

        int index = 0;
        for (String[] a:dataIn) {
            transformed.add(new double[a.length]);
            for (int i = 0; i < a.length; i++) {
                transformed.get(index)[i] = Double.parseDouble(a[i]);
            }
            index++;
        }


        return transformed;
    }

    public double[] convertIntegerArrayList(ArrayList<Integer> dataIn) {

        double[] transformed = new double[dataIn.size()];

        int index = 0;
        for (Integer i:dataIn) {
            transformed[index] = i;
            index++;
        }

        return transformed;
    }

}
