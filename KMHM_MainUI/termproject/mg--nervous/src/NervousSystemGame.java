import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Timer;

public class NervousSystemGame {

    public static void main(String[] args) {
        JFrame frame = new JFrame("신경계 미니게임");
        frame.setContentPane(new NervousGamePanel());
        frame.setSize(1000, 800);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }
}

// 🧠 배경 이미지 포함된 커스텀 패널
class NervousGamePanel extends JPanel {
    private final String[] imagePaths = {
            "/assets/nose_icon_on_background 2.png",
            "/assets/ear_icon_on_background 3.png",
            "/assets/lips_icon_on_background 7.png",
            "/assets/eye_icon_visible 5.png"
    };

    private final List<CardButton> cardButtons = new ArrayList<>();
    private CardButton firstSelected = null;
    private boolean canClick = false;
    private Image backgroundImage;

    public NervousGamePanel() {
        setLayout(null); // 수동 위치 설정
        setFocusable(true);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/Frame 367.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // 카드 패널
        JPanel cardPanel = new JPanel(new GridLayout(2, 4, 40, 40));
        cardPanel.setOpaque(false);
        cardPanel.setBounds(100, 200, 800, 500);
        add(cardPanel);

        // 카드 4쌍 생성
        List<String> cardImages = new ArrayList<>();
        for (String path : imagePaths) {
            cardImages.add(path);
            cardImages.add(path);
        }
        Collections.shuffle(cardImages);

        // 카드 버튼 생성
        for (String path : cardImages) {
            CardButton card = new CardButton(path);
            card.addActionListener(new CardClickHandler(card));
            cardButtons.add(card);
            cardPanel.add(card);
        }

        // 시작 시 3초 공개 후 자동 뒤집기
        Timer startTimer = new Timer(3000, e -> {
            for (CardButton card : cardButtons) {
                card.hideImage();
            }
            canClick = true;
        });
        startTimer.setRepeats(false);
        startTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
    }

    // 카드 클릭 로직
    private class CardClickHandler implements ActionListener {
        private final CardButton card;

        public CardClickHandler(CardButton card) {
            this.card = card;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (!canClick || card.isMatched() || card.isRevealed()) return;

            card.revealImage();

            if (firstSelected == null) {
                firstSelected = card;
            } else {
                canClick = false;
                Timer delay = new Timer(500, ev -> {
                    if (card.getImagePath().equals(firstSelected.getImagePath())) {
                        card.setMatched(true);
                        firstSelected.setMatched(true);

                        boolean allMatched = cardButtons.stream().allMatch(CardButton::isMatched);
                        if (allMatched) {
                            JOptionPane.showMessageDialog(null, "Success! 모든 카드를 맞췄습니다!", "게임 완료", JOptionPane.INFORMATION_MESSAGE);
                        }

                    } else {
                        card.hideImage();
                        firstSelected.hideImage();
                    }
                    firstSelected = null;
                    canClick = true;
                });
                delay.setRepeats(false);
                delay.start();
            }
        }
    }
}

// 카드 버튼 클래스
class CardButton extends JButton {
    private final String imagePath;
    private final ImageIcon front;
    private final ImageIcon back;
    private boolean matched = false;
    private boolean revealed = true;
    private final int width = 160;
    private final int height = 160;

    public CardButton(String imagePath) {
        this.imagePath = imagePath;

        this.front = new ImageIcon(new ImageIcon(getClass().getResource(imagePath))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));
        this.back = new ImageIcon(new ImageIcon(getClass().getResource("/assets/Rectangle 632.png"))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));

        setIcon(front); // 처음엔 전부 공개
        setBorderPainted(false);
        setFocusPainted(false);
        setContentAreaFilled(false);
    }

    public void revealImage() {
        setIcon(front);
        revealed = true;
    }

    public void hideImage() {
        if (!matched) {
            setIcon(back);
            revealed = false;
        }
    }

    public boolean isMatched() {
        return matched;
    }

    public void setMatched(boolean matched) {
        this.matched = matched;
    }

    public boolean isRevealed() {
        return revealed;
    }

    public String getImagePath() {
        return imagePath;
    }
}
