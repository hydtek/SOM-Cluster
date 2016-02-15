/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package cluster;

public class Node {
    private int x;
    private int y;
    private final double[] VECTOR;
    private static final int VECLENGTH = 26;

    public Node(){
        VECTOR = new double[VECLENGTH];
        for(int i = 0; i < VECLENGTH;i++){
            VECTOR[i] = (int)(Math.random()*255);
        }
        
    }
    public double getComponent(int pos){
        return VECTOR[pos];
    }
    public void setComponent(int pos, double comp){
        VECTOR[pos] = comp;
    }
    public int getX(){
        return x;
    }
    public int getY(){
        return y;
    }
}
