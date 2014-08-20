/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d;

import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.style.ElementId;

/**
 *
 * @author me
 */
public class LemurNode extends Container {
    private final String id;

    public LemurNode(String id) {
        super("glass");
        this.id = id;
        //            Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        //            mat.getAdditionalRenderState().setBlendMode(RenderState.BlendMode.Alpha);
        //            //mat.setBoolean("UseAlpha",true); //for textures
        //            setQueueBucket(Bucket.Translucent);
        //            mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        //            setMaterial(mat);
        setBackground(new QuadBackgroundComponent(new ColorRGBA(0.25f, 0.5f, 0.1f, 0.5f), 5, 5, 0.02f, false));
    }

    public LemurNode newPanel(ColorRGBA bgcolor, float xMargin, float yMargin, float zOffset) {
        // Create a top panel for some stats toggles.
        LemurNode panel = addChild(new LemurNode(id));
        panel.setBackground(new QuadBackgroundComponent(bgcolor, xMargin, yMargin, zOffset, false));
        //panel.addChild(new Label("Stats Settings", new ElementId("header"), "glass"));
        //panel.addChild(new Panel(2, 2, ColorRGBA.Cyan, "glass")).setUserData(LayerComparator.LAYER, 2);
        return panel;
    }

    public void layout(float sx, float sy, float sz, float scale) {
        Vector3f hudSize = new Vector3f(sx, sy, sz);
        hudSize.maxLocal(getPreferredSize());
        setPreferredSize(hudSize);
        //setLocalScale((1.0f/getLocalScale().normalize()).mult(hudSize.length()));
        //setLocalScale(getLocalScale().normalize().mult(scale));
    }

    public void enableDrag() {
        CursorEventControl.addListenersToSpatial(this, new DragHandler());
    }

    public Label newLabel(String text) {
        Label l = addChild(new Label(text, new ElementId("header"), "glass"));
        return l;
    }

    public Slider newSlider(float min, float max, float value) {
        Slider l = addChild(new Slider(new DefaultRangedValueModel(min, max, value), "glass"));
        return l;
    }
    //        Checkbox temp = panel.addChild(new Checkbox("Show Stats"));
    //        temp.setChecked(true);
    //        showStatsRef = temp.getModel().createReference();
    //        // Custom "spacer" element type
    //        hudPanel.addChild(new Panel(10f, 10f, new ElementId("spacer"), "glass"));
    //        redRef = panel.addChild(new Slider("glass")).getModel().createReference();
    //        blueRef = panel.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100), "glass")).getModel().createReference();
    
}
//    public void initLemur() {
