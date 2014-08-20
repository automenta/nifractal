/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d;

import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.PhysicsSpace;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.shape.Box;

/**
 *
 * @author me
 */
abstract public class FractalPhysicsApp extends FractalApp {
    public Material material;
    public BulletAppState bulletAppState;

    @Override
    public void simpleInitApp() {
        super.simpleInitApp(); 
                

        bulletAppState = new BulletAppState();
        bulletAppState.setEnabled(true);
        bulletAppState.setSpeed(0.5f);
        
        stateManager.attach(bulletAppState);
        
        
        AmbientLight light = new AmbientLight();
        light.setColor(ColorRGBA.White);
        
        rootNode.addLight(light);

        material = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
        material.setColor("Color", ColorRGBA.DarkGray);
        //material.setTexture("ColorMap", assetManager.loadTexture("Interface/Logo/Monkey.jpg"));

        Box floorBox = new Box(140, 0.25f, 140);
        Geometry floorGeometry = new Geometry("Floor", floorBox);
        floorGeometry.setMaterial(material);
        floorGeometry.setLocalTranslation(0, 0, 0);
//        Plane plane = new Plane();
//        plane.setOriginNormal(new Vector3f(0, 0.25f, 0), Vector3f.UNIT_Y);
//        floorGeometry.addControl(new RigidBodyControl(new PlaneCollisionShape(plane), 0));
        floorGeometry.addControl(new RigidBodyControl(0));
        rootNode.attachChild(floorGeometry);
        getPhysicsSpace().add(floorGeometry);

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
        
      //initWall(2,1,1);
        setupLight();

        cam.setLocation(new Vector3f(-8,6,0));
        cam.lookAt(new Vector3f(4,0,5), Vector3f.UNIT_Y);

        //model = (Node) assetManager        
        
        initWorld();
    }
    
    abstract public void initWorld();
    
   private void setupLight() {
        DirectionalLight dl = new DirectionalLight();        
        dl.setDirection(new Vector3f(-0.1f, -0.7f, -1).normalizeLocal());
        dl.setColor(new ColorRGBA(1f, 1f, 1f, 1.0f));
        rootNode.addLight(dl);
    }

    public PhysicsSpace getPhysicsSpace() {
        return bulletAppState.getPhysicsSpace();
    }

        public void box(float x, float y, float z, float sx, float sy, float sz, float mass) {
                
                Box brick = new Box(Vector3f.ZERO, sx, sy, sz);
                
                Geometry reBoxg = new Geometry("brick", brick);
                Material m = material.clone();
                m.setColor("Color", new ColorRGBA(0,((float)Math.random())*0.25f,0.5f,0.5f));
                reBoxg.setMaterial(m);
                
                Vector3f ori = new Vector3f(x, y, z);
                reBoxg.setLocalTranslation(ori);
                reBoxg.addControl(new RigidBodyControl(mass));

                //for geometry with sphere mesh the physics system automatically uses a sphere collision shape
                //reBoxg.addControl(new RigidBodyControl(1.5f));
                //reBoxg.setShadowMode(ShadowMode.CastAndReceive);
                //reBoxg.getControl(RigidBodyControl.class).setFriction(0.6f);
                
                
                addPhysical(reBoxg);
                
    }
        
        public void addPhysical(Spatial n) {
            rootNode.attachChild(n);
            getPhysicsSpace().add(n);
        }
            
}
