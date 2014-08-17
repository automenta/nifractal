/*
 * $Id: CursorButtonEvent.java 1295 2013-12-28 17:24:41Z PSpeed42@gmail.com $
 *
 * Copyright (c) 2012-2012 jMonkeyEngine
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

package com.simsilica.lemur.event;

import com.jme3.collision.CollisionResult;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Spatial;


/**
 *  Contains information about a 'cursor button' event over a particular
 *  spatial.
 *
 *  @author    Paul Speed
 */
public class CursorButtonEvent extends AbstractCursorEvent {

    private int buttonIndex;
    private boolean pressed;

    public CursorButtonEvent( int buttonIndex, boolean pressed,
                              ViewPort view, Spatial target, float x, float y, 
                              CollisionResult collision ) {
        super(view, target, x, y, collision);
        this.buttonIndex = buttonIndex;
        this.pressed = pressed;                              
    }
    
    public int getButtonIndex() {
        return buttonIndex;
    }
    
    public boolean isPressed() {
        return pressed;
    }
    
    @Override
    public String toString() {
        return getClass().getSimpleName() + "[buttonIndex=" + buttonIndex + ", pressed=" + pressed + ", " + parmsToString() + "]";
    }    
}


