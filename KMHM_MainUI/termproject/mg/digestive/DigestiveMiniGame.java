import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.*;

public class DigestiveMiniGame extends JFrame {
    private final int TIME_LIMIT = 3000;
    private final List<FoodItem> foodList = new ArrayList<>();
    private final List<FoodItem> gameList = new ArrayList<>();
    private final JLabel foodLabel = new JLabel("", SwingConstants.CENTER);
    private final JLabel scoreLabel = new JLabel("0 / 5", SwingConstants.RIGHT);
    private final JLabel timeLeftLabel = new JLabel("Time Left: 3s", SwingConstants.LEFT);
    private final JButton healthyButton = new JButton("Healthy Food");
    private final JButton unhealthyButton = new JButton("Unhealthy Food");
    private final JProgressBar timerBar = new JProgressBar();
    private javax.swing.Timer countdownTimer;
    private int currentIndex = 0;
    private int correctCount = 0;
    private Image backgroundImage;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DigestiveMiniGame().setVisible(true));
    }

    public DigestiveMiniGame() {
        setTitle("소화계 미니게임");
        setSize(1024, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setResizable(true);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/asset/FrameD.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        // 타이머 바와 텍스트 묶음 중앙에 배치
        JPanel centerTimerPanel = new JPanel();
        centerTimerPanel.setLayout(new BoxLayout(centerTimerPanel, BoxLayout.Y_AXIS));
        centerTimerPanel.setOpaque(false);
        centerTimerPanel.setBorder(BorderFactory.createEmptyBorder(120, 0, 10, 0)); // 아래로 이동

        // ⬛ 타이머 바
        JPanel barPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        barPanel.setOpaque(false);
        timerBar.setMaximum(TIME_LIMIT);
        timerBar.setForeground(Color.YELLOW);
        timerBar.setPreferredSize(new Dimension(400, 20));
        barPanel.add(timerBar);
        centerTimerPanel.add(barPanel);

        // ⏳ Time & Score 라벨 - 좌우 배치
        JPanel labelPanel = new JPanel(new BorderLayout());
        labelPanel.setOpaque(false);
        labelPanel.setMaximumSize(new Dimension(400, 30));
        labelPanel.setPreferredSize(new Dimension(400, 30));

        timeLeftLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        timeLeftLabel.setForeground(Color.WHITE);
        scoreLabel.setFont(new Font("맑은 고딕", Font.BOLD, 14));
        scoreLabel.setForeground(Color.WHITE);

        labelPanel.add(timeLeftLabel, BorderLayout.WEST);
        labelPanel.add(scoreLabel, BorderLayout.EAST);
        centerTimerPanel.add(labelPanel);

        backgroundPanel.add(centerTimerPanel, BorderLayout.NORTH);

        // 음식 텍스트
        foodLabel.setFont(new Font("맑은 고딕", Font.BOLD, 64));
        foodLabel.setForeground(Color.WHITE);
        backgroundPanel.add(foodLabel, BorderLayout.CENTER);

        // 버튼 영역
        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 40, 200));

        healthyButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        unhealthyButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        healthyButton.setFocusPainted(false);
        unhealthyButton.setFocusPainted(false);

        // ✅ 버튼 색상 스타일
        healthyButton.setBackground(Color.WHITE);
        healthyButton.setForeground(Color.BLACK);
        unhealthyButton.setBackground(Color.BLACK);
        unhealthyButton.setForeground(Color.WHITE);
        buttonPanel.add(healthyButton);
        buttonPanel.add(unhealthyButton);
        backgroundPanel.add(buttonPanel, BorderLayout.SOUTH);

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
            JOptionPane.showMessageDialog(this, "✅ Game Clear!");
            System.exit(0);
        }

        FoodItem item = gameList.get(currentIndex);
        foodLabel.setText(item.name);
        scoreLabel.setText(correctCount + " / 5");
        timeLeftLabel.setText("Time Left: 3s");
        startTimer();
    }

    private void startTimer() {
        if (countdownTimer != null) countdownTimer.stop();

        final int[] timeLeft = {TIME_LIMIT};
        countdownTimer = new javax.swing.Timer(30, null);
        countdownTimer.addActionListener(e -> {
            timeLeft[0] -= 30;
            timerBar.setValue(timeLeft[0]);

            int seconds = Math.max(0, timeLeft[0] / 1000);
            timeLeftLabel.setText("Time Left: " + seconds + "s");

            if (timeLeft[0] <= 0) {
                countdownTimer.stop();
                gameOver("⏰ Time's Up!");
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
                JOptionPane.showMessageDialog(this, "✅ Game Clear!");
                System.exit(0);
            } else {
                showNext();
            }
        } else {
            if (countdownTimer != null) countdownTimer.stop();
            gameOver("❌Game Over!");
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
}
