import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Random;

import mg.nervous.NervousSystemGame;
import mg.respiratory.respiratory;
import mg.digestive.DigestiveMiniGame;
import mg.circulatory.CirculatoryGame;

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

    public KMHM_MainUI() {
        setTitle("KMHM - Game Screen");
        setSize(1073, 768);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        bgImg = new ImageIcon(getClass().getResource("UIBackground.png")).getImage();
        humanImg = new ImageIcon(getClass().getResource("3D Illustration.png")).getImage();
        scanningImg = new ImageIcon(getClass().getResource("_Scanning_.png")).getImage();
        timerImg = new ImageIcon(getClass().getResource("Game Timer.png")).getImage();
        groupImg = new ImageIcon(getClass().getResource("Group.png")).getImage();
        rightImg = new ImageIcon(getClass().getResource("Right Components.png")).getImage();
        stopImg = new ImageIcon(getClass().getResource("CTA.png")).getImage();
        lungRaw = new ImageIcon(getClass().getResource("폐 이미지.png")).getImage();
        brainRaw = new ImageIcon(getClass().getResource("신경계 이미지.png")).getImage();
        digestiveRaw = new ImageIcon(getClass().getResource("소화계 이미지.png")).getImage();
        pulseRateImg = new ImageIcon(getClass().getResource("Pulse Rate.png")).getImage();
        pulseGroupImg = new ImageIcon(getClass().getResource("Group-1.png")).getImage();

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
        });
        decayTimer.start();

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

        // ✅ 수정된 부분: 클릭 시 미니게임 실행
        human.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                Point p = e.getPoint();
                try {
                    if (brainArea != null && brainArea.contains(p)) {
                        new NervousSystemGame().setVisible(true);
                    } else if (lungArea != null && lungArea.contains(p)) {
                        new respiratory().setVisible(true);
                    } else if (digArea != null && digArea.contains(p)) {
                        new DigestiveMiniGame().setVisible(true);
                    } else {
                        new CirculatoryGame().setVisible(true);
                    }
                } catch (Exception ex) {
                    System.err.println("미니게임 실행 오류: " + ex.getMessage());
                }
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

    private void resizeComponents() {
        // ... 기존 컴포넌트 위치 계산 그대로 유지 (생략)
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
                if (!waveform.isEmpty() && waveform.size() >= getWidth()) {
                    waveform.removeFirst();
                }
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
