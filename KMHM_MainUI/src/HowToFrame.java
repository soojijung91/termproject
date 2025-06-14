import javax.swing.*;
import java.awt.*;

public class HowToFrame extends JFrame {

    private JLabel imageLabel;
    private JButton nextBtn, backBtn, cancelBtn;
    private boolean isFirstPage = true;

    private ImageIcon rawPage1, rawPage2;
    private ImageIcon nextIcon, backIcon, cancelIcon;

    public HowToFrame() {
        setTitle("HEALTH CARE SIMULATION 안내");
        setSize(1073, 768);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null);
        setResizable(false);

       
        rawPage1 = new ImageIcon(getClass().getResource("/img/HowToFrame01.png"));
        rawPage2 = new ImageIcon(getClass().getResource("/img/HowToFrame02.png"));

        
        nextIcon = new ImageIcon(getClass().getResource("/img/NEXT.png"));
        backIcon = new ImageIcon(getClass().getResource("/img/BACK.png"));
        cancelIcon = new ImageIcon(getClass().getResource("/img/CANCEL.png"));

        
        imageLabel = new JLabel();
        imageLabel.setBounds(0, 0, getWidth(), getHeight());
        imageLabel.setIcon(resizeImage(rawPage1));
        imageLabel.setLayout(null);
        add(imageLabel);

        
        nextBtn = createButton(nextIcon, 0.6);
        backBtn = createButton(backIcon, 0.6);
        cancelBtn = createButton(cancelIcon, 0.6);

        
        positionButtons(true);

        
        imageLabel.add(nextBtn);
        imageLabel.add(cancelBtn);

       
        nextBtn.addActionListener(e -> showSecondPage());
        backBtn.addActionListener(e -> showFirstPage());
        cancelBtn.addActionListener(e -> dispose());

        setVisible(true);
    }

    private JButton createButton(ImageIcon icon, double scale) {
        int originalW = icon.getIconWidth();
        int originalH = icon.getIconHeight();

        int newW = (int)(originalW * scale);
        int newH = (int)(originalH * scale);

        Image scaled = icon.getImage().getScaledInstance(newW, newH, Image.SCALE_SMOOTH);
        ImageIcon resizedIcon = new ImageIcon(scaled);

        JButton btn = new JButton(resizedIcon);
        btn.setSize(newW, newH);
        btn.setContentAreaFilled(false);
        btn.setBorderPainted(false);
        btn.setFocusPainted(false);
        return btn;
    }

    private void positionButtons(boolean firstPage) {
        int margin = 100;
        int y = getHeight() - nextBtn.getHeight() - margin;

        if (firstPage) {
            nextBtn.setLocation(getWidth() - nextBtn.getWidth() - margin, y);
            cancelBtn.setLocation(margin, y);
        } else {
            backBtn.setLocation(margin, y);
            cancelBtn.setLocation(getWidth() - cancelBtn.getWidth() - margin, y);
        }
    }

    private ImageIcon resizeImage(ImageIcon rawImg) {
        Image scaled = rawImg.getImage().getScaledInstance(getWidth(), getHeight(), Image.SCALE_SMOOTH);
        return new ImageIcon(scaled);
    }

    private void showSecondPage() {
        isFirstPage = false;
        imageLabel.setIcon(resizeImage(rawPage2));
        imageLabel.remove(nextBtn);
        imageLabel.remove(cancelBtn);
        imageLabel.add(backBtn);
        imageLabel.add(cancelBtn);
        positionButtons(false);
        imageLabel.repaint();
        imageLabel.revalidate();
    }

    private void showFirstPage() {
        isFirstPage = true;
        imageLabel.setIcon(resizeImage(rawPage1));
        imageLabel.remove(backBtn);
        imageLabel.remove(cancelBtn);
        imageLabel.add(nextBtn);
        imageLabel.add(cancelBtn);
        positionButtons(true);
        imageLabel.repaint();
        imageLabel.revalidate();
    }
}
