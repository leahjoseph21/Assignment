import javax.swing.JFrame;
import java.applet.Applet;
import java.applet.AudioClip;
import java.util.ArrayList;
import java.awt.Graphics;
import javax.swing.*;
import javax.imageio.ImageIO;
import java.io.File;
import java.io.IOException;
import java.awt.Image;

public class GoalPost extends Sprite {
    private JFrame window;                  
    private ArrayList frames;                   
    private int currFrameIndex;                 
    private long animTime;                  
    private long startTime;                 
    private long totalDuration;                 



    public GoalPost(JFrame f,int x, int y, int dx, int dy, int xSize, int ySize, String filename){
        super(f, x, y, dx, dy, xSize, ySize, filename);

        window = f;
        frames = new ArrayList();
        totalDuration = 0;
        start();

    }
    
    public synchronized void addFrame(Image image, long duration) {
            totalDuration += duration;
            frames.add(new AnimFrame(image, totalDuration));
        }
        
    public synchronized void start() {
        animTime = 0;              
        currFrameIndex = 0;         
        startTime = System.currentTimeMillis(); 
     }
     
    public synchronized void update() {
        long currTime = System.currentTimeMillis(); 
        long elapsedTime = currTime - startTime;    
        startTime = currTime;               

        if (frames.size() > 1) {
            animTime += elapsedTime;        
            if (animTime >= totalDuration) {    
                animTime = animTime % totalDuration;    
                                
                currFrameIndex = 0;     
            }

            while (animTime > getFrame(currFrameIndex).endTime) {
                currFrameIndex++;       
            }
        }
    
    }
    
    public synchronized Image getImage() {
        if (frames.size() == 0) {
            return null;
        }
            else {
            return getFrame(currFrameIndex).image;
        }
      }

    public void draw (Graphics g) {
        int x = (dimension.width - xSize)-20;
        int y = (dimension.height - ySize)-120;
        g.drawImage(getImage(), x, y, null);
    }

    public int getNumFrames() {             
        return frames.size();
    }

    public AnimFrame getFrame(int i) {
        return (AnimFrame)frames.get(i);
    }

    public class AnimFrame {

        Image image;
        long endTime;

        public AnimFrame(Image image, long endTime) {
            this.image = image;
            this.endTime = endTime;
        }
    }

    
}
