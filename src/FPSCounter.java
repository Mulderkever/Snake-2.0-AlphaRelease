import java.awt.*;

public class FPSCounter {
    private long lastTime;
    private int frames;
    private int fps;

    public FPSCounter() {
        lastTime = System.currentTimeMillis();
        frames = 0;
        fps = 0;
    }

    public void update() {
        long currentTime = System.currentTimeMillis();
        frames++;

        if (currentTime - lastTime >= 1000) {
            fps = frames;
            frames = 0;
            lastTime = currentTime;
        }
    }

    public int getFPS() {
        return fps;
    }

    public void draw(Graphics2D g2d, int x, int y) {
        g2d.setColor(Color.WHITE);
        g2d.setFont(new Font("Arial", Font.BOLD, 16));
        g2d.drawString("FPS: " + fps, x, y);
    }
}
