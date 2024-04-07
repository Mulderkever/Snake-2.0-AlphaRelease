import javax.swing.*;
import java.awt.Image;
import java.util.Objects;

public class GameFrame extends JFrame{
    GameFrame(){
        this.add(new GamePanel());
        this.setTitle("Snake 2.0");
        ImageIcon imageIcon = new ImageIcon(Objects.requireNonNull(getClass().getResource("/textures/icons.png")));
        Image icon = imageIcon.getImage();
        this.setIconImage(icon);

        this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        this.setResizable(false);
        this.setVisible(true);
        this.pack();
        this.setLocationRelativeTo(null);



    }



}

