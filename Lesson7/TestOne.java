public class TestOne {

    @BeforeSuite
    public static void test1() {
        System.out.println("Method says: before");
    }

//    @BeforeSuite
//    public static void test11() {
//        System.out.println("!!!");
//    }

    @Test(1)
    public static void test2() {
        System.out.println("Method says: 1");
    }

    @Test
    public static void test3() {
        System.out.println("Method says: 3");
    }

    @Test(3)
    public static void test4() {
        System.out.println("Method says: 2");
    }

    @AfterSuite
    public static void test5() {
        System.out.println("Method says: after");
    }
}
