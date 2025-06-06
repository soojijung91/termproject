
import java.awt.*;
import java.awt.event.*;
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
        SwingUtilities.invokeLater(() -> new DigestiveMiniGame());
    }

    public DigestiveMiniGame() {
        setTitle("소화계 미니게임");
        setSize(1073, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setResizable(true);
        setVisible(true);

        try {
            backgroundImage = new ImageIcon(getClass().getResource("/img/FrameD.png")).getImage();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }

        BackgroundPanel backgroundPanel = new BackgroundPanel();
        backgroundPanel.setLayout(new BorderLayout());
        setContentPane(backgroundPanel);

        JPanel centerTimerPanel = new JPanel();
        centerTimerPanel.setLayout(new BoxLayout(centerTimerPanel, BoxLayout.Y_AXIS));
        centerTimerPanel.setOpaque(false);
        centerTimerPanel.setBorder(BorderFactory.createEmptyBorder(120, 0, 10, 0));

        JPanel barPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 0, 0));
        barPanel.setOpaque(false);
        timerBar.setMaximum(TIME_LIMIT);
        timerBar.setForeground(Color.YELLOW);
        timerBar.setPreferredSize(new Dimension(400, 20));
        barPanel.add(timerBar);
        centerTimerPanel.add(barPanel);

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

        foodLabel.setFont(new Font("맑은 고딕", Font.BOLD, 64));
        foodLabel.setForeground(Color.WHITE);
        backgroundPanel.add(foodLabel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 2, 20, 0));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 200, 40, 200));
        healthyButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        unhealthyButton.setFont(new Font("맑은 고딕", Font.BOLD, 24));
        healthyButton.setFocusPainted(false);
        unhealthyButton.setFocusPainted(false);
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
        while (!tempList.isEmpty() && gameList.size() < count) {
            FoodItem item = tempList.remove(0);
            gameList.add(item);
        }
    }

    private void showNext() {
        if (currentIndex >= gameList.size()) {
            String msg = correctCount >= 5 ? "✅ Game Clear!" : "❌ Game Over!";
            JOptionPane.showMessageDialog(this, msg, "결과", JOptionPane.INFORMATION_MESSAGE);
            System.exit(0);
        }

        FoodItem currentFood = gameList.get(currentIndex);
        foodLabel.setText(currentFood.name);
        timerBar.setValue(0);

        if (countdownTimer != null) countdownTimer.stop();
        countdownTimer = new javax.swing.Timer(30, new ActionListener() {
            int elapsed = 0;
            @Override
            public void actionPerformed(ActionEvent e) {
                elapsed += 30;
                timerBar.setValue(elapsed);
                timeLeftLabel.setText("Time Left: " + (TIME_LIMIT - elapsed) / 1000 + "s");
                if (elapsed >= TIME_LIMIT) {
                    countdownTimer.stop();
                    currentIndex++;
                    showNext();
                }
            }
        });
        countdownTimer.start();
    }

    private void checkAnswer(boolean userChoice) {
        if (countdownTimer != null) countdownTimer.stop();

        FoodItem current = gameList.get(currentIndex);
        if (current.isHealthy == userChoice) correctCount++;
        scoreLabel.setText(correctCount + " / 5");
        currentIndex++;
        showNext();
    }

    private static class FoodItem {
        String name;
        boolean isHealthy;
        public FoodItem(String name, boolean isHealthy) {
            this.name = name;
            this.isHealthy = isHealthy;
        }
    }

    class BackgroundPanel extends JPanel {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        }
    }
}
