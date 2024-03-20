package ru.fafurin.hw.lesson2;

import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

public class TestRunner {

    final static String TEST_COMPLETED = "Test completed";
    private final Class<?> testClass;
    private final Object testObj;

    public TestRunner(Class<?> testClass) {
        this.testClass = testClass;
        testObj = initTestObj(testClass);
    }

    public void run() {
        for (Method annotatedTestMethod : getAnnotatedTestMethodsList()) {
            try {
                Object result = annotatedTestMethod.invoke(testObj);
                if (annotatedTestMethod.isAnnotationPresent(Test.class)) {
                    TestLogger.setMethodLog(annotatedTestMethod.getName(), result);
                }
            } catch (IllegalAccessException | InvocationTargetException e) {
                throw new RuntimeException();
            }
        }
        TestLogger.printLogs();
    }

    private List<Method> getAnnotatedTestMethodsList() {
        List<Method> annotatedTestMethods = new ArrayList<>();
        List<Method> testMethods = getAnnotatedNonPrivateTestClassMethods(Test.class);
        testMethods.sort(new TestMethodComparator());
        for (Method testMethod : testMethods) {
            annotatedTestMethods.addAll(getAnnotatedNonPrivateTestClassMethods(BeforeEach.class));
            annotatedTestMethods.add(testMethod);
            annotatedTestMethods.addAll(getAnnotatedNonPrivateTestClassMethods(AfterEach.class));
        }
        annotatedTestMethods.addAll(0, getAnnotatedNonPrivateTestClassMethods(BeforeAll.class));
        annotatedTestMethods.addAll(getAnnotatedNonPrivateTestClassMethods(AfterAll.class));
        return annotatedTestMethods;
    }

    private List<Method> getAnnotatedNonPrivateTestClassMethods(Class<? extends Annotation> annotationClass) {
        List<Method> testMethods = new ArrayList<>();
        for (Method testClassMethod : testClass.getDeclaredMethods()) {
            if (testClassMethod.isAnnotationPresent(annotationClass)) {
                if (testClassMethod.getModifiers() != Modifier.PRIVATE) {
                    testMethods.add(testClassMethod);
                }
            }
        }
        return testMethods;
    }

    private Object initTestObj(Class<?> testClass) {
        try {
            Constructor<?> defaultConstructor = testClass.getConstructor();
            return defaultConstructor.newInstance();
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("No default constructor");
        } catch (InstantiationException | InvocationTargetException | IllegalAccessException e) {
            throw new RuntimeException("TestClass object was not created");
        }
    }
}