package Lesson6;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;


public class MainTest {

    @Test
    public void taskOneTest1() throws NullArrayException {
        Assertions.assertArrayEquals(new int[]{5, 6}, Main.taskOne(new int[]{1, 2, 3, 4, 5, 6, 7}));
    }

    @Test
    public void taskOneTest2() throws NullArrayException {
        Assertions.assertArrayEquals(new int[]{5, }, Main.taskOne(new int[]{1, 2, 3, 4, 5}));
    }

    @Test
    public void taskOneTest3() throws NullArrayException {
        Assertions.assertArrayEquals(new int[]{ }, Main.taskOne(new int[]{1, 2, 3, 4}));
    }

    @Test
    public void taskOneTest4() {
        Assertions.assertThrows(NullArrayException.class, () -> Main.taskOne(new int[]{ }));
    }

    @Test
    public void taskOneTest5() {
        Assertions.assertThrows(RuntimeException.class, () -> Main.taskOne(new int[]{1, 2, 3, 5, 6, 7}));
    }


    @Test
    public void taskTwoTest1() throws NullArrayException {
        Assertions.assertTrue(Main.taskTwo(new int[]{1, 4}));
    }

    @Test
    public void taskTwoTest2() throws NullArrayException {
        Assertions.assertTrue(Main.taskTwo(new int[]{1, 1, 4, 1, 1, 4}));
    }

    @Test
    public void taskTwoTest3() throws NullArrayException {
        Assertions.assertFalse(Main.taskTwo(new int[]{1, 1, 1, 1, 1, 1}));
    }

    @Test
    public void taskTwoTest4() throws NullArrayException {
        Assertions.assertFalse(Main.taskTwo(new int[]{11, 2, 3, 4, 5, 44}));
    }
}