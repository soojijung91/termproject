import java.awt.*;
import javax.swing.*;

public class HowToFrame extends JFrame {

    public HowToFrame() {
        setTitle("User Info");
        setSize(1073, 790);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // ✅ 배경 이미지
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/img/UIBackground.png")));
        background.setBounds(0, 0, 1100, 820);
        background.setLayout(null);
        add(background);


        // ✅ 제목
        JLabel titleLabel = new JLabel("환자 정보 입력", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(150, 50, 800, 60);
        background.add(titleLabel);

        // ✅ 라벨 및 입력 필드
        Font labelFont = new Font("맑은 고딕", Font.BOLD, 24);
        Font fieldFont = new Font("맑은 고딕", Font.PLAIN, 22);

        String[] labels = { "이름", "나이", "성별", "키 (cm)", "몸무게 (kg)" };
        int y = 160;
        JTextField nameField = new JTextField();
        JTextField ageField = new JTextField();
        JComboBox<String> genderBox = new JComboBox<>(new String[] { "남성", "여성", "기타" });
        JTextField heightField = new JTextField();
        JTextField weightField = new JTextField();

        JComponent[] fields = { nameField, ageField, genderBox, heightField, weightField };

        for (int i = 0; i < labels.length; i++) {
            JLabel label = new JLabel(labels[i] + ":");
            label.setFont(labelFont);
            label.setForeground(Color.WHITE);
            label.setBounds(250, y, 150, 40);
            background.add(label);

            JComponent field = fields[i];
            field.setFont(fieldFont);
            field.setBounds(420, y, 300, 40);
            background.add(field);

            y += 70;
        }

        // ✅ 확인 버튼
        JButton submitBtn = new JButton("확인");
        submitBtn.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        submitBtn.setBounds(460, 550, 140, 50);
        submitBtn.setFocusPainted(false);
        submitBtn.setBackground(Color.WHITE);
        submitBtn.setForeground(Color.BLACK);

        submitBtn.addActionListener(e -> {
            String name = nameField.getText().trim();
            String age = ageField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            String height = heightField.getText().trim();
            String weight = weightField.getText().trim();

            // 간단한 유효성 검사
            if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                return;
            }

            System.out.println("입력된 정보:");
            System.out.println("이름: " + name);
            System.out.println("나이: " + age);
            System.out.println("성별: " + gender);
            System.out.println("키: " + height);
            System.out.println("몸무게: " + weight);

            // TODO: 필요한 로직에 따라 해당 정보를 저장하거나 넘겨주세요

            dispose(); // 창 닫기
        });

        background.add(submitBtn);

        setVisible(true);
    }
}
