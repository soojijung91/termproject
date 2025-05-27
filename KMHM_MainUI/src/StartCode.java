import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StartCode extends JFrame {

    private JButton startButton;
    private JButton howToButton;
    private BackgroundPanel background;

    public StartCode() {
        setTitle("Kill Me Heal Me");
        setSize(1440, 1040);
        setResizable(true); // ✅ 창 크기 조절 허용
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // 절대 배치

        // 배경 패널
        BackgroundPanel background = new BackgroundPanel("\\img\\StartFrame.png");
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        add(background);

        // 버튼 이미지 리사이즈
        ImageIcon rawStart = new ImageIcon("\\img\\StartButton.png");
        ImageIcon rawHowTo = new ImageIcon("\\img\\HowToButton.png");

        int btnWidth = 300;
        int btnHeight = 150;

        Image scaledStartImg = rawStart.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
        Image scaledHowToImg = rawHowTo.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

        startButton = new JButton(new ImageIcon(scaledStartImg));
        howToButton = new JButton(new ImageIcon(scaledHowToImg));
        setButtonStyle(startButton);
        setButtonStyle(howToButton);

        background.add(startButton);
        background.add(howToButton);

        // 버튼 기능
        startButton.addActionListener(e -> {
            dispose();
            new KMHM_MainUI();
        });

        howToButton.addActionListener(e -> new HowToFrame());

        // 🔁 창 크기 조절에 반응하는 리스너
        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                // 배경 사이즈 맞추기
                background.setBounds(0, 0, w, h);

                // 버튼 위치 반응형 조정
                int btnWidth = 300;
                int btnHeight = 150;
                int yOffset = h - 260;

                startButton.setBounds(w / 4 - btnWidth / 2, yOffset, btnWidth, btnHeight);
                howToButton.setBounds(3 * w / 4 - btnWidth / 2, yOffset, btnWidth, btnHeight);
            }
        });

        // 최초 위치 한번 설정
        SwingUtilities.invokeLater(() -> {
            setVisible(true);
            dispatchEvent(new ComponentEvent(this, ComponentEvent.COMPONENT_RESIZED));
        });
    }

    private void setButtonStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
    }

    class BackgroundPanel extends JPanel {
        private Image backgroundImage;

        public BackgroundPanel(String path) {
            backgroundImage = new ImageIcon(path).getImage();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(StartCode::new);
    }
}
