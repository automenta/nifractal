package nars3d;

import com.jme3.app.FlyCamAppState;
import com.jme3.app.SimpleApplication;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.system.AppSettings;
import com.simsilica.lemur.Checkbox;
import com.simsilica.lemur.Container;
import com.simsilica.lemur.DefaultRangedValueModel;
import com.simsilica.lemur.GuiGlobals;
import com.simsilica.lemur.Label;
import com.simsilica.lemur.Panel;
import com.simsilica.lemur.Slider;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.event.CursorEventControl;
import com.simsilica.lemur.event.DragHandler;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.Styles;

abstract public class FractalApp extends SimpleApplication {

    private GUICamera guiCam;
    
    

    /*
    public static void main(String[] args) {
        FractalApp app = new FractalApp();


    }
    */
    


    public FractalApp() {
        super();
        AppSettings settings;
        setSettings(settings = new AppSettings(true));
        settings.put("Width", 1024);
        settings.put("Height", 800);
        settings.put("VSync", true);
        settings.put("frameRate", 30);
        setShowSettings(false);
        start();

    }

    public void simpleInitApp() {
        GuiGlobals.initialize(this);
        
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

        guiCam = new GUICamera(cam);
        flyCam = guiCam;
        flyCam.setMoveSpeed(4f); // odd to set this here but it did it before
        
        
        stateManager.attach(new FlyCamAppState());
        stateManager.getState(FlyCamAppState.class).setCamera(flyCam);
                
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


    }
        private static int counter = 0;

    public static class LemurNode extends Container {

        
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
            setLocalScale(getLocalScale().normalize().mult(scale));
            
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
//
//        // Adding components returns the component so we can set other things
//        // if we want.
//        Checkbox temp = panel.addChild(new Checkbox("Show Stats"));
//        temp.setChecked(true);
//        showStatsRef = temp.getModel().createReference();
//
//        temp = panel.addChild(new Checkbox("Show FPS"));
//        temp.setChecked(true);
//        showFpsRef = temp.getModel().createReference();
//
//        // Custom "spacer" element type
//        hudPanel.addChild(new Panel(10f, 10f, new ElementId("spacer"), "glass"));
//
//        // Create a second panel in the same overall HUD panel
//        // that lets us tweak things about the cube.
//        panel = new Container("glass");
//        panel.setBackground(new QuadBackgroundComponent(new ColorRGBA(0, 0.5f, 0.5f, 0.5f), 5, 5, 0.02f, false));
//        // Custom "header" element type.
//        panel.addChild(new Label("Cube Settings", new ElementId("header"), "glass"));
//        panel.addChild(new Panel(2, 2, ColorRGBA.Cyan, "glass")).setUserData(LayerComparator.LAYER, 2);
//        Label redLabel = panel.addChild(new Label("Red:"));
//        redLabel.setLocalTranslation(0, 0, 5);
//        
//        redRef = panel.addChild(new Slider("glass")).getModel().createReference();
//        
//        panel.addChild(new Label("Green:"));
//        greenRef = panel.addChild(new Slider("glass")).getModel().createReference();
//        panel.addChild(new Label("Blue:"));
//        blueRef = panel.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100), "glass")).getModel().createReference();
//        panel.addChild(new Label("Alpha:"));
//        alphaRef = panel.addChild(new Slider(new DefaultRangedValueModel(0, 100, 100), "glass")).getModel().createReference();
//
//        hudPanel.addChild(panel);
//        
//        /*
//        rootNode.addControl(new UpdateControl() {
//            
//            @Override protected void controlUpdate(float tpf) {
//                hudPanel.rotate(0, 0.01f, 0);
//                
//            }
//        }); 
//                */
//
//        // Increase the default size of the hud to be a little wider
//        // if it would otherwise be smaller.  Height is unaffected.
//        Vector3f hudSize = new Vector3f(20, 1, 1);
//        hudSize.maxLocal(hudPanel.getPreferredSize());        
//        hudPanel.setPreferredSize(hudSize);
//        
//        hudPanel.scale(0.01f);
//        
//        
//
//        // Note: after next nightly, this will also work:
//        // hudPanel.setPreferredSize( new Vector3f(200,0,0).maxLocal(hudPanel.getPreferredSize()) );
//        // Something in scene
//        Box box = new Box(Vector3f.ZERO, 1, 1, 1);
//        Geometry geom = new Geometry("Box", box);
//        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
//        mat.setColor("Color", boxColor);
//        mat.getAdditionalRenderState().setBlendMode(BlendMode.Alpha);
//        geom.setMaterial(mat);
//        rootNode.attachChild(geom);
//
//        // A draggable bordered panel
//        Container testPanel = new Container();
//        testPanel.setPreferredSize(new Vector3f(200, 200, 0));
//        testPanel.setBackground(TbtQuadBackgroundComponent.create("/com/simsilica/lemur/icons/border.png",           1, 2, 2, 3, 3, 0, false));
//        Label test = testPanel.addChild(new Label("Border Test"));
//
//        // Center the text in the box.
//        test.setInsetsComponent(new DynamicInsetsComponent(0.5f, 0.5f, 0.5f, 0.5f));
//        testPanel.setLocalTranslation(400, 400, 0);
//
//        CursorEventControl.addListenersToSpatial(testPanel, new DragHandler());
//        guiNode.attachChild(testPanel);
//    }
//
//    @Override
//    public void simpleUpdate(float tpf) {
//        if (showStatsRef.update()) {
//            setDisplayStatView(showStatsRef.get());
//        }
//        if (showFpsRef.update()) {
//            setDisplayFps(showFpsRef.get());
//        }
//
//        boolean updateColor = false;
//        if (redRef.update()) {
//            updateColor = true;
//        }
//        if (greenRef.update()) {
//            updateColor = true;
//        }
//        if (blueRef.update()) {
//            updateColor = true;
//        }
//        if (alphaRef.update()) {
//            updateColor = true;
//        }
//        if (updateColor) {
//            boxColor.set((float) (redRef.get() / 100.0),
//                    (float) (greenRef.get() / 100.0),
//                    (float) (blueRef.get() / 100.0),
//                    (float) (alphaRef.get() / 100.0));
//        }
//        
//        
//    }

}
