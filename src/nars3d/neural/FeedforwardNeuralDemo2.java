/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import syncleus.dann.data.random.UniformRandomData;
import syncleus.dann.graph.Graph;
import syncleus.dann.math.random.XORShiftRandom;
import syncleus.dann.neural.feedforward.FullyConnectedFeedforwardBrain;
import syncleus.dann.neural.util.activation.DannActivationFunction;
import syncleus.dann.neural.util.activation.HyperbolicTangentActivationFunction;

/**
 *
 * @author me
 */
public class FeedforwardNeuralDemo2 extends AbstractGraphView {
    private FullyConnectedFeedforwardBrain brain = null;

    //private static final int TRAINING_CYCLES = 1000;
    private static final double LEARNING_RATE = 0.5;
    private static final int[] TOPOLOGY = {2, 7};
    private UniformRandomData randomData;




    @Override
    public void updateGraph(NeuralNode graphSpace, float tpf) {
        
        randomData.update(Math.random() < 0.1 ? true : false, 0.99);

        brain.setCurrentInput(randomData);
        brain.propagate();
                
        //graphSpace.rotate(0, (float)s2.getModel().getValue()/10.0f, 0);
    }
    
    public int getDrawingDimensions() {
        return 3;
    }

    
    
    


    @Override
    public Graph getGraph() {
        
        // Adjust the learning rate
        //final DannActivationFunction activationFunction = new SineActivationFunction();
        final DannActivationFunction activationFunction = new HyperbolicTangentActivationFunction();

        /*final int cores = Runtime.getRuntime().availableProcessors();
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(cores + 1,
                cores * 2, 20, TimeUnit.SECONDS, new LinkedBlockingQueue());*/
        
            this.brain = new FullyConnectedFeedforwardBrain(TOPOLOGY,
                    LEARNING_RATE, activationFunction);
            

            int count = 10;
            for (int lcv = 0; lcv < count; lcv++) {
                
                for (int i = 0; i < 50; i++) {
                    
                    UniformRandomData ii = new UniformRandomData(new XORShiftRandom(), brain.getInputNeurons().size(), 0, 1);
                
                    UniformRandomData oo = new UniformRandomData(new XORShiftRandom(), brain.getOutputNeurons().size(), 0, 1);
                    brain.train(ii, oo);
                }
            }
            


            
            this.randomData = new UniformRandomData(new XORShiftRandom(), brain.getInputNeurons().size(), 0, 1);
                
        return brain;
    }
    
    public static void main(String[] args) {
        new FeedforwardNeuralDemo2();
    }
    
}
