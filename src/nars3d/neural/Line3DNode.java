/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nars3d.neural;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.Bucket;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Cylinder;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author me
 */
public class Line3DNode extends Node {

    private static final Vector3f upDirection = new Vector3f(0, 0, 1);

    protected Geometry geom;

    //cache of unit-scale cylinder objects.  the only difference between them is the number of segments surrounding.
    private static Map<Integer, Cylinder> cylinders = new HashMap();

    Quaternion rot = new Quaternion();

    ColorRGBA color = new ColorRGBA();
    private Vector3f direction = new Vector3f();

    public final Vector3f from = new Vector3f();
    public final Vector3f to = new Vector3f();
    
    public Line3DNode() {
        super();

        geom = newLineSpatial();
//		lineSpatial.setModelBound(new OrientedBoundingBox());
//		lineSpatial.updateModelBound();
        attachChild(geom);

    }

    private double getLength() {
        return from.distance(to);
    }


    private Geometry newLineSpatial() {
        Mesh shape = getCylinder(3 /* segments */);
        //Mesh shape = new Cylinder(2, 3, 1, 1f, false);
        Geometry g = new Geometry("l", shape);
        Material mat = NeuralDemo1.unshaded.clone();
        mat.setColor("Color", ColorRGBA.Gray);
        g.setQueueBucket(Bucket.Translucent);
        g.setMaterial(mat);        
        return g;
    }

    private static Cylinder getCylinder(int numEdges) {
        Cylinder c = cylinders.get(numEdges);
        if (c == null) {
            c = new Cylinder(2, numEdges, 1, 1f, false);
            cylinders.put(numEdges, c);
        }
        return c;
    }

    protected void updateLineNode(Vector3f newFrom, Vector3f newTo, double radius, int c) {
        this.from.set(newFrom);
        this.to.set(newTo);
        
        color.fromIntRGBA(c);
        color.a = 0.5f;
        
        geom.getMaterial().setColor("Color", color);

        //SIZE
        double length = getLength();
        //line.getSize().set((float)(currentThickness/length), (float)(currentThickness/length), 1.0f);
        float l = (float) ((float) radius / length);
        //float l = (float)length;
        geom.getLocalScale().set((float) radius, (float) radius, (float) length);

        //ORIENTATION
        direction.set(  (float) (to.x- from.x),
                        (float) (to.y - from.y),
                        (float) (to.z - from.z) );
        direction.normalizeLocal();

        geom.getLocalRotation().lookAt(direction, upDirection);

        geom.setLocalTranslation(
                (float) ((from.x + to.x) / 2.0),
                (float) ((from.y + to.y) / 2.0),
                (float) ((from.z + to.z) / 2.0));

        //updateLogicalState(0.01f);
        updateGeometricState();
        updateModelBound();
        
    }


}
