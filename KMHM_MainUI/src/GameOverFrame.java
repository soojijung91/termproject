import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class GameOverFrame extends JFrame {

    public GameOverFrame(String timeResult) {
        setTitle("Game Over");
        setSize(1073, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(true);

        // ✅ 배경 이미지 패널
        BackgroundPanel background = new BackgroundPanel("/img/GameOverImg.png");
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        add(background);

        // ✅ 결과 라벨
        JLabel resultLabel = new JLabel("Time: " + timeResult, SwingConstants.CENTER);
        resultLabel.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        resultLabel.setForeground(Color.WHITE);
        resultLabel.setBounds(400, 200, 640, 80);
        background.add(resultLabel);

        // ✅ 버튼 이미지 로드
        ImageIcon rawRestart = new ImageIcon(getClass().getResource("/img/RestartButton.png"));
        ImageIcon rawExit = new ImageIcon(getClass().getResource("/img/ExitButton.png"));

        int btnWidth = 300;
        int btnHeight = 150;

        Image restartImg = rawRestart.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
        Image exitImg = rawExit.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

        JButton restartButton = new JButton(new ImageIcon(restartImg));
        JButton exitButton = new JButton(new ImageIcon(exitImg));

        setButtonStyle(restartButton);
        setButtonStyle(exitButton);

        restartButton.setBounds(360, 780, btnWidth, btnHeight);
        exitButton.setBounds(780, 780, btnWidth, btnHeight);

        background.add(restartButton);
        background.add(exitButton);

        // ✅ 버튼 기능
        restartButton.addActionListener(e -> {
            dispose();
            new StartCode(); // 다시 실행
        });

        exitButton.addActionListener(e -> System.exit(0));

        // 리사이징 대응
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                background.setBounds(0, 0, getWidth(), getHeight());
                int w = getWidth(), h = getHeight();
                resultLabel.setBounds(w / 2 - 320, h / 2 - 300, 640, 80);
                restartButton.setBounds(w / 4 - btnWidth / 2, h - 260, btnWidth, btnHeight);
                exitButton.setBounds(3 * w / 4 - btnWidth / 2, h - 260, btnWidth, btnHeight);
            }
        });

        setVisible(true);
    }

    private void setButtonStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        // ✅ 여기 이거 추가!!
        public BackgroundPanel(String path) {
            backgroundImage = new ImageIcon(getClass().getResource(path)).getImage();
        }

        public void setBackgroundImage(String path) {
            backgroundImage = new ImageIcon(getClass().getResource(path)).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

}
