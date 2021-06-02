package Lesson6;

import java.util.Arrays;

public class Main {

//    1. Написать метод, которому в качестве аргумента передается не пустой одномерный целочисленный массив.
//    Метод должен вернуть новый массив, который получен путем вытаскивания из исходного массива элементов,
//    идущих после последней четверки. Входной массив должен содержать хотя бы одну четверку,
//    иначе в методе необходимо выбросить RuntimeException.
//    Написать набор тестов для этого метода (по 3-4 варианта входных данных).
//    Вх: [ 1 2 4 4 2 3 4 1 7 ] -> вых: [ 1 7 ].
//    Вх: [ 1 2 4 4 2 3 4 ] -> вых: [ ].
//    Вх: [ 1 2 44 2 34 1 2 ] -> вых: RuntimeException.
//    Вх: [ 1 2 1 7 ] -> вых: RuntimeException.

    public static int[] taskOne(int[] arr) throws NullArrayException {
        System.out.println("IN: " + Arrays.toString(arr));
        if (Arrays.equals(arr, new int[]{})) {
            throw new NullArrayException("Null array is not allowed!");
        } else {
            int j = whereIs4(arr);
            int[] resultArr = new int[] { };
            if (j + 1 == arr.length) {
                System.out.println("OUT: " + Arrays.toString(resultArr));
                return resultArr;
            } else {
                if (j + 2 == arr.length) {
                    resultArr = new int[]{arr[j + 1], };
                    System.out.println("OUT: " + Arrays.toString(resultArr));
                    return resultArr;
                } else {
                    resultArr = new int[]{arr[j + 1], arr[j + 2] };
                    System.out.println("OUT: " + Arrays.toString(resultArr));
                    return resultArr;
                }
            }
        }

    }

    public static int whereIs4(int[] arr) throws RuntimeException {
        for (int i = arr.length - 1; i >= 0; i--) {
            if (arr[i] == 4) {
                return i;
            }
        }
        throw new RuntimeException("Array doesn't have any 4!");
    }

//    2. Написать метод, который проверяет состав массива из чисел 1 и 4.
//    Если в нем нет хоть одной четверки или единицы, то метод вернет false; если в нем есть что то,
//    кроме 1 и 4, то метод вернет false; Написать набор тестов для этого метода (по 3-4 варианта входных данных).
//            [ 1 1 1 4 4 1 4 4 ] -> true
//            [ 1 1 1 1 1 1 ] -> false
//            [ 4 4 4 4 ] -> false
//            [ 1 4 4 1 1 4 3 ] -> false

    public static boolean taskTwo(int[] arr) throws NullArrayException {
        if (Arrays.equals(arr, new int[]{})) {
            throw new NullArrayException("Null array is not allowed!");
        }
        System.out.println("IN: " + Arrays.toString(arr));
        boolean have1 = false, have4 = false;
        for (int i = 0; i < arr.length; i++) {
            if (arr[i] == 1) {
                have1 = true;
            } else {
                if (arr[i] == 4) {
                    have4 = true;
                } else {
                    System.out.println("False");
                    return false;
                }
            }
        }
        if (have1 && have4) {
            System.out.println("True");
            return true;
        } else {
            System.out.println("False");
            return false;
        }
    }
}
