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
import com.jme3.math.Ray;
import com.jme3.math.Vector3f;
import com.jme3.niftygui.NiftyJmeDisplay;
import com.jme3.renderer.Camera;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.shape.Box;
import com.jme3.system.AppSettings;
import com.jme3.texture.FrameBuffer;
import com.jme3.texture.Image.Format;
import com.jme3.texture.Texture.MagFilter;
import com.jme3.texture.Texture.MinFilter;
import com.jme3.texture.Texture2D;
import de.lessvoid.nifty.Nifty;

public class DemoNiftyGUICube extends SimpleApplication{

    private Nifty nifty;
    int guiCursorX, guiCursorY;
    

    public static void main(String[] args){
        DemoNiftyGUICube app = new DemoNiftyGUICube();
        
        AppSettings settings;
        app.setSettings(settings = new AppSettings(true));
        settings.put("VSync", true);
        settings.put("frameRate", 30);
        app.setShowSettings(false);
        
        app.start();
        
        
    }

    public void simpleInitApp() {
        stateManager.detach(stateManager.getState(FlyCamAppState.class));
        
        
        flyCam = new GUICamera(cam);
        flyCam.setMoveSpeed(1f); // odd to set this here but it did it before
        stateManager.attach(new FlyCamAppState());
        stateManager.getState(FlyCamAppState.class).setCamera( flyCam ); 
                
                
       ViewPort niftyView = renderManager.createPreView("NiftyView", new Camera(1024, 768));
       niftyView.setClearFlags(true, true, true);
        NiftyJmeDisplay niftyDisplay = new NiftyJmeDisplay(assetManager,
                                                          inputManager,
                                                          audioRenderer,
                                                          niftyView) {
        };
        
        
        
        nifty = niftyDisplay.getNifty();
        nifty.fromXml("all/intro.xml", "start");
        nifty.addControlsWithoutStartScreen();
        
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

        final Box b = new Box(Vector3f.ZERO, 1, 1, 1);
        Geometry geom = new Geometry("Box", b);
        Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        mat.setTexture("ColorMap", tex);
        geom.setMaterial(mat);
        rootNode.attachChild(geom);

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

            
            @Override
            public void onAction(String name, boolean isPressed, float tpf) {
            
                //NiftyMouseInputEvent e = new NiftyMouseInputEvent();
                /*int mouseX = (int)(Math.random() * nifty.getCurrentScreen().getRootElement().getWidth());
                int mouseY = (int)(Math.random() * nifty.getCurrentScreen().getRootElement().getHeight());*/
                //e.initialize(guiCursorX, guiCursorY, 0, isPressed,false,false);
                //e.setButton0Release(!isPressed);
                //System.out.println(e);
                //nifty.getMouseInputEventQueue().process(e);    
                
                
                niftyDisplay.inputSys.simulateMouseButtonEvent(new MouseButtonEvent(0, isPressed, guiCursorX, guiCursorY));
                
            }
            
        }, "MouseClick");
        // Test multiple listeners per mapping
        inputManager.addListener(new AnalogListener() {

           @Override
           public void onAnalog(String name, float value, float tpf) {
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
                Vector3f p = c.getContactPoint();
                Vector3f pLocal = c.getGeometry().worldToLocal(p, new Vector3f());

                int lastX = guiCursorX;
                int lastY = guiCursorY;
                
                guiCursorX = (int)((p.x + 1.0f)/2f * nifty.getCurrentScreen().getRootElement().getWidth());
                guiCursorY = (int)((p.y + 1.0f)/2f * nifty.getCurrentScreen().getRootElement().getHeight());
                
                int dx = guiCursorX - lastX;
                int dy = guiCursorY - lastY;
                
                niftyDisplay.inputSys.simulateMouseMotionEvent(new MouseMotionEvent(guiCursorX, guiCursorY, dx, dy, 0, 0));
                
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