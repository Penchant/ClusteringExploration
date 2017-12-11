package clustering.competitiveLearning;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import clustering.util.*;

public class CompetitiveLearning {

    public int epoch;
    public Map<Integer, List<AttributeSet>> mapOut;

    /**
     * Creates Competitive Learning clustering
     * @param hiddenLayers size of hidden layers
     * @param numOfClusters number of clusters to make
     * @param data data to cluster on
     */
    public CompetitiveLearning(final List<Integer> hiddenLayers, int numOfClusters, List<AttributeSet> data){

        if(hiddenLayers.size() > 0 && hiddenLayers.get(0) != 0){
            hiddenLayers.set(hiddenLayers.size() -1 , data.get(0).attributes.size());
        }
        Network net = new Network(hiddenLayers, numOfClusters, data);
        net.run();
        this.epoch = net.epoch;

        this.mapOut = new HashMap<>();

        for (int i = 0; i < net.clusters.size(); i++) {
            this.mapOut.put(i, net.clusters.get(i).members);
        }
    }

}
