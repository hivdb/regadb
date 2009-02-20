package net.sf.regadb.align.local;


public class Score {
    private double score = FastLSA.defaultscore;
    private int gapSize = 0;
    
    public Score(){
    }
    public Score(double score){
        this.setScore(score);
    }
    
    public void setScore(double score) {
        this.score = score;
    }
    public double getScore() {
        return score;
    }

    public void setGapSize(int gapSize) {
        this.gapSize = gapSize;
    }
    public int getGapSize() {
        return gapSize;
    }
    
    public Score clone(){
        return this.clone();
    }
}
