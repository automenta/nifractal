package de.lessvoid.nifty.effects.impl;


import de.lessvoid.nifty.Nifty;
import de.lessvoid.nifty.effects.EffectImpl;
import de.lessvoid.nifty.effects.EffectProperties;
import de.lessvoid.nifty.effects.Falloff;
import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.render.NiftyRenderEngine;
import de.lessvoid.nifty.spi.render.RenderFont;

/**
 * ChangeFont.
 * @author void
 */
public class ChangeFont implements EffectImpl {
  private RenderFont font;

  public void activate(final Nifty nifty, final Element element, final EffectProperties parameter) {
    font = nifty.getRenderEngine().createFont(parameter.getProperty("font"));
  }

  public void execute(
      final Element element,
      final float normalizedTime,
      final Falloff falloff,
      final NiftyRenderEngine r) {
    r.setFont(font);
  }

  public void deactivate() {
  }
}
