
package nars3d.neural;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.control.UpdateControl;
import nars3d.FractalApp;
import syncleus.dann.data.random.UniformRandomData;
import syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;
import syncleus.dann.neural.VectorNeuralGraph;
import syncleus.dann.neural.activation.ActivationSigmoid;
import syncleus.dann.neural.networks.VectorNeuralNetwork;
import syncleus.dann.neural.networks.layers.BasicLayer;

/**
 *
 * @author me
 */
public class NeuralDemo1 extends FractalApp {
    private LemurNode gui;
    public static Material unshaded;
    public static Material lighted;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        
        gui = new LemurNode("glass");
        gui.newPanel(ColorRGBA.Blue, 0.5f, 0.5f, 0);
        gui.newLabel("dANN/Encog/NARS");
        gui.enableDrag();
        gui.scale(0.1f);
        gui.move(0,0,-6f);

        unshaded = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        unshaded.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        
        lighted = new Material(assetManager, "Common/MatDefs/Light/Lighting.j3md");
        
//        LemurNode gui2 = new LemurNode("gui");
//        gui2.newPanel(ColorRGBA.Blue, 0.5f, 0.9f, 0.9f);        
//        gui2.enableDrag();
//        gui2.scale(0.1f);
//        gui2.move(2,2,-6f);
        
        rootNode.attachChild(gui);

        
        
        final VectorNeuralNetwork basicNetwork = new VectorNeuralNetwork();
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 12));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 10));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), true, 4));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 5));
        //basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 2));
        basicNetwork.getStructure().finalizeStructure();
        basicNetwork.reset();
        
        VectorNeuralGraph graph = new VectorNeuralGraph(basicNetwork);
        
        rootNode.attachChild(new NeuralNode(graph, new HyperassociativeMap(graph,3)));
        
        final UniformRandomData randomData = new UniformRandomData(basicNetwork.getInputCount(), 2, -1);
        rootNode.addControl(new UpdateControl() {
            @Override public void update(float tpf) {
                basicNetwork.compute(randomData);
                randomData.update(0.9f);
            }                    
        });
        
    }


    @Override
    public void simpleUpdate(float tpf) {
        
    }
 
    public static void main(String[] args) {
        new NeuralDemo1();
    }
}
