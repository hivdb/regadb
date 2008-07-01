package net.sf.regadb.install.generateConfigFile;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;

import javax.swing.JLabel;

public class DialogSeparator extends JLabel {
    public static final int OFFSET = 15;

    public DialogSeparator() {
    }

    public DialogSeparator(String text) {
      super(text);
    }

    public Dimension getPreferredSize() {
      return new Dimension(getParent().getWidth(), 20);
    }

    public Dimension getMinimumSize() {
      return getPreferredSize();
    }

    public Dimension getMaximumSize() {
      return getPreferredSize();
    }

    public void paint(Graphics g) {
      g.setColor(getBackground());
      g.fillRect(0, 0, getWidth(), getHeight());

      Dimension d = getSize();
      int y = (d.height - 3) / 2;
      g.setColor(Color.white);
      g.drawLine(1, y, d.width - 1, y);
      y++;
      g.drawLine(0, y, 1, y);
      g.setColor(Color.gray);
      g.drawLine(d.width - 1, y, d.width, y);
      y++;
      g.drawLine(1, y, d.width - 1, y);

      String text = getText();
      if (text.length() == 0)
        return;

      g.setFont(getFont());
      FontMetrics fm = g.getFontMetrics();
      y = (d.height + fm.getAscent()) / 2;
      int fontWidth = fm.stringWidth(text);

      g.setColor(getBackground());
      g.fillRect(OFFSET - 5, 0, OFFSET + fontWidth, d.height);

      g.setColor(getForeground());
      g.drawString(text, OFFSET, y);
    }
}
