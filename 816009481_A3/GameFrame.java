import javax.swing.*;
import java.awt.event.*;
import java.awt.*;
import java.awt.image.BufferStrategy;
import java.applet.Applet;
import java.applet.AudioClip;
import java.util.Timer;
import java.util.TimerTask;

public class GameFrame extends JFrame implements Runnable, KeyListener {
    private static final int NUM_BUFFERS = 2;
    private Timer timer;
    private int time = 60000;
    private String points;

    private int pWidth, pHeight;          

    private Thread gameThread = null;               
    private volatile boolean running = false;      
    private Man man;
    private GoalPost post = null;
    private Ball ball;
    //other game parts
    private Image bgImage;             
    AudioClip playSound = null;         

    // termination
    private boolean finishedOff = false;

    // quit 'button'
    private volatile boolean isOverQuitButton = false;
    private Rectangle quitButtonArea;

 
    private volatile boolean isOverPauseButton = false;
    private Rectangle pauseButtonArea;
    private volatile boolean isPaused = false;


    private volatile boolean isOverStopButton = false;
    private Rectangle stopButtonArea;
    private volatile boolean isStopped = false;


    private GraphicsDevice device;
    private Graphics gScr;
    private BufferStrategy bufferStrategy;

    public GameFrame () {
        super("Football Game: Full Screen Exclusive Mode");

        initFullScreen();

       
        man = new Man (this, 0, 585, 7, 0, 100, 80, "resources/man.png");
        ball = new Ball (this, man, 0, 0, 0, 10, 20, 20, "resources/football.png");

        

        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                testMousePress(e.getX(), e.getY());
            }
        });

        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseMoved(MouseEvent e) {
                testMouseMove(e.getX(), e.getY());
            }
        });

        addKeyListener(this);           

        
        int leftOffset = (pWidth - (5 * 150) - (4 * 20)) / 2;
        leftOffset = leftOffset + 170;
        pauseButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

        leftOffset = leftOffset + 170;
        stopButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

        leftOffset = leftOffset + 170;
        quitButtonArea = new Rectangle(leftOffset, pHeight-60, 150, 40);

        loadImages();
        loadClips();
        startGame();
    }

    private void initFullScreen() {
    //Add code here
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        device = ge.getDefaultScreenDevice();

        setUndecorated(true);
        setIgnoreRepaint(true);
        setResizable(false);
        if (!device.isFullScreenSupported()){
            System.out.println("Full-Screen exclusive mode not supported");
            System.exit(0);
        }

        device.setFullScreenWindow(this);

        showCurrentMode();

        pWidth = getBounds().width;
        pHeight = getBounds().height;

        try{
            createBufferStrategy(NUM_BUFFERS);
        }
        catch (Exception e){
            System.out.println("Error while creating buffer strategy " + e);
            System.exit(0);
        }
        bufferStrategy = getBufferStrategy();
}



    private void startGame() {
        if (gameThread == null || !running) {
            gameThread = new Thread(this);
            gameThread.start();
            playSound.loop();
        }
    }


    private void testMousePress(int x, int y) {

        if (isStopped && !isOverQuitButton)     
            return;

        if (isOverStopButton) {        
            isStopped = true;
            isPaused = false;
        }
        else
        if (isOverPauseButton) {        
            isPaused = !isPaused;    
        }
        else if (isOverQuitButton) {       
            running = false;        
        }
    }


    private void testMouseMove(int x, int y) {
        if (running) {
            isOverPauseButton = pauseButtonArea.contains(x,y) ? true : false;
            isOverStopButton = stopButtonArea.contains(x,y) ? true : false;
            isOverQuitButton = quitButtonArea.contains(x,y) ? true : false;
        }
    }


    public void keyPressed (KeyEvent e) {

        int keyCode = e.getKeyCode();

        if ((keyCode == KeyEvent.VK_ESCAPE) || (keyCode == KeyEvent.VK_Q) ||
                   (keyCode == KeyEvent.VK_END)) {
                running = false;        
            return;             
            }

        if (man == null || isPaused || isStopped)
            return;

        if (keyCode == KeyEvent.VK_LEFT) {
            man.moveLeft();
        }
        else
        if (keyCode == KeyEvent.VK_RIGHT) {
            man.moveRight();
        }
    }

    public void keyReleased (KeyEvent e) {

    }

    public void keyTyped (KeyEvent e) {

    }

    public void run() {

        running = true;
        try {
    
            while (running) {
                gameUpdate();
                screenUpdate();
                Thread.sleep(200);
            
            }
        }
        catch(InterruptedException e) {};

        finishOff();
    }

    private void gameUpdate() {

        if (!isPaused) {
            if (!isStopped){
                ball.update();
                post.update();
            }
        }
    }


    private void screenUpdate() {
        try{
            gScr = bufferStrategy.getDrawGraphics();
            gameRender(gScr);
            if (!bufferStrategy.contentsLost())
                bufferStrategy.show();
            else
                System.out.println("Contents of buffer lost");

            Toolkit.getDefaultToolkit().sync();
        }
        catch (Exception e){
            e.printStackTrace();
            running = false ;

        }

    }

    private void gameRender(Graphics gScr){

        gScr.drawImage (bgImage, 0, 0, pWidth, pHeight, null);
                           

        drawButtons(gScr);          

        gScr.setColor(Color.black);

        ball.draw((Graphics2D)gScr);      

        man.draw((Graphics2D)gScr);
        
        post.draw((Graphics2D)gScr);


        if (isStopped)             
            gameOverMessage(gScr);
    }

    private void drawButtons (Graphics g) {
        Font oldFont, newFont;

        oldFont = g.getFont();      

        newFont = new Font ("Comic Sans MS", Font.ITALIC + Font.BOLD, 18);
        g.setFont(newFont);     

            g.setColor(Color.black);    

        g.setColor(Color.BLACK);
        g.drawOval(pauseButtonArea.x, pauseButtonArea.y,
               pauseButtonArea.width, pauseButtonArea.height);

        if (isOverPauseButton && !isStopped)
            g.setColor(Color.WHITE);
        else
            g.setColor(Color.RED);

        if (isPaused && !isStopped)
            g.drawString("Paused", pauseButtonArea.x+45, pauseButtonArea.y+25);
        else
            g.drawString("Pause", pauseButtonArea.x+55, pauseButtonArea.y+25);

        g.setColor(Color.BLACK);
        g.drawOval(stopButtonArea.x, stopButtonArea.y,
               stopButtonArea.width, stopButtonArea.height);

        if (isOverStopButton && !isStopped)
            g.setColor(Color.WHITE);
        else
            g.setColor(Color.RED);

        if (isStopped)
            g.drawString("Stopped", stopButtonArea.x+40, stopButtonArea.y+25);
        else
            g.drawString("Stop", stopButtonArea.x+60, stopButtonArea.y+25);

    

        g.setColor(Color.BLACK);
        g.drawOval(quitButtonArea.x, quitButtonArea.y,
               quitButtonArea.width, quitButtonArea.height);
        if (isOverQuitButton)
            g.setColor(Color.WHITE);
        else
            g.setColor(Color.RED);

        g.drawString("Quit", quitButtonArea.x+60, quitButtonArea.y+25);
        g.setFont(oldFont);    

    }

    private void gameOverMessage(Graphics g) {

        Font font = new Font("SansSerif", Font.BOLD, 24);
        FontMetrics metrics = this.getFontMetrics(font);


        String msg = "GAME OVER!";

        int x = (pWidth - metrics.stringWidth(msg)) / 2;
        int y = (pHeight - metrics.getHeight()) / 2;

        g.setColor(Color.BLACK);
        g.setFont(font);
        g.drawString(msg, x, y);

    }


    private void finishOff() {
            if (!finishedOff) {
            finishedOff = true;
            restoreScreen();
            System.exit(0);
        }
    }

    private void restoreScreen() {
        Window w = device.getFullScreenWindow();

        if (w != null)
            w.dispose();

        device.setFullScreenWindow(null);
    }

    private void showCurrentMode() {
        DisplayMode dm = device.getDisplayMode();
        System.out.println("Current Display Mode: (" +
                           dm.getWidth() + "," + dm.getHeight() + "," +
                           dm.getBitDepth() + "," + dm.getRefreshRate() + ")  " );
    }

    public void loadImages() {

        bgImage = loadImage("resources/background.jpg");
        
        Image post1 = loadImage("resources/post.png");
        Image post2 = loadImage("resources/post1.png");
        
        post = new GoalPost(this, 0, 585, 7, 0, 150, 80, "resources/post.png");
        
        post.addFrame(post2, 250);
        post.addFrame(post1, 150);
        post.addFrame(post2, 100);
        post.addFrame(post1, 300);

    }

    public Image loadImage (String fileName) {
        return new ImageIcon(fileName).getImage();
    }

    public void loadClips() {

        try {
            playSound = Applet.newAudioClip (
                    getClass().getResource("resources/background.au"));

        }
        catch (Exception e) {
            System.out.println ("Error loading sound file: " + e);
        }

    }

    public void playClip (int index) {

        if (index == 1 && playSound != null)
            playSound.play();

    }

}
