/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d.robot;

import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.joints.SixDofJoint;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector3f;
import com.jme3.scene.Geometry;
import com.jme3.scene.Node;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.UpdateControl;
import com.jme3.scene.shape.Box;
import com.simsilica.lemur.Slider;
import java.util.List;
import nars3d.FractalPhysicsApp;
import nars3d.LemurNode;
import nars3d.plot.SurfacePlot;
import syncleus.dann.data.vector.VectorData;
import syncleus.dann.math.Sigmoids;
import syncleus.dann.math.matrix.SimpleRealMatrix;
import syncleus.dann.neural.spiking.SpikingNeuralNetwork;
import syncleus.dann.neural.spiking.SpikingNeuron;
import syncleus.dann.neural.spiking.subnetworks.SimpleRecurrentNetwork;

/**
 *
 * @author me
 */
public class RobotArm1 extends FractalPhysicsApp {
    private Material m;
    private SixDofJoint baseRotate;
    final float halfpi = ((float)Math.PI)/2f;
    private SixDofJoint uarmRotate;
    private SixDofJoint larmRotate;
    

     private Node createRagDoll() {
        m = material.clone();
        m.setColor("Color", new ColorRGBA(0,((float)Math.random())*0.25f,0.9f,0.8f));

        Node ragDoll = new Node();

        Spatial swivel = createLimb(0.5f, 0.5f, 0.1f, new Vector3f(0.5f, 0.4f, 0.5f), 6f);
        Spatial uArmL = createLimb(0.2f, 0.2f, 1.2f, new Vector3f(0.5f, 1.4f, 0.5f), 3f);
        Spatial lArmL = createLimb(0.15f, 0.15f, 0.8f, new Vector3f(0.5f, 2.4f, 0.5f), 1.5f);
        Spatial base = createBase(1f, 0.1f, 1f, new Vector3f(0.00f, 1f, 0), true);

        ragDoll.attachChild(swivel);
        ragDoll.attachChild(base);
        ragDoll.attachChild(uArmL);
        ragDoll.attachChild(lArmL);

        
        Vector3f pivotA = new Vector3f(0,0.05f,0);
        Vector3f pivotB = new Vector3f(0,-0.05f,0);
        baseRotate = new SixDofJoint(base.getControl(RigidBodyControl.class), swivel.getControl(RigidBodyControl.class), pivotA, pivotB, true);
        baseRotate.setCollisionBetweenLinkedBodys(false);
        
        Vector3f pivotC = new Vector3f(0,0.11f,0);
        Vector3f pivotD = new Vector3f(0,-1.2f,0);
        uarmRotate = new SixDofJoint(swivel.getControl(RigidBodyControl.class), uArmL.getControl(RigidBodyControl.class), pivotC, pivotD, true);
        uarmRotate.setCollisionBetweenLinkedBodys(false);
        
        Vector3f pivotE = new Vector3f(0.15f,0.8f,0);
        Vector3f pivotF = new Vector3f(-0.15f,-0.8f,0);
        larmRotate = new SixDofJoint(uArmL.getControl(RigidBodyControl.class), lArmL.getControl(RigidBodyControl.class), pivotE, pivotF, true);
        larmRotate.setCollisionBetweenLinkedBodys(false);
        
        
        return ragDoll;
    }

    private Spatial createLimb(float width, float height, float length, Vector3f location, float mass) {
        //Cylinder c= new Cylinder(4, 6, width, height, true);
        Box b= new Box(width, length, height);
        
        
        Geometry node = new Geometry("Limb", b);
        node.setMaterial(m);
        //node.rotate(halfpi/4, halfpi/2, halfpi);
                
        RigidBodyControl rigidBodyControl = new RigidBodyControl(mass);
        
        
        node.setLocalTranslation(location);
        node.addControl(rigidBodyControl);
        return node;
    }

    private Spatial createBase(float width, float height, float depth, Vector3f location, boolean fixed) {
        Box b= new Box(width, height, depth);
        
        Geometry node = new Geometry("Base", b);
        node.setMaterial(m);
                        
        RigidBodyControl rigidBodyControl = new RigidBodyControl(fixed ? 0 : 1.0f);
        node.setLocalTranslation(location);
        node.addControl(rigidBodyControl);
        return node;
    }


