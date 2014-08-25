/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import syncleus.dann.data.random.UniformRandomData;
import syncleus.dann.graph.Graph;
import syncleus.dann.data.vector.Vector;
import syncleus.dann.math.random.XORShiftRandom;
import syncleus.dann.neural.som.brain.ExponentialDecaySomBrain;

/**
 *
 * @author me
 */
public class SOMNeuralDemo1 extends AbstractGraphView {
    
    private static final int TEST_ITERATIONS = 100;
    private static final int TRAIN_ITERATIONS = 10000;
    private static final double DRIFT_FACTOR = 400.0;
    private static final int OUTPUT_WIDTH = 4;
    private static final int OUTPUT_HEIGHT = 4;
    private static final int OUTPUT_DIMENSIONS = 2;
    private static final double LEARNING_RATE = 0.1;
    private static final int INPUT_DIMENSIONS = 3;
    
    private UniformRandomData randomData;
    private ExponentialDecaySomBrain brain;



    @Override
    public void updateGraph(NeuralNode graphSpace, float tpf) {
        //b.compute(randomData);
        brain.compute(randomData);
        brain.getBestMatchingUnit(true);
        
        
        randomData.update(Math.random() < 0.01 ? true : false, 0.95);
        
        //graphSpace.rotate(0, (float)s2.getModel().getValue()/10.0f, 0);
    }

    
    
    


    @Override
    public Graph getGraph() {
        
        brain = new ExponentialDecaySomBrain(
               INPUT_DIMENSIONS, OUTPUT_DIMENSIONS, TRAIN_ITERATIONS,
               LEARNING_RATE);
        
        
       // create the output latice
       for (double x = 0; x < OUTPUT_WIDTH; x++)
           for (double y = 0; y < OUTPUT_HEIGHT; y++) {
               brain.createOutput(new Vector(x, y));
           }
       
       
  
        // run through RANDOM training data
       for (int iteration = 0; iteration < TRAIN_ITERATIONS; iteration++) {
           brain.setInput(0, Math.random());
           brain.setInput(1, Math.random());
           brain.setInput(2, Math.random());
           //brain.setCurrentInput(i);
           brain.getBestMatchingUnit(true);
       }                    
        
        this.randomData = new UniformRandomData(new XORShiftRandom(), INPUT_DIMENSIONS, 0, 1);
                
        return brain;
    }
    
    public static void main(String[] args) {
        new SOMNeuralDemo1();
        
    }
    
}
