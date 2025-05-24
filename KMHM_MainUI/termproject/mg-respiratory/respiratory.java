import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.*;

public class respiratory extends JPanel implements ActionListener, KeyListener {
    private double scale = 0.1;
    private final double TARGET_SCALE = 0.4;
    private final double SCALE_STEP = 0.004; // 천천히 증가
    private final int IMAGE_SIZE = 1024;

    private Timer timer;
    private boolean isGrowing = false;
    private boolean gameEnded = false;

    private BufferedImage lungImage;
    private BufferedImage targetImage;
    private Image backgroundImage;

    private double gaugeProgress = 0.0;

    public respiratory() {
        setFocusable(true);
        addKeyListener(this);
        timer = new Timer(30, this);
        timer.start();

        try {
            lungImage = ImageIO.read(getClass().getResource("/asset/lung.png"));
            targetImage = ImageIO.read(getClass().getResource("/asset/target.png"));
            backgroundImage = new ImageIcon(getClass().getResource("/asset/FrameF.png")).getImage();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2d = (Graphics2D) g;

        g2d.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2d.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        int panelWidth = getWidth();
        int panelHeight = getHeight();
        int centerX = panelWidth / 2;
        int centerY = panelHeight / 2;
        int offsetY = 30;

        if (backgroundImage != null) {
            g2d.drawImage(backgroundImage, 0, 0, panelWidth, panelHeight, this);
        } else {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, panelWidth, panelHeight);
        }

        int targetW = (int)(IMAGE_SIZE * TARGET_SCALE);
        int targetH = (int)(IMAGE_SIZE * TARGET_SCALE);
        int lungW = (int)(IMAGE_SIZE * scale);
        int lungH = (int)(IMAGE_SIZE * scale);
        int targetX = centerX - targetW / 2;
        int targetY = centerY - targetH / 2 - offsetY;
        int lungX = centerX - lungW / 2;
        int lungY = centerY - lungH / 2 - offsetY;

        g2d.drawImage(targetImage, targetX, targetY, targetW, targetH, this);
        g2d.drawImage(lungImage, lungX, lungY, lungW, lungH, this);

        int gaugeWidth = 300;
        int gaugeHeight = 20;
        int gaugeX = centerX - gaugeWidth / 2;
        int gaugeY = panelHeight - 50;

        g2d.setColor(Color.DARK_GRAY);
        g2d.fillRect(gaugeX, gaugeY, gaugeWidth, gaugeHeight);
        int fillWidth = (int)(gaugeWidth * gaugeProgress);
        g2d.setColor(Color.GREEN);
        g2d.fillRect(gaugeX, gaugeY, fillWidth, gaugeHeight);
        g2d.setColor(Color.WHITE);
        g2d.drawRect(gaugeX, gaugeY, gaugeWidth, gaugeHeight);
        g2d.drawString(String.format("Progress: %.0f%%", gaugeProgress * 100), gaugeX, gaugeY - 10);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (gameEnded) return;

        if (isGrowing) {
            scale = Math.min(scale + SCALE_STEP, TARGET_SCALE); // 천천히 증가
            gaugeProgress = scale / TARGET_SCALE; // 게이지는 비율로
        }

        repaint();
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (gameEnded) return;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isGrowing = true;
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
        if (gameEnded) return;
        if (e.getKeyCode() == KeyEvent.VK_SPACE) {
            isGrowing = false;
            evaluateSuccess();
        }
    }

    private void evaluateSuccess() {
        double scaleDiff = Math.abs(scale - TARGET_SCALE);
        boolean sizeMatch = scaleDiff <= 0.005;
        boolean gaugeFull = gaugeProgress >= 0.99;

        if (gaugeFull && sizeMatch) {
            gameEnded = true;
            JOptionPane.showMessageDialog(this, "✅ Game Clear!", "성공", JOptionPane.INFORMATION_MESSAGE);
        } else {
            gameEnded = true;
            JOptionPane.showMessageDialog(this, "❌ Game Over!", "실패", JOptionPane.ERROR_MESSAGE);
        }

        System.exit(0);
    }

    @Override
    public void keyTyped(KeyEvent e) {}

    public static void main(String[] args) {
        JFrame frame = new JFrame("호흡계 미니게임");
        respiratory gamePanel = new respiratory();
        frame.add(gamePanel);
        frame.setSize(1024, 768);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}
