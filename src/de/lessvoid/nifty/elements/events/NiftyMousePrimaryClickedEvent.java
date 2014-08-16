package de.lessvoid.nifty.elements.events;

import de.lessvoid.nifty.elements.Element;
import de.lessvoid.nifty.input.NiftyMouseInputEvent;

public class NiftyMousePrimaryClickedEvent extends NiftyMouseBaseEvent {
  public NiftyMousePrimaryClickedEvent(final Element element) {
    super(element);
  }

  public NiftyMousePrimaryClickedEvent(final Element element, final NiftyMouseInputEvent mouseEvent) {
    super(element, mouseEvent);
  }
}
