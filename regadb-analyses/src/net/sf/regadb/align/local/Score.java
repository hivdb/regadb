package net.sf.regadb.align.local;


public class Score {
    private double score = FastLSA.defaultscore;
    private int gapSize = 0;
    
    public Score(){
    }
    public Score(double score){
        this.setScore(score);
    }
    public Score(double score, int gapSize){
        setScore(score);
        setGapSize(gapSize);
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
        return new Score(getScore(),getGapSize());
    }
}
