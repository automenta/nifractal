/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package nars3d;

import com.jme3.input.FlyByCamera;
import com.jme3.input.InputManager;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.math.Vector3f;
import com.jme3.renderer.Camera;

/**
 *
 * @author me
 */
public class GUICamera extends FlyByCamera {

    public GUICamera(Camera cam) {
        super(cam);
        setRotationSpeed(2f);
    }

    @Override
    public void setEnabled(boolean enable) {
        if (inputManager != null) {
            inputManager.setCursorVisible(true);
            canRotate = false;
        }
    }

    @Override
    public void onAnalog(String name, float value, float tpf) {
        super.onAnalog(name, value, tpf);
    }

    @Override
    public void onAction(String name, boolean value, float tpf) {
        if (!enabled) {
            return;
        }
        //left button
        if (name.equals("LookMode")) {
            canRotate = value;
            inputManager.setCursorVisible(!value);
        }
    }

    @Override
    protected void rotateCamera(float value, Vector3f axis) {
        if (!canRotate) {
            return;
        }
        super.rotateCamera(value, axis);
    }

    @Override
    public void registerWithInput(InputManager inputManager) {
        inputManager.addMapping("LookMode", new MouseButtonTrigger(MouseInput.BUTTON_RIGHT));
        super.registerWithInput(inputManager);
        inputManager.addListener(this, "LookMode");
        inputManager.setCursorVisible(true);
    }
    
}
