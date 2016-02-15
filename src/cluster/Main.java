/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package cluster;

import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.awt.Color;

public class Main {

    private static final int SIDE              = 14;
    private static final int IDIM              = 18;
    private static final int IMAGESIZE         = SIDE * IDIM;
    private static final double RED[][]        = new double[IDIM][IDIM];
    private static final double GREEN[][]      = new double[IDIM][IDIM];
    private static final double BLUE[][]       = new double[IDIM][IDIM];
    private static final double RGB[][]        = new double[IDIM][IDIM];
    private static final int NUMIMAGES         = SIDE * SIDE;
    private static final int VECSIZE           = 26;
    private static final ImageNode[] IMAGELIST = new ImageNode[NUMIMAGES];

    public static void readImages() {
        int imageThird   = (int) ((double) (IDIM) / 3.0);
        String origImage = "pic";
        String image     = "pic";
        int count        = 0;
        try {
            File[] f = new File[NUMIMAGES];
            BufferedImage bi[] = new BufferedImage[NUMIMAGES];
            for (int t = 0; t < NUMIMAGES; t++) {
                image = image + count;
                image = image + ".png";
                f[t]  = new File(image);
                image = origImage;
                count++;
            }

            for (int k = 0; k < NUMIMAGES; k++) {
                bi[k] = ImageIO.read(f[k]);
                for (int i = 0; i < IDIM; i++) {
                    for (int j = 0; j < IDIM; j++) {
                        int mypixel = bi[k].getRGB(i, j);
                        Color c     = new Color(mypixel);
                        RED[i][j]   = c.getRed();
                        GREEN[i][j] = c.getGreen();
                        BLUE[i][j]  = c.getBlue();
                        RGB[i][j]   = c.getRGB();
                    }
                }
                double vector[] = new double[VECSIZE];
                double temp[];
                temp = vectorOne();
                for (int z = 0; z < 8; z++) {
                    vector[z] = temp[z];
                }
                temp = vectorTwo(0, 0);
                vector[8] = temp[0];
                vector[9] = temp[1];
                temp = vectorTwo(imageThird, 0);
                vector[10] = temp[0];
                vector[11] = temp[1];
                temp = vectorTwo((imageThird * 2), 0);
                vector[12] = temp[0];
                vector[13] = temp[1];
                temp = vectorTwo(0, imageThird);
                vector[14] = temp[0];
                vector[15] = temp[1];
                temp = vectorTwo(imageThird, imageThird);
                vector[16] = temp[0];
                vector[17] = temp[1];
                temp = vectorTwo((imageThird * 2), imageThird);
                vector[18] = temp[0];
                vector[19] = temp[1];
                temp = vectorTwo(0, (imageThird * 2));
                vector[20] = temp[0];
                vector[21] = temp[1];
                temp = vectorTwo(imageThird, (imageThird * 2));
                vector[22] = temp[0];
                vector[23] = temp[1];
                temp = vectorTwo((imageThird * 2), (imageThird * 2));
                vector[24] = temp[0];
                vector[25] = temp[1];
                IMAGELIST[k] = new ImageNode(vector, RED, GREEN, BLUE);

            }
        } catch (Exception e) {
        }

    }

    public static void writeImages() {
        int x      = 0;
        int y      = 0;
        int argb   = 0;
        int r      = 0;
        int g      = 0;
        int b      = 0;
        int count  = 0;
        int count2 = 0;
        try {
            BufferedImage nbi = new BufferedImage(IMAGESIZE, IMAGESIZE, BufferedImage.TYPE_INT_ARGB);

            for (int i = 0; i < IMAGESIZE; i++) {
                for (int j = 0; j < IMAGESIZE; j++) {
                    if (j % IDIM == 0 && j != 0) {
                        count++;
                        y = 0;
                    } else if (j % IDIM == 0 && j == 0 && i % IDIM != 0) {
                        count = count2;
                        y = 0;
                    } else if (j % IDIM == 0 && j == 0 && i % IDIM == 0 && i != 0) {
                        count++;
                        count2 = count;
                        y = 0;
                        x = 0;
                    }
                    r = (int) (IMAGELIST[count].getRedPixel(x, y));
                    g = (int) (IMAGELIST[count].getGreenPixel(x, y));
                    b = (int) (IMAGELIST[count].getBluePixel(x, y));
                    argb = (0xFF << 24) + (r << 16) + (g << 8) + b;
                    nbi.setRGB(i, j, argb);
                    y++;
                }
                x++;
            }
            File outfile = new File("Cluster Map orig");
            System.out.println(ImageIO.write(nbi, "png", outfile));
        } catch (Exception e) {
        }

    }

