package Lesson5;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class MainClass {
    public static final int CARS_COUNT = 4;
    /*синхронизация машин на старте/финише и системных сообщений*/
    public static CyclicBarrier cb1 = new CyclicBarrier(CARS_COUNT + 1);
    /*семафор для туннеля*/
    public  static Semaphore smp1 = new Semaphore(Tunnel.getTunnelLimit());
    /*выпускает машины только после сообщения ГОНКА НАЧАЛАСЬ*/
    public static CountDownLatch cdl1 = new CountDownLatch(1);
    /*атомарный инт для определения победителя*/
    public static AtomicInteger winnerNumber = new AtomicInteger(0);
    /*семафор для вывода сообщенеия о победе сразу после финиша машины (не работает)*/
    public static Semaphore smp2 = new Semaphore(1);


    public static void main(String[] args) {

        Thread msgThread = new Thread(() -> {
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Подготовка!!!");
            try {
                cb1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка началась!!!");
            cdl1.countDown();
            try {
                cb1.await();
            } catch (InterruptedException | BrokenBarrierException e) {
                e.printStackTrace();
            }
            System.out.println("ВАЖНОЕ ОБЪЯВЛЕНИЕ >>> Гонка закончилась!!!");
        });
        msgThread.start();


        Race race = new Race(new Road(60), new Tunnel(), new Road(40));
        Car[] cars = new Car[CARS_COUNT];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Car(race, 20 + (int) (Math.random() * 10));
        }

        ExecutorService service = Executors.newFixedThreadPool(CARS_COUNT);
        for (int i = 0; i < cars.length; i++) {
            int finalI = i;
            service.execute(() ->
                    cars[finalI].run());
        }
        service.shutdown();
//        msgThread.interrupt();
    }
}

