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
package nars3d.physics;

import com.jme3.animation.AnimChannel;
import com.jme3.animation.AnimControl;
import com.jme3.animation.AnimEventListener;
import com.jme3.animation.LoopMode;
import com.jme3.asset.AssetManager;
import com.jme3.asset.TextureKey;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.KinematicRagdollControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.queue.RenderQueue.ShadowMode;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.control.UpdateControl;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.simsilica.lemur.Slider;
import nars3d.FractalApp;
import nars3d.LemurNode;

/**
 * @author normenhansen
 */
public class TestRagdollCharacter extends FractalApp implements AnimEventListener, ActionListener {
    private static Material material;

    BulletAppState bulletAppState;
    Node model;
    KinematicRagdollControl ragdoll;
    boolean leftStrafe = false, rightStrafe = false, forward = false, backward = false,
            leftRotate = false, rightRotate = false;
    AnimControl animControl;
    AnimChannel animChannel;

    public static void main(String[] args) {
        TestRagdollCharacter app = new TestRagdollCharacter();
    }

    public void simpleInitApp() {
        super.simpleInitApp();
        
        setupKeys();

        bulletAppState = new BulletAppState();
        bulletAppState.setEnabled(true);
        bulletAppState.setSpeed(0.5f);
        
        stateManager.attach(bulletAppState);


//        bulletAppState.getPhysicsSpace().enableDebug(assetManager);
        createPhysicsTestWorld(rootNode, assetManager, bulletAppState.getPhysicsSpace());
        //initWall(2,1,1);
        setupLight();

        cam.setLocation(new Vector3f(-8,0,0));
        cam.lookAt(new Vector3f(4,0,5), Vector3f.UNIT_Y);

        //model = (Node) assetManager.loadModel("Models/Sinbad/Sinbad.mesh.xml");
        
        
        float w = 0.5f;
        float h = 3f;
        box(2,-4,2,w*6,h,w);
        box(2,-4,6,w*6,h,w);
        box(0,-4,4,w,h,w*6);
        box(6,-4,4,w,h,w*6);
        
        model = (Node) assetManager.loadModel("assets/Models/Ninja/Ninja.mesh.xml");        
        model.scale(0.04f);
        
        model.lookAt(new Vector3f(0,0,-1), Vector3f.UNIT_Y);
        model.setLocalTranslation(4, 0, 4f);

        ragdoll = new KinematicRagdollControl(0.5f);
        ragdoll.setRootMass(0.01f);
        model.addControl(ragdoll);

        ragdoll.setRagdollMode();
        ragdoll.setApplyPhysicsLocal(true);
        
        for (int i = 1; i < 27; i++) {
            String jn = "Joint" + i;
            final SixDofJoint j = ragdoll.getJoint(jn);
            
            LemurNode l = new LemurNode("jg");
            l.newLabel("J" + i);
            l.newPanel(ColorRGBA.Orange, 0.25f,0.25f,0.1f);
            final Slider angleSlider = l.newSlider(-1, 1, 0);
            l.layout(50,50,1,0.5f);
            l.rotate((float)Math.PI, 0f, 0f);
            
            
            
            model.attachChild(l);

            l.addControl(new UpdateControl() {
                @Override public void update(float tpf) {
                    //System.out.println("joint" + j.getPivotA() + " " + j.getBodyA().getPhysicsLocation());
                    //l.setLocalTranslation(j.getPivotA());
                    Vector3f worldLoc = j.getBodyB().getMotionState().getWorldLocation();
                    Vector3f loc = new Vector3f();
                    model.worldToLocal(worldLoc, loc);
                    
                    
                    l.setLocalTranslation(loc);
                    
                    final float da = 0.25f;
                    j.getRotationalLimitMotor(0).setLoLimit((float)angleSlider.getModel().getValue()-da);
                    j.getRotationalLimitMotor(0).setHiLimit((float)angleSlider.getModel().getValue()+da);
                    //l.setLocalTranslation(4,0,4);
                    
                    
                }                
            });
        }
        
//        rootNode.addControl(new UpdateControl() {
//            float t = 0;
//            @Override public void update(float tpf) {
//                t += tpf;
//                
//                int j = ((int)(t/4.0))%27+1;
//                String joint = "Joint" + j;
//                float a = (float)Math.sin(t/2.0f);
//                SixDofJoint larmj = ragdoll.getJoint(joint);
//                
//                //larmj.constraint.getRigidBodyA().getCenterOfMassPosition(null)
//                //larmj.getRotationalLimitMotor(0).setEnableMotor(false);
//                //larmj.getRotationalLimitMotor(1).setMaxMotorForce(1.0f);
//                larmj.getRotationalLimitMotor(1).setLoLimit(a - 0.25f);
//                larmj.getRotationalLimitMotor(1).setHiLimit(a + 0.25f);
//                
//            }          
//        });
        
        getPhysicsSpace().add(ragdoll);
        //speed = 1.3f;

        rootNode.attachChild(model);


        AnimControl control = model.getControl(AnimControl.class);
        animChannel = control.createChannel();
        //animChannel.setAnim("IdleTop");
        control.addListener(this);

    }

