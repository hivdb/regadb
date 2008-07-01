package net.sf.regadb.workflow.tooltip;

import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Window;

import javax.swing.JApplet;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JInternalFrame;
import javax.swing.JPopupMenu;
import javax.swing.JToolTip;
import javax.swing.JWindow;
import javax.swing.Popup;
import javax.swing.PopupFactory;
import javax.swing.SwingUtilities;

public class ToolTip 
{
    public static JComponent insideComponent;
    static JToolTip tip;
    static boolean enabled = true;
    static Window window;
    static Popup tipWindow;
    static Rectangle popupRect;
    
    static Rectangle popupFrameRect;
    
    public static void showTipWindow(String toolTipText, int xx, int yy) {
        tip = insideComponent.createToolTip();
        window = SwingUtilities.windowForComponent(tip);

        if(insideComponent == null || !insideComponent.isShowing())
            return;
        for (Container p = insideComponent.getParent(); p != null; p = p.getParent()) {
            if (p instanceof JPopupMenu) break;
            if (p instanceof Window) {
                if (!((Window)p).isFocused()) {
                    return;
                }
                break;
            }
        }
        if (enabled) {
            Dimension size;
            Point screenLocation = insideComponent.getLocationOnScreen();
            Point location = new Point();
            Rectangle sBounds = insideComponent.getGraphicsConfiguration().
                                                getBounds();
            //!boolean leftToRight
            //!    = SwingUtilities.isLeftToRight(insideComponent);
            boolean leftToRight = true;

            // Just to be paranoid
            hideTipWindow();

            tip = insideComponent.createToolTip();
            tip.setTipText(toolTipText);
            size = tip.getPreferredSize();

            /*if(preferredLocation != null) {
                location.x = screenLocation.x + preferredLocation.x;
                location.y = screenLocation.y + preferredLocation.y;
                if (!leftToRight) {
                    location.x -= size.width;
                }
            } else {*/
                location.x = screenLocation.x + xx;
                location.y = screenLocation.y + yy + 20;
                if (!leftToRight) {
                    if(location.x - size.width>=0) {
                        location.x -= size.width;
                    }
                }

            //}

            // we do not adjust x/y when using awt.Window tips
            if (popupRect == null){
                popupRect = new Rectangle();
            }
            popupRect.setBounds(location.x,location.y,
                                size.width,size.height);

            // Fit as much of the tooltip on screen as possible
            if (location.x < sBounds.x) {
                location.x = sBounds.x;
            }
            else if (location.x - sBounds.x + size.width > sBounds.width) {
                location.x = sBounds.x + Math.max(0, sBounds.width - size.width);
            }
            if (location.y < sBounds.y) {
                location.y = sBounds.y;
            }
            else if (location.y - sBounds.y + size.height > sBounds.height) {
                location.y = sBounds.y + Math.max(0, sBounds.height - size.height);
            }

            PopupFactory popupFactory = PopupFactory.getSharedInstance();

            boolean lightWeightPopupEnabled = true;
            if (lightWeightPopupEnabled) {
                int y = getPopupFitHeight(popupRect, insideComponent);
                int x = getPopupFitWidth(popupRect,insideComponent);
                /*if (x>0 || y>0) {
                    popupFactory.setPopupType(PopupFactory.MEDIUM_WEIGHT_POPUP);
                } else {
                    popupFactory.setPopupType(PopupFactory.LIGHT_WEIGHT_POPUP);
                }*/
            }
            /*else {
                popupFactory.setPopupType(PopupFactory.MEDIUM_WEIGHT_POPUP);
            }*/
            tipWindow = popupFactory.getPopup(insideComponent, tip,
                                              location.x,
                                              location.y);
            //popupFactory.setPopupType(PopupFactory.LIGHT_WEIGHT_POPUP);

            tipWindow.show();

            Window componentWindow = SwingUtilities.windowForComponent(
                                                    insideComponent);

            window = SwingUtilities.windowForComponent(tip);
            /*if (window != null && window != componentWindow) {
                window.addMouseListener(this);
            }
            else {
                window = null;
            }*/

            //insideTimer.start();
            //tipShowing = true;
        }
    }
    


    public static void hideTipWindow() {
        if (tipWindow != null) {
            if (window != null) {
                //window.removeMouseListener(this);
                window = null;
            }
            tipWindow.hide();
            tipWindow = null;
            //tipShowing = false;
            (tip.getUI()).uninstallUI(tip);
            tip = null;
            //insideTimer.stop();
        }
    }
    
    private static int getPopupFitWidth(Rectangle popupRectInScreen, Component invoker){
        if (invoker != null){
          Container parent;
          for (parent = invoker.getParent(); parent != null; parent = parent.getParent()){
            // fix internal frame size bug: 4139087 - 4159012
            if(parent instanceof JFrame || parent instanceof JDialog ||
               parent instanceof JWindow) { // no check for awt.Frame since we use Heavy tips
              return getWidthAdjust(parent.getBounds(),popupRectInScreen);
            } else if (parent instanceof JApplet || parent instanceof JInternalFrame) {
              if (popupFrameRect == null){
                popupFrameRect = new Rectangle();
              }
              Point p = parent.getLocationOnScreen();
              popupFrameRect.setBounds(p.x,p.y,
                                       parent.getBounds().width,
                                       parent.getBounds().height);
              return getWidthAdjust(popupFrameRect,popupRectInScreen);
            }
          }
        }
        return 0;
      }


    // Returns:  0 no adjust
    //          >0 adjust by value return
    private static int getPopupFitHeight(Rectangle popupRectInScreen, Component invoker){
      if (invoker != null){
        Container parent;
        for (parent = invoker.getParent(); parent != null; parent = parent.getParent()){
          if(parent instanceof JFrame || parent instanceof JDialog ||
             parent instanceof JWindow) {
            return getHeightAdjust(parent.getBounds(),popupRectInScreen);
          } else if (parent instanceof JApplet || parent instanceof JInternalFrame) {
            if (popupFrameRect == null){
              popupFrameRect = new Rectangle();
            }
            Point p = parent.getLocationOnScreen();
            popupFrameRect.setBounds(p.x,p.y,
                                     parent.getBounds().width,
                                     parent.getBounds().height);
            return getHeightAdjust(popupFrameRect,popupRectInScreen);
          }
        }
      }
      return 0;
    }

    private static int getHeightAdjust(Rectangle a, Rectangle b){
      if (b.y >= a.y && (b.y + b.height) <= (a.y + a.height))
        return 0;
      else
        return (((b.y + b.height) - (a.y + a.height)) + 5);
    }

    // Return the number of pixels over the edge we are extending.
    // If we are over the edge the ToolTipManager can adjust.
    // REMIND: what if the Tooltip is just too big to fit at all - we currently will just clip
    private static int getWidthAdjust(Rectangle a, Rectangle b){
      //    System.out.println("width b.x/b.width: " + b.x + "/" + b.width +
      //                 "a.x/a.width: " + a.x + "/" + a.width);
      if (b.x >= a.x && (b.x + b.width) <= (a.x + a.width)){
        return 0;
      }
      else {
        return (((b.x + b.width) - (a.x +a.width)) + 5);
      }
    }


}
