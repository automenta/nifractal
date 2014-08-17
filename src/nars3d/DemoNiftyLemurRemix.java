/*
 * Copyright (c) 2009-2012 jMonkeyEngine
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are
 * met:
 *
 * * Redistributions of source code must retain the above copyright
 *   notice, this list of conditions and the following disclaimer.
 *
 * * Redistributions in binary form must reproduce the above copyright
 *   notice, this list of conditions and the following disclaimer in the
 *   documentation and/or other materials provided with the distribution.
 *
 * * Neither the name of 'jMonkeyEngine' nor the names of its contributors
 *   may be used to endorse or promote products derived from this software
 *   without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED
 * TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL,
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF
 * LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING
 * NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package nars3d;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.AnalogListener;
import com.jme3.input.controls.MouseAxisTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.input.event.MouseButtonEvent;
import com.jme3.input.event.MouseMotionEvent;
import com.jme3.material.Material;
import com.jme3.material.RenderState.BlendMode;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.LayerComparator;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.DynamicInsetsComponent;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TbtQuadBackgroundComponent;
import com.simsilica.lemur.core.VersionedReference;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.Styles;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class DemoNiftyLemurRemix extends SimpleApplication {

    private Map<Geometry, NiftyJmeDisplay> nifty = new HashMap();
    private NiftyJmeDisplay focusedNifty = null;

    int guiCursorX, guiCursorY;

    //LEMUR SHIT
    private VersionedReference<Double> redRef;
    private VersionedReference<Double> greenRef;
    private VersionedReference<Double> blueRef;
    private VersionedReference<Double> alphaRef;
    private VersionedReference<Boolean> showStatsRef;
    private VersionedReference<Boolean> showFpsRef;

    private ColorRGBA boxColor = ColorRGBA.Blue.clone();
    //----------

    public static void main(String[] args) {
        DemoNiftyLemurRemix app = new DemoNiftyLemurRemix();

        AppSettings settings;
        app.setSettings(settings = new AppSettings(true));
        settings.put("Width", 1024);
        settings.put("Height", 800);
        settings.put("VSync", true);
        settings.put("frameRate", 30);
        app.setShowSettings(false);

        app.start();

    }
    private GUICamera guiCam;

    public void addGUI(String xmlFile, float x, float y, float z, float sx, float sy, Quaternion rot) {
        ViewPort niftyView = renderManager.createPreView("NiftyView", new Camera(1024, 768));
        niftyView.setClearFlags(true, true, true);

        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                inputManager,
                audioRenderer,
                niftyView) {
                };

        Nifty nif = niftyDisplay.getNifty();
        nif.fromXml(xmlFile, "start");
        nif.addControlsWithoutStartScreen();

        niftyView.addProcessor(niftyDisplay);

        Texture2D depthTex = new Texture2D(1024, 768, Format.Depth);
        FrameBuffer fb = new FrameBuffer(1024, 768, 1);

        fb.setDepthTexture(depthTex);

        Texture2D tex = new Texture2D(1024, 768, Format.RGBA8);
        tex.setMinFilter(MinFilter.Trilinear);
        tex.setMagFilter(MagFilter.Bilinear);

        fb.setColorTexture(tex);
        niftyView.setClearFlags(true, true, true);
        niftyView.setOutputFrameBuffer(fb);

        //final Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        final Quad b = new Quad(1, 1);
        Geometry geom = new Geometry("Quad", b);
        geom.setLocalScale(sx, sy, 1.0f);
        geom.setLocalTranslation(x, y, z);
        geom.setLocalRotation(rot);

        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);

        geom.setMaterial(mat);

        rootNode.attachChild(geom);

        nifty.put(geom, niftyDisplay);
    }

    public void simpleInitApp() {
        initCommon();
        initNifty();
        initLemur();
        
        
    }

    
    protected void initCommon() {
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

        guiCam = new GUICamera(cam);
        flyCam = guiCam;
        flyCam.setMoveSpeed(4f); // odd to set this here but it did it before
        
        
        stateManager.attach(new FlyCamAppState());
        stateManager.getState(FlyCamAppState.class).setCamera(flyCam);
        
        rootNode.setLocalScale(10f,10f,1f);

    }
    
    public void initNifty() {

        
        addGUI("dragndrop/dragndrop.xml", 0f,-1.5f,6f,1f,1f, new Quaternion());
        addGUI("multiplayer/multiplayer.xml",2f,2f,6f,2f,2f, new Quaternion(new float[] { 0.5f,0,0}));
        addGUI("console/console.xml",-2f,-2f,6f,2f,2f, new Quaternion(new float[] { 0,0.5f,0}));
        addGUI("tutorial/tutorial.xml",0,0,6f,2f,2f, new Quaternion(new float[] { 0,0f,0.2f}));
        addGUI("textfield/textfield.xml",-4,-2,7f,1f,1f, new Quaternion(new float[] { -0.2f,0f,0f}));
        addGUI("scroll/scroll.xml",2,-1,7f,1f,1f, new Quaternion(new float[] { 0.2f,0f,0.0f}));
        
        flyCam.setEnabled(true);
        //inputManager.setSimulateMouse(true);
        mouseInput.setCursorVisible(true);

        inputManager.addMapping("MouseMove",
                new MouseAxisTrigger(MouseInput.AXIS_X, false),
                new MouseAxisTrigger(MouseInput.AXIS_Y, false),
                new MouseAxisTrigger(MouseInput.AXIS_X, true),
                new MouseAxisTrigger(MouseInput.AXIS_Y, true)
        );
        inputManager.addMapping("MouseClick", new MouseButtonTrigger(0) /* also 2*/);

        inputManager.addListener(new ActionListener() {
            @Override public void onAction(String name, boolean isPressed, float tpf) { 
                if (focusedNifty!=null)
                    focusedNifty.inputSys.simulateMouseButtonEvent(new MouseButtonEvent(0, isPressed, guiCursorX, guiCursorY));

            }
        }, "MouseClick");
        
        // Test multiple listeners per mapping
        inputManager.addListener(new AnalogListener() {
            @Override public void onAnalog(String name, float value, float tpf) {
                                
                if (guiCam.isDragToRotate())
                    return;
                
                Vector3f origin = cam.getWorldCoordinates(
                        inputManager.getCursorPosition(), 0.0f);

                Vector3f direction = cam.getWorldCoordinates(
                        inputManager.getCursorPosition(), 0.3f);

                direction.subtractLocal(origin).normalizeLocal();

                Ray ray = new Ray(origin, direction);
                CollisionResults results = new CollisionResults();

                
                rootNode.collideWith(ray, results);

            //        System.out.println("----- Collisions? " + results.size() + "-----");
                //        for (int i = 0; i < results.size(); i++) {
                //            // For each hit, we know distance, impact point, name of geometry.
                //            float dist = results.getCollision(i).getDistance();
                //            Vector3f pt = results.getCollision(i).getWorldContactPoint();
                //            String hit = results.getCollision(i).getGeometry().getName();
                //            System.out.println("* Collision #" + i);
                //            System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
                //        }
                
                if (results.size() > 0) {
                    CollisionResult c = results.getClosestCollision();
                    
//Vector3f p = c.getContactPoint();
                    ////p = c.getGeometry().worldToLocal(p, new Vector3f());
                    focusedNifty = nifty.get(c.getGeometry());
                    if (focusedNifty == null)
                        return;
                    
                    VertexBuffer tc = c.getGeometry().getMesh().getBuffer(Type.Position);
                    FloatBuffer fb = (FloatBuffer) tc.getData();                    
                    Vector3f[] pa = BufferUtils.getVector3Array(fb);
                    
                    Vector3f contact = c.getContactPoint();
                    Vector3f contactLocal = new Vector3f();
                    c.getGeometry().getWorldTransform().transformInverseVector(contact, contactLocal);
                    
                    int lastX = guiCursorX;
                    int lastY = guiCursorY;
                    
                    Element root = focusedNifty.getNifty().getCurrentScreen().getRootElement();

                    guiCursorX = (int) ((contactLocal.x) * root.getWidth());
                    guiCursorY = (int) ((contactLocal.y) * root.getHeight());

                    int dx = guiCursorX - lastX;
                    int dy = guiCursorY - lastY;

                    
                    focusedNifty.inputSys.simulateMouseMotionEvent(new MouseMotionEvent(guiCursorX, guiCursorY, dx, dy, 0, 0));

                //System.out.println(c.getDistance() + " " + p + " " + pLocal + " " + guiCursorX + " " + guiCursorY);
                    //Quaternion q = new Quaternion();
                    //q.lookAt(closest.getContactNormal(), Vector3f.UNIT_Y);
                } else {
                }

               //guiCursorX = 
            }
        }, "MouseMove");

    }

    public void initLemur() {
        // Initialize the globals access so that the defualt
        // components can find what they need.
        GuiGlobals.initialize(this);

        // Remove the flycam because we don't want it in this
        // demo
        //stateManager.detach(stateManager.getState(FlyCamAppState.class));

        // Now, let's create some styles in code.
        // For this demo, we'll just give some of the elements
        // different backgrounds as we define a "glass" style.
        // We also define a custom element type called "spacer" which
        // picks up a specific style.
        Styles styles = GuiGlobals.getInstance().getStyles();
        styles.getSelector(Slider.THUMB_ID, "glass").set("text", "[]", false);
        styles.getSelector(Panel.ELEMENT_ID, "glass").set("background",
                new QuadBackgroundComponent(new ColorRGBA(0, 0.25f, 0.25f, 0.5f),0,0,0,true));
        styles.getSelector(Checkbox.ELEMENT_ID, "glass").set("background",
                new QuadBackgroundComponent(new ColorRGBA(0, 0.5f, 0.5f, 0.5f),0,0,0,true));
        styles.getSelector("spacer", "glass").set("background",
                new QuadBackgroundComponent(new ColorRGBA(1, 0.0f, 0.0f, 0.0f)));
        styles.getSelector("header", "glass").set("background",
                new QuadBackgroundComponent(new ColorRGBA(0, 0.75f, 0.75f, 0.5f)));

        // Now construct some HUD panels in the "glass" style that
        // we just configured above.
        Container hudPanel = new Container("glass");
        //hudPanel.setLocalTranslation(5, cam.getHeight() - 50, 0);
        rootNode.attachChild(hudPanel);

        // Create a top panel for some stats toggles.
        Container panel = new Container("glass");
        hudPanel.addChild(panel);

        panel.setBackground(new QuadBackgroundComponent(new ColorRGBA(0, 0.5f, 0.5f, 0.5f), 5, 5, 0.02f, false));
        panel.addChild(new Label("Stats Settings", new ElementId("header"), "glass"));
        panel.addChild(new Panel(2, 2, ColorRGBA.Cyan, "glass")).setUserData(LayerComparator.LAYER, 2);

        // Adding components returns the component so we can set other things
        // if we want.
        Checkbox temp = panel.addChild(new Checkbox("Show Stats"));
        temp.setChecked(true);
        showStatsRef = temp.getModel().createReference();

        temp = panel.addChild(new Checkbox("Show FPS"));
        temp.setChecked(true);
        showFpsRef = temp.getModel().createReference();

        // Custom "spacer" element type
        hudPanel.addChild(new Panel(10f, 10f, new ElementId("spacer"), "glass"));

        // Create a second panel in the same overall HUD panel
        // that lets us tweak things about the cube.
        panel = new Container("glass");
        panel.setBackground(new QuadBackgroundComponent(new ColorRGBA(0, 0.5f, 0.5f, 0.5f), 5, 5, 0.02f, false));
        // Custom "header" element type.
        panel.addChild(new Label("Cube Settings", new ElementId("header"), "glass"));
        panel.addChild(new Panel(2, 2, ColorRGBA.Cyan, "glass")).setUserData(LayerComparator.LAYER, 2);
        Label redLabel = panel.addChild(new Label("Red:"));
        redLabel.setLocalTranslation(0, 0, 5);
        
        redRef = panel.addChild(new Slider("glass")).getModel().createReference();
        
        panel.addChild(new Label("Green:"));
        greenRef = panel.addChild(new Slider("glass")).getModel().createReference();
        panel.addChild(new Label("Blue:"));
        blueRef = panel.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100), "glass")).getModel().createReference();
        panel.addChild(new Label("Alpha:"));
        alphaRef = panel.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100), "glass")).getModel().createReference();

        hudPanel.addChild(panel);
        
        /*
        rootNode.addControl(new UpdateControl() {
            
            @Override protected void controlUpdate(float tpf) {
                hudPanel.rotate(0, 0.01f, 0);
                
            }
        }); 
                */

        // Increase the default size of the hud to be a little wider
        // if it would otherwise be smaller.  Height is unaffected.
        Vector3f hudSize = new Vector3f(20, 1, 1);
        hudSize.maxLocal(hudPanel.getPreferredSize());        
        hudPanel.setPreferredSize(hudSize);
        
        hudPanel.scale(0.01f);
        
        

        // Note: after next nightly, this will also work:
        // hudPanel.setPreferredSize( new Vector3f(200,0,0).maxLocal(hudPanel.getPreferredSize()) );
        // Something in scene
        Box box = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", box);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setColor("Color", boxColor);
        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);

        // A draggable bordered panel
        Container testPanel = new Container();
        testPanel.setPreferredSize(new Vector3f(200, 200, 0));
        testPanel.setBackground(TbtQuadBackgroundComponent.create("/com/simsilica/lemur/icons/border.png",           1, 2, 2, 3, 3, 0, false));
        Label test = testPanel.addChild(new Label("Border Test"));

        // Center the text in the box.
        test.setInsetsComponent(new DynamicInsetsComponent(0.5f, 0.5f, 0.5f, 0.5f));
        testPanel.setLocalTranslation(400, 400, 0);

        CursorEventControl.addListenersToSpatial(testPanel, new DragHandler());
        guiNode.attachChild(testPanel);
    }

    @Override
    public void simpleUpdate(float tpf) {
        if (showStatsRef.update()) {
            setDisplayStatView(showStatsRef.get());
        }
        if (showFpsRef.update()) {
            setDisplayFps(showFpsRef.get());
        }

        boolean updateColor = false;
        if (redRef.update()) {
            updateColor = true;
        }
        if (greenRef.update()) {
            updateColor = true;
        }
        if (blueRef.update()) {
            updateColor = true;
        }
        if (alphaRef.update()) {
            updateColor = true;
        }
        if (updateColor) {
            boxColor.set((float) (redRef.get() / 100.0),
                    (float) (greenRef.get() / 100.0),
                    (float) (blueRef.get() / 100.0),
                    (float) (alphaRef.get() / 100.0));
        }
        
        
    }

}
