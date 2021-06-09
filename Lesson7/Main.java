import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

public class Main {

        public static void main(String[] args) {
        start(classGetter("TestOne"));
    }

    private static Class classGetter(String testClassName) {
        try {
            return Class.forName(testClassName);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static void start(Class classObject) {
        Method[] methods = classObject.getDeclaredMethods();
        try {
            int i = 0;
            for (Method o : methods) {

                if (o.getAnnotation(BeforeSuite.class) != null) {
                    if (i == 1) throw new RuntimeException();
                    System.out.println(o);
                    o.invoke(null, null);
                    i++;
                }
            }
            for (int j = 1; j < 11; j++) {
                for (Method o : methods) {
                    if (o.getAnnotation(Test.class) != null) {
                        Test test = o.getAnnotation(Test.class);
                        if (test.value() == j) {
                            System.out.println(o);
                            System.out.println("value: " + test.value());
                            o.invoke(null, null);
                        }
                    }
                }
            }
            int k = 0;
            for (Method o : methods) {
                if (o.getAnnotation(AfterSuite.class) != null) {
                    if (k == 1) throw new RuntimeException();
                    System.out.println(o);
                    o.invoke(null, null);
                    k++;
                }
            }
        } catch (RuntimeException | InvocationTargetException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
