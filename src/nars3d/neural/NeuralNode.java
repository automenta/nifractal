/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.neural;

import com.jme3.material.Material;
import com.jme3.material.RenderState;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;
import com.jme3.scene.shape.Box;
import java.awt.Color;
import java.util.HashMap;
import java.util.Map;
import syncleus.dann.graph.drawing.GraphDrawer;
import syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;
import syncleus.dann.math.Vector;
import syncleus.dann.neural.VectorNeuralGraph;
import syncleus.dann.neural.VectorNeuralGraph.VectorNeuralEdge;
import syncleus.dann.neural.VectorNeuralGraph.VectorNeuralNode;

/**
 *
 * @author me
 */
public class NeuralNode extends Node {
    private final VectorNeuralGraph net;

    Map<VectorNeuralNode, Geometry> nodes = new HashMap();
    Map<VectorNeuralEdge, Line3DNode> edges = new HashMap();
    
    private final GraphDrawer<VectorNeuralGraph, VectorNeuralNode> layout;
    float nodeScale = 0.1f;
    float networkScale = 1.0f;
    
    public NeuralNode(VectorNeuralGraph g, GraphDrawer<VectorNeuralGraph,VectorNeuralNode> layout) {
        super();
        this.net = g;
        this.layout = layout;
        
        for (VectorNeuralNode n :  g.getNodes() ) {
            newNode(n);
        }
        for (VectorNeuralEdge e :  g.getEdges() ) {
            newEdge(e);
        }
        
        layout.reset();
        updateLayout(0);

        
        
        addControl(new UpdateControl() {


            
            @Override
            protected void controlUpdate(float tpf) {
                updateLayout(tpf);
            }
            
        });
        
        
    }
    
    protected Geometry newNode(VectorNeuralNode n) {
        
        Box box = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", box);
        Material mat = NeuralDemo1.unshaded.clone();
        mat.setColor("Color", ColorRGBA.Green);
        mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(mat);
        
        attachChild(geom);        
        
        nodes.put(n, geom);
        return geom;
    }
    
    protected Spatial newEdge(VectorNeuralEdge e) {
        Line3DNode geom = new Line3DNode();
        attachChild(geom);
        edges.put(e, geom);
    
        return geom;
    }
    
    protected void updateLayout(float tpf) {
        ((HyperassociativeMap)layout).setMaxSpeed(0.01);
        layout.align();
        
        for (Map.Entry<VectorNeuralNode, Geometry> e : nodes.entrySet()) {
            VectorNeuralNode node = e.getKey();
            Geometry s = e.getValue();
            
            float act = ((float)node.getActivation())/2f+0.5f;
            ColorRGBA c = new ColorRGBA();
            c.fromIntRGBA( Color.getHSBColor(act, 1f, 1f).getRGB() );
            c.a = 0.75f;
            s.getMaterial().setColor("Color", c);
                    
            
            Vector p = layout.getCoordinates().get(node);
            
            
            if (p.getDimension() == 2) 
                s.setLocalTranslation((float)p.get(1), (float)p.get(2), 0);
            else
                s.setLocalTranslation((float)p.get(1), (float)p.get(2), (float)p.get(3));
            
            s.setLocalScale(nodeScale * (1f+(float)node.getActivation()));
        }
        
        for (Map.Entry<VectorNeuralEdge, Line3DNode> e : edges.entrySet()) {
            VectorNeuralEdge edge = e.getKey();
            Line3DNode s = e.getValue();
            
            VectorNeuralNode fromNode = edge.getSourceNode();
            VectorNeuralNode toNode = edge.getDestinationNode();

            Vector3f from = nodes.get(fromNode).getLocalTranslation();
            Vector3f to = nodes.get(toNode).getLocalTranslation();
            
            float act = ((float)fromNode.getActivation())/2.0f+0.5f;
            float w = ((float)edge.getWeight())/2.0f+0.5f;
            s.updateLineNode(from, to, 0.01 + (0.05 * w), 
                            Color.getHSBColor(act, 1f, 1f).getRGB()
            );
        }
    }
    
    
}
