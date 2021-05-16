package Lesson_1;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class Lesson1 {
    public static void main(String[] args) {

        System.out.println("Task №1");
        String[] arrStr = {"QQQ", "WWW", "EEE", "RRR", "TTT", "YYY"};
        Integer[] arrInt = {111, 222, 333, 444, 555, 666};
        System.out.println("String array: " + Arrays.toString(arrStr));
        swap(arrStr, 1, 4);
        System.out.println("Swapped string array: " + Arrays.toString(arrStr));
        System.out.println("Integer array: " + Arrays.toString(arrInt));
        swap(arrInt, 0, 5);
        System.out.println("Swapped integer array: " + Arrays.toString(arrInt));

        System.out.println("Task №2");
        System.out.println("ArrayList from String[]: " + toList(arrStr).toString());
        System.out.println("ArrayList from Integer[]: " + toList(arrInt).toString());

        System.out.println("Task №3");
        Box<Apple> box1 = new Box<>();
        Box<Apple> box2 = new Box<>();
        Apple apple = new Apple();
        for (int i = 0; i < 6; i++) {
            box1.add(apple);
        }
        for (int i = 0; i < 10; i++) {
            box2.add(apple);
        }
        Box<Orange> box3 = new Box<>();
        Orange orange = new Orange();
        for (int i = 0; i < 4; i++) {
            box3.add(orange);
        }

        System.out.println("Box1 weight: " + box1.getWeight());
        System.out.println("Box2 weight: " + box2.getWeight());
        System.out.println("Box3 weight: " + box3.getWeight());

        System.out.println("Box1 compare to box2: " + box1.compare(box2));
        System.out.println("Box1 compare to box3: " + box1.compare(box3));

        System.out.println("Moving fruits from box1 to box2");
        box1.move(box2);
        System.out.println("Box1 new weight: " + box1.getWeight());
        System.out.println("Box2 new weight: " + box2.getWeight());
//        box2.move(box3);      При попытке положить яблоки в коробку с апельсинами
//                              Идея выводит ошибку на этапе компиляции.
    }

    private static <T> void swap (T[] arr, int pos1, int pos2) {
        T temp = arr[pos2];
        arr[pos2] = arr[pos1];
        arr[pos1] = temp;
    }

    private static <T> ArrayList<T> toList (T[] arr) {
        ArrayList<T> list = new ArrayList<>();
        Collections.addAll(list, arr);
        return list;
    }
}
