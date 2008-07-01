package net.sf.regadb.ui.form.singlePatient.chart;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Ellipse2D;


public class SymbolDrawer implements VectorGraphicsConstants
{
    public static void drawSymbol(int x, int y, int size, int symbol, Graphics2D graphics) {
        drawSymbol((double) x, (double) y, (double) size, symbol, graphics);
    }

    public static void fillSymbol(int x, int y, int size, int symbol, Graphics2D graphics) {
        fillSymbol((double) x, (double) y, (double) size, symbol, graphics);
    }

    public static void fillAndDrawSymbol(int x, int y, int size, int symbol,
            Color fillColor, Graphics2D graphics) {
        fillAndDrawSymbol((double) x, (double) y, (double) size, symbol,
                fillColor, graphics);
    }

    public static SymbolShape cachedShape = new SymbolShape();
    
    protected static void drawSymbol(double x, double y,
            double size, int symbol, Graphics2D g) {
        if (size <= 0)
            return;
        
        switch (symbol) {
        case SYMBOL_VLINE:
        case SYMBOL_STAR:
        case SYMBOL_HLINE:
        case SYMBOL_PLUS:
        case SYMBOL_CROSS:
        case SYMBOL_BOX:
        case SYMBOL_UP_TRIANGLE:
        case SYMBOL_DN_TRIANGLE:
        case SYMBOL_DIAMOND:
            cachedShape.create(symbol, x, y, size);
            g.draw(cachedShape);
            break;

        case SYMBOL_CIRCLE: {
            double diameter = Math.max(1, size);
            diameter += (diameter % 2);
            drawOval(x - diameter / 2, y - diameter / 2, diameter, diameter, g);
            break;
        }
        }
    }

    protected static void fillSymbol(double x, double y, double size, int symbol, Graphics2D g) {
        if (size <= 0)
            return;
    	switch (symbol) {
        case SYMBOL_VLINE:
        case SYMBOL_STAR:
        case SYMBOL_HLINE:
        case SYMBOL_PLUS:
        case SYMBOL_CROSS:
            cachedShape.create(symbol, x, y, size);
            g.draw(cachedShape);
            break;

        case SYMBOL_BOX:
        case SYMBOL_UP_TRIANGLE:
        case SYMBOL_DN_TRIANGLE:
        case SYMBOL_DIAMOND:
            cachedShape.create(symbol, x, y, size);
            g.fill(cachedShape);
            break;

        case SYMBOL_CIRCLE: {
            double diameter = Math.max(1, size);
            diameter += (diameter % 2);
            fillOval(x - diameter / 2, y - diameter / 2, diameter, diameter, g);
            break;
        }
        }
    }

    public static void fillAndDrawSymbol(double x, double y, double size, int symbol,
            Color fillColor, Graphics2D graphics) {
        Color color = graphics.getColor();
        graphics.setColor(fillColor);
        fillSymbol(x, y, size, symbol, graphics);
        graphics.setColor(color);
        drawSymbol(x, y, size, symbol, graphics);
    }

    public static void drawOval(double x, double y, double width, double height, Graphics2D graphics) 
    {
    	graphics.draw(new Ellipse2D.Double(x, y, width, height));
    }
    
    public static void fillOval(double x, double y, double width, double height, Graphics2D graphics) {
    	graphics.fill(new Ellipse2D.Double(x, y, width, height));
    }

}
