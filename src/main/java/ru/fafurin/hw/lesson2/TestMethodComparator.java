package ru.fafurin.hw.lesson2;

import java.lang.reflect.Method;
import java.util.Comparator;

public class TestMethodComparator implements Comparator<Method> {
    @Override
    public int compare(Method method1, Method method2) {
        Test annotation1 = method1.getAnnotation(Test.class);
        Test annotation2 = method2.getAnnotation(Test.class);
        if (annotation1.order() == annotation2.order()) return 0;
        return annotation1.order() > annotation2.order() ? 1 : -1;
    }
}
