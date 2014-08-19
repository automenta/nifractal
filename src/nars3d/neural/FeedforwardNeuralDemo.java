/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import syncleus.dann.data.random.UniformRandomData;
import syncleus.dann.graph.Graph;
import syncleus.dann.math.random.XORShiftRandom;
import syncleus.dann.neural.VectorNeuralGraph;
import syncleus.dann.neural.VectorNeuralNetwork;
import syncleus.dann.neural.util.activation.ActivationSigmoid;
import syncleus.dann.neural.util.layer.BasicLayer;

/**
 *
 * @author me
 */
public class FeedforwardNeuralDemo extends AbstractGraphView {
    private VectorNeuralGraph graph;
    private UniformRandomData randomData;
    private VectorNeuralNetwork basicNetwork;



    @Override
    public void updateGraph(NeuralNode graphSpace, float tpf) {
        basicNetwork.compute(randomData);
        randomData.update(Math.random() < 0.01 ? true : false, 0.99);
        //randomData.update(1.0f - s1.getModel().getValue());
        //graphSpace.rotate(0, (float)s2.getModel().getValue()/10.0f, 0);
    }

    
    
    


    @Override
    public Graph getGraph() {
        basicNetwork = new VectorNeuralNetwork();
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 22));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 15));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 12));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 6));
        basicNetwork.getStructure().finalizeStructure();
        basicNetwork.reset();
        
        this.graph = new VectorNeuralGraph(basicNetwork);
        this.randomData = new UniformRandomData(new XORShiftRandom(), basicNetwork.getInputCount(), -1, 1);
                
        return graph;
    }
    
    public static void main(String[] args) {
        new FeedforwardNeuralDemo();
    }
    
}
