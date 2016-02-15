
package cluster;

public class ImageNode {
    private double[] vector;
    private static final int vecLength = 26;
    private static final int side = 18;
     double[][] red = new double[side][side];
     double[][] green = new double[side][side];
     double[][] blue = new double[side][side];

    public ImageNode(){
      vector = new double[vecLength];
    }
    public ImageNode(double[] vec, double[][] r, double[][] g, double[][] b){
       vector = new double[vecLength];
        for(int k = 0; k < vecLength;k++){
            vector[k] = vec[k];
        }
      
        for(int i = 0; i < side; i++)
            for(int j = 0; j < side; j++){
                red[i][j] = r[i][j];
                green[i][j] = g[i][j];
                blue[i][j] = b[i][j];
            }
    }
    public double getComponent(int pos){
        return vector[pos];
    }
    public void setComponent(int pos, double comp){
        vector[pos] = comp;
    }
    public double getRedPixel(int x, int y){
        return red[x][y];
    }
    public double getGreenPixel(int x, int y){
        return green[x][y];
    }
    public double getBluePixel(int x, int y){
        return blue[x][y];
    }
}
