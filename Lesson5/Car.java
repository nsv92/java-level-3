package Lesson5;


import java.util.concurrent.BrokenBarrierException;

public class Car implements Runnable {
    private static int CARS_COUNT;
    static {
        CARS_COUNT = 0;
    }
    private Race race;
    private int speed;
    private String name;
    public String getName() {
        return name;
    }
    public int getSpeed() {
        return speed;
    }
    public Car(Race race, int speed) {
        this.race = race;
        this.speed = speed;
        CARS_COUNT++;
        this.name = "Участник #" + CARS_COUNT;
    }
    @Override
    public void run() {
        try {
            System.out.println(this.name + " готовится");
            Thread.sleep(500 + (int)(Math.random() * 800));
            System.out.println(this.name + " готов");
            MainClass.cb1.await();  /*тут ждем остальные машины*/
            MainClass.cdl1.await(); /*тут ждем объявление ГОНКА НАЧАЛАСЬ*/
        } catch (Exception e) {
            e.printStackTrace();
        }
        for (int i = 0; i < race.getStages().size(); i++) {
            race.getStages().get(i).go(this);

            if (i + 1 == race.getStages().size()) {
                try {
                    MainClass.smp2.acquire();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
//            if (i + 1 == race.getStages().size() && MainClass.winnerNumber.compareAndSet(0, CARS_COUNT)) {
//                System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> " + this.name + " ВЫИГРАЛ!");
//            }
        }

        if (MainClass.winnerNumber.compareAndSet(0, CARS_COUNT)) {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> " + this.name + " ВЫИГРАЛ!");
        }
        MainClass.smp2.release();

        try {
            MainClass.cb1.await();
        } catch (InterruptedException | BrokenBarrierException e) {
            e.printStackTrace();
        }
    }
}
