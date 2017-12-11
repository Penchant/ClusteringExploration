

import java.util.ArrayList;
import java.util.List;
import java.util.stream.IntStream;

class CompetitiveLearning {

    public CompetitiveLearning(final List<Integer> hiddenLayers, int numOfClusters, List<AttributeSet> data){

        if(hiddenLayers.size() > 0 && hiddenLayers.get(0) != 0){
            hiddenLayers.set(hiddenLayers.size() -1 , data.get(0).attributes.size());
        }
        Network net = new Network(hiddenLayers, numOfClusters, data);
        net.run();
    }

}