    public void controlMotor(SixDofJoint m, double min1, double max1, double min2, double max2, double min3, double max3) {
        m.getRotationalLimitMotor(0).setLoLimit((float)min1);
        m.getRotationalLimitMotor(0).setHiLimit((float)max1);
        m.getRotationalLimitMotor(0).setEnableMotor(true);
        m.getRotationalLimitMotor(0).setBounce(0);
        
        m.getRotationalLimitMotor(1).setLoLimit((float)min2);
        m.getRotationalLimitMotor(1).setHiLimit((float)max2);
        m.getRotationalLimitMotor(1).setEnableMotor(true);
        m.getRotationalLimitMotor(1).setBounce(0);
        
        
        m.getRotationalLimitMotor(2).setLoLimit((float)min3);
        m.getRotationalLimitMotor(2).setHiLimit((float)max3);                 
        m.getRotationalLimitMotor(2).setEnableMotor(true);         
        m.getRotationalLimitMotor(2).setBounce(0);
    }
    
    @Override
    public void initWorld() {
        getPhysicsSpace().enableDebug(assetManager);

        cam.setLocation(new Vector3f(6,4,2));
        cam.lookAt(new Vector3f(), new Vector3f(0,1,0));
         //box(0,7,0,1,1,1, 0.5f);
        
        LemurNode l = new LemurNode("a");
        l.newLabel("Motors");
        final Slider baseMotorSlider = l.newSlider(-0.95f, 0.95f, 0);                
        final Slider uarmSlider = l.newSlider(-0.95f, 0.95f, 0);
        final Slider larmSlider = l.newSlider(-0.95f, 0.95f, 0);
        l.layout(4,4,0.25f,1);
        
        l.enableDrag();
        
        Node n = new Node();
        n.attachChild(l);
        n.setLocalTranslation(1, 3, -1);
        n.setLocalScale(0.01f,0.01f,0.01f);
        rootNode.attachChild(n);
        
        
        
        Node ragDoll = createRagDoll();
        rootNode.attachChild(ragDoll);
        bulletAppState.getPhysicsSpace().addAll(ragDoll);
        
        
        
        
        SpikingNeuralNetwork net = new SpikingNeuralNetwork();

        SimpleRecurrentNetwork h = new SimpleRecurrentNetwork(net, 9, 7, 2);
        
        int historyLength = 64;
        final SimpleRealMatrix mm = new SimpleRealMatrix(3*3+h.getFlatNeuronList().size(), historyLength);
        final SurfacePlot plot = new SurfacePlot(mm, material);
        
        plot.scale(0.2f, 0.2f, 1.0f);
        plot.rotate((float)-Math.PI/2f, 0, 0);
        plot.move(-2, 1, -1);
        
        rootNode.attachChild(plot);
        

        
        /*
        Hopfield h = new Hopfield(net, 16);
        inputs.addAll(h.getInputNeurons());
        h.randomize();
        */
        
        /*
        FeedForward h2 = new FeedForward(net, new int[] { 4, 3, 3, 2}, new Point2D.Double());
        inputs.addAll(h2.getInputNeurons());
                */
        
        
        net.randomizeNeurons();
        net.randomizeWeights();


        
        rootNode.addControl(new UpdateControl() {

            public double jointDist(SixDofJoint s, int dim) {
                Vector3f posB = s.getBodyB().getMotionState().getWorldLocation();
                Vector3f posA = s.getBodyA().getMotionState().getWorldLocation();
                float sensitivity = 1f;
                float v = 0;
                if (dim == 0)
                    v = (posB.x - posA.x);
                else if (dim == 1)
                    v = (posB.y - posA.y);
                else if (dim == 2)
                    v = (posB.z - posA.z);
                
                return Sigmoids.sigmoidBiFast(v * sensitivity);
                
            }
            
            float t = 0;
             @Override
             public void update(float tpf) {
                 
                final float tolerance = 0.2f;
                 
                float a = (float)baseMotorSlider.getModel().getValue();
                float b = (float)uarmSlider.getModel().getValue();
                float c = (float)larmSlider.getModel().getValue();
                controlMotor(baseRotate, 0,0,a-tolerance,a+tolerance,0,0);
                controlMotor(uarmRotate, 0,0,0,0,b-tolerance,b+tolerance);
                controlMotor(larmRotate, c-tolerance,c+tolerance,0,0,0,0);
                baseRotate.getBodyA().activate();;
                baseRotate.getBodyB().activate();;
                 
                
                
                 t += tpf;
                 
                 VectorData vv = new VectorData((int)9);
                 vv.setData(0, jointDist(baseRotate, 0));
                 vv.setData(1, jointDist(baseRotate, 1));
                 vv.setData(2, jointDist(baseRotate, 2));
                 vv.setData(3, jointDist(uarmRotate, 0));
                 vv.setData(4, jointDist(uarmRotate, 1));
                 vv.setData(5, jointDist(uarmRotate, 2));
                 vv.setData(6, jointDist(larmRotate, 0));
                 vv.setData(7, jointDist(larmRotate, 1));
                 vv.setData(8, jointDist(larmRotate, 2));
                 
                 mm.shiftColUp();
                 for (int i = 0; i < 9; i++) {
                     mm.set(i, 0, vv.getData(i));
                 }
                 
                 net.setInput(vv);
                 
    
                net.update();
                
                List<SpikingNeuron> nn = net.getFlatNeuronList();
                
                
                 
                int i = 9;
                for (SpikingNeuron N : nn) {
                    mm.set(i++, 0, N.getActivation());
                    if (i == mm.getRows())
                        break;
                }
                 plot.update();
             }
        
        });
    }

    
    public static void main(String[] args) {
        new RobotArm1();
    }
    
//     private Node createRagDoll() {
//         Node ragDoll = new Node();
//         Node shoulders = createLimb(0.2f, 1.0f, new Vector3f(0.00f, 1.5f, 0), true);
//        Node uArmL = createLimb(0.2f, 0.5f, new Vector3f(-0.75f, 0.8f, 0), false);
//        Node uArmR = createLimb(0.2f, 0.5f, new Vector3f(0.75f, 0.8f, 0), false);
//        Node lArmL = createLimb(0.2f, 0.5f, new Vector3f(-0.75f, -0.2f, 0), false);
//        Node lArmR = createLimb(0.2f, 0.5f, new Vector3f(0.75f, -0.2f, 0), false);
//        Node body = createLimb(0.2f, 1.0f, new Vector3f(0.00f, 0.5f, 0), false);
//        Node hips = createLimb(0.2f, 0.5f, new Vector3f(0.00f, -0.5f, 0), true);
//        Node uLegL = createLimb(0.2f, 0.5f, new Vector3f(-0.25f, -1.2f, 0), false);
//        Node uLegR = createLimb(0.2f, 0.5f, new Vector3f(0.25f, -1.2f, 0), false);
//        Node lLegL = createLimb(0.2f, 0.5f, new Vector3f(-0.25f, -2.2f, 0), false);
//        Node lLegR = createLimb(0.2f, 0.5f, new Vector3f(0.25f, -2.2f, 0), false);
//
//        join(body, shoulders, new Vector3f(0f, 1.4f, 0));
//        join(body, hips, new Vector3f(0f, -0.5f, 0));
//
//        join(uArmL, shoulders, new Vector3f(-0.75f, 1.4f, 0));
//        join(uArmR, shoulders, new Vector3f(0.75f, 1.4f, 0));
//        join(uArmL, lArmL, new Vector3f(-0.75f, .4f, 0));
//        join(uArmR, lArmR, new Vector3f(0.75f, .4f, 0));
//
//        join(uLegL, hips, new Vector3f(-.25f, -0.5f, 0));
//        join(uLegR, hips, new Vector3f(.25f, -0.5f, 0));
//        join(uLegL, lLegL, new Vector3f(-.25f, -1.7f, 0));
//        join(uLegR, lLegR, new Vector3f(.25f, -1.7f, 0));
//
//        ragDoll.attachChild(shoulders);
//        ragDoll.attachChild(body);
//        ragDoll.attachChild(hips);
//        ragDoll.attachChild(uArmL);
//        ragDoll.attachChild(uArmR);
//        ragDoll.attachChild(lArmL);
//        ragDoll.attachChild(lArmR);
//        ragDoll.attachChild(uLegL);
//        ragDoll.attachChild(uLegR);
//        ragDoll.attachChild(lLegL);
//        ragDoll.attachChild(lLegR);
//
//        return ragDoll;
//    }
    
}
