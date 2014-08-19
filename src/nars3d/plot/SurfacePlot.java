/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.plot;

import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.shape.Quad;
import java.lang.reflect.Array;
import syncleus.dann.math.matrix.RealMatrix;

/**
 *
 * @author me
 */
public class SurfacePlot extends Node {

    int width;
    int height;
    final Geometry[][] point;
    private final RealMatrix data;
    private final Material mat;
    
    public SurfacePlot(RealMatrix data, Material m) {
        super();
        
        this.mat = m;
        width = data.getCols();
        height = data.getRows();
        
        this.data = data;
        point = (Geometry[][])Array.newInstance(Geometry.class, height, width);
        
        
        update();
    }
    
    public void update() {
        for (int i = 0; i < height; i++)
            for (int j = 0; j < width; j++) {
                if (point[i][j] == null) {
                    point[i][j] = newPoint();
                    attachChild(point[i][j]);
                }
                updatePoint(i, j, point[i][j], data.get(i,j));
            }
        updateGeometricState();
    }
    
    public Geometry newPoint() {
        Quad q = new Quad(1f, 1f);        
        Geometry g = new Geometry("_", q);
        Material m = mat.clone();        
        g.setMaterial(m);
        return g;
    }

    private void updatePoint(int x, int y, Geometry spatial, double value) {
        //bipolar to unipolar
        value = value / 2 + 0.5;
        
        spatial.setLocalTranslation(x, y, (float)value);
        spatial.setLocalScale(0.8f, 0.8f, 1f); //TODO parameter
        
        ColorRGBA c = new ColorRGBA(0.2f+ 0.8f*(float)value, 0.4f, 0.2f, 1f);
        spatial.getMaterial().setColor("Color", c);
    }
    
}
