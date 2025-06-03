package mg.nervous;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.Random;
import java.util.NoSuchElementException;


public class NervousSystemGame extends JFrame {
    public NervousSystemGame() {
        setTitle("신경계 미니게임");
        setContentPane(new NervousGamePanel());
        setSize(1000, 800);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setVisible(true);
    }
}

class NervousGamePanel extends JPanel {
    private final String[] imagePaths = {
            "/mg/nervousss/assets/asset/nose_icon_on_background 2.png",
            "/mg/nervousss/assets/asset/ear_icon_on_background 3.png",
            "/mg/nervousss/assets/asset/lips_icon_on_background 7.png",
            "/mg/nervousss/assets/asset/eye_icon_visible 5.png"
    };

    private final List<CardButton> cardButtons = new ArrayList<>();
    private CardButton firstSelected = null;
    private boolean canClick = false;
    private Image backgroundImage;

    private int elapsedTime = 0;
    private final int totalTime = 10000; // 10초
    private Timer gaugeTimer;
    private final LinkedList<Integer> waveform = new LinkedList<>();
    private final Timer ecgTimer;
    private final Random rand = new Random();
    private int t = 0;

    public NervousGamePanel() {
        setLayout(null);
        setFocusable(true);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/mg/nervousss/assets/asset/FrameN.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
        }

        JPanel cardPanel = new JPanel(new GridLayout(2, 4, 40, 40));
        cardPanel.setOpaque(false);
        cardPanel.setBounds(100, 200, 800, 500);
        add(cardPanel);

        List<String> cardImages = new ArrayList<>();
        for (String path : imagePaths) {
            cardImages.add(path);
            cardImages.add(path);
        }
        Collections.shuffle(cardImages);

        for (String path : cardImages) {
            CardButton card = new CardButton(path);
            card.addActionListener(new CardClickHandler(card));
            cardButtons.add(card);
            cardPanel.add(card);
        }

        Timer startTimer = new Timer(3000, e -> {
            for (CardButton card : cardButtons) {
                card.hideImage();
            }
            canClick = true;
        });
        startTimer.setRepeats(false);
        startTimer.start();

        gaugeTimer = new Timer(100, e -> {
            elapsedTime += 100;
            if (elapsedTime >= totalTime) {
                gaugeTimer.stop();
            }
            repaint();
        });
        gaugeTimer.start();

        Timer gameTimer = new Timer(totalTime, e -> {
            boolean allMatched = cardButtons.stream().allMatch(CardButton::isMatched);
            if (!allMatched) {
                JOptionPane.showMessageDialog(null, "⏰ Time's Up!", "실패", JOptionPane.ERROR_MESSAGE);
                System.exit(0);
            }
        });
        gameTimer.setRepeats(false);
        gameTimer.start();

        ecgTimer = new Timer(30, e -> {
            int width = Math.max(getWidth(), 1);  // 최소 1 이상으로 방어
            if (waveform != null && !waveform.isEmpty() && waveform.size() >= width) {
                try {
                    waveform.removeFirst();  // 예외 발생 가능성 완전히 차단
                } catch (NoSuchElementException ex) {
                    System.err.println("waveform 비어 있음 → remove 실패 방지: " + ex.getMessage());
                }
            }
            waveform.add(generateWaveformPoint());
            repaint();
        });



        ecgTimer.start();
    }

    private int generateWaveformPoint() {
        t++;
        int height = getHeight();
        int base = height / 2;
        int spike = 0;
        if (t % 90 == 0)
            spike = -30;
        else if (t % 150 == 0)
            spike = 35;
        else if (t % 200 == 0)
            spike = -20;
        double sin = Math.sin(t * 0.2) * height * 0.25;
        double noise = rand.nextGaussian() * 4;
        return (int) (base + sin + noise + spike);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);

        int barWidth = 400;
        int barHeight = 20;
        int barX = getWidth() / 2 - barWidth / 2;
        int barY = 130;

        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(barX, barY, barWidth, barHeight);

        int filledWidth = (int) ((1 - (elapsedTime / (double) totalTime)) * barWidth);
        g.setColor(new Color(0, 255, 255));
        g.fillRect(barX, barY, filledWidth, barHeight);

        g.setColor(Color.BLACK);
        g.drawRect(barX, barY, barWidth, barHeight);
        int timeLeftSec = Math.max(0, (totalTime - elapsedTime) / 1000);
        g.setColor(Color.WHITE);
        g.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        g.drawString("Time Left: " + timeLeftSec + "s", barX, barY + barHeight + 25);

        // ECG waveform 그리기
        g.setColor(Color.GREEN);
        int prevY = waveform.size() > 0 ? waveform.get(0) : getHeight() / 2;
        for (int i = 1; i < waveform.size(); i++) {
            int y = waveform.get(i);
            g.drawLine(i - 1, prevY, i, y);
            prevY = y;
        }
    }

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
                            System.exit(0);
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
        this.back = new ImageIcon(new ImageIcon(getClass().getResource("/mg/nervousss/assets/asset/Rectangle 632.png"))
                .getImage().getScaledInstance(width, height, Image.SCALE_SMOOTH));

        setIcon(front);
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

