/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import com.jme3.material.Material;
import nars3d.FractalApp;
import static nars3d.neural.AbstractGraphView.unshaded;
import nars3d.plot.SurfacePlot;
import syncleus.dann.data.random.UniformRandomData;
import syncleus.dann.data.matrix.SimpleRealMatrix;
import syncleus.dann.math.random.XORShiftRandom;
import syncleus.dann.neural.spiking.SpikingNeuralNetwork;
import syncleus.dann.neural.spiking.SpikingNeuron;
import syncleus.dann.neural.spiking.subnetworks.SimpleRecurrentNetwork;

/**
 *
 * @author me
 */
public class SpikingNeuralNetworkDemo1 extends FractalApp {
    private SurfacePlot p;
    int historyLength = 128;
    private SpikingNeuralNetwork net;
    private SimpleRealMatrix m;
    private UniformRandomData rd;
    
    @Override
    public void simpleInitApp() {
        super.simpleInitApp(); 
        
        
        net = new SpikingNeuralNetwork();
        
        //HodgkinHuxleyRule rule = new HodgkinHuxleyRule();
        //IzhikevichRule rule = new IzhikevichRule();
        SimpleRecurrentNetwork h = new SimpleRecurrentNetwork(net, 2, 4, 6);
        
        /*
        Hopfield h = new Hopfield(net, 16);
        inputs.addAll(h.getInputNeurons());
        h.randomize();
        */
        
        /*
        FeedForward h2 = new FeedForward(net, new int[] { 4, 3, 3, 2}, new Point2D.Double());
        inputs.addAll(h2.getInputNeurons());
                */
        
        
        net.randomizeNeurons();
        net.randomizeWeights();

        rd = new UniformRandomData(new XORShiftRandom(), net.getInputNeurons().size(), -1, 1);
        

        unshaded = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");        
        
        
        m = new SimpleRealMatrix(net.getFlatNeuronList().size(), historyLength);
        p = new SurfacePlot(m, unshaded);
        
        p.scale(1f, 0.1f, 1f);
        p.rotate((float)-Math.PI/2f, 0, 0);
        p.move(0, -8, 0);
        
        rootNode.attachChild(p);
    }

    @Override
    public void simpleUpdate(float tpf) {
        super.simpleUpdate(tpf); 
        
        net.setInput(rd);
        
        //System.out.println("t=" + net.getTime());
        net.update();
        //System.out.println(net.getFlatNeuronList());
        //System.out.println(net.getFlatSynapseList());
          
        int j = 0;
        //System.out.println(net.getFlatNeuronList().stream().map(n -> n.getClass()).collect(Collectors.toList()));
        for (SpikingNeuron n : net.getFlatNeuronList()) {
            m.set(j++, 0, n.getActivation());
        }
        
        p.update();
        rd.update(0.3, 0.7);
        
        m.shiftColUp();
        
    }


    

    
    public static void main(String[] args) {
        new SpikingNeuralNetworkDemo1();
    }
    
}
