package net.sf.regadb.align.local;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.util.LinkedList;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class FastLSA extends JFrame{
    VisualTrace visualTrace;
    
    public int k=2;
    public long bufferSizeBits = 96 * 1000 * 1000;
    
    String seq1 = "-actgcttggaccgtt";
    String seq2 = "-acggcttggccg";
    
    public static class VisualTrace extends JPanel{
        private FastLSA flsa;
        
        public VisualTrace(FastLSA flsa){
            super();
            this.flsa = flsa;
        }
        
        private int xOffset(){
            return 20;
        }
        private int yOffset(){
            return 20;
        }
        
        private int xWidth(){
            return (getWidth() - xOffset())/flsa.seq1.length();
        }
        private int yWidth(){
            return (getHeight() - yOffset())/flsa.seq2.length();
        }

        private int x,y,xo,yo,xw,yw,xmax,ymax;
        
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;

            x = flsa.seq1.length();
            y = flsa.seq2.length();

            xo = xOffset();
            yo = yOffset();

            xw = xWidth();
            yw = yWidth();
            
            xmax = xo + (x * xw);
            ymax = yo + (y * yw);            
            
            //fillRow(g2d,new Color(0,0,0,100),2,1,5);
            int k = flsa.k;

            Path path = new Path();
            path.add(new PathNode(x-1,y-1));
            path.add(new PathNode(x-2,y-1));
            path.add(new PathNode(x-2,y-2));
            path.add(new PathNode(x-3,y-3));
            path.add(new PathNode(x-4,y-4));
            path.add(new PathNode(x-5,y-4));
            path.add(new PathNode(x-6,y-4));
            path.add(new PathNode(x-7,y-4));
            path.add(new PathNode(x-8,y-4));
            path.add(new PathNode(x-9,y-4));
            path.add(new PathNode(x-10,y-5));
            path.add(new PathNode(x-11,y-6));
            path.add(new PathNode(x-12,y-7));
            
            Grid grid = new Grid(k,new Bounds(0,0,x,y),new Line(x,-1),new Line(y,-1));

            Grid grid2 = new Grid(k,flsa.bottomRightBounds(grid),new Line(x/k,-1),new Line(y/k,-1));      

            Bounds upleft = flsa.upLeft(grid, path);
            Grid grid3 = new Grid(k, upleft,new Line(x/k,-1),new Line(y/k,-1));

            drawGrid(g2d,grid);
            //drawGrid(g2d,grid2);
            //drawGrid(g2d,grid3);
            drawBounds(g2d,upleft);

            drawPath(g2d, path);            
            drawMatrixLines(g2d);
        }
        
        private void drawPath(Graphics2D g2d, Path path){
            BasicStroke pathStroke = new BasicStroke(4);
            Color pathColor = new Color(0,200,50,100);
            
            int n = path.size();
            
            int xs[] = new int[n];
            int ys[] = new int[n];
            
            int i=0;
            for(PathNode node : path){
                xs[i] = xo + (node.x * xw) + xw/2;
                ys[i] = yo + (node.y * yw) + yw/2;
                ++i;
            }
            
            Stroke s = g2d.getStroke();
            Color c = g2d.getColor();
            g2d.setStroke(pathStroke);
            g2d.setColor(pathColor);
            g2d.drawPolyline(xs, ys, n);
            g2d.setColor(c);
            g2d.setStroke(s);
        }
        
        private void drawCenteredString(Graphics2D g2d, String s, int x, int y){
            int fw = 8;
            int fh = 8;
            
            g2d.drawString(s, x - (fw*s.length())/2, y + fh/2);
        }
        
        private void fillRow(Graphics2D g2d, Color c, int row, int col, int col2, Line values){
            int y1 = yo + yw*row;
            int x1 = xo + xw*col;
            
            Color co = g2d.getColor();
            g2d.setColor(c);
            g2d.fillRect(x1, y1, (col2-col)*xw, yw);
            g2d.setColor(co);
            
            if(values != null){
                for(int i = 0; i < values.size(); ++i){
                    drawCenteredString(g2d, values.at(i)+"", x1 +xw/2, y1 +yw/2);
                    x1 += xw;
                }
            }
        }
        private void fillCol(Graphics2D g2d, Color c, int col, int row, int row2, Line values){
            int y1 = yo + yw*row;
            int x1 = xo + xw*col;
            
            Color co = g2d.getColor();
            g2d.setColor(c);
            g2d.fillRect(x1, y1, xw, (row2-row)*yw);
            g2d.setColor(co);
            
            if(values != null){
                for(int i = 0; i < values.size(); ++i){
                    drawCenteredString(g2d, values.at(i)+"", x1 +xw/2, y1 +yw/2);
                    y1 += yw;
                }
            }
        }
        
        private void drawBounds(Graphics2D g2d, Bounds bounds){
            Color boundsColor = new Color(0,50,200,100);
            
            fillRow(g2d, boundsColor, bounds.y1(), bounds.x1(), bounds.x2(), null);
            fillRow(g2d, boundsColor, bounds.y2(), bounds.x1(), bounds.x2(), null);
            fillCol(g2d, boundsColor, bounds.x1(), bounds.y1(), bounds.y2(), null);
            fillCol(g2d, boundsColor, bounds.x2(), bounds.y1(), bounds.y2(), null);
        }
        
        private void drawGrid(Graphics2D g2d, Grid grid){
            Color gridColor = new Color(100,0,0,100);
            
            for(int i=0; i<grid.rows.length; ++i){
                int row = grid.bounds.y1 + (i * grid.stepY);
                fillRow(g2d, gridColor, row, grid.bounds.x1, grid.bounds.x2, grid.rows[i]);
            }
            for(int i=0; i<grid.cols.length; ++i){
                int row = grid.bounds.x1 + (i * grid.stepX);
                fillCol(g2d, gridColor, row, grid.bounds.y1, grid.bounds.y2, grid.cols[i]);
            }            
        }
        
        private void drawMatrixLines(Graphics2D g2d){
            for(int i = 0; i <= x; ++i){
                int xi = xo + (xw*i);
                g2d.drawLine(xi, yo, xi, ymax);

                if(i < x)
                    drawCenteredString(g2d, flsa.seq1.charAt(i)+"" , xi +(xw/2), 10);
            }
            for(int i = 0; i <= y; ++i){
                int yi = yo + (yw*i);
                g2d.drawLine(xo, yi, xmax, yi);

                if(i < y)
                    drawCenteredString(g2d, flsa.seq2.charAt(i)+"" , 10, yi +(yw/2));
            }
        }
    }
    

    public static void main(String args[]){
        Runtime runtime = Runtime.getRuntime();
        System.out.println("Processors:   "+ runtime.availableProcessors());
        System.out.println("Free memory:  "+ FileUtils.byteCountToDisplaySize(runtime.freeMemory()));
        System.out.println("Total memory: "+ FileUtils.byteCountToDisplaySize(runtime.totalMemory()));
        System.out.println("Max memory:   "+ FileUtils.byteCountToDisplaySize(runtime.maxMemory()));
        
        FastLSA flsa = new FastLSA();
        flsa.run();
    }
    
    public static class PathNode{
        private int x;
        private int y;
        
        public PathNode(){
        }
        public PathNode(int x, int y){
            x(x);
            y(y);
        }
        
        public int x(){
            return x;
        }
        public void x(int x){
            this.x = x;
        }
        public int y(){
            return y;
        }
        public void y(int y){
            this.y = y;
        }
    }
    
    public static class Path extends LinkedList<PathNode>{
        public Path(){
        }
    }

    public static class Bounds{
        private int x1,y1,x2,y2;
        
        public Bounds(int x1, int y1, int x2, int y2){
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
        }
        
        public int sizeX(){
            return x2 - x1;
        }
        public int sizeY(){
            return y2 - y1;
        }
        
        public int x1(){
            return x1;
        }
        public int x2(){
            return x2;
        }
        public int y1(){
            return y1;
        }
        public int y2(){
            return y2;
        }
    }

    public static class Line{
        private double scores[];
        
        public Line(int size){
            scores = new double[size];
        }
        public Line(int size, double value){
            scores = new double[size];
            for(int i=0;i<size;++i)
                scores[i]=value;
        }
        
        public double at(int i){
            return scores[i];
        }
        public void at(int i, double score){
            scores[i] = score;
        }
        
        public void copy(int begin, int end, Line line){
            int i=0;
            for(int j=begin; j<end; ++j)
                scores[i++] = line.scores[j];
                
        }
        
        public int size(){
            return scores.length;
        }
    }
    
    public static class Grid{
        
        public Bounds bounds;
        public Line rows[];
        public Line cols[];
        
        public int stepX;
        public int stepY;
        
        public Grid(int k, Bounds bounds, Line row, Line col){
            this.bounds = bounds;
            rows = new Line[k];
            cols = new Line[k];

            stepX = bounds.sizeX()/k;            
            stepY = bounds.sizeY()/k;
            
            rows[0] = row;
            cols[0] = col;            
            for(int i = 1; i<k; ++i){
                rows[i] = new Line(bounds.sizeX());
                cols[i] = new Line(bounds.sizeY());
            }
            
            
//            stepX = bounds.sizeX()/k;            
//            stepY = bounds.sizeY()/k;
//            
//            int bx = row.size() - (row.size()/k);
//            int by = col.size() - (col.size()/k);
//
//            System.err.println("copy calculated values");
//            for(int i=0; i<k; ++i){
//                rows[i] = new Line(stepX);
//                cols[i] = new Line(stepY);
//                
//                int ex = bx + stepX;
//                int ey = by + stepY;
//                
//                System.err.println("copy "+ bx +" -> "+ ex +" from row");
//                rows[i].copy(bx, ex, row);
//                System.err.println("copy "+ by +" -> "+ ey +" from col");
//                cols[i].copy(by, ey, col);
//                
//                bx = ex;
//                by = ey;
//            }
        }
        
        public Line getTopRow(){
            return rows[0];
        }
        public Line getBottomRow(){
            return rows[rows.length-1];
        }

        public Line getLeftColumn(){
            return cols[0];
        }
        public Line getRightColumn(){
            return cols[cols.length-1];
        }

    }
        
    public boolean fitsInBuffer(Bounds bounds){
        return (bounds.sizeX() * bounds.sizeY() * 96) <= bufferSizeBits;
    }
    
    public Path fullMatrix(Bounds bounds, Grid grid){
        return null;
    }
    
    public void findScore(Bounds bounds, Grid grid, Grid newGrid){
        
    }
    
    public Grid allocateNewGrid(Bounds bounds, Grid grid){
        return null;
    }
    
    public Bounds lowerRightQuadrant(Bounds bounds){
        return null;
    }
    
    public boolean intersectsUpperLeft(Bounds bounds, Path path){
        return false;
    }
    
    public Bounds pruneBounds(Bounds bounds, Path path){
        return null;
    }
    
    public Path concatenatePaths(Path p1, Path p2){
        Path r = new Path();
        r.addAll(p1);
        r.addAll(p2);
        return r;
    }
    
    public void deallocateGrid(Grid grid){
        
    }
    
    public void run(){
        Bounds bounds = new Bounds(0,0,seq1.length(),seq2.length());
        Line cacheRow = new Line(seq1.length(),-1);
        Line cacheCol = new Line(seq2.length(),-1);
        Path path = new Path();
        
        run(bounds, cacheRow, cacheCol, path);
    }

    public Path run(Bounds bounds, Grid grid){
        if (fitsInBuffer(bounds))
            return fullMatrix(bounds, grid);
        
        Grid newGrid = allocateNewGrid(bounds, grid);
        findScore(bounds, grid, newGrid);
        Bounds newBounds = lowerRightQuadrant(bounds);
        Path path = run(newBounds, newGrid);
        while (!intersectsUpperLeft(bounds, path)){
            Bounds subBounds = pruneBounds(bounds, path);
            Path newPath = run(subBounds, newGrid);
            path = concatenatePaths(path, newPath);
        }
        deallocateGrid(newGrid);
        return path;
    }
    
    public Path run(Bounds bounds, Line cacheRow, Line cacheCol, Path path){
        if(fitsInBuffer(bounds)){
            return fullMatrix( bounds, cacheRow, cacheCol, path );
        }
        Grid grid = new Grid(k, bounds, cacheRow, cacheCol );

        fillGridCache(bounds, grid);

        Line newCacheRow = grid.getBottomRow();
        Line newCacheCol = grid.getRightColumn();

        Bounds subProblem = bottomRightBounds(grid);
        
        Path pathExt = run( subProblem, newCacheRow, newCacheCol, path );
        while (notExtended(bounds, pathExt)){
            subProblem = upLeft( grid, pathExt );
            newCacheRow = cachedRow( grid, subProblem );
            newCacheCol = cachedColumn( grid, subProblem );
            /* Figure 3.6 (e) */
            pathExt = run( subProblem, newCacheRow, newCacheCol, pathExt );
        }
        deallocateGrid( grid );
        
        return pathExt;
    }
    
    private Path fullMatrix(Bounds bounds, Line cacheRow, Line cacheCol,
            Path path) {
        // TODO Auto-generated method stub
        return null;
    }

    private void fillGridCache(Bounds bounds, Grid grid) {
        // TODO Auto-generated method stub
        
    }

    private Line cachedColumn(Grid grid, Bounds bounds) {
        int x = bounds.x1() - grid.bounds.x1;
        x = (int)Math.floor(x / grid.stepX);
        
        return grid.cols[x];
    }

    private Line cachedRow(Grid grid, Bounds bounds) {
        int y = bounds.y1() - grid.bounds.y1;
        y = (int)Math.floor(y / grid.stepY);
        
        return grid.rows[y];
    }

    public Bounds bottomRightBounds(Grid grid){
        int x1,x2,y1,y2;

        x1 = grid.bounds.x1 + (grid.stepX * (k-1));
        y1 = grid.bounds.y1 + (grid.stepY * (k-1));
        
        x2 = grid.bounds.x2;
        y2 = grid.bounds.y2;
        
        return new Bounds(x1,y1,x2,y2);
    }
    
    public Bounds upLeft(Grid grid, Path path){
        int x1,x2,y1,y2;
        int tx, ty;

        PathNode node = path.getLast();        
        
        tx = node.x() - grid.bounds.x1;
        if( tx % grid.stepX == 0)
            tx = (tx/grid.stepX) - 1;
        else
            tx = (int)Math.floor(tx / grid.stepX);
        x1 = (tx * grid.stepX) + grid.bounds.x1;
        
        
        ty = node.y() - grid.bounds.y1;
        if(ty % grid.stepY == 0)
            ty = (ty/grid.stepY) - 1;
        else
            ty = (int)Math.floor(ty / grid.stepY);
        y1 = (ty * grid.stepY) + grid.bounds.y1;
        
        x2 = node.x();
        y2 = node.y();

        System.out.println(x1 +","+ y1 +" "+ x2 +","+ y2);
        return new Bounds(x1,y1,x2,y2);
    }
    
    public boolean notExtended(Bounds bounds, Path path){
        PathNode node = path.getFirst();
        
        return (node.x() <= bounds.x1()) || (node.y() <= bounds.y1());
    }
    
    public FastLSA(){
        init();
    }
    private void init(){
        int width = 640, height = 640;
        setSize(width, height);
        
        setLocation((getToolkit().getScreenSize().width - width) / 2,
                (getToolkit().getScreenSize().height - height) / 2);
        
        setResizable(true);
        
        visualTrace = new VisualTrace(this);
        visualTrace.setBackground(new Color(255,255,255));
        add(visualTrace);
        
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    
}
