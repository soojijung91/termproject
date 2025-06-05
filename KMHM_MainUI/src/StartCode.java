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

        // ✅ 사용자 정보 입력 및 KMHM_MainUI로 전달
        startButton.addActionListener(e -> {
            JTextField nameField = new JTextField();
            JTextField ageField = new JTextField();
            String[] genderOptions = { "남성", "여성", "기타" };
            JComboBox<String> genderBox = new JComboBox<>(genderOptions);
            JTextField heightField = new JTextField();
            JTextField weightField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(0, 2, 10, 10));
            panel.add(new JLabel("이름:"));
            panel.add(nameField);
            panel.add(new JLabel("나이:"));
            panel.add(ageField);
            panel.add(new JLabel("성별:"));
            panel.add(genderBox);
            panel.add(new JLabel("키 (cm):"));
            panel.add(heightField);
            panel.add(new JLabel("몸무게 (kg):"));
            panel.add(weightField);

            int result = JOptionPane.showConfirmDialog(
                    this, panel, "환자 정보 입력", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

            if (result == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String age = ageField.getText().trim();
                String gender = (String) genderBox.getSelectedItem();
                String height = heightField.getText().trim();
                String weight = weightField.getText().trim();

                if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                    return;
                }

                System.out.println("이름: " + name);
                System.out.println("나이: " + age);
                System.out.println("성별: " + gender);
                System.out.println("키: " + height);
                System.out.println("몸무게: " + weight);

                dispose();

                // ✅ 환자 정보를 전달하는 생성자 사용
                KMHM_MainUI game = new KMHM_MainUI(name, age, gender, height, weight);
                game.setSize(getSize());
                game.setLocationRelativeTo(null);
                game.setVisible(true);
            }
        });

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
