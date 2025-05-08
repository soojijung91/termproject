import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class KMHM_GameScreen extends JFrame {
    private Rectangle lungArea = new Rectangle(500, 220, 100, 120); // 호흡계
    private Rectangle heartArea = new Rectangle(500, 170, 80, 60); // 순환계
    private Rectangle digestiveArea = new Rectangle(500, 330, 90, 80); // 소화계
    private Rectangle brainArea = new Rectangle(520, 100, 70, 60); // 신경계

    private double lungGauge = 30;
    private double heartGauge = 50;
    private double digestiveGauge = 65;
    private double brainGauge = 80;

    private Image bgImage, gaugePanel;

    public KMHM_GameScreen() {
        setTitle("KMHM - Game UI");
        setSize(1073, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        bgImage = new ImageIcon("src/img/스크린샷 2025-05-07 154846.png").getImage();

        JPanel panel = new JPanel() {
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bgImage, 0, 0, getWidth(), getHeight(), this);
                g.drawImage(gaugePanel, 60, 110, 200, 180, this); // 패널도 약간 위로 올림 + 넓힘

                int startX = 65;
                int startY = 115; // ✅ 더 위쪽으로 올림
                int gaugeHeight = 35;
                int fullWidth = 180; // ✅ 가로 길이 넓힘
                int gap = 10;

                drawGauge(g, lungGauge, startX, startY + 0 * (gaugeHeight + gap), fullWidth, gaugeHeight, Color.CYAN);
                drawGauge(g, heartGauge, startX, startY + 1 * (gaugeHeight + gap), fullWidth, gaugeHeight, Color.RED);
                drawGauge(g, digestiveGauge, startX, startY + 2 * (gaugeHeight + gap), fullWidth, gaugeHeight,
                        Color.GREEN);
                drawGauge(g, brainGauge, startX, startY + 3 * (gaugeHeight + gap), fullWidth, gaugeHeight,
                        Color.YELLOW);
            }

            private void drawGauge(Graphics g, double value, int x, int y, int fullWidth, int height, Color color) {
                int width = (int) (value * (fullWidth / 100.0));
                g.setColor(Color.DARK_GRAY);
                g.fillRect(x, y, fullWidth, height);
                g.setColor(color);
                g.fillRect(x, y, width, height);
                g.setFont(new Font("Arial", Font.BOLD, 14));
                g.drawString((int) value + " %", x + fullWidth + 10, y + height - 10);
            }
        };

        panel.setLayout(null);
        panel.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (lungArea.contains(p))
                    lungGauge = Math.min(100, lungGauge + 3);
                if (heartArea.contains(p))
                    heartGauge = Math.min(100, heartGauge + 3);
                if (digestiveArea.contains(p))
                    digestiveGauge = Math.min(100, digestiveGauge + 3);
                if (brainArea.contains(p))
                    brainGauge = Math.min(100, brainGauge + 3);
                repaint();
            }
        });

        add(panel);

        Timer repaintTimer = new Timer(500, e -> {
            lungGauge = Math.max(0, lungGauge - 0.2);
            heartGauge = Math.max(0, heartGauge - 0.2);
            digestiveGauge = Math.max(0, digestiveGauge - 0.2);
            brainGauge = Math.max(0, brainGauge - 0.2);
            repaint();
        });
        repaintTimer.start();

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new KMHM_GameScreen());
    }
}
