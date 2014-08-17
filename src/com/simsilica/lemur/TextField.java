/*
 * $Id: TextField.java 1242 2013-12-01 05:11:21Z PSpeed42@gmail.com $
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

package com.simsilica.lemur;

import com.simsilica.lemur.style.StyleDefaults;
import com.simsilica.lemur.style.Attributes;
import com.simsilica.lemur.style.ElementId;
import com.simsilica.lemur.style.StyleAttribute;
import com.simsilica.lemur.style.Styles;
import com.simsilica.lemur.event.KeyActionListener;
import com.simsilica.lemur.event.KeyAction;
import com.simsilica.lemur.event.FocusMouseListener;
import com.simsilica.lemur.event.MouseEventControl;
import com.simsilica.lemur.component.QuadBackgroundComponent;
import com.simsilica.lemur.component.TextEntryComponent;
import java.util.Map;

import com.jme3.font.BitmapFont;
import com.jme3.math.ColorRGBA;

import com.simsilica.lemur.core.GuiControl;


/**
 *  A GUI element allowing text entry.
 *
 *  @author    Paul Speed
 */
public class TextField extends Panel {

    public static final String ELEMENT_ID = "textField";

    public static final String KEY_TEXT = "text";

    private TextEntryComponent text;

    public TextField( String text ) {
        this(new DocumentModel(text), true, new ElementId(ELEMENT_ID), null);
    }

    public TextField( String text, String style ) {
        this(new DocumentModel(text), true, new ElementId(ELEMENT_ID), style);
    }

    public TextField( String text, ElementId elementId, String style ) {
        this(new DocumentModel(text), true, elementId, style);
    }

    public TextField( DocumentModel model, String style ) {
        this(model, true, new ElementId(ELEMENT_ID), style);
    }

    protected TextField( DocumentModel model, boolean applyStyles, ElementId elementId, String style ) {
        super(false, elementId, style);

        Styles styles = GuiGlobals.getInstance().getStyles();
        BitmapFont font = styles.getAttributes(elementId.getId(), style).get("font", BitmapFont.class);
        this.text = new TextEntryComponent(model, font);
        getControl(GuiControl.class).addComponent(KEY_TEXT, text);

        addControl(new MouseEventControl(FocusMouseListener.INSTANCE));

        if( applyStyles ) {
            styles.applyStyles(this, elementId.getId(), style);
        }
    }

    @StyleDefaults(ELEMENT_ID)
    public static void initializeDefaultStyles( Attributes attrs ) {
        attrs.set("background", new QuadBackgroundComponent(new ColorRGBA(0,0,0,1)), false);
        attrs.set("singleLine", true);
    }

    public Map<KeyAction,KeyActionListener> getActionMap() {
        return text.getActionMap();
    }

    public DocumentModel getDocumentModel() {
        return text.getDocumentModel();
    }

    @StyleAttribute(value="text", lookupDefault=false)
    public void setText( String s ) {
        text.setText(s);
    }

    public String getText() {
        return text == null ? null : text.getText();
    }

    @StyleAttribute(value="textVAlignment", lookupDefault=false)
    public void setTextVAlignment( VAlignment a ) {
        text.setVAlignment(a);
    }

    public VAlignment getTextVAlignment() {
        return text.getVAlignment();
    }

    @StyleAttribute(value="textHAlignment", lookupDefault=false)
    public void setTextHAlignment( HAlignment a ) {
        text.setHAlignment(a);
    }

    public HAlignment getTextHAlignment() {
        return text.getHAlignment();
    }

    @StyleAttribute("font")
    public void setFont( BitmapFont f ) {
        text.setFont(f);
    }

    public BitmapFont getFont() {
        return text.getFont();
    }

    @StyleAttribute("color")
    public void setColor( ColorRGBA color ) {
        text.setColor(color);
    }

    public ColorRGBA getColor() {
        return text == null ? null : text.getColor();
    }

    @StyleAttribute("fontSize")
    public void setFontSize( float f ) {
        text.setFontSize(f);
    }

    public float getFontSize() {
        return text == null ? 0 : text.getFontSize();
    }

    @StyleAttribute("singleLine")
    public void setSingleLine( boolean f ) {
        text.setSingleLine(f);
    }

    public boolean isSingleLine() {
        return text.isSingleLine();
    }

    @StyleAttribute("preferredWidth")
    public void setPreferredWidth( float f ) {
        text.setPreferredWidth(f);
    }

    public float getPreferredWidth() {
        return text.getPreferredWidth();
    }

    @Override
    public String toString() {
        return getClass().getName() + "[text=" + getText() + ", color=" + getColor() + ", elementId=" + getElementId() + "]";
    }
}

