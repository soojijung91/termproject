import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.function.Consumer;

public class UserInfoDialog extends JDialog {

    public static class UserInfo {
        public String name, age, gender, height, weight;
    }

    public UserInfoDialog(Frame owner, Consumer<UserInfo> onSubmit) {
        super(owner, "환자 정보 입력", true);
        setSize(500, 500);
        setLocationRelativeTo(owner);
        setLayout(null);
        setUndecorated(true);

        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/img/InfoBackground.png")));
        background.setBounds(0, 0, 500, 500);
        background.setLayout(null);
        add(background);

        Font font = new Font("맑은 고딕", Font.PLAIN, 18);
        int y = 80;

        JLabel nameLabel = new JLabel("이름:");
        nameLabel.setBounds(80, y, 100, 30);
        nameLabel.setForeground(Color.WHITE);
        nameLabel.setFont(font);
        JTextField nameField = new JTextField();
        nameField.setBounds(180, y, 200, 30);
        background.add(nameLabel);
        background.add(nameField);

        y += 50;
        JLabel ageLabel = new JLabel("나이:");
        ageLabel.setBounds(80, y, 100, 30);
        ageLabel.setForeground(Color.WHITE);
        ageLabel.setFont(font);
        JTextField ageField = new JTextField();
        ageField.setBounds(180, y, 200, 30);
        background.add(ageLabel);
        background.add(ageField);

        y += 50;
        JLabel genderLabel = new JLabel("성별:");
        genderLabel.setBounds(80, y, 100, 30);
        genderLabel.setForeground(Color.WHITE);
        genderLabel.setFont(font);
        JComboBox<String> genderBox = new JComboBox<>(new String[] { "남성", "여성", "기타" });
        genderBox.setBounds(180, y, 200, 30);
        background.add(genderLabel);
        background.add(genderBox);

        y += 50;
        JLabel heightLabel = new JLabel("키 (cm):");
        heightLabel.setBounds(80, y, 100, 30);
        heightLabel.setForeground(Color.WHITE);
        heightLabel.setFont(font);
        JTextField heightField = new JTextField();
        heightField.setBounds(180, y, 200, 30);
        background.add(heightLabel);
        background.add(heightField);

        y += 50;
        JLabel weightLabel = new JLabel("몸무게 (kg):");
        weightLabel.setBounds(80, y, 100, 30);
        weightLabel.setForeground(Color.WHITE);
        weightLabel.setFont(font);
        JTextField weightField = new JTextField();
        weightField.setBounds(180, y, 200, 30);
        background.add(weightLabel);
        background.add(weightField);

        JButton confirmButton = new JButton("확인");
        confirmButton.setBounds(200, y + 60, 100, 40);
        confirmButton.setBackground(Color.WHITE);
        confirmButton.setFont(font);
        background.add(confirmButton);

        confirmButton.addActionListener(e -> {
            String name = nameField.getText().trim();
            String age = ageField.getText().trim();
            String gender = (String) genderBox.getSelectedItem();
            String height = heightField.getText().trim();
            String weight = weightField.getText().trim();

            if (name.isEmpty() || age.isEmpty() || height.isEmpty() || weight.isEmpty()) {
                JOptionPane.showMessageDialog(this, "모든 항목을 입력해주세요.");
                return;
            }

            UserInfo info = new UserInfo();
            info.name = name;
            info.age = age;
            info.gender = gender;
            info.height = height;
            info.weight = weight;

            dispose(); // 닫고
            if (onSubmit != null)
                onSubmit.accept(info);
        });
    }
}
