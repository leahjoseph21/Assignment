import java.awt.geom.Rectangle2D;
import javax.swing.JFrame;
import java.util.Random;
import java.applet.Applet;
import java.applet.AudioClip;
import java.awt.Image;
import javax.swing.JPanel;


public class Ball extends Sprite {

    private Man man;
    private Random random;
    private JPanel panel;
    public int score = 0;
    public int scoreAmt[];

    AudioClip hitManSound;
    AudioClip fallOffSound;

    public Ball (JFrame f, Man m, 
             int x, int y, int dx, int dy, 
             int xSize, int ySize, 
             String filename) {
        super(f, x, y, dx, dy, xSize, ySize, filename);
        man = m;
        random = new Random();
        setPosition();
        loadClips();
    }

    public void setPosition () {
        int i = dimension.width/2;
        int x = random.nextInt(dimension.width - xSize);
        setX(x);
        int y = dimension.height/2;
        setY(y);
    }

    public boolean manHitsBall () {
        
        Rectangle2D.Double rectBall = getBoundingRectangle();
        Rectangle2D.Double rectMan = man.getBoundingRectangle();
        
        if (rectBall.intersects(rectMan))
            return true;
        else
            return false; 
    }

    public boolean isOffScreen () {

        if (y + ySize > dimension.height)
            return true;
        else
            return false;
    }


    public void update () {

        if (!window.isVisible ()) return;
    
        y = y + dy;

        boolean hitMan = manHitsBall();

        if (hitMan || isOffScreen()) {
            if (hitMan) {
                playClip (1); 
                score++;
            }
            else {                  
                playClip (2);
            }

            try {                   
                Thread.sleep (2000);        
            }
            catch (InterruptedException e) {};

            setPosition ();             
        }
    }

    public void loadClips() {

        try {

            hitManSound = Applet.newAudioClip (
                    getClass().getResource("resources/kick.au"));

            fallOffSound = Applet.newAudioClip (
                    getClass().getResource("resources/Buzzer.au"));

        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }

    public void playClip (int index) {

        if (index == 1 && hitManSound != null)
            hitManSound.play();
        else
        if (index == 2 && fallOffSound != null)
            fallOffSound.play();

    }
}

