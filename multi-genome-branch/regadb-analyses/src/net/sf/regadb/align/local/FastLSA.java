package net.sf.regadb.align.local;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Stroke;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.event.MouseInputAdapter;

import net.sf.regadb.align.local.Grid.Line;

import org.apache.commons.io.FileUtils;

@SuppressWarnings("serial")
public class FastLSA extends JFrame{    
    final static double defaultscore = 0;
    
    final static double gapOpenScore = 0;
    final static double gapExtensionScore = -10;
    final static double gapEdgeExtensionScore = 0;
    
    final static double matchscore = 20;
    final static double mismatchscore = 0;
    
    public int k=2;
    private long bufferSizeBits = 96 * 1000 * 1000;
    
    private SymbolSequence seq1;
    private SymbolSequence seq2;
    
    private IAlphabet<Symbol> alphabet;
    private ISymbolMatchTable<Symbol> matchTable;
    
    private VisualTrace visualTrace;
    
    public static class VisualTrace extends JPanel{
        private FastLSA flsa;
        
        private ArrayList<Grid> grids = new ArrayList<Grid>();
        private ArrayList<Bounds> bounds = new ArrayList<Bounds>();
        private Path path = null;
        
        public VisualTrace(FastLSA flsa){
            super();
            this.flsa = flsa;

            x = flsa.seq1.length();
            y = flsa.seq2.length();
            
            this.addMouseWheelListener(new MouseWheelListener(){
                private float changeratio = 1.10f;

                public void mouseWheelMoved(MouseWheelEvent arg0) {
                    float r = ratio;
                    if(arg0.getWheelRotation() > 0)
                        ratio = (float)(ratio * changeratio * arg0.getWheelRotation());
                    else
                        ratio = (float)(ratio / (changeratio * -arg0.getWheelRotation()));
                    
                    xOffset += (arg0.getX()*(r-ratio));
                    yOffset += (arg0.getY()*(r-ratio));
                    
                    repaint();
                }
                
            });
            
            MouseInputAdapter ma = new MouseInputAdapter(){
                private int oldx,oldy;
                @Override
                public void mousePressed(MouseEvent arg0) {
                    super.mousePressed(arg0);
                    oldx = arg0.getX();
                    oldy = arg0.getY();
                }
                
                @Override
                public void mouseDragged(MouseEvent e) {
                    super.mouseDragged(e);
                    drag(e.getX(),e.getY());
                }
                
                private void drag(int newx, int newy){
                    int dragx = newx - oldx;
                    int dragy = newy - oldy;
                    
                    oldx = newx;
                    oldy = newy;
                    
                    xOffset += dragx;
                    yOffset += dragy;
                    
                    repaint();                    
                }
            };
            
            this.addMouseListener(ma);
            this.addMouseMotionListener(ma);
        }
        
        private int xOffset(){
            return xOffset;
        }
        private int yOffset(){
            return yOffset;
        }
        
        private int xWidth(){
            return (int) ((getWidth() * ratio)/flsa.seq1.length());
        }
        private int yWidth(){
            return (int) ((getHeight() * ratio)/flsa.seq2.length());
        }

        private float ratio = 1;
        private int xOffset = 0, yOffset = 0;
        
        private int x,y,xo,yo,xw,yw,xmax,ymax;
        private void updateGeometryValues(){
            xo = xOffset();
            yo = yOffset();

            xw = (int)(xWidth() * ratio);
            yw = (int)(yWidth() * ratio);
            
            xmax = xo + (x * xw);
            ymax = yo + (y * yw);
        }
        
        public void paintComponent(Graphics g){
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D)g;

            updateGeometryValues();
            
            g2d.setFont(new Font(Font.MONOSPACED,Font.PLAIN,yw/2));
            
            for(Grid grid : grids)
                drawGrid(g2d, grid);

            for(Bounds b : bounds)
                drawBounds(g2d, b);
            
            if(path != null)
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
            for(Path.Node node : path){
                xs[i] = xo + (node.x() * xw) + xw/2;
                ys[i] = yo + (node.y() * yw) + yw/2;
                ++i;
            }
            