    public static void createPhysicsTestWorld(Node rootNode, AssetManager assetManager, PhysicsSpace space) {
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White);
        
        rootNode.addLight(light);

        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.DarkGray);
        //material.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));

        Box floorBox = new Box(140, 0.25f, 140);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material);
        floorGeometry.setLocalTranslation(0, -5, 0);
//        Plane plane = new Plane();
//        plane.setOriginNormal(new Vector3f(0, 0.25f, 0), Vector3f.UNIT_Y);
//        floorGeometry.addControl(new RigidBodyControl(new PlaneCollisionShape(plane), 0));
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        space.add(floorGeometry);

        //movable boxes
        /*
        for (int i = 0; i < 12; i++) {
            Box box = new Box(0.25f, 0.25f, 0.25f);
            Geometry boxGeometry = new Geometry("Box", box);
            boxGeometry.setMaterial(material);
            boxGeometry.setLocalTranslation(i, 5, -3);
            //RigidBodyControl automatically uses box collision shapes when attached to single geometry with box mesh
            boxGeometry.addControl(new RigidBodyControl(2));
            rootNode.attachChild(boxGeometry);
            space.add(boxGeometry);
        }
                */

        /*
        //immovable sphere with mesh collision shape
        Sphere sphere = new Sphere(8, 8, 1);
        Geometry sphereGeometry = new Geometry("Sphere", sphere);
        sphereGeometry.setMaterial(material);
        sphereGeometry.setLocalTranslation(4, -4, 2);
        sphereGeometry.addControl(new RigidBodyControl(new MeshCollisionShape(sphere), 0));
        rootNode.attachChild(sphereGeometry);
        space.add(sphereGeometry);
*/
        
    }
    
    public void box(float x, float y, float z, float sx, float sy, float sz) {
                
                Box brick = new Box(Vector3f.ZERO, sx, sy, sz);
                
                Geometry reBoxg = new Geometry("brick", brick);
                Material m = material.clone();
                m.setColor("Color", new ColorRGBA(0,((float)Math.random())*0.25f,0.5f,0.5f));
                reBoxg.setMaterial(m);
                
                Vector3f ori = new Vector3f(x, y, z);
                reBoxg.setLocalTranslation(ori);
                reBoxg.addControl(new RigidBodyControl(0)); //motionless

                //for geometry with sphere mesh the physics system automatically uses a sphere collision shape
                //reBoxg.addControl(new RigidBodyControl(1.5f));
                //reBoxg.setShadowMode(ShadowMode.CastAndReceive);
                //reBoxg.getControl(RigidBodyControl.class).setFriction(0.6f);
                
                
                this.rootNode.attachChild(reBoxg);
                this.getPhysicsSpace().add(reBoxg);        
                
    }
            
    
    private void setupLight() {
        DirectionalLight dl = new DirectionalLight();        
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);
    }

    private PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

    private void setupKeys() {
        inputManager.addMapping("Rotate Left",
                new KeyTrigger(KeyInput.KEY_H));
        inputManager.addMapping("Rotate Right",
                new KeyTrigger(KeyInput.KEY_K));
        inputManager.addMapping("Walk Forward",
                new KeyTrigger(KeyInput.KEY_U));
        inputManager.addMapping("Walk Backward",
                new KeyTrigger(KeyInput.KEY_J));
        inputManager.addMapping("Slice",
                new KeyTrigger(KeyInput.KEY_SPACE),
                new KeyTrigger(KeyInput.KEY_RETURN));
        inputManager.addListener(this, "Strafe Left", "Strafe Right");
        inputManager.addListener(this, "Rotate Left", "Rotate Right");
        inputManager.addListener(this, "Walk Forward", "Walk Backward");
        inputManager.addListener(this, "Slice");
    }

    public void initWall(float bLength, float bWidth, float bHeight) {
        Box brick = new Box(Vector3f.ZERO, bLength, bHeight, bWidth);
        brick.scaleTextureCoordinates(new Vector2f(1f, .5f));
        Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        TextureKey key = new TextureKey("Textures/Terrain/BrickWall/BrickWall.jpg");
        key.setGenerateMips(true);
        Texture tex = assetManager.loadTexture(key);
        mat2.setTexture("ColorMap", tex);
        
        float startpt = bLength / 4;
        float height = -5;
        for (int j = 0; j < 15; j++) {
            for (int i = 0; i < 4; i++) {
                Vector3f ori = new Vector3f(i * bLength * 2 + startpt, bHeight + height, -10);
                Geometry reBoxg = new Geometry("brick", brick);
                reBoxg.setMaterial(mat2);
                reBoxg.setLocalTranslation(ori);
                //for geometry with sphere mesh the physics system automatically uses a sphere collision shape
                reBoxg.addControl(new RigidBodyControl(1.5f));
                reBoxg.setShadowMode(ShadowMode.CastAndReceive);
                reBoxg.getControl(RigidBodyControl.class).setFriction(0.6f);
                this.rootNode.attachChild(reBoxg);
                this.getPhysicsSpace().add(reBoxg);
            }
            startpt = -startpt;
            height += 2 * bHeight;
        }
    }

    public void onAnimCycleDone(AnimControl control, AnimChannel channel, String animName) {

        if (channel.getAnimationName().equals("SliceHorizontal")) {
            channel.setLoopMode(LoopMode.DontLoop);
            channel.setAnim("IdleTop", 5);
            channel.setLoopMode(LoopMode.Loop);
        }

    }

    public void onAnimChange(AnimControl control, AnimChannel channel, String animName) {
    }
    
    public void onAction(String binding, boolean value, float tpf) {
        if (binding.equals("Rotate Left")) {
            if (value) {
                leftRotate = true;
            } else {
                leftRotate = false;
            }
        } else if (binding.equals("Rotate Right")) {
            if (value) {
                rightRotate = true;
            } else {
                rightRotate = false;
            }
        } else if (binding.equals("Walk Forward")) {
            if (value) {
                forward = true;
            } else {
                forward = false;
            }
        } else if (binding.equals("Walk Backward")) {
            if (value) {
                backward = true;
            } else {
                backward = false;
            }
        } else if (binding.equals("Slice")) {
            if (value) {
                animChannel.setAnim("SliceHorizontal");
                animChannel.setSpeed(0.3f);
            }
        }
    }

    @Override
    public void simpleUpdate(float tpf) {
        if(forward){
            model.move(model.getLocalRotation().multLocal(new Vector3f(0,0,1)).multLocal(tpf));
        }else if(backward){
            model.move(model.getLocalRotation().multLocal(new Vector3f(0,0,1)).multLocal(-tpf));
        }else if(leftRotate){
            model.rotate(0, tpf, 0);
        }else if(rightRotate){
            model.rotate(0, -tpf, 0);
        }
        fpsText.setText(cam.getLocation() + "/" + cam.getRotation());
    }

}
