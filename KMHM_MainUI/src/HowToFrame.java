import java.awt.*;
import javax.swing.*;
import javax.swing.text.*;

public class HowToFrame extends JFrame {

    public HowToFrame() {
        setTitle("How to Play");
        setSize(1073, 790); // 💡 게임 창보다 살짝 작은 크기
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);

        // ✅ 배경 이미지
        JLabel background = new JLabel(new ImageIcon(getClass().getResource("/img/UIBackground.png")));
        background.setBounds(0, 0, 1100, 820);
        background.setLayout(null);
        add(background);

        // ✅ 제목
        JLabel titleLabel = new JLabel("How to Play", SwingConstants.CENTER);
        titleLabel.setFont(new Font("맑은 고딕", Font.BOLD, 50));
        titleLabel.setForeground(Color.WHITE);
        titleLabel.setBounds(150, 50, 800, 60);
        background.add(titleLabel);

        // ✅ 텍스트 영역
        JTextPane styledText = new JTextPane();
        styledText.setOpaque(false);
        styledText.setEditable(false);
        styledText.setForeground(Color.WHITE);
        styledText.setFont(new Font("맑은 고딕", Font.PLAIN, 26));
        styledText.setBounds(100, 140, 900, 540);

        StyledDocument doc = styledText.getStyledDocument();

        // 스타일
        SimpleAttributeSet bold = new SimpleAttributeSet();
        StyleConstants.setFontFamily(bold, "맑은 고딕");
        StyleConstants.setFontSize(bold, 28);
        StyleConstants.setBold(bold, true);
        StyleConstants.setForeground(bold, Color.WHITE);

        SimpleAttributeSet normal = new SimpleAttributeSet();
        StyleConstants.setFontFamily(normal, "맑은 고딕");
        StyleConstants.setFontSize(normal, 26);
        StyleConstants.setForeground(normal, Color.WHITE);

        try {
            doc.insertString(doc.getLength(), "게임 개요\n", bold);
            doc.insertString(doc.getLength(), "KILL ME HILL ME는 환자의 건강 게이지를 관리하여, 환자의 건강을 회복시키는 게임입니다.\n\n", normal);

            doc.insertString(doc.getLength(), "게임 설명\n", bold);
            doc.insertString(doc.getLength(), "회복시키고 싶은 게이지에 해당하는 부분을 클릭하면 일정 확률로 미니게임이 생성됩니다.\n", normal);
            doc.insertString(doc.getLength(), "미니게임 성공 시에 환자의 건강 게이지가 대폭 상승하고, 미니게임 실패 시에 건강 게이지가 감소합니다.\n", normal);
            doc.insertString(doc.getLength(), "각 게임은 마우스 클릭 또는 키보드 방향키로 조작할 수 있습니다.\n\n", normal);

            doc.insertString(doc.getLength(), "조작법\n", bold);
            doc.insertString(doc.getLength(), "- 방향키 ← → : 캐릭터 이동\n", normal);
            doc.insertString(doc.getLength(), "- Spacebar : 폐 부풀리기\n\n", normal);

            doc.insertString(doc.getLength(), "팁\n", bold);
            doc.insertString(doc.getLength(), "- 모든 장기의 체력을 0으로 만들지 않도록 주의하세요!\n", normal);
            doc.insertString(doc.getLength(), "- 제한 시간 내 최대한 많은 점수를 획득하세요!\n", normal);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }

        background.add(styledText);

        // ✅ OK 버튼
        JButton okBtn = new JButton("OK");
        okBtn.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        okBtn.setBounds(480, 650, 140, 50);
        okBtn.setFocusPainted(false);
        okBtn.setBackground(Color.WHITE);
        okBtn.setForeground(Color.BLACK);

        okBtn.addActionListener(e -> dispose());

        background.add(okBtn);

        setVisible(true);
    }
}

/*
 * "HOW TO PLAY\n\n" +
 * "게임 개요: KILL ME HILL ME는 환자의 건강 게이지를 관리하여, 환자의 건강을 회복시키는 게임입니다.\n" +
 * "게임 설명\n" +
 * "회복시키고 싶은 게이지에 해당하는 부분을 클릭하면 일정 확률로 미니게임이 생성됩니다.\n" +
 * "미니게임 성공 시에 환자의 건강 게이지가 대폭 상승하고, 미니게임 실패 시에 건강 게이지가 감소합니다.\n" +
 * "각 게임은 마우스 클릭 또는 키보드 방향키로 조작할 수 있습니다.\n" +
 * "조작법\n" +
 * "- 방향키 ← → : 캐릭터 이동\n" +
 * "- Spacebar : 폐 부풀리기\n" +
 * "팁\n" +
 * "모든 장기의 체력을 0으로 만들지 않도록 주의하세요!\n" +
 * "- 제한 시간 내 최대한 많은 점수를 획득하세요!\n\n"
 */
