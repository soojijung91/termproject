// 주요 인터페이스 및 추상 클래스
import java.util.*;

abstract class MiniGame {
    public abstract boolean play();
}

abstract class Organ {
    protected String name;
    protected int gauge;

    public Organ(String name) {
        this.name = name;
        this.gauge = 50;
    }

    public String getName() {
        return name;
    }

    public int getGauge() {
        return gauge;
    }

    public void increaseGauge(int value) {
        gauge = Math.min(100, gauge + value);
    }

    public void decreaseGauge(int value) {
        gauge = Math.max(0, gauge - value);
    }

    public boolean isDead() {
        return gauge <= 0;
    }

    public abstract MiniGame getMiniGame();
}

// 구체적 기관 클래스

class RespiratorySystem extends Organ {
    public RespiratorySystem() {
        super("호흡계");
    }

    @Override
    public MiniGame getMiniGame() {
        return new LungGame();
    }
}

class CirculatorySystem extends Organ {
    public CirculatorySystem() {
        super("순환계");
    }

    @Override
    public MiniGame getMiniGame() {
        return new BloodGame();
    }
}

class DigestiveSystem extends Organ {
    public DigestiveSystem() {
        super("소화계");
    }

    @Override
    public MiniGame getMiniGame() {
        return new FoodGame();
    }
}

class NervousSystem extends Organ {
    public NervousSystem() {
        super("신경계");
    }

    @Override
    public MiniGame getMiniGame() {
        return new MemoryCardGame();
    }
}

// 미니게임 클래스들 (콘솔 기반 예시)

class LungGame extends MiniGame {
    @Override
    public boolean play() {
        System.out.println("[호흡계 게임] 스페이스바를 10번 이상 눌러주세요 (모의 입력)");
        int spacePress = 11; // 입력 가정
        return spacePress >= 10;
    }
}

class BloodGame extends MiniGame {
    @Override
    public boolean play() {
        System.out.println("[순환계 게임] 장애물을 피했습니다! (모의 성공)");
        return true;
    }
}

class FoodGame extends MiniGame {
    @Override
    public boolean play() {
        System.out.println("[소화계 게임] 건강한 음식을 선택하였습니다! (모의 성공)");
        return true;
    }
}

class MemoryCardGame extends MiniGame {
    @Override
    public boolean play() {
        System.out.println("[신경계 게임] 카드 짝 맞추기 성공! (모의 성공)");
        return true;
    }
}

// 게임 제어 클래스



class GameController {
    private List<Organ> organs;
    private Random random = new Random();

    public GameController() {
        organs = List.of(
                new RespiratorySystem(),
                new CirculatorySystem(),
                new DigestiveSystem(),
                new NervousSystem()
        );
    }

    public void clickOrgan(Organ organ) {
        int chance = random.nextInt(8); // 1/8 확률로 미니게임 등장
        if (chance == 0) {
            boolean success = organ.getMiniGame().play();
            if (success) organ.increaseGauge(50);
            else organ.decreaseGauge(15);
        } else {
            organ.increaseGauge(3);
        }
    }

    public boolean isGameClear() {
        return organs.stream().allMatch(o -> o.getGauge() >= 80);
    }

    public boolean isGameOver() {
        return organs.stream().anyMatch(Organ::isDead);
    }

    public List<Organ> getOrgans() {
        return organs;
    }
}

// 메인 실행 클래스

public class BioGameMain {
    public static void main(String[] args) {
        GameController controller = new GameController();
        Scanner sc = new Scanner(System.in);

        while (!controller.isGameClear() && !controller.isGameOver()) {
            System.out.println("--- 각 기관의 상태 ---");
            int i = 1;
            for (Organ organ : controller.getOrgans()) {
                System.out.println(i + ". " + organ.getName() + " - 게이지: " + organ.getGauge());
                i++;
            }

            System.out.print("어떤 기관을 클릭하시겠습니까? (1~4): ");
            int choice = sc.nextInt();
            if (choice >= 1 && choice <= 4) {
                controller.clickOrgan(controller.getOrgans().get(choice - 1));
            }
        }

        if (controller.isGameClear()) {
            System.out.println("🎉 모든 게이지 80% 이상! 게임 클리어!");
        } else {
            System.out.println("💀 기관 중 하나가 사망! 게임 오버!");
        }
    }
}
