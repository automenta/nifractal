
package nars3d.neural;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.control.UpdateControl;
import com.simsilica.lemur.Slider;
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
    private Slider s1;
    private Slider s2;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp();
        
        gui = new LemurNode("glass");
        
        gui.newLabel("dANN/Encog/NARS");
        LemurNode panel1 = gui.newPanel(ColorRGBA.Blue, 0.5f, 0.5f, 0);
        s1 = panel1.newSlider(0,1,0.25f);
        s2 = panel1.newSlider(0,1,0);
        
        
        gui.enableDrag();
        gui.move(0,0,-6f);
        gui.layout(10,10,10,0.2f);

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
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 22));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 15));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 12));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 9));
        basicNetwork.addLayer(new BasicLayer(new ActivationSigmoid(), false, 6));
        basicNetwork.getStructure().finalizeStructure();
        basicNetwork.reset();
        
        VectorNeuralGraph graph = new VectorNeuralGraph(basicNetwork);
        final HyperassociativeMap hmap = new HyperassociativeMap(graph,3);
        NeuralNode graphSpace = new NeuralNode(graph, hmap);
        rootNode.attachChild(graphSpace);
        
        hmap.setEquilibriumDistance(1.2f);
        
        
        final UniformRandomData randomData = new UniformRandomData(basicNetwork.getInputCount(), 2, -1);
        rootNode.addControl(new UpdateControl() {
            @Override public void update(float tpf) {
                basicNetwork.compute(randomData);
                randomData.update(1.0f - s1.getModel().getValue());
                graphSpace.rotate(0, (float)s2.getModel().getValue()/10.0f, 0);
                
                
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