    public static double[] vectorOne() {
        double[] vec1    = new double[8];
        double totR      = 0;
        double totG      = 0;
        double totB      = 0;
        double totRgb    = 0;
        double sdR       = 0;
        double sdG       = 0;
        double sdB       = 0;
        double sdRgb     = 0;
        double numPixels = (double) (IDIM * IDIM);
        double sdValue   = 1.0 / numPixels;
        for (int i = 0; i < IDIM; i++) {
            for (int j = 0; j < IDIM; j++) {
                totR   += RED[i][j];
                totG   += GREEN[i][j];
                totB   += BLUE[i][j];
                totRgb += RGB[i][j];
            }
        }
        vec1[0] = totRgb / numPixels;
        vec1[2] = totR / numPixels;
        vec1[4] = totG / numPixels;
        vec1[6] = totB / numPixels;
        for (int i = 0; i < IDIM; i++) {
            for (int j = 0; j < IDIM; j++) {
                sdR   += Math.pow(RED[i][j] - (totR / numPixels), 2);
                sdG   += Math.pow(GREEN[i][j] - (totG / numPixels), 2);
                sdB   += Math.pow(BLUE[i][j] - (totB / numPixels), 2);
                sdRgb += Math.pow(RGB[i][j] - (totRgb / numPixels), 2);

            }
        }
        sdR     = Math.sqrt(sdValue * sdR);
        sdG     = Math.sqrt(sdValue * sdG);
        sdB     = Math.sqrt(sdValue * sdB);
        sdRgb   = Math.sqrt(sdValue * sdRgb);
        vec1[1] = sdRgb;
        vec1[3] = sdR;
        vec1[5] = sdG;
        vec1[7] = sdB;

        return vec1;
    }

    public static double[] vectorTwo(int x, int y) {
        double[] vec2          = new double[2];
        double totRgb          = 0;
        double sdRgb           = 0;
        double imageThird      = (double) (IDIM) / 3.0;
        double imageTotalThird = ((double) (IDIM) / 3.0) * ((double) (IDIM) / 3.0);
        double sdValue         = 1.0 / imageTotalThird;

        for (int i = x; i < (x + imageThird); i++) {
            for (int j = y; j < (y + imageThird); j++) {
                totRgb += RGB[i][j];
            }
        }
        vec2[0] = totRgb / imageTotalThird;
        for (int i = x; i < (x + imageThird); i++) {
            for (int j = y; j < (y + imageThird); j++) {
                sdRgb += Math.pow(RGB[i][j] - (totRgb / imageTotalThird), 2);
            }
        }
        sdRgb   = Math.sqrt(sdValue * sdRgb);
        vec2[1] = sdRgb;
        return vec2;
    }

    public static void main(String[] args) {
        String input = "";
        double learnRate = 0.0;
        int iteration = 0;
        readImages();
        writeImages();
        DataInputStream in = new DataInputStream(System.in);
        System.out.println("Please Enter the Learning Rate between 0 - 1");
        System.out.println();
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("Invalid Input :");
        }
        learnRate = Double.parseDouble(input);
        System.out.println("Please enter an integer that represents the number of iterations to run for: 1 iteration = 25 input vectors");
        System.out.println();
        try {
            input = in.readLine();
        } catch (Exception e) {
            System.out.println("Invalid Input :");
        }
        iteration = Integer.parseInt(input);
        new Cluster(IMAGELIST, iteration, learnRate);
    }

}
