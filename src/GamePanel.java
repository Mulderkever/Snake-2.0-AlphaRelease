import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;
import javax.swing.border.LineBorder;
import java.io.*;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;

public class GamePanel extends JPanel implements ActionListener {
    private FPSCounter fpsCounter;
    private MyKeyAdapter keyAdapter;
    SettingsFrame settingsFrame;

    // Constants
    static final int SCREEN_WIDTH = 600;
    static final int SCREEN_HEIGHT = 600;
    static final int UNIT_SIZE = 25;
    static final int GAME_UNITS = (SCREEN_WIDTH  * SCREEN_HEIGHT ) / UNIT_SIZE;
    int DELAY = 75;


    // Variables
    final int x[] = new int[GAME_UNITS];
    final int y[] = new int[GAME_UNITS];
    int bodyParts = 6;
    int applesEaten;
    int appleX;
    int diffLevel = 50;
    int appleY;
    char direction = 'R';
    boolean running = false;
    boolean hidden = false;
    boolean grid = false;
    boolean wasStartScreen = true;
    boolean wasRetryScreen = false;
    boolean isStartScreen = true;
    private boolean paused = false;
    Timer timer;
    Random random;
    JButton retryButton;
    JButton settingsButton;
    JButton startButton;
    JButton diffButton;
    private int highScore = 0;
    private int leftKey = KeyEvent.VK_LEFT;
    private int rightKey = KeyEvent.VK_RIGHT;
    private int upKey = KeyEvent.VK_UP;
    private int downKey = KeyEvent.VK_DOWN;
    private int hideKey = KeyEvent.VK_H;
    private int pauseKey = KeyEvent.VK_P;
    private int gridkey = KeyEvent.VK_G;
    private int restartkey = KeyEvent.VK_R;

    private static final String HIGH_SCORE_FILE = "highscore.txt";
    private Image backgroundImage;
    private Image hideImage;
    private Image snakeImage;
    private Image snakeBody1;
    private Image snakeBody2;
    private Image berry;
    public Image title;


