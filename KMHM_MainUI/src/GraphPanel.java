import javax.swing.*;
import java.awt.*;
import java.util.LinkedList;
import java.util.Random;

public class GraphPanel extends JPanel {
    private final LinkedList<Integer> waveform = new LinkedList<>();
    private final int maxPoints = 300;
    private final Timer timer;
    private final Random rand = new Random();
    private int t = 0;

    public GraphPanel() {
        setOpaque(true);
        setBackground(new Color(10, 20, 30)); // 어두운 파란색 배경
        timer = new Timer(30, e -> {
            if (waveform.size() >= maxPoints)
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

        // 다양한 파형 생성
        int spike = 0;
        if (t % 90 == 0)
            spike = -30;
        else if (t % 150 == 0)
            spike = +35;
        else if (t % 200 == 0)
            spike = -20;

        double sin = Math.sin(t * 0.2) * height * 0.25;
        double noise = rand.nextGaussian() * 4;

        return (int) (base + sin + noise + spike);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int width = getWidth();
        int height = getHeight();

        // 눈금 격자선
        g.setColor(new Color(50, 65, 80)); // 약간 밝은 청회색
        int gridSpacing = 20;
        for (int x = 0; x < width; x += gridSpacing)
            g.drawLine(x, 0, x, height);
        for (int y = 0; y < height; y += gridSpacing)
            g.drawLine(0, y, width, y);

        // 파형 그리기
        g.setColor(Color.GREEN);
        int prevY = waveform.size() > 0 ? waveform.get(0) : height / 2;

        for (int i = 1; i < waveform.size(); i++) {
            int y = waveform.get(i);
            g.drawLine(i - 1, prevY, i, y);
            prevY = y;
        }
    }
}
