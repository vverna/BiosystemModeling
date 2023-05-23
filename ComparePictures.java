package SourceCode;

import java.io.IOException;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import javax.imageio.ImageIO;
import java.awt.Color;

public class ComparePictures {

    public ComparePictures() {
    }

    public static BufferedImage resize(BufferedImage img, int newW, int newH) { 
        Image tmp = img.getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        BufferedImage dimg = new BufferedImage(newW, newH, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = dimg.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return dimg;
    } 


    public static double compareImages(BufferedImage image1, BufferedImage image2){
        double  percentage = 0;
        int white = Color.WHITE.getRGB();
        try {
            if (image1.getWidth() != image2.getWidth() || image1.getHeight() != image2.getHeight())
                image2 = resize(image2, image1.getWidth(), image1.getHeight());
            int countOverLap = 0;
            int countNonWhite = 0;
            for (int x = 0; x < image1.getWidth(); x++) {
                for (int y = 0; y < image1.getHeight(); y++) {
                    if (image1.getRGB(x, y) != white ||  image2.getRGB(x, y) != white)
                        countNonWhite++;
                    if (image1.getRGB(x, y) != white &&  image2.getRGB(x, y) != white)
                        countOverLap++;
                }
            }
            percentage = (double) countOverLap / (double) countNonWhite * 100;
        }catch (Exception e) { 
            System.out.println("Failed to compare image files ..." + e);
            return 0;
            }
        return percentage;
        }

    public void runTest(){
        String file1 = "Images/Tree1.png";
        String file2 = "Images/Tree1_L.png";
        BufferedImage image1;
        BufferedImage image2;
        try {
            image1 = ImageIO.read(getClass().getResource(file1));
            image2 = ImageIO.read(getClass().getResource(file2));
            double percentage = compareImages(image1, image2);
            System.out.println(percentage);

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
      }
}