    // Constructor
    public GamePanel() {



        // Initialization
        random = new Random();
        this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
        this.setBackground(Color.BLACK);
        this.setFocusable(true);
        this.addKeyListener(new MyKeyAdapter());
        timer = new Timer(DELAY, this);

        // Buttons
        startButton = createButton("Start", SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2+100, 200,60);
        startButton.setVisible(true);
        retryButton = createButton("Retry", SCREEN_WIDTH / 2 - 100, SCREEN_HEIGHT / 2 + 150, 200, 60);
        settingsButton = createButton("Settings", SCREEN_WIDTH / 2 - 50, SCREEN_HEIGHT / 2 + 220, 100, 30);
        settingsButton.setFont(new Font("Trebuchet MS", Font.BOLD, 21));
        settingsButton.setBorder(new LineBorder(Color.darkGray, 2));
        settingsButton.setForeground(Color.darkGray);
        diffButton = createButton("gameDif", SCREEN_WIDTH/2 - 100, SCREEN_HEIGHT/2+180, 200,60);

        retryButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                restartGame();
            }
        });

        settingsButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                openSettings();
            }
        });
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Start button clicked!");
                startGame();
            }

        });
        diffButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {

                System.out.println("Diff button clicked!");
                if (DELAY > 25 && DELAY < 125) {
                    DELAY -= 5;
                    diffLevel = 100 - DELAY + 25;

                } else {
                    DELAY = 120;
                    diffLevel = 100 - DELAY + 25;

                }



            }

        });


        this.add(retryButton);
        this.add(settingsButton);
        this.add(startButton);
        this.add(diffButton);
        this.setLayout(null);
        startFace();
        // Other initialization code...
        fpsCounter = new FPSCounter();
        // Start the timer here

        timer.start(); // Start the timer here
    }


    // Helper method to create a styled button
    private JButton createButton(String label, int x, int y, int width, int height) {
        JButton button = new JButton(label);
        button.setFont(new Font("Trebuchet MS", Font.BOLD, 42));
        button.setBackground(new Color(30,0,20));
        button.setBorder(new LineBorder(new Color(242,0,182),3));
        button.setForeground(new Color(255,0,182));
        button.setBounds(x, y, width, height);
        button.setVisible(false);
        return button;
    }



    // Helper method to open settings frame
    private void openSettings() {
        SwingUtilities.invokeLater(() -> {
            settingsFrame = new SettingsFrame(leftKey, rightKey, upKey, downKey, pauseKey);
            settingsFrame.setVisible(true);
            settingsFrame.addSaveButtonListener(e -> {
                leftKey = settingsFrame.getLeftKey();
                rightKey = settingsFrame.getRightKey();
                upKey = settingsFrame.getUpKey();
                downKey = settingsFrame.getDownKey();
                pauseKey = settingsFrame.getPauseKey();
                // Update key bindings in the MyKeyAdapter inner class
                removeKeyListener(keyAdapter); // Remove previous key listener
                keyAdapter = new MyKeyAdapter(); // Reassign keyAdapter
                addKeyListener(keyAdapter); // Add new key listener with updated key bindings
            });
        });
    }
    public void setKeyBindings(int newLeftKey, int newRightKey, int newUpKey, int newDownKey, int newPauseKey) {
        leftKey = newLeftKey;
        rightKey = newRightKey;
        upKey = newUpKey;
        downKey = newDownKey;
        pauseKey = newPauseKey;
    }
    public void hideGame(){
        System.out.println("hide the game works");
        paused = true;
        startButton.setVisible(false);
        diffButton.setVisible(false);
        retryButton.setVisible(false);
        settingsButton.setVisible(false);
        if(isStartScreen){
            wasStartScreen=true;
            isStartScreen=false;
        }
        else if(!running && !isStartScreen){
            wasRetryScreen=true;

        }
        else{
            wasRetryScreen=false;
            wasStartScreen=false;
        }


    }
    public void unhideGame(){
        System.out.println("unhide the game works");
        paused = true;
        if(wasStartScreen) {
            startButton.setVisible(true);
            diffButton.setVisible(true);
        }
        else if (wasRetryScreen) {
            retryButton.setVisible(true);
            settingsButton.setVisible(true);
        }



    }



    public void startGame() {
        System.out.println("Start game works?");
        timer.stop();
        newApple();
        loadHighScore();
        running = true;
        startButton.setVisible(false);
        diffButton.setVisible(false);
        running = true;
        timer = new Timer(DELAY, this);
        timer.start();
        isStartScreen = false;
        // Calculate the initial position of the snake's head
        int initialSnakeX = SCREEN_WIDTH / 2 - 75;
        int initialSnakeY = SCREEN_HEIGHT / 2;

        // Set the initial positions of the snake's body parts
        for (int i = 0; i < bodyParts; i++) {
            x[i] = initialSnakeX - i * UNIT_SIZE;
            y[i] = initialSnakeY;
        }

    }
    private void drawBackground(Graphics g) {
        // Load background image
        Image backgroundImage = loadImage("textures/background.png");

        // Draw background image
        if (backgroundImage != null) {
            g.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        } else {
            System.out.println("Failed to load background image.");
        }
    }


    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        drawBackground(g);


        drawGameElements(g);


        fpsCounter.draw(g2d, SCREEN_WIDTH / 2, 30);




    }
    private void drawGameElements(Graphics g){
        Graphics2D g2d = (Graphics2D) g;

        // Load images
        Image backgroundImage = loadImage("textures/background.png");
        Image snakeImage = loadImage("textures/snakehead.png");
        Image snakeBody1 = loadImage("textures/snakebody1.png");
        Image snakeBody2 = loadImage("textures/snakebody2.png");
        Image berry = loadImage("textures/berry.png");
        Image title = loadImage("textures/snake_title.png");
        Image hideImage = loadImage("textures/hide.png");



        if (backgroundImage != null && snakeImage != null && snakeBody1 != null) {
            // Draw background
            g2d.drawImage(backgroundImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);

            if (isStartScreen && title != null) {
                startScreen(g2d);

                g2d.drawImage(title, SCREEN_WIDTH / 2 - 175, SCREEN_HEIGHT / 2 - 140 , 350, 100, null);
            }
            else {
                // Rotate snake image based on direction
                Image rotatedSnakeImage = rotateImage(snakeImage, direction);
                draw(g2d, rotatedSnakeImage, snakeBody1, snakeBody2, berry); // Draw game elements
            }
        }   else {
            System.out.println("Failed to load images.");
        }

        if (hidden){

            g2d.drawImage(hideImage, 0, 0, SCREEN_WIDTH, SCREEN_HEIGHT, null);
        }


    }

    private Image rotateImage(Image image, char direction) {
        // Create a blank buffered image
        BufferedImage bufferedImage = new BufferedImage(image.getWidth(null), image.getHeight(null), BufferedImage.TYPE_INT_ARGB);

        // Create Graphics2D object from the buffered image
        Graphics2D g2d = bufferedImage.createGraphics();

        // Set the rotation angle based on direction
        double rotationAngle = 0; // Default angle (no rotation)
        switch (direction) {
            case 'U':
                rotationAngle = -Math.PI / 2; // 90 degrees counter-clockwise
                break;
            case 'D':
                rotationAngle = Math.PI / 2; // 90 degrees clockwise
                break;
            case 'L':
                rotationAngle = Math.PI; // 180 degrees
                break;
            case 'R':
                rotationAngle = 0; // No rotation
                break;
            default:
                // Handle other cases or use a default angle
                break;
        }

        // Translate and rotate the graphics context
        g2d.translate(image.getWidth(null) / 2, image.getHeight(null) / 2);
        g2d.rotate(rotationAngle);
        g2d.translate(-image.getWidth(null) / 2, -image.getHeight(null) / 2);

        // Draw the rotated image onto the buffered image
        g2d.drawImage(image, 0, 0, null);

        // Dispose the graphics context
        g2d.dispose();

        // Return the rotated image
        return bufferedImage;
    }


    private Image loadImage(String imagePath) {
        try {
            // Load image using ImageIO
            BufferedImage image = ImageIO.read(getClass().getResource(imagePath));
            return image;
        } catch (IOException e) {
            System.err.println("Error loading image: " + e.getMessage());
            return null;
        }
    }


    public void draw(Graphics2D g2d, Image snakeImage, Image snakeBody1, Image snakeBody2, Image berry) {
        if (running) {





            if (grid) {
                for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
                    g2d.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
                    g2d.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
                }
            }



            g2d.setFont(new Font("Trebuchet MS", Font.BOLD, 42));
            FontMetrics metrics = getFontMetrics(g2d.getFont());
            g2d.drawString("Score: " + applesEaten, (SCREEN_WIDTH - metrics.stringWidth("Score: " + applesEaten)) / 2, g2d.getFont().getSize());

            g2d.setColor(Color.RED);
            g2d.drawImage(berry, appleX, appleY, UNIT_SIZE, UNIT_SIZE, null);

            for (int i = 0; i < bodyParts; i++) {
                if (i == 0) {
                    g2d.drawImage(snakeImage, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
                } else if (i % 2 == 0) {
                    g2d.drawImage(snakeBody1, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
                } else {
                    g2d.drawImage(snakeBody2, x[i], y[i], UNIT_SIZE, UNIT_SIZE, null);
                }
            }
        } else {
            gameOver(g2d);
        }
    }

    public void newApple() {
        appleX = (random.nextInt((int) ((SCREEN_WIDTH - 2 * UNIT_SIZE) / UNIT_SIZE)) + 1) * UNIT_SIZE;
        appleY = (random.nextInt((int) ((SCREEN_HEIGHT - 2 * UNIT_SIZE) / UNIT_SIZE)) + 1) * UNIT_SIZE;
    }



    private void togglePause() {
        paused = !paused;
        if (paused) {
            timer.stop(); // Pause the timer
        } else {
            timer.start(); // Resume the timer
        }
    }
    private void toggleHide() {
        hidden = !hidden;
        if (hidden) {
            hideGame();

        }
        else{
            unhideGame();
        }


    }


    public void move() {
        for (int i = bodyParts; i > 0; i--) {
            x[i] = x[i - 1];
            y[i] = y[i - 1];
        }
        switch (direction) {
            case 'U':
                y[0] = y[0] - UNIT_SIZE;
                break;
            case 'D':
                y[0] = y[0] + UNIT_SIZE;
                break;
            case 'L':
                x[0] = x[0] - UNIT_SIZE;
                break;
            case 'R':
                x[0] = x[0] + UNIT_SIZE;
                break;
        }
    }

    private void checkHighScore() {
        if (applesEaten > highScore) {
            highScore = applesEaten;
            saveHighScore();
        }
    }

    private void saveHighScore() {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(HIGH_SCORE_FILE))) {
            writer.write(Integer.toString(highScore));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadHighScore() {
        try (BufferedReader reader = new BufferedReader(new FileReader(HIGH_SCORE_FILE))) {
            String line = reader.readLine();
            if (line != null) {
                highScore = Integer.parseInt(line);
            }
        } catch (IOException | NumberFormatException e) {
            e.printStackTrace();
        }
    }

    public void checkApple() {
        if ((x[0] == appleX) && (y[0] == appleY)) {
            bodyParts++;
            applesEaten++;

            newApple();
            checkHighScore();
        }
    }

    public void checkCollisions() {
        for (int i = bodyParts; i > 0; i--) {
            if ((x[0] == x[i]) && (y[0] == y[i])) {
                running = false; // Snake collides with itself
            }
        }

        // Check if snake's head is one unit away from the border
        if (x[0] < UNIT_SIZE || x[0] >= SCREEN_WIDTH - UNIT_SIZE ||
                y[0] < UNIT_SIZE || y[0] >= SCREEN_HEIGHT - UNIT_SIZE) {
            running = false; // Snake collides with the border
        }
    }

    public void gameOver(Graphics g) {
        if (!running) {
            g.drawString("Difficulty Level:" + diffLevel, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 - 160);
            g.drawString("Delay:" + DELAY, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 - 145);
        }
        g.setColor(new Color(218, 61, 132));
        g.setFont(new Font("Trebuchet MS", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g.getFont());
        g.drawString("Game over", (SCREEN_WIDTH - metrics1.stringWidth("Game Over")) / 2, SCREEN_HEIGHT / 2);
        g.setColor(new Color(190, 7, 145));
        g.setFont(new Font("Trebuchet MS", Font.BOLD, 52));
        FontMetrics metrics2 = getFontMetrics(g.getFont());
        g.drawString("Final Score: " + applesEaten, (SCREEN_WIDTH - metrics2.stringWidth("Final Score: " + applesEaten)) / 2, SCREEN_HEIGHT/2-170);
        if(!wasRetryScreen){
            retryButton.setVisible(true);}
        g.setColor(new Color(124, 25, 69));
        g.setFont(new Font("Trebuchet MS", Font.BOLD, 40));
        g.drawString("High Score: " + highScore, (SCREEN_WIDTH - metrics2.stringWidth("High Score: " + applesEaten)) / 2, SCREEN_HEIGHT / 2 + 70);
        settingsButton.setVisible(true);
        startButton.setVisible(false);
        isStartScreen = false;
        running = false;
    }


    public void startScreen(Graphics2D g2d) {
        if (!running) {
            g2d.drawString("Difficulty Level:" + diffLevel, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 - 160);
            g2d.drawString("Delay:" + DELAY, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 - 145);
        }
        g2d.setColor(Color.DARK_GRAY);
        g2d.setFont(new Font("Trebuchet MS", Font.BOLD, 75));
        FontMetrics metrics1 = getFontMetrics(g2d.getFont());
        g2d.drawString("", (SCREEN_WIDTH - metrics1.stringWidth("Snake 2.0")) / 2, SCREEN_HEIGHT / 2);
        g2d.drawImage(title, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 , 50, 20, null);

        // Draw title image
        if (title != null) {

            g2d.drawImage(title, SCREEN_WIDTH / 2, SCREEN_HEIGHT / 2 + 100, 100, 50, null);
        }

        startButton.setVisible(true);
        diffButton.setVisible(true);
    }
    public void restartGame() {
        timer.stop();
        for (int i = 0; i < bodyParts; i++) {
            x[i] = 0;
            y[i] = 0;
        }

        bodyParts = 6;
        applesEaten = 0;
        direction = 'R';
        running = true;

        retryButton.setVisible(false);
        settingsButton.setVisible(false);
        startButton.setVisible(false);

        startGame();
    }

    public void startFace(){
        startButton.setVisible(isStartScreen);
    }

    @Override
    public void actionPerformed(ActionEvent e) {

        if (running && !paused) {
            move();
            checkApple();
            checkCollisions();
        }
        fpsCounter.update();
        repaint();
    }

    // Inner class for key events

    public class MyKeyAdapter extends KeyAdapter {
        @Override
        public void keyPressed(KeyEvent e) {
            int keyCode = e.getKeyCode();
            if (keyCode == leftKey) {
                if(direction!='R') {
                    direction = 'L';
                }
                // Handle left key press
            } else if (keyCode == rightKey) {
                if(direction!='L'){
                    direction = 'R';
                }
                // Handle right key press
            } else if (keyCode == upKey) {
                if(direction!='D'){
                    direction = 'U';
                }
                // Handle up key press
            } else if (keyCode == downKey) {
                if(direction!='U'){
                    direction = 'D';
                }
                // Handle down key press
            } else if (keyCode == pauseKey) {
                togglePause();
                // Handle pause key press
            } else if (keyCode == gridkey) {
                grid=true;
            } else if (keyCode == hideKey) {
                toggleHide();
            } else if (keyCode == restartkey && !running) {
                restartGame();
            } else {
                // Handle other keys if needed
            }
        }
    }
}

