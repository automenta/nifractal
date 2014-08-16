package de.lessvoid.nifty.elements.events;

import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;

public class NiftyMouseSecondaryClickedEvent extends NiftyMouseBaseEvent {
  public NiftyMouseSecondaryClickedEvent(final Element element) {
    super(element);
  }

  public NiftyMouseSecondaryClickedEvent(final Element element, final NiftyMouseInputEvent mouseEvent) {
    super(element, mouseEvent);
  }
}
