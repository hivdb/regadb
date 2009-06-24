package net.sf.regadb.align.local;

import java.util.LinkedList;

public class Path extends LinkedList<Path.Node>{
    public boolean add(Path.Node node){
        boolean ret = super.add(node);
//        System.err.println("path: "+ node.x +","+ node.y);
        return ret;
    }
    
    public static class Node{
        private int x;
        private int y;
        
        public Node(){
        }
        public Node(int x, int y){
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
}
