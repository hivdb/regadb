package net.sf.regadb.install.generateGenomes;

public class Region implements Comparable<Region>{
    private int start;
    private int end;
    
    public Region(int start, int end){
        this.setStart(start);
        this.setEnd(end);
    }
    
    public boolean overlaps(Region r){
        if(getEnd() <= r.getStart() || getStart() >= r.getEnd())
            return false;
        return true;
    }
    
    public int getLength(){
        return getEnd() - getStart();
    }

    public int compareTo(Region arg0) {
        if(getStart() < arg0.getStart())
            return -1;
        if(getStart() > arg0.getStart())
            return 1;
        if(getEnd() < arg0.getEnd())
            return -1;
        if(getEnd() > arg0.getEnd())
            return 1;
        return 0;
    }

    public void setStart(int start) {
        this.start = start;
    }

    public int getStart() {
        return start;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getEnd() {
        return end;
    }
}