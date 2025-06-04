import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;


public class KMHM_MainUI extends JFrame {

    private JLabel background, human, scanning, gameTimer, groupWave, rightComponents, stopBtn;
    private JLabel lungImg, brainImg, digestiveImg;
    private JLabel pulseRateLabel, pulseGroupIcon;
    private JLabel centerClockLabel;
    private Timer centerClockTimer;
    private int elapsedSeconds = 0;

    private JLabel[] nameLabels = new JLabel[4];
    private JLabel[] percentLabels = new JLabel[4];
    private JProgressBar[] bars = new JProgressBar[4];
    private final String[] systemNames = { "Nervous", "Respiratory", "Digestive", "Circulatory" };
    private final Color[] systemColors = { Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN };

    private Rectangle lungArea, brainArea, digArea;
    private Image bgImg, humanImg, scanningImg, timerImg, groupImg, rightImg, stopImg;
    private Image lungRaw, brainRaw, digestiveRaw, pulseRateImg, pulseGroupImg;

    private GraphPanel ecgPanel;
    private Timer decayTimer;
    private boolean gameOverShown = false;

    public KMHM_MainUI() {
        setTitle("KMHM - Game Screen");
        setSize(1073, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);


        bgImg = new ImageIcon(getClass().getResource("/img/UIBackground.png")).getImage();
        humanImg = new ImageIcon(getClass().getResource("/img/3D Illustration.png")).getImage();
        scanningImg = new ImageIcon(getClass().getResource("/img/_Scanning_.png")).getImage();
        timerImg = new ImageIcon(getClass().getResource("/img/Game Timer.png")).getImage();
        groupImg = new ImageIcon(getClass().getResource("/img/Group.png")).getImage();
        rightImg = new ImageIcon(getClass().getResource("/img/Right Components.png")).getImage();
        stopImg = new ImageIcon(getClass().getResource("/img/CTA.png")).getImage();
        lungRaw = new ImageIcon(getClass().getResource("/img/폐 이미지.png")).getImage();
        brainRaw = new ImageIcon(getClass().getResource("/img/신경계 이미지.png")).getImage();
        digestiveRaw = new ImageIcon(getClass().getResource("/img/소화계 이미지.png")).getImage();
        pulseRateImg = new ImageIcon(getClass().getResource("/img/Pulse Rate.png")).getImage();
        pulseGroupImg = new ImageIcon(getClass().getResource("/img/Group-1.png")).getImage();

        background = new JLabel();
        human = new JLabel();
        scanning = new JLabel();
        gameTimer = new JLabel();
        groupWave = new JLabel();
        rightComponents = new JLabel();
        stopBtn = new JLabel();
        lungImg = new JLabel();
        brainImg = new JLabel();
        digestiveImg = new JLabel();
        pulseRateLabel = new JLabel();
        pulseGroupIcon = new JLabel();

        centerClockLabel = new JLabel("00:00", SwingConstants.CENTER);
        centerClockLabel.setForeground(Color.GREEN);
        centerClockLabel.setOpaque(false);
        try {
            Font digitalFont = Font.createFont(Font.TRUETYPE_FONT, new File("src/fonts/digital-7.ttf")).deriveFont(36f);
            centerClockLabel.setFont(digitalFont);
        } catch (FontFormatException | IOException e) {
            centerClockLabel.setFont(new Font("Dialog", Font.BOLD, 33));
            System.err.println("폰트를 불러오는 데 실패했습니다: " + e.getMessage());
        }

        add(centerClockLabel);
        add(pulseRateLabel);
        add(pulseGroupIcon);
        for (int i = 0; i < 4; i++) {
            nameLabels[i] = new JLabel(systemNames[i]);
            nameLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 14));
            nameLabels[i].setForeground(systemColors[i]);
            add(nameLabels[i]);

            percentLabels[i] = new JLabel("0%");
            percentLabels[i].setFont(new Font("맑은 고딕", Font.BOLD, 13));
            percentLabels[i].setForeground(systemColors[i]);
            add(percentLabels[i]);

