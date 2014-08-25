
package nars3d.neural;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.control.UpdateControl;
import com.simsilica.lemur.Slider;
import nars3d.FractalApp;
import nars3d.LemurNode;
import syncleus.dann.graph.Graph;
import syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;

/**
 *
 * @author me
 */
abstract public class AbstractGraphView extends FractalApp {
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
        
        //rootNode.attachChild(gui);

        
        
        Graph graph = getGraph();
        final HyperassociativeMap hmap = new HyperassociativeMap(graph, getDrawingDimensions());
        
        NeuralNode graphSpace = new NeuralNode(graph, hmap, unshaded);
        rootNode.attachChild(graphSpace);
        
        hmap.setEquilibriumDistance(1.f);
        
        rootNode.addControl(new UpdateControl() {
            @Override public void update(float tpf) {
                if (!hmap.isAligned())
                    hmap.align();
                updateGraph(graphSpace, tpf);
            }                    
        });
        
    }

    public int getDrawingDimensions() {
        return 3;
    }
    
    abstract public Graph getGraph();  
    abstract public void updateGraph(NeuralNode n, float tpf);
    

}
