import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import javax.swing.*;

public class KMHM_MainUI extends JFrame {
    // 🔻 사용자 정보 생성자 추가
    public KMHM_MainUI(String name, String age, String gender, String height, String weight) {
        this(); // 기존 기본 생성자 호출
        showPatientInfo(name, age, gender, height, weight); // 정보 표시
    }


    // --- UI 변수들
    private JLabel background, human, scanning, gameTimer, groupWave, rightComponents, stopBtn;
    private JLabel lungImg, brainImg, digestiveImg;
    private JLabel pulseRateLabel, pulseGroupIcon;
    private JLabel centerClockLabel;
    private int elapsedSeconds = 0;
    private Thread clockThread;
    private volatile boolean running = true;

    private JLabel[] nameLabels = new JLabel[4];
    private JLabel[] percentLabels = new JLabel[4];
    private JProgressBar[] bars = new JProgressBar[4];
    private final String[] systemNames = { "Nervous", "Respiratory", "Digestive", "Circulatory" };
    private final Color[] systemColors = { Color.GREEN, Color.RED, Color.YELLOW, Color.CYAN };

    private Rectangle lungArea, brainArea, digArea;
    private Image bgImg, humanImg, scanningImg, timerImg, groupImg, rightImg, stopImg;
    private Image lungRaw, brainRaw, digestiveRaw, pulseRateImg, pulseGroupImg;

    private GraphPanel ecgPanel;

    // 설문 및 피드백 저장용 변수들
    private int[] userTotalScores = new int[4];
    private int[] userGaugePercents = new int[4];
    private String[] userFeedbacks = new String[4];
    private java.util.List<String>[] userMissions = new java.util.List[4];

    private JButton btnRespiratory;
    private JButton btnNervous;
    private JButton btnDigestive;
    private JButton btnCirculatory;

    public KMHM_MainUI() {
        setTitle("KMHM - Game Screen");
        setSize(1073, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        btnRespiratory = createMiniGameButton("img/RespiratoryMG.png", () -> new RespiratoryGame(), 20, 400);
        btnNervous = createMiniGameButton("img/NervousMG.png", () -> new NervousSystemGame(), 150, 400);
        btnDigestive = createMiniGameButton("img/DigestiveMG.png", () -> new DigestiveMiniGame(), 20, 540);
        btnCirculatory = createMiniGameButton("img/CirculatoryMG.png", () -> new CirculatoryGame(), 150, 540);

        // --- 이미지 및 컴포넌트 생성

        bgImg = new ImageIcon(getClass().getResource("/img/UIBackground.png")).getImage();
        humanImg = new ImageIcon(getClass().getResource("/img/3DIllustration.png")).getImage();
        scanningImg = new ImageIcon(getClass().getResource("/img/_Scanning_.png")).getImage();
        timerImg = new ImageIcon(getClass().getResource("/img/GameTimer.png")).getImage();
        groupImg = new ImageIcon(getClass().getResource("/img/Group.png")).getImage();
        rightImg = new ImageIcon(getClass().getResource("/img/RightComponents.png")).getImage();
        stopImg = new ImageIcon(getClass().getResource("/img/CTA.png")).getImage();
        lungRaw = new ImageIcon(getClass().getResource("/img/lungimage.png")).getImage();
        brainRaw = new ImageIcon(getClass().getResource("/img/nervousImage.png")).getImage();
        digestiveRaw = new ImageIcon(getClass().getResource("/img/digestiveImage.png")).getImage();
        pulseRateImg = new ImageIcon(getClass().getResource("/img/PulseRate.png")).getImage();
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

        // centerClockLabel 세팅
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
        for (int i = 0; i < 4; i++) {
            bars[i].setValue(0);

            bars[i].setString("--");
            bars[i].setStringPainted(true);
            percentLabels[i].setText("--%");

        }

        // --- centerClock 타이머 (스레드) 시작
        clockThread = new Thread(() -> {
            while (running) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    break;
                }
                elapsedSeconds++;
                int min = elapsedSeconds / 60;
                int sec = elapsedSeconds % 60;
                SwingUtilities.invokeLater(() -> centerClockLabel.setText(String.format("%02d:%02d", min, sec)));
            }
        });
        clockThread.start();

        // --- ECG 그래프 패널 추가
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

