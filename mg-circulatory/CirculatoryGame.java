import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class CirculatoryGame extends JFrame {
    public CirculatoryGame() {
        setTitle("순환계 미니게임 - 장애물 피하기");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 768);
        setLocationRelativeTo(null);
        setResizable(false);
        add(new GamePanel());
        setVisible(true);
    }

    public static void main(String[] args) {
        new CirculatoryGame();
    }
}

class GamePanel extends JPanel implements ActionListener, KeyListener {
    private Timer gameTimer;
    private Timer countdownTimer;
    private int timeLeft = 20;

    private Image heartImage = new ImageIcon("heart.png").getImage();
    private Image obstacle1 = new ImageIcon("erythrocyte.png").getImage();
    private Image obstacle2 = new ImageIcon("leukocyte.png").getImage();
    private Image background = new ImageIcon("Frame.png").getImage();

    private final int GAUGE_WIDTH = 300;
    private final int GAUGE_HEIGHT = 20;
    private final int GAUGE_Y = 650;

    private final int MOVE_ZONE_WIDTH = (int)(GAUGE_WIDTH * 2.5); // 750
    private final int PLAYER_WIDTH = 40;
    private final int PLAYER_HEIGHT = 40;

    private int playerX;
    private final int playerY = GAUGE_Y - PLAYER_HEIGHT - 30;

    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private int obstacleSpeed = 5;
    private double spawnRate = 0.02;

    public GamePanel() {
        setFocusable(true);
        setLayout(null);
        addKeyListener(this);

        playerX = getWidth() / 2 - PLAYER_WIDTH / 2; // 초기값은 중앙

        gameTimer = new Timer(20, this);
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            if (obstacleSpeed < 15) obstacleSpeed++;
            if (spawnRate < 0.07) spawnRate += 0.005;

            if (timeLeft <= 0) {
                gameTimer.stop();
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "Game Clear!");
            }
        });

        gameTimer.start();
        countdownTimer.start();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        for (Obstacle obs : obstacles) {
            obs.y += obstacleSpeed;
        }

        Rectangle playerRect = new Rectangle(playerX + 5, playerY + 5, PLAYER_WIDTH - 10, PLAYER_HEIGHT - 10);
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obs = iterator.next();
            Rectangle obsRect = new Rectangle(obs.x + 5, obs.y + 5, 30, 30);
            if (playerRect.intersects(obsRect)) {
                gameTimer.stop();
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "Game Over!");
                return;
            }
        }

        if (Math.random() < spawnRate) {
            int x = (int) (Math.random() * (getWidth() - 40));
            Image img = Math.random() < 0.5 ? obstacle1 : obstacle2;
            obstacles.add(new Obstacle(x, 0, img));
        }

        obstacles.removeIf(obs -> obs.y > getHeight());
        repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        int centerX = getWidth() / 2;
        

    // 배경
        g.drawImage(background, 0, 0, getWidth(), getHeight(), this);

    // ⏳ 시간 게이지
        int gaugeX = centerX - GAUGE_WIDTH / 2;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(gaugeX, GAUGE_Y, GAUGE_WIDTH, GAUGE_HEIGHT);

        int fillWidth = (int)(GAUGE_WIDTH * (timeLeft / 20.0));
        g.setColor(Color.RED);
        g.fillRect(gaugeX, GAUGE_Y, fillWidth, GAUGE_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("Arial", Font.BOLD, 14));
        g.drawString("Time Left: " + timeLeft + "s", gaugeX, GAUGE_Y + GAUGE_HEIGHT + 16);

        // 🟫 하트 이동 가이드 바 (레일)
        int moveZoneX = centerX - MOVE_ZONE_WIDTH / 2;
        int guideBarY = playerY + PLAYER_HEIGHT + 5; // 하트 아래 살짝

        g.setColor(new Color(100, 100, 100, 120)); // 반투명 회색
        g.fillRoundRect(moveZoneX, guideBarY, MOVE_ZONE_WIDTH, 8, 10, 10);

        // 💘 하트
        g.drawImage(heartImage, playerX, playerY, PLAYER_WIDTH, PLAYER_HEIGHT, this);

        // 장애물
        for (Obstacle obs : obstacles) {
            g.drawImage(obs.image, obs.x, obs.y, 40, 40, this);
        }
    }
    

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int centerX = getWidth() / 2;
        int moveZoneX = centerX - MOVE_ZONE_WIDTH / 2;

        if (key == KeyEvent.VK_LEFT && playerX > moveZoneX) {
            playerX -= 10;
        } else if (key == KeyEvent.VK_RIGHT && playerX < moveZoneX + MOVE_ZONE_WIDTH - PLAYER_WIDTH) {
            playerX += 10;
        }
    }

    @Override public void keyReleased(KeyEvent e) {}
    @Override public void keyTyped(KeyEvent e) {}
}

class Obstacle {
    int x, y;
    Image image;

    public Obstacle(int x, int y, Image image) {
        this.x = x;
        this.y = y;
        this.image = image;
    }
}
