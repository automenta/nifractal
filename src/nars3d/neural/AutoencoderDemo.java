/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import syncleus.dann.data.random.UniformRandomData;
import syncleus.dann.graph.Graph;
import syncleus.dann.learn.autoencoder.DenoisingAutoencoderLayer;
import syncleus.dann.math.random.XORShiftRandom;
import syncleus.dann.neural.feedforward.FullyConnectedFeedforwardBrain;
import syncleus.dann.neural.util.activation.DannActivationFunction;
import syncleus.dann.neural.util.activation.HyperbolicTangentActivationFunction;

/**
 *
 * @author me
 */
public class AutoencoderDemo extends AbstractGraphView {
    private DenoisingAutoencoderLayer dal;
    

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
            

            dal = new DenoisingAutoencoderLayer(null, count, count) {
                
                @Override
                protected DenoisingAutoencoderLayer.AutoencoderNeuron newInput(int i) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                protected DenoisingAutoencoderLayer.AutoencoderNeuron newOutput(int o) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
                
                @Override
                protected DenoisingAutoencoderLayer.AutoencoderSynapse newSynapse(DenoisingAutoencoderLayer.AutoencoderNeuron input, DenoisingAutoencoderLayer.AutoencoderNeuron output) {
                    throw new UnsupportedOperationException("Not supported yet.");
                }
            }

            
        this.randomData = new UniformRandomData(new XORShiftRandom(), brain.getInputNeurons().size(), 0, 1);
                
        return dal;
    }
    
    public static void main(String[] args) {
        new AutoencoderDemo();
    }
    
}