        // --- stopBtn 클릭 시 요약+종료
        stopBtn.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                showSummaryAndExit();
            }
        });

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

    private JButton createMiniGameButton(String imageFileName, Runnable onClickAction, int x, int y) {
        ImageIcon icon = new ImageIcon(getClass().getResource("/" + imageFileName));
        JButton button = new JButton(icon);
        button.setBounds(x, y, 110, 120);
        button.setContentAreaFilled(false);
        button.setBorderPainted(false);
        button.setFocusPainted(false);
        button.addActionListener(e -> onClickAction.run());
        add(button); // ← 버튼을 현재 컨테이너에 추가
        return button;
    }


    private int[] clickCounts = new int[4];
    private int[] triggerClicks = new int[4];
    private Random random = new Random();

    private void checkGameStatus() {
    }

    private void updateGauge(int index, int increment, String feedbackMsg) {
        int val = bars[index].getValue();
        val = Math.min(100, val + increment);
        bars[index].setValue(val);
        bars[index].setString(null);
        percentLabels[index].setText(val + "%");

        JOptionPane.showMessageDialog(this, feedbackMsg, systemNames[index] + " 피드백", JOptionPane.INFORMATION_MESSAGE);
    }

    // --- 설문 조사(모든 저장)
    @SuppressWarnings("unchecked")
    private void increase(int index) {
        String[][] missionRecommends = {
                { "밤 11시 이전 스마트폰 사용 중단", "오늘 7시간 이상 자기", "스트레스 느낄 때 5분간 천천히 숨쉬기", "취침 30분 전 음악 듣기",
                        "오후 10시 이후 카페인 음료 피하기", "마음이 불안할 때 가벼운 산책하기", "명상 앱 켜고 10분간 마음 집중" },
                { "외출 전 미세먼지 수치 체크하고 마스크 챙기기", "하루 2회 이상 방 환기", "실내 가습기 사용 혹은 젖은 수건 걸기", "집안 공기청정기 청소하기",
                        "오늘은 담배와 먼 거리 유지", "심호흡 10회 천천히 해보기", "실내 식물 가까이서 5분간 심호흡" },
                { "오늘은 야식 금지!", "아침 식사 꼭 챙기기", "식사 전후 따뜻한 물 한 잔 마시기", "하루 채소 반찬 2가지 이상 먹기", "탄산음료 대신 물 마시기",
                        "식사 중 천천히 20번 이상 씹기", "저녁 8시 이후 음식 섭취 줄이기" },
                { "오늘 20분간 산책하기", "계단 오르기 3회 실천", "식사 시 짠 음식 줄이기", "식후 10분 스트레칭", "잠깐 일어나서 다리 털기 5회",
                        "버스 한 정거장 미리 내려 걷기", "마트에서 저염식 제품 하나 구매해보기" }
        };

        String[][] questions = {
                { "1. 하루 평균 수면 시간은 몇 시간인가요?", "2. 최근 일주일 동안 스트레스를 많이 받았나요?", "3. 자기 전에 전자기기(스마트폰, 컴퓨터 등)를 얼마나 사용하나요?",
                        "4. 평소 마음을 안정시키는 활동이 있나요?", "5. 최근 우울감을 느낀 적이 있나요?" },
                { "1. 하루에 얼마나 자주 환기를 시키시나요?", "2. 운동 중 숨이 차거나 호흡 곤란을 느낀 적이 있나요?", "3. 최근 감기, 기침 또는 인후통 등의 증상이 있었나요?",
                        "4. 미세먼지 심한 날 외출 시 마스크를 착용하나요?", "5. 최근 흡연 또는 간접흡연 경험이 있나요?" },
                { "1. 평소 식사 시간을 규칙적으로 지키시나요?", "2. 일주일에 몇 번 외식을 하나요?", "3. 변비나 복부 불편감을 자주 느끼시나요?",
                        "4. 평소 야식이나 폭식을 하나요?", "5. 물을 충분히 마시나요?" },
                { "1. 평소 혈압을 측정한 적이 있나요?", "2. 일주일에 몇 번 정도 유산소 운동(걷기, 조깅 등)을 하시나요?", "3. 짠 음식이나 기름진 음식을 자주 드시나요?",
                        "4. 최근 체중 변화를 느끼셨나요?", "5. 가족 중 고혈압이나 심장 질환 병력이 있나요?" }
        };
        String[][][] options = {
                { { "7~8시간", "6시간", "5시간", "4시간", "4시간 미만" },
                        { "아니오", "보통", "가끔", "자주", "매우 자주" },
                        { "1시간 전 끔", "30분 전", "10분 전", "잠들 때까지 사용", "계속 사용" },
                        { "있다(규칙적)", "가끔 있다", "거의 없다", "전혀 없다", "모름" },
                        { "없다", "거의 없다", "가끔", "자주", "매우 자주" } },
                { { "3번 이상", "2번", "1번", "가끔", "전혀 없음" },
                        { "없음", "거의 없음", "가끔 있음", "자주 있음", "매우 자주 있음" },
                        { "없음", "한 번", "두 번", "세 번 이상", "지금도 있음" },
                        { "항상 착용", "대부분 착용", "가끔 착용", "거의 안 함", "전혀 안 함" },
                        { "없음", "거의 없음", "가끔", "자주", "매우 자주" } },
                { { "항상", "대부분", "가끔", "거의 없음", "불규칙" },
                        { "없음", "주 1~2회", "주 3~4회", "주 5회 이상", "매일" },
                        { "없음", "거의 없음", "가끔 있음", "자주 있음", "매우 자주 있음" },
                        { "없음", "거의 없음", "가끔", "자주", "매우 자주" },
                        { "1.5L 이상", "1L 이상", "0.5~1L", "0.5L 미만", "거의 안 마심" } },
                { { "주기적으로 측정", "가끔", "1~2번", "거의 없음", "전혀 없음" },
                        { "주 5회 이상", "주 3~4회", "주 1~2회", "가끔", "전혀 없음" },
                        { "거의 안 먹음", "가끔 먹음", "보통", "자주 먹음", "매우 자주 먹음" },
                        { "없음", "1~2kg 증가", "1~2kg 감소", "3kg 이상 변화", "모름" },
                        { "없음", "잘 모름", "먼 친척", "직계 가족 중 1명", "직계 가족 2명 이상" } }
        };

        int[][][] scores = {

                { { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 } },
                { { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 } },
                { { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 } },
                { { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 }, { 10, 8, 6, 3, 0 } }
        };

        JPanel panel = new JPanel(new GridLayout(0, 2, 5, 8));
        JComboBox<String>[] fields = new JComboBox[5];
        for (int i = 0; i < 5; i++) {
            panel.add(new JLabel(questions[index][i]));
            fields[i] = new JComboBox<>(options[index][i]);
            panel.add(fields[i]);
        }

        int result = JOptionPane.showConfirmDialog(
                this, panel, systemNames[index] + " 건강 설문", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            int totalScore = 0;
            for (int i = 0; i < 5; i++) {
                int selIdx = fields[i].getSelectedIndex();
                totalScore += scores[index][i][selIdx];
            }
            int gaugePercent;
            String feedbackMsg;
            if (totalScore >= 45)
                gaugePercent = 100;
            else if (totalScore >= 40)
                gaugePercent = 80;
            else if (totalScore >= 35)
                gaugePercent = 60;
            else if (totalScore >= 25)
                gaugePercent = 40;
            else
                gaugePercent = 20;

            String[][] feedbacks = {
                    { "정말 훌륭해요! 신경계 건강이 완벽합니다.", "아주 좋네요. 조금만 더 신경쓰면 완벽해요.", "보통입니다. 생활습관을 점검해보세요.",
                            "다소 주의가 필요해요. 개선이 필요합니다.", "위험 신호! 반드시 관리하세요." },
                    { "호흡기 건강이 매우 좋습니다. 앞으로도 꾸준히 관리해 주세요.", "전반적으로 좋으나, 미세먼지 등 환경에도 신경 써주세요.",
                            "보통입니다. 운동, 환기에 신경 써주세요.", "주의가 필요합니다. 호흡기 건강을 점검해보세요.", "경고! 호흡기 건강 개선이 꼭 필요합니다." },
                    { "소화기 건강이 아주 우수합니다!", "좋은 편이나, 식습관을 조금 더 챙기면 더 좋아요.", "보통입니다. 야식, 폭식 등을 점검해 보세요.",
                            "소화기 건강에 주의가 필요합니다.", "경고! 식습관 개선이 시급합니다." },
                    { "순환기 건강이 매우 우수해요.", "아주 좋지만, 운동이나 식단에 조금 더 신경 써주세요.", "보통입니다. 가족력 등 점검 필요.",
                            "주의! 체중, 혈압 등 관리 필요합니다.", "경고! 순환기 건강에 심각한 위험이 있습니다." }
            };
            int fbIdx;
            if (totalScore >= 45)
                fbIdx = 0;
            else if (totalScore >= 40)
                fbIdx = 1;
            else if (totalScore >= 35)
                fbIdx = 2;
            else if (totalScore >= 25)
                fbIdx = 3;
            else
                fbIdx = 4;

            feedbackMsg = feedbacks[index][fbIdx];

            // 저장
            userTotalScores[index] = totalScore;
            userGaugePercents[index] = gaugePercent;
            userFeedbacks[index] = feedbackMsg;
            java.util.List<String> missions = new ArrayList<>(Arrays.asList(missionRecommends[index]));
            java.util.Collections.shuffle(missions);
            int count = Math.min(3, missions.size());
            userMissions[index] = new ArrayList<>();
            for (int i = 0; i < count; i++) {
                userMissions[index].add(missions.get(i));
            }

            bars[index].setValue(gaugePercent);
            bars[index].setString(null);
            percentLabels[index].setText(gaugePercent + "%");

            String msg = String.format(
                    "점수: %d점\n게이지: %d%%\n\n%s",
                    totalScore, gaugePercent, feedbackMsg);
            JOptionPane.showMessageDialog(this, msg, systemNames[index] + " 설문 결과", JOptionPane.INFORMATION_MESSAGE);

            StringBuilder goalMsg = new StringBuilder();
            goalMsg.append("오늘의 추천 건강 목표\n\n");
            for (String mission : userMissions[index]) {
                goalMsg.append("• ").append(mission).append("\n");
            }
            JOptionPane.showMessageDialog(this, goalMsg.toString(), "실천 목표", JOptionPane.INFORMATION_MESSAGE);

        } else {
            JOptionPane.showMessageDialog(this, "답변을 건너뛰셨습니다.", "알림", JOptionPane.WARNING_MESSAGE);
        }
        checkGameStatus();
    }

    // --- 요약+종료
    private void showSummaryAndExit() {
        StringBuilder sb = new StringBuilder();
        sb.append("====== 내 건강 설문 요약 ======\n\n");
        for (int i = 0; i < 4; i++) {
            sb.append("[").append(systemNames[i]).append("]\n");
            sb.append("- 점수: ").append(userTotalScores[i]).append("점\n");
            sb.append("- 게이지: ").append(userGaugePercents[i]).append("%\n");
            sb.append("- 피드백: ").append(userFeedbacks[i] != null ? userFeedbacks[i] : "(응답 없음)").append("\n");
            sb.append("- 오늘의 목표: ");
            if (userMissions[i] != null) {
                for (String m : userMissions[i])
                    sb.append(m).append(", ");
                sb.setLength(sb.length() - 2); // 마지막 콤마 제거
            } else {
                sb.append("(없음)");
            }
            sb.append("\n\n");
        }
        JOptionPane.showMessageDialog(this, sb.toString(), "설문 및 피드백 요약", JOptionPane.INFORMATION_MESSAGE);

        // 타이머 스레드 종료
        running = false;
        dispose();
        new GameOverFrame("Game Over");
    }

    private void resizeComponents() {
        int w = getWidth();
        int h = getHeight();

        int marginX = 20;
        int usableWidth = (int) (w * 0.28);
        int startY = 40;
        int startX = 150;
        int btnSize = 90;
        int spacing = (int) ((h - startY * 2) / 9.0);
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
        lungImg.setIcon(new ImageIcon(lungRaw.getScaledInstance(lungW, lungH, Image.SCALE_SMOOTH)));
        lungArea = new Rectangle(lungX - humanX, lungY - 80 - humanY, lungW, lungH);

        int brainW = (int) (humanW * 0.12);
        int brainH = brainW;
        int brainX = humanX + (humanW - brainW) / 2;
        int brainY = humanY - (int) (brainH * 0.025);
        brainImg.setBounds(brainX, brainY + 30, brainW, brainH);
        brainImg.setIcon(new ImageIcon(brainRaw.getScaledInstance(brainW, brainH, Image.SCALE_SMOOTH)));
        brainArea = new Rectangle(brainX - humanX, brainY + 30 - humanY, brainW, brainH);

        int digW = (int) (humanW * 0.20);
        int digH = (int) (humanH * 0.15);
        int digX = humanX + (humanW - digW) / 2;
        int digY = humanY + (int) (humanH * 0.25);
        digestiveImg.setBounds(digX, digY, digW, digH);
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
        private final Random rand = new Random();
        private int t = 0;

    }

    public static void main(String[] args) {
        new KMHM_MainUI();
    }
    // 🔻 오른쪽 하단에 환자 정보 출력
    private void showPatientInfo(String name, String age, String gender, String height, String weight) {
        JLabel infoLabel = new JLabel();
        infoLabel.setBounds(880, 420, 240, 160); // 오른쪽 하단 위치
        infoLabel.setForeground(Color.WHITE);
        infoLabel.setFont(new Font("맑은 고딕", Font.PLAIN, 23));
        infoLabel.setVerticalAlignment(SwingConstants.TOP);

        String infoText = "<html><div style='line-height:1.8'>"
                + "<b>이름:</b> " + name + "<br>"
                + "<b>나이:</b> " + age + "<br>"
                + "<b>성별:</b> " + gender + "<br>"
                + "<b>키:</b> " + height + " cm<br>"
                + "<b>몸무게:</b> " + weight + " kg"
                + "</div></html>";
        infoLabel.setText(infoText);

        // 🔻 기존 컴포넌트보다 위에 표시되도록 가장 나중에 add
        this.getContentPane().add(infoLabel);
        this.getContentPane().setComponentZOrder(infoLabel, 0);  // z-index 조정 (가장 위로)

        repaint();
    }



}
