import java.awt.*;
import javax.swing.*;

public class GameOverFrame extends JFrame {
    private JButton restartButton;
    private JButton closeButton;

    public GameOverFrame() {
        setTitle("Game Over");
        setSize(1440, 1040);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(null);

        // 배경
        JLabel background = new JLabel(new ImageIcon("StartFrame.png"));
        background.setBounds(0, 0, 1440, 1040);
        background.setLayout(null);
        add(background);

        // 버튼 이미지
        ImageIcon restartIcon = new ImageIcon("RestartButton.png");
        ImageIcon closeIcon = new ImageIcon("CloseButton.png");

        int btnWidth = 300;
        int btnHeight = 110;

        Image scaledRestart = restartIcon.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);
        Image scaledClose = closeIcon.getImage().getScaledInstance(btnWidth, btnHeight, Image.SCALE_SMOOTH);

        restartButton = new JButton(new ImageIcon(scaledRestart));
        closeButton = new JButton(new ImageIcon(scaledClose));
        setButtonStyle(restartButton);
        setButtonStyle(closeButton);

        // 위치
        restartButton.setBounds(360, 780, btnWidth, btnHeight);
        closeButton.setBounds(780, 780, btnWidth, btnHeight);

        background.add(restartButton);
        background.add(closeButton);

        // 기능
        restartButton.addActionListener(e -> {
            dispose();
            new KMHM_MainUI();
        });

        closeButton.addActionListener(e -> System.exit(0));

        setVisible(true);
    }

    private void setButtonStyle(JButton btn) {
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        btn.setMargin(new Insets(0, 0, 0, 0));
        btn.setIconTextGap(0);
        btn.setOpaque(false);
        btn.setBorder(BorderFactory.createEmptyBorder());
    }
}
