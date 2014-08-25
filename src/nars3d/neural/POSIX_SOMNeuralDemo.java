/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import java.util.ArrayDeque;
import static nars3d.neural.AbstractGraphView.unshaded;
import nars3d.plot.SurfacePlot;
import syncleus.dann.data.DataCase;
import syncleus.dann.data.feature.aima.AttributeVectorsNormalized;
import syncleus.dann.data.matrix.SimpleRealMatrix;
import syncleus.dann.data.software.posix.POSIXState;
import syncleus.dann.data.vector.Vector;
import syncleus.dann.data.vector.VectorData;
import syncleus.dann.data.vector.VectorDataset;
import syncleus.dann.graph.Graph;
import syncleus.dann.neural.som.brain.ExponentialDecaySomBrain;

/**
 *
 * @author me
 */
public class POSIX_SOMNeuralDemo extends AbstractGraphView {

    private static final int OUTPUT_WIDTH = 6;
    private static final int OUTPUT_HEIGHT = 6;
    private static final int OUTPUT_DIMENSIONS = 2;
    private static final double LEARNING_RATE = 0.01;
    private static final int INPUT_DIMENSIONS = 28;
    
    final int historyLength = 64;
    
    private ExponentialDecaySomBrain brain;

    ArrayDeque<POSIXState> states = new ArrayDeque();
    int MAX_STATES = 128;
    private SimpleRealMatrix m;
    private SurfacePlot sp;
    private SimpleRealMatrix ms;
    private SurfacePlot ssp;


    @Override
    public void updateGraph(NeuralNode graphSpace, float tpf) {
        //b.compute(randomData);
        
        
        
    
        states.add(new POSIXState());
        if (states.size() > MAX_STATES)
            states.pop();
        
        
        
        AttributeVectorsNormalized n = new AttributeVectorsNormalized(states);
        VectorDataset d = n.toDataset();
        
        DataCase<VectorData> midwindow = d.getData().get(states.size()/2);
        
        //for (DataCase<VectorData> c : d.getData()) {
            //System.out.println(c);
        //}        
        
        brain.compute(midwindow.getInput());            
        brain.getBestMatchingUnit(true);
        
        for (int w = 0; w < OUTPUT_WIDTH; w++) {
            for (int h = 0; h < OUTPUT_HEIGHT; h++) {
                ms.set(w, h, -1.0 + 2*brain.getOutput(new Vector(w, h)));
            }            
        }
        
        
        for (int j = 0; j < 28; j++) {
            m.set(j, 0, midwindow.getInput().getData(j));
        }
        
        sp.update();
        ssp.update();
        
        m.shiftColUp();        
        //graphSpace.rotate(0, (float)s2.getModel().getValue()/10.0f, 0);
    }

    
    
    


    @Override
    public Graph getGraph() {
        
        brain = new ExponentialDecaySomBrain(
               INPUT_DIMENSIONS, OUTPUT_DIMENSIONS, 0,
               LEARNING_RATE);
        
        
       // create the output latice
       for (double x = 0; x < OUTPUT_WIDTH; x++)
           for (double y = 0; y < OUTPUT_HEIGHT; y++) {
               brain.createOutput(new Vector(x, y));
           }
       
        m = new SimpleRealMatrix(28, historyLength);
        ms = new SimpleRealMatrix(OUTPUT_WIDTH, OUTPUT_HEIGHT);
        
        sp = new SurfacePlot(m, unshaded);
        
        sp.scale(1f, 0.4f, 0.6f);
        sp.rotate((float)-Math.PI/2f, 0, 0);
        sp.move(0, -8, 0);
        
        rootNode.attachChild(sp);
        
        
        ssp = new SurfacePlot(ms, unshaded);
        ssp.move(5, -1, 0);
        ssp.scale(0.5f);
        
        rootNode.attachChild(ssp);
       
        
  
                
        return brain;
    }
    
    public static void main(String[] args) {
        new POSIX_SOMNeuralDemo();
        
    }
    
}
