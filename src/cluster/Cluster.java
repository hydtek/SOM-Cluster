package cluster;
import java.awt.Color;
import java.io.*;
import java.util.*;
import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.image.BufferedImage;
public class Cluster extends JFrame  {
    private double radius;
    private double learnRate;
    private final int ITERATION;
    private final double constlearnrate;
    private static final int NUMIMAGES = 196;
    private static final int BMUADDON  = 3;
    private static final int SIDE      = 14;
    private static final int PIXELLEN  = 18;
    private static final int BMUSIDE   = SIDE + BMUADDON;
    Node[][] kohMap = new Node[BMUSIDE][BMUSIDE];
    private final ImageNode[] IMAGELIST = new ImageNode[NUMIMAGES];

    public Cluster(){
        radius         = 0;
        learnRate      = 0;
        ITERATION      = 0;
        constlearnrate = 0;
    }
    public Cluster(ImageNode[] iList, int iter,double lRate){
        int x     = 0;
        int y     = 0;
        int l     = 0;
        int count = 0;
        double numInputsPerIteration = 25;
        ITERATION = iter;
        learnRate = lRate;
        radius = (double)SIDE/2.0;
        constlearnrate = lRate;
        
        System.arraycopy(iList, 0, IMAGELIST, 0, NUMIMAGES);

        initializeMap();
        List<String> list = initTrain();

          for(int k = 0; k < ITERATION;k++){
            Collections.shuffle(list);
            for(l = 0; l < numInputsPerIteration; l++){
                training(Integer.parseInt(list.get(l)));
            }
            numInputsPerIteration += 25;
            BufferedImage image;
            image = writeFile(count);
            displayFile(count, image);
            count++;
            if(l == 100){
                l = 0;
                numInputsPerIteration = 25;
            }
            System.out.println("iteration : " + k);
            updateLearningRate(k,ITERATION);
            updateRadius(k,ITERATION);
            if(k == (ITERATION-1) || k % 50 == 0 )
              writeFile2(k);
         }

    }
    private void training(int loc){
      int lowx = 0;
      int lowy = 0;
      double distance = 0;
      double temp = 0;
      for(int i = 0; i < BMUSIDE; i++){
       for(int j = 0; j < BMUSIDE; j++){
             temp = euclidean(i,j,loc);
             if(i == 0 && j == 0)
                 distance = temp;

             else if(distance > temp){
                 lowx = i;
                 lowy = j;
                 distance = temp;
             }
            }
          }
        updateWeights(lowx,lowy,loc);
    }
    private void updateWeights(int bmux, int bmuy, int loc){
      //System.out.println(" " + neighx + " " + neighy + " " + Math.sqrt(Math.pow((bmuy - (neighy-1)),2) + Math.pow((bmux - neighx), 2)) + " " + bmux + " " + bmuy + " " + x + " " + y);
        for(int i = 0; i < BMUSIDE;i++)
            for(int j = 0; j < BMUSIDE;j++){
                for(int k = 0; k < 26; k++){
                 double dist = Math.sqrt(Math.pow((bmuy - j),2) + Math.pow((bmux - i), 2));
                 if(dist <= radius){
                   double guas = (Math.exp((-1.0 *(Math.pow(dist,2)))/(2 * Math.pow(radius, 2))));
                   kohMap[i][j].setComponent(k, (kohMap[i][j].getComponent(k) + (guas*learnRate *(IMAGELIST[loc].getComponent(k) - kohMap[i][j].getComponent(k)))));
                   }
                }
            }
       // System.out.println("back track " + " " + neighx + " " + neighy + " " + bmux + " " + bmuy + " " + x + " " + y);
    }
    private double euclidean(int x,int y, int loc){
        double distance = 0;
        for(int i = 0; i < 26; i++){
            distance += Math.pow((IMAGELIST[loc].getComponent(i) - kohMap[x][y].getComponent(i)),2);
        }
        Math.sqrt(distance);
        return distance;
    }
    private void updateLearningRate(int iter,int iterTot){
         double e1 = iter;
         double e2 = iterTot;
         learnRate = constlearnrate * (Math.exp(-((e1)/(e2))));
         System.out.println(" learn rate " + learnRate + " " + constlearnrate);
    }
    private void updateRadius(int iter, int iterTot){
      double rad = (double)SIDE/2.0;
      double e1 = iter;
      double mTimeConst = (double)iterTot/Math.log(rad);
      radius = rad * (Math.exp((-(e1/mTimeConst))));
      System.out.println(" radius " + radius + " " + mTimeConst);
    }
    private void initializeMap(){
        for(int i = 0; i < BMUSIDE; i++)
          for(int j = 0; j < BMUSIDE; j++){
             kohMap[i][j] = new Node();
          }
    }
    private List<String> initTrain(){
        String fileOrder[] = new String[NUMIMAGES];
        for(int z = 0; z < NUMIMAGES; z++){
           fileOrder[z] = "" + z;
          
   }
   List<String> list = Arrays.asList(fileOrder);
   return list;
    }
    public BufferedImage writeFile(int k) {
        int x           = 0;
        int y           = 0;
        int argb        = 0;
        int r           = 0;
        int g           = 0;
        int b           = 0;
        int count       = 0;
        int count2      = 0;
        double distance = -1;
        double temp     = 0;
        int lowBmuX     = 0;
        int lowBmuY     = 0;
        int[] winnerX = new int[NUMIMAGES];
        int[] winnerY = new int[NUMIMAGES];
        int newImagePixelSize = PIXELLEN * (SIDE + BMUADDON);
        ImageNode[] iList = new ImageNode[(BMUSIDE * BMUSIDE)];
        boolean[][] bmuUsed = new boolean[BMUSIDE][BMUSIDE];

        //initializes array to make sure images are not on same bmu
        for (int a = 0; a < BMUSIDE; a++) {
            for (int a1 = 0; a1 < BMUSIDE; a1++) {
                bmuUsed[a][a1] = false;
            }
        }

        for (int e = 0; e < NUMIMAGES; e++) {
            for (int c = 0; c < BMUSIDE; c++) {
                for (int d = 0; d < BMUSIDE; d++) {
                    temp = euclidean(c, d, e);
                    if (distance == -1 && bmuUsed[c][d] == false) {
                        distance = temp;
                        lowBmuX = c;
                        lowBmuY = d;
                    } else if (distance > temp && bmuUsed[c][d] == false) {
                        lowBmuX = c;
                        lowBmuY = d;
                        distance = temp;
                    }
                }
            }
            bmuUsed[lowBmuX][lowBmuY] = true;
            winnerX[e] = lowBmuX;
            winnerY[e] = lowBmuY;
            distance = -1;
            lowBmuX = 0;
            lowBmuY = 0;
        }

        for (int q = 0; q < (SIDE * SIDE); q++) {
            for (int q1 = 0; q1 < BMUSIDE; q1++) {
                for (int q2 = 0; q2 < BMUSIDE; q2++) {
                    if (winnerX[q] == q1 && winnerY[q] == q2) {
                        iList[((winnerY[q] * BMUSIDE) + winnerX[q])] = IMAGELIST[q];
                    }
                }
            }
        }
        BufferedImage nbi = new BufferedImage(newImagePixelSize, newImagePixelSize, BufferedImage.TYPE_INT_ARGB);
        for (int i = 0; i < newImagePixelSize; i++) {
            for (int j = 0; j < newImagePixelSize; j++) {
                if (j % PIXELLEN == 0 && j != 0) {
                    count++;
                    y = 0;
                } else if (j % PIXELLEN == 0 && j == 0 && i % PIXELLEN != 0) {
                    count = count2;
                    y = 0;
                } else if (j % PIXELLEN == 0 && j == 0 && i % PIXELLEN == 0 && i != 0) {
                    count++;
                    count2 = count;
                    y = 0;
                    x = 0;
                }
                if (iList[count] != null) {
                    r = (int) (iList[count].getRedPixel(x, y));
                    g = (int) (iList[count].getGreenPixel(x, y));
                    b = (int) (iList[count].getBluePixel(x, y));
                    argb = (0xFF << 24) + (r << 16) + (g << 8) + b;
                    nbi.setRGB(i, j, argb);
                }
                y++;
            }
            x++;
        }
        return nbi;

}
    private void displayFile(int k, BufferedImage image){
        setSize(500,500);
        JPanel panel = new JPanel();
        panel.setBackground(Color.BLACK);
        ImageIcon icon = new ImageIcon(image);
        JLabel label = new JLabel();
        label.setLocation(20,20);
        label.setIcon(icon);
        panel.add(label);
        this.getContentPane().add(panel);
        setVisible(true);
    }
    public void writeFile2(int k) {
        int x                 = 0;
        int y                 = 0;
        int argb              = 0;
        int r                 = 0;
        int g                 = 0;
        int b                 = 0;
        int count             = 0;
        int count2            = 0;
        double distance       = -1;
        double temp           = 0;
        int lowBmuX           = 0;
        int lowBmuY           = 0;
        int[] winnerX         = new int[NUMIMAGES];
        int[] winnerY         = new int[NUMIMAGES];
        int newImagePixelSize = PIXELLEN * (SIDE+BMUADDON);
        ImageNode[] iList     = new ImageNode[(BMUSIDE*BMUSIDE)];
        boolean[][] bmuUsed   = new boolean[BMUSIDE][BMUSIDE];

      //initializes array to make sure images are not on same bmu
        for (int a = 0; a < BMUSIDE; a++) {
            for (int a1 = 0; a1 < BMUSIDE; a1++) {
                bmuUsed[a][a1] = false;
            }
        }

        for (int e = 0; e < NUMIMAGES; e++) {
            for (int c = 0; c < BMUSIDE; c++) {
                for (int d = 0; d < BMUSIDE; d++) {
                    temp = euclidean(c, d, e);
                    if (distance == -1 && bmuUsed[c][d] == false) {
                        distance = temp;
                        lowBmuX = c;
                        lowBmuY = d;
                    } else if (distance > temp && bmuUsed[c][d] == false) {
                        lowBmuX = c;
                        lowBmuY = d;
                        distance = temp;
                    }
                }
            }
            bmuUsed[lowBmuX][lowBmuY] = true;
            winnerX[e] = lowBmuX;
            winnerY[e] = lowBmuY;
            distance = -1;
            lowBmuX = 0;
            lowBmuY = 0;
        }

        for (int q = 0; q < (SIDE * SIDE); q++) {
            for (int q1 = 0; q1 < BMUSIDE; q1++) {
                for (int q2 = 0; q2 < BMUSIDE; q2++) {
                    if (winnerX[q] == q1 && winnerY[q] == q2) {
                        iList[((winnerY[q] * BMUSIDE) + winnerX[q])] = IMAGELIST[q];
                    }
                }
            }
        }
        try {
            BufferedImage nbi = new BufferedImage(newImagePixelSize, newImagePixelSize, BufferedImage.TYPE_INT_ARGB);
            for (int i = 0; i < newImagePixelSize; i++) {
                for (int j = 0; j < newImagePixelSize; j++) {
                    if (j % PIXELLEN == 0 && j != 0) {
                        count++;
                        y = 0;
                    } else if (j % PIXELLEN == 0 && j == 0 && i % PIXELLEN != 0) {
                        count = count2;
                        y = 0;
                    } else if (j % PIXELLEN == 0 && j == 0 && i % PIXELLEN == 0 && i != 0) {
                        count++;
                        count2 = count;
                        y = 0;
                        x = 0;
                    }
                    if (iList[count] != null) {
                        r = (int) (iList[count].getRedPixel(x, y));
                        g = (int) (iList[count].getGreenPixel(x, y));
                        b = (int) (iList[count].getBluePixel(x, y));
                        argb = (0xFF << 24) + (r << 16) + (g << 8) + b;
                        nbi.setRGB(i, j, argb);
                    }
                    y++;
                }
                x++;
            }
            File outfile = new File("Cluster Map Solution " + k);
            System.out.println(ImageIO.write(nbi, "png", outfile));
        } catch (Exception e) {
        }

    }
}
