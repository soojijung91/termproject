import java.awt.*;
import java.awt.font.GlyphVector;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.*;

public class DigestiveMiniGame extends JFrame {
    private final int TIME_LIMIT = 3000;
    private final List<FoodItem> foodList = new ArrayList<>();
    private final List<FoodItem> gameList = new ArrayList<>();
    private final OutlinedLabel foodLabel = new OutlinedLabel("");
    private final JLabel scoreLabel = new JLabel("0 / 5", SwingConstants.RIGHT);
    private final JButton healthyButton = new JButton("Healthy Food");
    private final JButton unhealthyButton = new JButton("Unhealthy Food");
    private final JProgressBar timerBar = new JProgressBar();
    private javax.swing.Timer countdownTimer;
    private int currentIndex = 0;
    private int correctCount = 0;
    private final Random random = new Random();
    private Image backgroundImage;
    private boolean whiteText = true;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DigestiveMiniGame().setVisible(true));
    }

    public DigestiveMiniGame() {
        setTitle("Digestive Mini Game");
        setSize(1440, 1024);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        try {
            ImageIcon bgIcon = new ImageIcon(getClass().getResource("Frame.png"));
            backgroundImage = bgIcon.getImage();
        } catch (Exception e) {
        e.printStackTrace();
        System.exit(1);
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(null);
        setContentPane(backgroundPanel);

        timerBar.setMaximum(TIME_LIMIT);
        timerBar.setForeground(Color.RED);
        timerBar.setBounds(100, 20, 1240, 25);
        backgroundPanel.add(timerBar);

        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 30));
        scoreLabel.setForeground(Color.WHITE);
        scoreLabel.setBounds(1140, 74, 200, 40);
        backgroundPanel.add(scoreLabel);

        foodLabel.setFont(new Font("맑은 고딕", Font.BOLD, 64));
        foodLabel.setBounds(420, 300, 600, 100);
        foodLabel.setOpaque(false);
        backgroundPanel.add(foodLabel);

        JLabel guideLabel = new JLabel("음식 글자를 보고 3초 안에 버튼을 눌러주세요", SwingConstants.CENTER);
        guideLabel.setFont(new Font("맑은 고딕", Font.BOLD, 20));
        guideLabel.setForeground(Color.WHITE);
        guideLabel.setBounds(420, 135, 600, 30);
        backgroundPanel.add(guideLabel);

        healthyButton.setBounds(400, 460, 250, 70);
        healthyButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        healthyButton.setBackground(Color.WHITE);
        healthyButton.setForeground(Color.BLACK);
        healthyButton.setFocusPainted(false);
        backgroundPanel.add(healthyButton);

        unhealthyButton.setBounds(750, 460, 250, 70);
        unhealthyButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        unhealthyButton.setBackground(Color.BLACK);
        unhealthyButton.setForeground(Color.WHITE);
        unhealthyButton.setFocusPainted(false);
        backgroundPanel.add(unhealthyButton);

        healthyButton.addActionListener(e -> checkAnswer(true));
        unhealthyButton.addActionListener(e -> checkAnswer(false));

        loadFoodItems();
        selectRandomItems(10);
        showNext();
    }

    private void loadFoodItems() {
        String[] healthyFoods = {"브로콜리", "시금치", "당근", "토마토", "두부", "콩나물", "고등어", "아몬드", "닭가슴살", "가지",
                "양파", "버섯", "마늘", "쑥", "양배추", "녹차", "미역", "김치", "달걀", "호두",
                "블루베리", "아보카도", "딸기", "사과", "레몬"};

        String[] unhealthyFoods = {"치킨", "감자튀김", "핫도그", "햄버거", "라면", "피자", "젤리", "사탕", "에너지음료", "믹스커피",
                "라면스프", "초콜릿", "아이스크림", "술", "도넛", "케이크", "설탕시럽", "콜라", "사이다", "마시멜로우",
                "과자", "밀크티", "마카롱", "조미료", "마카롱"};

        for (String food : healthyFoods) foodList.add(new FoodItem(food, true));
        for (String food : unhealthyFoods) foodList.add(new FoodItem(food, false));
    }

    private void selectRandomItems(int count) {
        Collections.shuffle(foodList);
        List<FoodItem> tempList = new ArrayList<>(foodList);
        gameList.clear();

        int healthyCount = 0, unhealthyCount = 0;

        while (!tempList.isEmpty() && gameList.size() < count) {
            FoodItem item = tempList.remove(0);
            if (item.isHealthy && healthyCount < count) {
                gameList.add(item);
                healthyCount++;
            } else if (!item.isHealthy && unhealthyCount < count) {
                gameList.add(item);
                unhealthyCount++;
            }
        }

        long good = gameList.stream().filter(f -> f.isHealthy).count();
        if (good == 0 || good == count) selectRandomItems(count);
    }

    private void showNext() {
        if (currentIndex >= gameList.size()) {
            if (countdownTimer != null) countdownTimer.stop();
            JOptionPane.showMessageDialog(this, "🎉 게임 종료!");
            System.exit(0);
        }

        FoodItem item = gameList.get(currentIndex);
        whiteText = new Random().nextBoolean();
        foodLabel.setText(item.name);
        foodLabel.setColors(whiteText ? Color.WHITE : Color.BLACK, whiteText ? Color.BLACK : Color.WHITE);
        scoreLabel.setText(correctCount + "개 / 5개");
        startTimer();
    }

    private void startTimer() {
        if (countdownTimer != null) countdownTimer.stop();

        final int[] timeLeft = {TIME_LIMIT};
        countdownTimer = new javax.swing.Timer(30, null);
        countdownTimer.addActionListener(e -> {
            timeLeft[0] -= 30;
            timerBar.setValue(timeLeft[0]);
            if (timeLeft[0] <= 0) {
                countdownTimer.stop();
                gameOver("⏰ 시간 초과!");
            }
        });
        timerBar.setValue(TIME_LIMIT);
        countdownTimer.start();
    }

    private void checkAnswer(boolean userChoice) {
        FoodItem item = gameList.get(currentIndex);
        if (item.isHealthy == userChoice) {
            correctCount++;
            currentIndex++;

            if (correctCount >= 5) {
                if (countdownTimer != null) countdownTimer.stop();
                JOptionPane.showMessageDialog(this, "Success!");
                System.exit(0);
            } else {
                showNext();
            }
        } else {
            if (countdownTimer != null) countdownTimer.stop();
            gameOver("❌ Incorrect");
        }
    }

    private void gameOver(String message) {
        JOptionPane.showMessageDialog(this, message);
        System.exit(0);
    }

    private static class FoodItem {
        String name;
        boolean isHealthy;

        FoodItem(String name, boolean isHealthy) {
            this.name = name;
            this.isHealthy = isHealthy;
        }
    }

    private class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }

    private static class OutlinedLabel extends JLabel {
        private Color strokeColor = Color.BLACK;
        private Color fillColor = Color.WHITE;

        public OutlinedLabel(String text) {
            super(text);
            setHorizontalAlignment(SwingConstants.CENTER);
        }

        public void setColors(Color fill, Color stroke) {
            this.fillColor = fill;
            this.strokeColor = stroke;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2 = (Graphics2D) g.create();
            g2.setFont(getFont());
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            String text = getText();
            FontMetrics fm = g2.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(text)) / 2;
            int y = (getHeight() - fm.getHeight()) / 2 + fm.getAscent();

            GlyphVector gv = getFont().createGlyphVector(g2.getFontRenderContext(), text);
            Shape textShape = gv.getOutline(x, y);

            g2.setColor(strokeColor);
            g2.setStroke(new BasicStroke(10f));
            g2.draw(textShape);

            g2.setColor(fillColor);
            g2.fill(textShape);

            g2.dispose();
        }
    }
}