            Stroke s = g2d.getStroke();
            Color c = g2d.getColor();
            g2d.setStroke(pathStroke);
            g2d.setColor(pathColor);
            g2d.drawPolyline(xs, ys, n);
            g2d.setColor(c);
            g2d.setStroke(s);
            
            StringBuilder s1 = new StringBuilder();
            StringBuilder s2 = new StringBuilder();
            
            Path.Node prev = null;
            for(Path.Node node : path){
            	if(node.x() >= flsa.seq1.length()
            			|| node.y() >= flsa.seq2.length())
            		continue;
            	
            	if(prev != null){
            		if(prev.x() == node.x())
            			s1.insert(0,FastLSA.GAP);
            		else
            			s1.insert(0,flsa.seq1.get(node.x()));
            		if(prev.y() == node.y())
            			s2.insert(0,FastLSA.GAP);
            		else
            			s2.insert(0,flsa.seq2.get(node.y()));
            	}
            	else{
            		s1.append(flsa.seq1.get(node.x()));
            		s2.append(flsa.seq2.get(node.y()));
            	}
            	prev = node;
            }
            g2d.drawString(s1.toString(), xo, ymax + yw/2);
            g2d.drawString(s2.toString(), xo, ymax + yw);
        }
        
        private void drawCenteredString(Graphics2D g2d, String s, int x, int y){
            int fw = 8;
            int fh = 8;
            
            g2d.drawString(s, x - (fw*s.length())/2, y + fh/2);
        }
        
