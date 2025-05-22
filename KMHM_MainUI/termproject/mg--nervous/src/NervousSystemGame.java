import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

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

    // ⏱️ 게이지 관련 변수
    private int elapsedTime = 0;
    private final int totalTime = 10000; // 10초
    private Timer gaugeTimer;

    public NervousGamePanel() {
        setLayout(null);
        setFocusable(true);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/assets/FrameN.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel cardPanel = new JPanel(new GridLayout(2, 4, 40, 40));
        cardPanel.setOpaque(false);
        cardPanel.setBounds(100, 200, 800, 500);
        add(cardPanel);

        // 카드 이미지 섞기
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

        // 시작 시 3초 공개
        Timer startTimer = new Timer(3000, e -> {
            for (CardButton card : cardButtons) {
                card.hideImage();
            }
            canClick = true;
        });
        startTimer.setRepeats(false);
        startTimer.start();

        // ⏱️ 게이지 업데이트 타이머
        gaugeTimer = new Timer(100, e -> {
            elapsedTime += 100;
            if (elapsedTime >= totalTime) {
                gaugeTimer.stop();
            }
            repaint();
        });
        gaugeTimer.start();

        // ⏲️ 게임 제한 타이머
        Timer gameTimer = new Timer(totalTime, e -> {
            boolean allMatched = cardButtons.stream().allMatch(CardButton::isMatched);
            if (!allMatched) {
                JOptionPane.showMessageDialog(null, "⏰ Time's Up!", "실패", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        gameTimer.setRepeats(false);
        gameTimer.start();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        // 🧠 배경 이미지
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        // ⏱️ 시간 제한 게이지 바
        int barWidth = 400;
        int barHeight = 20;
        int barX = getWidth() / 2 - barWidth / 2;
        int barY = 130;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        int filledWidth = (int) ((1 - (elapsedTime / (double) totalTime)) * barWidth);
        g.setColor(new Color(0, 255, 255)); // 형광 파랑
        g.fillRect(barX, barY, filledWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
        int timeLeftSec = Math.max(0, (totalTime - elapsedTime) / 1000); // 초 단위로 변환
        g.setColor(Color.WHITE);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        g.drawString("Time Left: " + timeLeftSec + "s", barX, barY + barHeight + 25);
    }

    // 카드 클릭 처리 핸들러
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
                            JOptionPane.showMessageDialog(null, "✅ Game Clear!", "성공", JOptionPane.INFORMATION_MESSAGE);
                            System.exit(0); // 게임 성공 후 종료
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

// 🃏 카드 클래스
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

        setIcon(front); // 처음엔 공개
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
