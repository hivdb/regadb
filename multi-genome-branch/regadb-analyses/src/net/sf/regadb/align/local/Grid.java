package net.sf.regadb.align.local;


public class Grid{
    
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

    public static class Line{
        private Score scores[];

        public Line(int size){
            this(size, FastLSA.defaultscore);
        }
        public Line(int size, boolean edge){
            if(edge){
                scores = new Score[size];
                for(int i=0; i<size; ++i)
                    scores[i] = new Score(i * FastLSA.gapExtensionScore);
            }
        }
        public Line(int size, double value){
            scores = new Score[size];
            for(int i=0;i<size;++i)
                scores[i] = new Score(value);
        }
        
        public Score at(int i){
            return scores[i];
        }
        public void at(int i, Score score){
            scores[i] = score;
        }
        
        public void copy(int begin, int end, Line line){
            int i=0;
            for(int j=begin; j<end; ++j)
                scores[i++] = line.scores[j].clone();
                
        }
        
        public int size(){
            return scores.length;
        }
        
        public Line clone(){
            Line clone = new Line(size());
            clone.scores = scores.clone();
            return clone;
        }
    }
}
