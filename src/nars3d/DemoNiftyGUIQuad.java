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
import com.jme3.math.Quaternion;
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.VertexBuffer;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Quad;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture2D;
import com.jme3.util.BufferUtils;
import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.elements.Element;
import java.nio.FloatBuffer;
import java.util.HashMap;
import java.util.Map;

public class DemoNiftyGUIQuad extends SimpleApplication {

    private Map<Geometry, NiftyJmeDisplay> nifty = new HashMap();
    private NiftyJmeDisplay focusedNifty = null;
    
    int guiCursorX, guiCursorY;

    public static void main(String[] args) {
        DemoNiftyGUIQuad app = new DemoNiftyGUIQuad();

        AppSettings settings;
        app.setSettings(settings = new AppSettings(true));
        settings.put("VSync", true);
        settings.put("frameRate", 30);
        //app.setShowSettings(false);

        app.start();

    }

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
        final Quad b = new Quad(1,1);
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
        stateManager.detach(stateManager.getState(FlyCamAppState.class));

        GUICamera guiCam = new GUICamera(cam);
        flyCam = guiCam;
        flyCam.setMoveSpeed(4f); // odd to set this here but it did it before
        
        
        stateManager.attach(new FlyCamAppState());
        stateManager.getState(FlyCamAppState.class).setCamera(flyCam);

        rootNode.setLocalScale(10f,10f,1f);
        
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

}
