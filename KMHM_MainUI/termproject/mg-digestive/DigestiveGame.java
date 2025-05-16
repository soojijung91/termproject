import java.awt.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import javax.swing.*;

public class DigestiveGame extends JFrame {

    private final int TIME_LIMIT = 3000; // 3초
    private final JProgressBar timerBar = new JProgressBar();
    private final JLabel foodLabel = new JLabel("", SwingConstants.CENTER);
    private final JButton healthyButton = new JButton("Healthy food");
    private final JButton unhealthyButton = new JButton("Unhealthy food");

    private final java.util.List<FoodItem> foodList = new ArrayList<>();
    private final java.util.List<FoodItem> gameList = new ArrayList<>();
    private final Random random = new Random();
    private Timer countdownTimer;
    private int currentIndex = 0;

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new DigestiveGame().setVisible(true));
    }

    public DigestiveGame() {
        setTitle("소화계 미니게임");
        setSize(800, 600);
        setLayout(new BorderLayout());
        setDefaultCloseOperation(EXIT_ON_CLOSE);

        // 타이머 바
        timerBar.setMaximum(TIME_LIMIT);
        add(timerBar, BorderLayout.NORTH);

        // 음식 텍스트
        foodLabel.setFont(new Font("Arial", Font.BOLD, 36));
        foodLabel.setOpaque(true);
        add(foodLabel, BorderLayout.CENTER);

        // 버튼 설정
        healthyButton.setBackground(Color.WHITE);
        healthyButton.setForeground(Color.BLACK);
        unhealthyButton.setBackground(Color.BLACK);
        unhealthyButton.setForeground(Color.WHITE);

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(healthyButton);
        buttonPanel.add(unhealthyButton);
        add(buttonPanel, BorderLayout.SOUTH);

        healthyButton.addActionListener(e -> checkAnswer(true));
        unhealthyButton.addActionListener(e -> checkAnswer(false));

        loadFoodItems();
        selectRandom10();
        showNext();
    }

    private void loadFoodItems() {
        String[] healthyFoods = {
            "브로콜리", "시금치", "당근", "토마토", "두부",
            "콩나물", "고등어", "아몬드", "닭가슴살", "가지",
            "양파", "버섯", "마늘", "쑥", "양배추",
            "녹차", "미역", "김치", "달걀", "호두",
            "블루베리", "아보카도", "딸기", "사과", "레몬"
        };

        String[] unhealthyFoods = {
            "치킨", "감자튀김", "핫도그", "햄버거", "라면",
            "피자", "젤리", "사탕", "에너지음료", "믹스커피",
            "라면스프", "초콜릿", "아이스크림", "술", "도넛",
            "케이크", "설탕시럽", "콜라", "사이다", "마시멜로우",
            "과자", "밀크티", "마카롱", "조미료", "마카롱"
        };

        for (String food : healthyFoods) {
            foodList.add(new FoodItem(food, true));
        }
        for (String food : unhealthyFoods) {
            foodList.add(new FoodItem(food, false));
        }
    }

    private void selectRandom10() {
        Collections.shuffle(foodList);
        java.util.List<FoodItem> tempList = new ArrayList<>(foodList);

        int healthyCount = 0;
        int unhealthyCount = 0;

        gameList.clear();

        while (!tempList.isEmpty() && gameList.size() < 10) {
            FoodItem item = tempList.remove(0);
            if (item.isHealthy && healthyCount < 7) {
                gameList.add(item);
                healthyCount++;
            } else if (!item.isHealthy && unhealthyCount < 7) {
                gameList.add(item);
                unhealthyCount++;
            } else if (gameList.size() >= 7) {
                gameList.add(item);
            }
        }

        // 최종 검사: 한 쪽으로만 10개 몰리는 경우 방지
        long good = gameList.stream().filter(f -> f.isHealthy).count();
        if (good == 0 || good == 10) {
            selectRandom10(); // 재시도
        }
    }

    private void showNext() {
        if (currentIndex >= gameList.size()) {
            JOptionPane.showMessageDialog(this, "🎉 축하합니다! 게임 클리어!");
            System.exit(0);
        }

        FoodItem item = gameList.get(currentIndex);
        foodLabel.setText(item.name);

        // 배경과 텍스트 색 랜덤 (흰색 / 검은색)
        boolean whiteText = random.nextBoolean();
        foodLabel.setForeground(whiteText ? Color.WHITE : Color.BLACK);
        foodLabel.setBackground(whiteText ? Color.BLACK : Color.WHITE);

        startTimer();
    }

    private void startTimer() {
        if (countdownTimer != null) {
            countdownTimer.stop();
        }

        final int[] timeLeft = {TIME_LIMIT};
        countdownTimer = new Timer(30, null);
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
            currentIndex++;
            showNext();
        } else {
            countdownTimer.stop();
            gameOver("❌ 오답입니다!");
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
}

