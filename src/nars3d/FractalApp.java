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


}
