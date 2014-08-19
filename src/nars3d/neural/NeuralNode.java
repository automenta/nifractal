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
import nars3d.FractalApp.LemurNode;
import syncleus.dann.graph.BidirectedEdge;
import syncleus.dann.graph.Edge;
import syncleus.dann.graph.Graph;
import syncleus.dann.graph.WeightedEdge;
import syncleus.dann.graph.drawing.GraphDrawer;
import syncleus.dann.graph.drawing.hyperassociativemap.HyperassociativeMap;
import syncleus.dann.math.Vector;
import syncleus.dann.neural.VectorNeuralGraph.VectorNeuralEdge;
import syncleus.dann.neural.VectorNeuralGraph.VectorNeuralNode;
import syncleus.dann.neural.feedforward.graph.BackpropStaticNeuron;
import syncleus.dann.neural.util.AbstractNeuron;

/**
 *
 * @author me
 */
public class NeuralNode<N,E extends Edge<N>> extends Node {
    private final Graph net;

    Map<N, Geometry> nodes = new HashMap();
    Map<E, Line3DNode> edges = new HashMap();
    
    private final GraphDrawer<Graph<N,E>,N> layout;
    float nodeScale = 0.1f;
    float networkScale = 2.0f;
    private final Material mat;
    
    public NeuralNode(Graph<N,E> g, GraphDrawer<Graph<N,E>,N> layout, Material m) {
        super();
        
        this.mat = m.clone();
        this.net = g;
        this.layout = layout;
        
        
        for (N n :  g.getNodes() ) {
            if (isValidNode(n))
                newNode(n);
        }
        for (E e :  g.getEdges() ) {
            if (e.streamNodes().anyMatch(n -> !isValidNode(n)))
                continue;
            
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
    protected Geometry newNode(N n) {
        
        
        Node group = new Node("neuron");
        
        Box box = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", box);
        
        Material m = mat.clone();
        m.setColor("Color", ColorRGBA.Green);        
        m.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        geom.setMaterial(m);
        
        
        
        LemurNode ln = new LemurNode("y");
        ln.newLabel(n.toString());
        //ln.newLabel(n.getClass().getSimpleName());
        //ln.newSlider(0,1,0.1f);
        
        ln.move(-0.5f, 0.5f, 1.1f);
        //ln.scale(0.1f);
        ln.layout(0.1f, 0.1f, 0.1f,0.03f);
               
        group.attachChild(geom);
        group.attachChild(ln);
        
        
        attachChild(group);
        
        
        
        nodes.put(n, geom);
        return geom;
    }
    
    protected Spatial newEdge(E e) {
        Line3DNode geom = new Line3DNode(mat);
        attachChild(geom);
        edges.put(e, geom);
    
        return geom;
    }
    
    protected boolean isValidNode(N n) {
        if (n instanceof BackpropStaticNeuron)
            return false;
        return (n instanceof VectorNeuralNode) || (n instanceof AbstractNeuron);
    }
    
    protected void updateLayout(float tpf) {
        if (!layout.isAligned()) {
            ((HyperassociativeMap)layout).setMaxSpeed(0.0025); 
            layout.align();
        }
        
        for (Map.Entry<N, Geometry> e : nodes.entrySet()) {
            N node = e.getKey();
            Geometry s = e.getValue();
            
            float act = 0.5f;
            if (node instanceof VectorNeuralNode) {
                act = ((float)((VectorNeuralNode)node).getActivation())/2f+0.5f;
            }
            else if (node instanceof AbstractNeuron) {                
                act = (float)((AbstractNeuron)node).getOutput()/2f+0.5f;
            }
            else {
                System.out.println("Unrecognized node type: " + node);
            }
            
            ColorRGBA c = new ColorRGBA();
            c.fromIntRGBA( Color.getHSBColor(act, 1f, act*0.5f+0.5f).getRGB() );
            c.a = 0.75f;
            s.getMaterial().setColor("Color", c);
                    
            
            Vector p = layout.getCoordinates().get(node);
            if (p == null) {
                //System.out.println(node + " missing from layout");
                continue;
            }
            
            if (p.getDimension() == 2) 
                s.getParent().setLocalTranslation(networkScale * (float)p.get(1), networkScale * (float)p.get(2), 0);
            else
                s.getParent().setLocalTranslation(networkScale * (float)p.get(1), networkScale * (float)p.get(2), networkScale * (float)p.get(3));
            
            s.getParent().setLocalScale(nodeScale * (1f+act));
        }
        
        for (Map.Entry<E, Line3DNode> e : edges.entrySet()) {
            E edge = e.getKey();
            Line3DNode s = e.getValue();
            
            if (edge instanceof BidirectedEdge) {
                BidirectedEdge<N> b = (BidirectedEdge<N>)edge;
                
                N fromNode = b.getLeftNode();
                N toNode = b.getRightNode();

                Vector3f from = nodes.get(fromNode).getParent().getLocalTranslation();
                Vector3f to = nodes.get(toNode).getParent().getLocalTranslation();

                float act = 0f;
                float w = 0.5f;
                if (edge instanceof VectorNeuralEdge) {
                    VectorNeuralEdge vne = (VectorNeuralEdge)edge;
                    act = ((float)vne.getSourceNode().getActivation())/2.0f+0.5f;
                    w = ((float)vne.getWeight())/2.0f+0.5f;
                }
                else if (edge instanceof WeightedEdge) {
                    WeightedEdge we = (WeightedEdge)edge;
                    w = (float)we.getWeight();
                    act = w;
                }
                else {
                    System.out.println("Unrecognized edge type: " + edge.getClass());
                }
                
                
                s.updateLineNode(from, to, 0.02 + (0.05 * w), 
                                Color.getHSBColor(act, 1f, act*0.5f+0.5f).getRGB()
                );
            }
        }
    }
    
    
}