            bars[i] = new JProgressBar(0, 100);
            bars[i].setValue(0);
            bars[i].setForeground(systemColors[i]);
            bars[i].setBackground(Color.LIGHT_GRAY);
            bars[i].setStringPainted(true);
            bars[i].setFont(new Font("맑은 고딕", Font.BOLD, 13));
            add(bars[i]);
        }

        Random rand = new Random();
        int[] indices = { 0, 1, 2, 3 };
        for (int i = 0; i < indices.length; i++) {
            int j = rand.nextInt(indices.length);
            int temp = indices[i];
            indices[i] = indices[j];
            indices[j] = temp;
        }
        for (int i = 0; i < 2; i++) {
            int val = 20 + rand.nextInt(11);
            bars[indices[i]].setValue(val);
            percentLabels[indices[i]].setText(val + "%");
        }
        for (int i = 2; i < 4; i++) {
            int val = 40 + rand.nextInt(11);
            bars[indices[i]].setValue(val);
            percentLabels[indices[i]].setText(val + "%");
        }

        decayTimer = new Timer(2000, e -> {
            for (int i = 0; i < 4; i++) {
                int val = bars[i].getValue();
                if (val > 0) {
                    bars[i].setValue(val - 1);
                    percentLabels[i].setText((val - 1) + "%");
                }
            }
            checkGameStatus();
        });
        decayTimer.start();
        for (int i = 0; i < 4; i++) {
            triggerClicks[i] = 1 + random.nextInt(8); // 1~8 중 무작위 시점
        }


        centerClockTimer = new Timer(1000, e -> {
            elapsedSeconds++;
            int min = elapsedSeconds / 60;
            int sec = elapsedSeconds % 60;
            centerClockLabel.setText(String.format("%02d:%02d", min, sec));
        });
        centerClockTimer.start();

        ecgPanel = new GraphPanel();
        add(ecgPanel);

        add(scanning);
        add(gameTimer);
        add(groupWave);
        add(rightComponents);
        add(stopBtn);
        add(lungImg);
        add(brainImg);
        add(digestiveImg);
        add(human);
        add(background);

        human.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                if (brainArea != null && brainArea.contains(p))
                    increase(0);
                else if (lungArea != null && lungArea.contains(p))
                    increase(1);
                else if (digArea != null && digArea.contains(p))
                    increase(2);
                else
                    increase(3);
            }
        });

        addComponentListener(new ComponentAdapter() {
            public void componentResized(ComponentEvent e) {
                resizeComponents();
            }
        });

        setVisible(true);
        resizeComponents();
    }
    private int[] clickCounts = new int[4];
    private int[] triggerClicks = new int[4];
    private Random random = new Random();


    private void checkGameStatus() {
        if (gameOverShown)
            return;

        boolean allAbove80 = true;
        for (JProgressBar bar : bars) {
            int value = bar.getValue();
            if (value <= 0) {
                gameOverShown = true;
                new GameOverFrame(centerClockLabel.getText());
                dispose();
                return;
            }
            if (value < 80) {
                allAbove80 = false;
            }
        }
        if (allAbove80) {
            gameOverShown = true;
            new GameClearFrame(centerClockLabel.getText());
            dispose();
        }
    }
    private void updateGauge(int index, int increment, String feedbackMsg) {
        int val = bars[index].getValue();
        val = Math.min(100, val + increment);
        bars[index].setValue(val);
        percentLabels[index].setText(val + "%");

        JOptionPane.showMessageDialog(this, feedbackMsg, systemNames[index] + " 피드백", JOptionPane.INFORMATION_MESSAGE);
    }

    private void increase(int index) {
        // 각 시스템별 질문, 선택지, 점수
        String[][] questions = {
                // Nervous System
                {
                        "1. 하루 평균 수면 시간은 몇 시간인가요?",
                        "2. 최근 일주일 동안 스트레스를 많이 받았나요?",
                        "3. 자기 전에 전자기기(스마트폰, 컴퓨터 등)를 얼마나 사용하나요?"
                },
                // Respiratory System
                {
                        "1. 하루에 얼마나 자주 환기를 시키시나요?",
                        "2. 운동 중 숨이 차거나 호흡 곤란을 느낀 적이 있나요?",
                        "3. 최근 감기, 기침 또는 인후통 등의 증상이 있었나요?"
                },
                // Digestive System
                {
                        "1. 평소 식사 시간을 규칙적으로 지키시나요?",
                        "2. 일주일에 몇 번 외식을 하나요?",
                        "3. 변비나 복부 불편감을 자주 느끼시나요?"
                },
                // Circulatory System
                {
                        "1. 평소 혈압을 측정한 적이 있나요?",
                        "2. 일주일에 몇 번 정도 유산소 운동(걷기, 조깅 등)을 하시나요?",
                        "3. 짠 음식이나 기름진 음식을 자주 드시나요?"
                }
        };

        String[][][] options = {
                { // Nervous System
                        {"7~8시간", "6시간", "5시간 이하"},
                        {"아니오", "보통", "예"},
                        {"1시간 전 끔", "10분 전", "계속 사용"}
                },
                { // Respiratory System
                        {"3번 이상", "1~2번", "없음"},
                        {"없음", "가끔 있음", "자주 있음"},
                        {"없음", "최근 있음", "지금도 있음"}
                },
                { // Digestive System
                        {"항상", "가끔", "불규칙"},
                        {"주 1~2회", "주 3~4회", "거의 매일"},
                        {"없음", "가끔 있음", "자주 있음"}
                },
                { // Circulatory System
                        {"주기적으로 측정", "가끔", "없음"},
                        {"주 3회 이상", "주 1~2회", "없음"},
                        {"거의 안 먹음", "가끔 먹음", "자주 먹음"}
                }
        };

        int[][][] scores = {
                { // Nervous System
                        {10, 7, 3},
                        {10, 5, 0},
                        {10, 5, 0}
                },
                { // Respiratory System
                        {10, 5, 0},
                        {10, 5, 0},
                        {10, 5, 0}
                },
                { // Digestive System
                        {10, 5, 0},
                        {10, 5, 0},
                        {10, 5, 0}
                },
                { // Circulatory System
                        {10, 5, 0},
                        {10, 5, 0},
                        {10, 5, 0}
                }
        };

        // 입력 패널
        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 8));
        JComboBox<String>[] fields = new JComboBox[3];

        for (int i = 0; i < 3; i++) {
            panel.add(new JLabel(questions[index][i]));
            fields[i] = new JComboBox<>(options[index][i]);
            panel.add(fields[i]);
        }

        int result = JOptionPane.showConfirmDialog(
                this, panel, systemNames[index] + " 건강 설문", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE
        );

        if (result == JOptionPane.OK_OPTION) {
            int totalScore = 0;
            for (int i = 0; i < 3; i++) {
                int selIdx = fields[i].getSelectedIndex();
                totalScore += scores[index][i][selIdx];
            }

            // 점수구간별 게이지 변화량, 피드백 결정
            int gaugeDelta;
            String feedbackMsg;
            if (totalScore >= 25) {
                gaugeDelta = 10;
            } else if (totalScore >= 15) {
                gaugeDelta = 5;
            } else {
                gaugeDelta = -10;
            }
            // 시스템별 상세 피드백
            String[][] feedbacks = {
                    {
                            "충분한 수면과 스트레스 관리, 올바른 스마트폰 사용 습관까지! 신경계 건강이 매우 우수합니다.",
                            "평균 이상의 습관이지만, 수면시간이나 스트레스 해소에 조금 더 신경 쓰면 더 좋겠어요.",
                            "수면, 스트레스, 스마트폰 사용 습관을 점검해 보세요. 신경계 건강 개선이 필요해요!"
                    },
                    {
                            "적절한 환기, 운동, 감기 예방까지! 호흡기 건강이 아주 좋습니다.",
                            "호흡기 건강을 잘 관리하고 있지만, 환기나 운동 습관을 조금만 더 챙기면 좋아요.",
                            "호흡기 건강이 걱정돼요. 환기·운동 습관을 다시 한번 점검해 보세요!"
                    },
                    {
                            "식습관이 매우 규칙적이고, 소화기 건강도 아주 우수합니다!",
                            "소화기 건강을 잘 관리하고 있지만, 식사시간이나 외식 빈도를 조금 더 신경 써보세요.",
                            "소화기 건강에 주의가 필요해요. 규칙적인 식사와 적당한 외식이 중요합니다."
                    },
                    {
                            "혈압 관리, 운동, 식습관까지! 순환기 건강이 매우 우수해요.",
                            "순환기 건강이 나쁘진 않지만, 더 규칙적인 운동과 식단이 필요해 보입니다.",
                            "순환기 건강에 빨간불! 혈압 측정과 운동, 식습관 개선을 시작해 보세요."
                    }
            };

            int fbIdx = (totalScore >= 25) ? 0 : (totalScore >= 15) ? 1 : 2;
            feedbackMsg = feedbacks[index][fbIdx];

            updateGauge(index, gaugeDelta, feedbackMsg);
        } else {
            updateGauge(index, 0, "답변을 건너뛰셨습니다. 건강 설문에 참여해 주세요!");
        }

        checkGameStatus();
    }



    private void resizeComponents() {
        int w = getWidth();
        int h = getHeight();

        int marginX = 20;
        int usableWidth = (int) (w * 0.28);
        int startY = 40;
        int spacing = (int) ((h - startY * 2) / 5.0);
        int barHeight = 24;
        int labelHeight = 18;

        for (int i = 0; i < 4; i++) {
            int y = startY + i * spacing;
            nameLabels[i].setBounds(marginX, y, 150, labelHeight);
            bars[i].setBounds(marginX, y + labelHeight + 2, usableWidth - 60, barHeight);
            percentLabels[i].setBounds(marginX + usableWidth - 50, y + labelHeight + 2, 50, barHeight);
        }

        background.setBounds(0, 0, w, h);
        background.setIcon(new ImageIcon(getHighQualityScaledImage(bgImg, w, h)));

        int humanW = (int) (w * 0.43);
        int humanH = (int) (h * 0.85);
        int humanX = (w - humanW) / 2;
        int humanY = (int) (h * 0.08);
        human.setBounds(humanX, humanY, humanW, humanH);
        human.setIcon(new ImageIcon(getHighQualityScaledImage(humanImg, humanW, humanH)));

        int lungW = (int) (humanW * 0.23);
        int lungH = (int) (humanH * 0.15);
        int lungX = humanX + (humanW - lungW) / 2;
        int lungY = humanY + (int) (humanH * 0.26);
        lungImg.setBounds(lungX, lungY - 80, lungW, lungH);
        // 기본 스케일 방식 사용
        lungImg.setIcon(new ImageIcon(lungRaw.getScaledInstance(lungW, lungH, Image.SCALE_SMOOTH)));
        lungArea = new Rectangle(lungX - humanX, lungY - 80 - humanY, lungW, lungH);

        int brainW = (int) (humanW * 0.12);
        int brainH = brainW;
        int brainX = humanX + (humanW - brainW) / 2;
        int brainY = humanY - (int) (brainH * 0.025);
        brainImg.setBounds(brainX, brainY + 30, brainW, brainH);
        // 기본 스케일 방식 사용
        brainImg.setIcon(new ImageIcon(brainRaw.getScaledInstance(brainW, brainH, Image.SCALE_SMOOTH)));
        brainArea = new Rectangle(brainX - humanX, brainY + 30 - humanY, brainW, brainH);

        int digW = (int) (humanW * 0.20);
        int digH = (int) (humanH * 0.15);
        int digX = humanX + (humanW - digW) / 2;
        int digY = humanY + (int) (humanH * 0.25);
        digestiveImg.setBounds(digX, digY, digW, digH);
        // 기본 스케일 방식 사용
        digestiveImg.setIcon(new ImageIcon(digestiveRaw.getScaledInstance(digW, digH, Image.SCALE_SMOOTH)));
        digArea = new Rectangle(digX - humanX, digY - humanY, digW, digH);

        int timerW = (int) (w * 0.14);
        int timerH = (int) (timerW * 0.1);
        int timerX = (int) (w * 0.76);
        int timerY = (int) (h * 0.06);
        gameTimer.setBounds(timerX, timerY, timerW, timerH);
        gameTimer.setIcon(new ImageIcon(getHighQualityScaledImage(timerImg, timerW, timerH)));

        int groupSize = (int) (timerH * 1.1);
        groupWave.setBounds(timerX + timerW + 15, timerY + 1, groupSize, groupSize);
        groupWave.setIcon(new ImageIcon(getHighQualityScaledImage(groupImg, groupSize, groupSize)));

        int rightSize = (int) (w * 0.25);
        int rightX = timerX;
        int rightY = timerY + timerH + 10;
        rightComponents.setBounds(rightX - 20, rightY, rightSize, rightSize);
        rightComponents.setIcon(new ImageIcon(getHighQualityScaledImage(rightImg, rightSize, rightSize)));

        int timeW = rightSize;
        int timeH = 30;
        int timeX = rightX - 21;
        int timeY = rightY + (rightSize - timeH) / 2;
        centerClockLabel.setBounds(timeX - 5, timeY - 5, timeW, timeH);

        int pulseW = timerW;
        int pulseH = timerH;
        int pulseX = rightX;
        int pulseY = rightY + rightSize + 5;
        pulseRateLabel.setBounds(pulseX, pulseY, pulseW, pulseH);
        pulseRateLabel.setIcon(new ImageIcon(getHighQualityScaledImage(pulseRateImg, pulseW, pulseH)));
        pulseGroupIcon.setBounds(pulseX + pulseW + 10, pulseY + 1, groupSize, groupSize);
        pulseGroupIcon.setIcon(new ImageIcon(getHighQualityScaledImage(pulseGroupImg, groupSize, groupSize)));

        ecgPanel.setBounds(pulseX, pulseY + pulseH + 5, rightSize, 80);

        int stopW = 140;
        int stopH = 60;
        stopBtn.setBounds(w - stopW - 30, h - stopH - 40, stopW, stopH);
        stopBtn.setIcon(new ImageIcon(getHighQualityScaledImage(stopImg, stopW, stopH)));

        scanning.setBounds((w - (int) (w * 0.17)) / 2 + 15, (int) (h * 0.03), (int) (w * 0.17), (int) (h * 0.05));
        scanning.setIcon(new ImageIcon(getHighQualityScaledImage(scanningImg, (int) (w * 0.17), (int) (h * 0.05))));
    }

    private Image getHighQualityScaledImage(Image srcImg, int w, int h) {
        BufferedImage resizedImg = new BufferedImage(w, h, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2 = resizedImg.createGraphics();
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.drawImage(srcImg, 0, 0, w, h, null);
        g2.dispose();
        return resizedImg;
    }

    class GraphPanel extends JPanel {
        private final LinkedList<Integer> waveform = new LinkedList<>();
        private final Timer timer;
        private final Random rand = new Random();
        private int t = 0;

        public GraphPanel() {
            setOpaque(true);
            setBackground(new Color(10, 20, 30));
            timer = new Timer(30, e -> {
                if (waveform.size() >= getWidth())
                    waveform.removeFirst();
                waveform.add(generateWaveformPoint());
                repaint();
            });
            timer.start();
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

        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            int width = getWidth();
            int height = getHeight();
            g.setColor(new Color(50, 65, 80));
            for (int x = 0; x < width; x += 20)
                g.drawLine(x, 0, x, height);
            for (int y = 0; y < height; y += 20)
                g.drawLine(0, y, width, y);
            g.setColor(Color.GREEN);
            int prevY = waveform.size() > 0 ? waveform.get(0) : height / 2;
            for (int i = 1; i < waveform.size(); i++) {
                int y = waveform.get(i);
                g.drawLine(i - 1, prevY, i, y);
                prevY = y;
            }
        }
    }

    public static void main(String[] args) {
        new KMHM_MainUI();
    }
}
