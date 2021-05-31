package Lesson5;

public class Tunnel extends Stage {
    private static int tunnelLimit = MainClass.CARS_COUNT / 2;

    public Tunnel() {
        this.length = 80;
        this.description = "Тоннель " + length + " метров";
    }

    @Override
    public void go(Car c) {
        try {
            try {
                System.out.println(c.getName() + " готовится к этапу(ждет): " + description);
                MainClass.smp1.acquire();
                System.out.println(c.getName() + " начал этап: " + description);
                Thread.sleep(length / c.getSpeed() * 1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                System.out.println(c.getName() + " закончил этап: " + description);
                MainClass.smp1.release();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static int getTunnelLimit() {
        return tunnelLimit;
    }
}
