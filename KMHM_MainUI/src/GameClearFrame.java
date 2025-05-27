import java.awt.Component; // ✅ 이거 꼭 있어야 함!


public class GameClearFrame extends GameOverFrame {

    public GameClearFrame(String timeResult) {
        super(timeResult);
        setTitle("Game Clear!");

        // ✅ 배경 바꾸기
    Component[] comps = getContentPane().getComponents();
    for (Component comp : comps) {
        if (comp instanceof BackgroundPanel) {
            ((BackgroundPanel) comp).setBackgroundImage("/img/GameClearImg.png");
            comp.repaint();
        }
    }


    }
}