        private void fillRow(Graphics2D g2d, Color c, int row, int col, int col2){
            fillRow(g2d, c, row, col, col2, null);
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
                    double val = values.at(i).getScore();
                    //if(val != 0)
                        drawCenteredString(g2d, val+"", x1 +xw/2, y1 +yw/2);
                    x1 += xw;
                }
            }
        }
        private void fillCol(Graphics2D g2d, Color c, int col, int row, int row2){
            fillCol(g2d, c, col, row, row2, null);
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
                    double val = values.at(i).getScore();
                    //if(val != 0)
                        drawCenteredString(g2d, val+"", x1 +xw/2, y1 +yw/2);
                    y1 += yw;
                }
            }
        }
        
        private void drawBounds(Graphics2D g2d, Bounds bounds){
            Color boundsColor = new Color(0,50,200,100);
            
            fillRow(g2d, boundsColor, bounds.y1(), bounds.x1(), bounds.x2());
            fillRow(g2d, boundsColor, bounds.y2(), bounds.x1(), bounds.x2());
            fillCol(g2d, boundsColor, bounds.x1(), bounds.y1(), bounds.y2());
            fillCol(g2d, boundsColor, bounds.x2(), bounds.y1(), bounds.y2());
        }
        
        private void drawGrid(Graphics2D g2d, Grid grid){
            Color gridColor = new Color(100,0,0,100);
            
            for(int i=0; i<grid.rows.length; ++i){
                int row = grid.bounds.y1() + (i * grid.stepY);
                fillRow(g2d, gridColor, row, grid.bounds.x1(), grid.bounds.x2(), grid.rows[i]);
            }
            for(int i=0; i<grid.cols.length; ++i){
                int row = grid.bounds.x1() + (i * grid.stepX);
                fillCol(g2d, gridColor, row, grid.bounds.y1(), grid.bounds.y2(), grid.cols[i]);
            }            
        }
        
        private void drawMatrixLines(Graphics2D g2d){
            for(int i = 0; i <= x; ++i){
                int xi = xo + (xw*i);
                g2d.drawLine(xi, yo, xi, ymax);

                if(i < x){
                    drawCenteredString(g2d, flsa.seq1.get(i).toString(), xi +(xw/2), yw/2);
                    drawCenteredString(g2d, ""+i, xi +(xw/2), yw);
                }
            }
            for(int i = 0; i <= y; ++i){
                int yi = yo + (yw*i);
                g2d.drawLine(xo, yi, xmax, yi);

                if(i < y)
                    drawCenteredString(g2d, flsa.seq2.get(i).toString() +" "+ i, 20, yi +(yw/2));
            }
        }
    }
    

    public static void main(String args[]){
        Runtime runtime = Runtime.getRuntime();
        System.out.println("OS:           "+ System.getProperty("os.name"));
        System.out.println("Processors:   "+ runtime.availableProcessors());
        System.out.println("Free memory:  "+ FileUtils.byteCountToDisplaySize(runtime.freeMemory()));
        System.out.println("Total memory: "+ FileUtils.byteCountToDisplaySize(runtime.totalMemory()));
        System.out.println("Max memory:   "+ FileUtils.byteCountToDisplaySize(runtime.maxMemory()));
        
        FastLSA flsa;
        try {
            flsa = new FastLSA("-actgcttggaccgttactgcttggaccgtt-","-acggcttggccgacggcttggccg-");
//            flsa = new FastLSA("-tldkllkd","-tdvlkad");
            flsa.run();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
        
    public boolean fitsInBuffer(Bounds bounds){
        return bounds.sizeX() * bounds.sizeY() < 100;
        //return (bounds.sizeX() * bounds.sizeY() * 96) <= bufferSizeBits;
    }
    
    public Path concatenatePaths(Path p1, Path p2){
        Path r = new Path();
        r.addAll(p1);
        r.addAll(p2);
        return r;
    }
    
    public void run(){
        int x = seq1.length();
        int y = seq2.length();
        
        Bounds bounds = new Bounds(0,0,x,y);
        Path p = new Path();
        p.add(new Path.Node(x-1,y-1));
        visualTrace.path = p;
        run(bounds, new Line(x,true), new Line(y,true), p);
    }

    public Path run(Bounds bounds, Line cacheRow, Line cacheCol, Path path){
        
        if(fitsInBuffer(bounds)){
            System.err.println("fit: "+ bounds.x1() +","+ bounds.y1() +" -> "+ bounds.x2() +","+ bounds.y2());
            
            Path p = fullMatrix( bounds, cacheRow, cacheCol, path );
            return p;
        }
        System.err.println("no fit: "+ bounds.x1() +","+ bounds.y1() +" -> "+ bounds.x2() +","+ bounds.y2());
        
        Grid grid = new Grid(k, bounds, cacheRow, cacheCol );
        visualTrace.grids.add(grid);

        fillGridCache(bounds, grid);
        
        Line newCacheRow = grid.getBottomRightRow();
        Line newCacheCol = grid.getBottomRightColumn();

        Bounds subProblem = bottomRightBounds(grid);
//        visualTrace.bounds.add(subProblem);
        
        Path pathExt = run( subProblem, newCacheRow, newCacheCol, path );
        while (notExtended(bounds, pathExt)){
//            visualTrace.bounds.remove(subProblem);
            subProblem = upLeft( grid, pathExt );
            visualTrace.bounds.add(subProblem);

            newCacheRow = cachedRow( grid, subProblem );
            newCacheCol = cachedColumn( grid, subProblem );
            /* Figure 3.6 (e) */
            pathExt = run( subProblem, newCacheRow, newCacheCol, pathExt );
        }
        
//        visualTrace.bounds.remove(subProblem);
        
        return pathExt;
    }


    public Score findScore(int i, int j, Score left, Score up, Score leftUp, Symbol s1, Symbol s2){
        Score ret = new Score();
        
        double sextend = leftUp.getScore() + matchTable.getScore(s1, s2);

        double ges = (j == seq2.length()) ? gapEdgeExtensionScore : gapExtensionScore;

        double horizGapScore = ((left.getGapSize() > 0) || (j == seq2.length()) ? ges : gapOpenScore + ges);
        double sgaphoriz = left.getScore() + horizGapScore;

        ges = (i == seq1.length()) ? gapEdgeExtensionScore : gapExtensionScore;

        double vertGapScore = (up.getGapSize() < 0 || (i == seq1.length()) ? ges : gapOpenScore + ges);
        double sgapvert = up.getScore() + vertGapScore;

        if ((sextend >= sgaphoriz) && (sextend >= sgapvert)) {
            ret.setScore(sextend);
            ret.setGapSize(0);
        } else {
            if (sgaphoriz > sgapvert) {
                ret.setScore(sgaphoriz);
                ret.setGapSize(Math.max(0,left.getGapSize()) + 1);
            } else {
                ret.setScore(sgapvert);
                ret.setGapSize(Math.min(0,up.getGapSize()) - 1);
            }
        }
        
        return ret;
    }
    
    private Path fullMatrix(Bounds bounds, Line cacheRow, Line cacheCol,
            Path path) {
    	int sx = bounds.sizeX();
    	int sy = bounds.sizeY();
    	
    	Score scores[][] = new Score[sx][sy];
    	for(int i=0; i<sx; ++i)
    		scores[i][0] = cacheRow.at(i);
    	for(int i=1; i<sy; ++i)
    		scores[0][i] = cacheCol.at(i);
    	
    	Score up;
    	Score left;
    	Score leftUp;
    	
    	for(int i=1; i<sx; ++i){
    		for(int j=1; j<sy; ++j){
    			left = scores[i-1][j];
    			leftUp = scores[i-1][j-1];
    			up = scores[i][j-1];
    			
    			int rx = i + bounds.x1();
    			int ry = j + bounds.y1();
    			scores[i][j] = findScore(rx, ry, left, up, leftUp, seq1.get(rx), seq2.get(ry));
    		}
    	}
    	
    	int i = sx-1;
    	int j = sy-1;
    	while(i > 0 && j > 0){
			left = scores[i-1][j];
			leftUp = scores[i-1][j-1];
			up = scores[i][j-1];
			
//			System.err.println("l: "+ left.getScore() +" lu: "+ leftUp.getScore() +" u: "+ up.getScore());
    			
			if(left.getScore() > leftUp.getScore())
				if(left.getScore() > up.getScore())
					--i;
				else
					--j;
			else if(leftUp.getScore() > up.getScore()){
				--i; --j;
			}
			else
				--j;
			path.add(new Path.Node(bounds.x1()+i,bounds.y1()+j));
    	}
    	
        return path;
    }

    private void fillGridCache(Bounds bounds, Grid grid) {
        Line colcache = grid.cols[0].clone();
        
        int gc = -1,gr = -1;
        Score up,upleft;
        
        for(int i = 1; i < bounds.x2() - bounds.x1(); ++i){
            if(i % grid.stepX == 0)
                gc = i/grid.stepX;
            else
                gc = -1;
            
            up = grid.rows[0].at(i);
            upleft = grid.rows[0].at(i-1);
            
            for(int j = 1; j < bounds.y2() - bounds.y1(); ++j){
                if(j % grid.stepY == 0)
                    gr = j/grid.stepY;
                else
                    gr = -1;
                
                Score score = findScore(i, j, colcache.at(j), up, upleft, seq1.get(i), seq2.get(j));
                upleft = colcache.at(j);
                up = score;
                
                colcache.at(j, score);
                if(gr != -1 && gr < grid.rows.length)
                    grid.rows[gr].at(i, score);
                if(gc != -1 && gc < grid.cols.length)
                    grid.cols[gc].at(j, score);
            }
        }
    }

    private Line cachedColumn(Grid grid, Bounds bounds) {
        int x = bounds.x1() - grid.bounds.x1() +1;
        x = (int)Math.floor(x / grid.stepX);
        
        int y = bounds.y1() - grid.bounds.y1();
        
        Line ret = new Line(bounds.sizeY());
        ret.copy(y, y+bounds.sizeY(), grid.cols[x]);
        return ret;
    }

    private Line cachedRow(Grid grid, Bounds bounds) {
        int y = bounds.y1() - grid.bounds.y1() +1;
        y = (int)Math.floor(y / grid.stepY);
        
        int x = bounds.x1() - grid.bounds.x1();
        
        Line ret = new Line(bounds.sizeX());
        ret.copy(x, x+bounds.sizeX(), grid.rows[y]);
        return ret;
    }

    public Bounds bottomRightBounds(Grid grid){
        int x1,x2,y1,y2;

        x1 = grid.bounds.x1() + (grid.stepX * (k-1));
        y1 = grid.bounds.y1() + (grid.stepY * (k-1));
        
        x2 = grid.bounds.x2();
        y2 = grid.bounds.y2();
        
        return new Bounds(x1,y1,x2,y2);
    }
    
    public Bounds upLeft(Grid grid, Path path){
        int x1,x2,y1,y2;
        int tx, ty;

        Path.Node node = path.getLast();        
        
        tx = node.x() - grid.bounds.x1();
        if( tx % grid.stepX == 0)
            tx = (tx/grid.stepX) - 1;
        else
            tx = (int)Math.floor(tx / grid.stepX);
        x1 = (tx * grid.stepX) + grid.bounds.x1();
        
        
        ty = node.y() - grid.bounds.y1();
        if(ty % grid.stepY == 0)
            ty = (ty/grid.stepY) - 1;
        else
            ty = (int)Math.floor(ty / grid.stepY);
        y1 = (ty * grid.stepY) + grid.bounds.y1();
        
        x2 = node.x()+1;
        y2 = node.y()+1;

        System.err.println("upleft: "+ x1 +","+ y1 +" "+ x2 +","+ y2);
        return new Bounds(x1,y1,x2,y2);
    }
    
    public boolean notExtended(Bounds bounds, Path path){
        Path.Node node = path.getLast();
        
        return (node.x() > bounds.x1()) && (node.y() > bounds.y1());
    }
    
    public FastLSA(String seq1, String seq2) throws Exception{
        super();
        init();
        
        this.seq1 = createSequence(seq1);
        this.seq2 = createSequence(seq2);
        
        initVisualization();
    }
    
    private static final Symbol GAP = new Symbol("-");
    private static final Symbol A = new Symbol("a");
    private static final Symbol B = new Symbol("b");
    private static final Symbol C = new Symbol("c");
    private static final Symbol D = new Symbol("d");
    private static final Symbol E = new Symbol("e");
    private static final Symbol F = new Symbol("f");
    private static final Symbol G = new Symbol("g");
    private static final Symbol H = new Symbol("h");
    private static final Symbol I = new Symbol("i");
    private static final Symbol J = new Symbol("j");
    private static final Symbol K = new Symbol("k");
    private static final Symbol L = new Symbol("l");
    private static final Symbol M = new Symbol("m");
    private static final Symbol N = new Symbol("n");
    private static final Symbol O = new Symbol("o");
    private static final Symbol P = new Symbol("p");
    private static final Symbol Q = new Symbol("q");
    private static final Symbol R = new Symbol("r");
    private static final Symbol S = new Symbol("s");
    private static final Symbol T = new Symbol("t");
    private static final Symbol U = new Symbol("u");
    private static final Symbol V = new Symbol("v");
    private static final Symbol W = new Symbol("w");
    private static final Symbol X = new Symbol("x");
    private static final Symbol Y = new Symbol("y");
    private static final Symbol Z = new Symbol("z");
    
    private void init(){
        alphabet = new DefaultAlphabet<Symbol>();
        alphabet.addSymbol(GAP);
        alphabet.addSymbol(A);
        alphabet.addSymbol(B);
        alphabet.addSymbol(C);
        alphabet.addSymbol(D);
        alphabet.addSymbol(E);
        alphabet.addSymbol(F);
        alphabet.addSymbol(G);
        alphabet.addSymbol(H);
        alphabet.addSymbol(I);
        alphabet.addSymbol(J);
        alphabet.addSymbol(K);
        alphabet.addSymbol(L);
        alphabet.addSymbol(M);
        alphabet.addSymbol(N);
        alphabet.addSymbol(O);
        alphabet.addSymbol(P);
        alphabet.addSymbol(Q);
        alphabet.addSymbol(R);
        alphabet.addSymbol(S);
        alphabet.addSymbol(T);
        alphabet.addSymbol(U);
        alphabet.addSymbol(V);
        alphabet.addSymbol(W);
        alphabet.addSymbol(X);
        alphabet.addSymbol(Y);
        alphabet.addSymbol(Z);

        DefaultMatchTable<Symbol> t = new DefaultMatchTable<Symbol>(alphabet);
        t.fill(20, 0);
        t.setScore(A, A, 16);
        t.setScore(C, C, 36);
        t.setScore(D, B, 20);
        t.setScore(E, A, 8);
        t.setScore(E, B, 10);
        t.setScore(E, D, 12);
        t.setScore(F, F, 28);
        t.setScore(G, G, 16);
        t.setScore(H, B, 6);
        t.setScore(H, H, 24);
        t.setScore(I, F, 12);
        t.setScore(K, B, 12);
        t.setScore(L, F, 14);
        t.setScore(L, I, 10);
        t.setScore(M, F, 6);
        t.setScore(M, I, 8);
        t.setScore(M, L, 16);
        t.setScore(M, M, 24);
        t.setScore(N, B, 20);
        t.setScore(N, D, 14);
        t.setScore(N, E, 10);
        t.setScore(N, H, 6);
        t.setScore(N, K, 12);
        t.setScore(O, B, 12);
        t.setScore(O, K, 20);
        t.setScore(O, N, 12);
        t.setScore(Q, B, 12);
        t.setScore(Q, D, 6);
        t.setScore(Q, E, 14);
        t.setScore(Q, H, 6);
        t.setScore(Q, K, 6);
        t.setScore(Q, N, 12);
        t.setScore(Q, O, 6);
        t.setScore(R, H, 10);
        t.setScore(R, K, 16);
        t.setScore(R, O, 16);
        t.setScore(R, Q, 10);
        t.setScore(S, A, 10);
        t.setScore(S, B, 8);
        t.setScore(S, N, 8);
        t.setScore(T, S, 10);
        t.setScore(V, I, 16);
        t.setScore(V, L, 12);
        t.setScore(V, M, 6);
        t.setScore(W, F, 10);
        t.setScore(W, R, 6);
        t.setScore(W, W, 36);
        t.setScore(Y, F, 20);
        t.setScore(Y, W, 10);
        t.setScore(Y, Y, 28);
        t.setScore(Z, A, 8);
        t.setScore(Z, B, 12);
        t.setScore(Z, D, 6);
        t.setScore(Z, E, 20);
        t.setScore(Z, H, 6);
        t.setScore(Z, K, 6);
        t.setScore(Z, N, 12);
        t.setScore(Z, O, 6);
        t.setScore(Z, Q, 20);
        t.setScore(Z, R, 10);
        
        t.setScore(X, A, 6);
        t.setScore(X, B, 6);
        t.setScore(X, C, 6);
        t.setScore(X, D, 6);
        t.setScore(X, E, 6);
        t.setScore(X, F, 6);
        t.setScore(X, G, 6);
        t.setScore(X, H, 6);
        t.setScore(X, I, 6);
        t.setScore(X, J, 6);
        t.setScore(X, K, 6);
        t.setScore(X, L, 6);
        t.setScore(X, M, 6);
        t.setScore(X, N, 6);
        t.setScore(X, O, 6);
        t.setScore(X, P, 6);
        t.setScore(X, Q, 6);
        t.setScore(X, R, 6);
        t.setScore(X, S, 6);
        t.setScore(X, T, 6);
        t.setScore(X, U, 6);
        t.setScore(X, V, 6);
        t.setScore(X, W, 6);
        t.setScore(X, Y, 6);
        t.setScore(X, Z, 6);
        
        
        matchTable = t;
    }
    
    private void initVisualization(){
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
    
    private SymbolSequence createSequence(String str) throws Exception{
        String s = str.toLowerCase();
        SymbolSequence seq = new SymbolSequence();
        for(int i=0; i<s.length(); ++i){
            Symbol smb = alphabet.get(s.charAt(i)+"");
            if(smb == null)
                throw new Exception("Unrecognized char: "+ s.charAt(i));
            
            seq.add(smb);
        }
        return seq;
    }
}
