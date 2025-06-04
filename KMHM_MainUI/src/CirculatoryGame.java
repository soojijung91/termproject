import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import javax.swing.*;

public class CirculatoryGame extends JFrame {
    public CirculatoryGame() {
        setTitle("순환계 미니게임");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1080, 768);
        setLocationRelativeTo(null);
        setResizable(true); // 창 크기 조절 허용
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

    private Image heartImage = new ImageIcon(getClass().getResource("\\img\\heart.png")).getImage();
    private Image obstacle1 = new ImageIcon(getClass().getResource("\\img\\erythrocyte.png")).getImage();
    private Image obstacle2 = new ImageIcon(getClass().getResource("\\img\\leukocyte.png")).getImage();
    private Image background = new ImageIcon(getClass().getResource("\\img\\FrameC.png")).getImage();

    private final int GAUGE_WIDTH = 300;
    private final int GAUGE_HEIGHT = 20;

    private final double MOVE_ZONE_SCALE = 2.5;
    private int moveZoneWidth;
    private int gaugeY;

    private int playerX;
    private int playerY;
    private int playerWidth;
    private int playerHeight;

    private ArrayList<Obstacle> obstacles = new ArrayList<>();
    private int obstacleSpeed = 5;
    private double spawnRate = 0.02;

    public GamePanel() {
        setFocusable(true);
        setLayout(null);
        addKeyListener(this);

        gameTimer = new Timer(20, this);
        countdownTimer = new Timer(1000, e -> {
            timeLeft--;
            if (obstacleSpeed < 15)
                obstacleSpeed++;
            if (spawnRate < 0.07)
                spawnRate += 0.005;

            if (timeLeft <= 0) {
                gameTimer.stop();
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "✅ Game Clear!");
                System.exit(0); // 게임 클리어 후 자동 종료
            }
        });

        gameTimer.start();
        countdownTimer.start();
    }

    @Override
    public void addNotify() {
        super.addNotify();
        updateLayout(); // 초기 위치 설정
    }

    private void updateLayout() {
        int width = getWidth();
        int height = getHeight();

        moveZoneWidth = (int) (GAUGE_WIDTH * MOVE_ZONE_SCALE);
        gaugeY = height - 100;

        // 창 너비에 비례한 하트 크기 설정
        playerWidth = Math.max(30, width / 27);  // 1080 / 27 ≈ 40
        playerHeight = playerWidth;

        playerY = gaugeY - playerHeight - 30;

        // 초기 중앙 위치 설정 또는 화면 크기에 따라 보정
        if (playerX == 0) {
            playerX = width / 2 - playerWidth / 2;
        } else {
            int minX = width / 2 - moveZoneWidth / 2;
            int maxX = width / 2 + moveZoneWidth / 2 - playerWidth;
            playerX = Math.max(minX, Math.min(playerX, maxX));
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        updateLayout();

        for (Obstacle obs : obstacles) {
            obs.y += obstacleSpeed;
        }

        Rectangle playerRect = new Rectangle(playerX + 5, playerY + 5, playerWidth - 10, playerHeight - 10);
        Iterator<Obstacle> iterator = obstacles.iterator();
        while (iterator.hasNext()) {
            Obstacle obs = iterator.next();
            Rectangle obsRect = new Rectangle(obs.x + 5, obs.y + 5, 30, 30);
            if (playerRect.intersects(obsRect)) {
                gameTimer.stop();
                countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "❌ Game Over!");
                System.exit(0); // 게임 오버 시 자동 종료
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

        // 시간 게이지
        int gaugeX = centerX - GAUGE_WIDTH / 2;
        g.setColor(Color.DARK_GRAY);
        g.fillRect(gaugeX, gaugeY, GAUGE_WIDTH, GAUGE_HEIGHT);

        int fillWidth = (int) (GAUGE_WIDTH * (timeLeft / 20.0));
        g.setColor(new Color(0, 255, 255));
        g.fillRect(gaugeX, gaugeY, fillWidth, GAUGE_HEIGHT);

        g.setColor(Color.WHITE);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        g.drawString("Time Left: " + timeLeft + "s", gaugeX, gaugeY + GAUGE_HEIGHT + 16);

        // 이동 레일
        int moveZoneX = centerX - moveZoneWidth / 2;
        int guideBarY = playerY + playerHeight + 5;
        g.setColor(new Color(100, 100, 100, 120));
        g.fillRoundRect(moveZoneX, guideBarY, moveZoneWidth, 8, 10, 10);

        // 하트
        g.drawImage(heartImage, playerX, playerY, playerWidth, playerHeight, this);

        // 장애물
        for (Obstacle obs : obstacles) {
            g.drawImage(obs.image, obs.x, obs.y, 40, 40, this);
        }
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int key = e.getKeyCode();
        int moveZoneX = getWidth() / 2 - moveZoneWidth / 2;

        if (key == KeyEvent.VK_LEFT && playerX > moveZoneX) {
            playerX -= 10;
        } else if (key == KeyEvent.VK_RIGHT && playerX < moveZoneX + moveZoneWidth - playerWidth) {
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
