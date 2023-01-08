package ShiftScheduler;

import java.util.ArrayList;

public class KMeansResults {

    private ArrayList<dataCluster> clusters;
    private int numClusters;

    public KMeansResults(int numClusters) {
        this.clusters = new ArrayList<dataCluster>();
        assert numClusters > 0;
        this.numClusters = numClusters;
        for (int i = 0; i < numClusters; i++) {
            this.clusters.add(new dataCluster());
        }
    }

    public void addPair(int clust, int key, double val) {
        this.clusters.get(clust).addDataPair(key,val);
    }

    public ArrayList<Integer> getKeys(int clust) {
        return this.clusters.get(clust).getKeyList();
    }

    public ArrayList<Double> getValues(int clust) {
        return this.clusters.get(clust).getValueList();
    }

    private class dataCluster {

        ArrayList<Integer> keys;
        ArrayList<Double> values;

        private dataCluster() {
            keys = new ArrayList<Integer>();
            values = new ArrayList<Double>();
        }

        public void addDataPair(int key, double val) {
            keys.add(key);
            values.add(val);
        }

        public ArrayList<Integer> getKeyList() {
            return this.keys;
        }

        public ArrayList<Double> getValueList() {
            return this.values;
        }

    }
}
