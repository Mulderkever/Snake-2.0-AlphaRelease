import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;

public class SettingsFrame extends JFrame {
    private JButton saveButton;
    private GamePanel gamePanel;
    private JTextField leftKeyField;
    private JTextField rightKeyField;
    private JTextField upKeyField;
    private JTextField downKeyField;
    private JTextField pauseKeyField;

    // Existing constructor
    public SettingsFrame(int leftKey, int rightKey, int upKey, int downKey, int pauseKey) {
        initializeComponents(leftKey, rightKey, upKey, downKey, pauseKey);
    }

    // New constructor with GamePanel parameter
    public SettingsFrame(GamePanel gamePanel, int leftKey, int rightKey, int upKey, int downKey, int pauseKey) {
        this.gamePanel = gamePanel;
        initializeComponents(leftKey, rightKey, upKey, downKey, pauseKey);
    }
    public int getLeftKey() {
        String leftKeyText = leftKeyField.getText();
        if (leftKeyText != null && !leftKeyText.isEmpty()) {
            char keyChar = leftKeyText.toUpperCase().charAt(0);
            return KeyEvent.getExtendedKeyCodeForChar(keyChar);
        }
        return KeyEvent.VK_UNDEFINED;
    }

    public int getRightKey() {
        String rightKeyText = rightKeyField.getText();
        if (rightKeyText != null && !rightKeyText.isEmpty()) {
            char keyChar = rightKeyText.toUpperCase().charAt(0);
            return KeyEvent.getExtendedKeyCodeForChar(keyChar);
        }
        return KeyEvent.VK_UNDEFINED;
    }

    public int getUpKey() {
        String upKeyText = upKeyField.getText();
        if (upKeyText != null && !upKeyText.isEmpty()) {
            char keyChar = upKeyText.toUpperCase().charAt(0);
            return KeyEvent.getExtendedKeyCodeForChar(keyChar);
        }
        return KeyEvent.VK_UNDEFINED;
    }

    public int getDownKey() {
        String downKeyText = downKeyField.getText();
        if (downKeyText != null && !downKeyText.isEmpty()) {
            char keyChar = downKeyText.toUpperCase().charAt(0);
            return KeyEvent.getExtendedKeyCodeForChar(keyChar);
        }
        return KeyEvent.VK_UNDEFINED;
    }

    public int getPauseKey() {
        String pauseKeyText = pauseKeyField.getText();
        if (pauseKeyText != null && !pauseKeyText.isEmpty()) {
            char keyChar = pauseKeyText.toUpperCase().charAt(0);
            return KeyEvent.getExtendedKeyCodeForChar(keyChar);
        }
        return KeyEvent.VK_UNDEFINED;
    }

    private void initializeComponents(int leftKey, int rightKey, int upKey, int downKey, int pauseKey) {
        setTitle("Settings");
        setSize(400, 300);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(new GridLayout(6, 2));

        saveButton = new JButton("Save");
        leftKeyField = createKeyField(leftKey);
        rightKeyField = createKeyField(rightKey);
        upKeyField = createKeyField(upKey);
        downKeyField = createKeyField(downKey);
        pauseKeyField = createKeyField(pauseKey);

        add(new JLabel("Left Key:"));
        add(leftKeyField);
        add(new JLabel("Right Key:"));
        add(rightKeyField);
        add(new JLabel("Up Key:"));
        add(upKeyField);
        add(new JLabel("Down Key:"));
        add(downKeyField);
        add(new JLabel("Pause Key:"));
        add(pauseKeyField);
        add(new JLabel());
        add(saveButton);

        saveButton.addActionListener(e -> saveKeyBindings());
    }

    private JTextField createKeyField(int keyCode) {
        JTextField textField = new JTextField(KeyEvent.getKeyText(keyCode));
        textField.setEditable(false);
        textField.setFocusable(false);
        textField.addMouseListener(new KeyBindingMouseListener(textField));
        return textField;
    }

    public void addSaveButtonListener(ActionListener listener) {
        saveButton.addActionListener(listener);
    }

    private void saveKeyBindings() {
        int newLeftKey = getKeyCode(leftKeyField.getText());
        int newRightKey = getKeyCode(rightKeyField.getText());
        int newUpKey = getKeyCode(upKeyField.getText());
        int newDownKey = getKeyCode(downKeyField.getText());
        int newPauseKey = getKeyCode(pauseKeyField.getText());

        if (gamePanel != null) {
            gamePanel.setKeyBindings(newLeftKey, newRightKey, newUpKey, newDownKey, newPauseKey);
        }
        dispose();
    }

    private int getKeyCode(String keyText) {
        if (keyText != null && !keyText.isEmpty()) {
            char keyChar = keyText.toUpperCase().charAt(0);
            return KeyEvent.getExtendedKeyCodeForChar(keyChar);
        }
        return KeyEvent.VK_UNDEFINED;
    }

    public class KeyBindingMouseListener extends java.awt.event.MouseAdapter {
        private final JTextField textField;

        KeyBindingMouseListener(JTextField textField) {
            this.textField = textField;
        }

        @Override
        public void mouseClicked(java.awt.event.MouseEvent evt) {
            textField.setText("Press a key...");
            textField.requestFocus(); // Request focus for capturing key events
            textField.addKeyListener(new java.awt.event.KeyAdapter() {
                @Override
                public void keyPressed(java.awt.event.KeyEvent evt) {
                    int keyCode = evt.getKeyCode();
                    textField.setText(KeyEvent.getKeyText(keyCode));
                    textField.removeKeyListener(this); // Remove listener after rebinding
                    textField.setFocusable(false); // Disable focus to prevent further key events
                }
            });
        }
    }

}
