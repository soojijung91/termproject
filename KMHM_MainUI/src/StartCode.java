import java.awt.*;
import java.awt.event.*;
import javax.swing.*;

public class StartCode extends JFrame {

    private JButton startButton;
    private JButton howToButton;
    private BackgroundPanel background;

    public StartCode() {
        setTitle("Kill Me Heal Me");
        setSize(1073, 768);
        setResizable(true);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        background = new BackgroundPanel("/img/StartFrame.png");
        background.setBounds(0, 0, getWidth(), getHeight());
        background.setLayout(null);
        add(background);

        ImageIcon rawStart = new ImageIcon(getClass().getResource("/img/StartButton.png"));
        ImageIcon rawHowTo = new ImageIcon(getClass().getResource("/img/HowToButton.png"));

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

        startButton.addActionListener(e -> new InfoFrame());

        howToButton.addActionListener(e -> new HowToFrame());

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                int w = getWidth();
                int h = getHeight();

                background.setBounds(0, 0, w, h);

                int yOffset = h - 260;
                startButton.setBounds(w / 4 - btnWidth / 2, yOffset, btnWidth, btnHeight);
                howToButton.setBounds(3 * w / 4 - btnWidth / 2, yOffset, btnWidth, btnHeight);
            }
        });

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

        public BackgroundPanel(String resourcePath) {
            backgroundImage = new ImageIcon(getClass().getResource(resourcePath)).getImage();
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

class InfoFrame extends JFrame {

    public InfoFrame() {
        setTitle("환자 정보 입력");
        setSize(1073, 768);
        setLocationRelativeTo(null);
        setResizable(false);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        JPanel backgroundPanel = new JPanel() {
            private final Image bg = new ImageIcon(getClass().getResource("/img/InfoBackground.png")).getImage();

            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                g.drawImage(bg, 0, 0, getWidth(), getHeight(), this);
            }
        };
        backgroundPanel.setLayout(null);
        backgroundPanel.setBounds(0, 0, getWidth(), getHeight());
        add(backgroundPanel);

        JLabel[] labels = new JLabel[5];
        JTextField[] fields = new JTextField[5];
        String[] texts = { "이름", "나이", "성별", "키 (cm)", "몸무게 (kg)" };

        for (int i = 0; i < 5; i++) {
            labels[i] = new JLabel(texts[i] + ":");
            labels[i].setForeground(Color.WHITE);
            labels[i].setFont(new Font("맑은 고딕", Font.BOLD, 18));
            labels[i].setBounds(340, 160 + i * 70, 120, 30);
            backgroundPanel.add(labels[i]);

            if (i == 2) {
                fields[i] = null;
                JComboBox<String> genderBox = new JComboBox<>(new String[] { "남성", "여성", "기타" });
                genderBox.setBounds(470, 160 + i * 70, 250, 30);
                genderBox.setFont(new Font("맑은 고딕", Font.PLAIN, 16));
                backgroundPanel.add(genderBox);
                genderBox.setName("genderBox");
            } else {
                fields[i] = new JTextField();
                fields[i].setBounds(470, 160 + i * 70, 250, 30);
                fields[i].setFont(new Font("맑은 고딕", Font.PLAIN, 16));
                backgroundPanel.add(fields[i]);
            }
        }

        JButton confirmBtn = new JButton("OK");
        confirmBtn.setBounds(430, 530, 200, 45);
        confirmBtn.setFont(new Font("맑은 고딕", Font.BOLD, 18));
        confirmBtn.setFocusPainted(false);
        backgroundPanel.add(confirmBtn);

        confirmBtn.addActionListener(e -> {
            String name = fields[0].getText().trim();
            String age = fields[1].getText().trim();
            JComboBox<?> genderBox = (JComboBox<?>) findComponentByName(backgroundPanel, "genderBox");
            String gender = genderBox.getSelectedItem().toString();
            String height = fields[3].getText().trim();
            String weight = fields[4].getText().trim();

            if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                return;
            }

            dispose();
            KMHM_MainUI game = new KMHM_MainUI();
            game.setSize(getSize());
            game.setLocationRelativeTo(null);
            game.setVisible(true);
        });

        setVisible(true);
    }

    private Component findComponentByName(Container container, String name) {
        for (Component c : container.getComponents()) {
            if (name.equals(c.getName()))
                return c;
            if (c instanceof Container) {
                Component child = findComponentByName((Container) c, name);
                if (child != null)
                    return child;
            }
        }
        return null;
    }
}
