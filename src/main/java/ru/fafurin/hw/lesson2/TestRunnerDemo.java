package ru.fafurin.hw.lesson2;

public class TestRunnerDemo {
    public static void main(String[] args) {
        TestRunner testRunner = new TestRunner(TestRunnerDemo.class);
        testRunner.run();
    }

    @BeforeAll
    static void beforeAll() {
        System.out.println("Before all");
    }

    @BeforeAll
    static void beforeAll1() {
        System.out.println("Before all1");
    }

    @BeforeEach
    void beforeEach() {
        System.out.println("Before each");
    }

    @AfterEach
    void afterEach() {
        System.out.println("After each");
    }

    @AfterEach
    void afterEach1() {
        System.out.println("After each1");
    }

    @AfterAll
    static void afterAll() {
        System.out.println("After all");
    }

    @Test(order = 1)
    String test1() {
        return Asserter.assertEquals(123, 100);
    }

    @Test(order = 8)
    String test2() {
        System.out.println("test2");
        return Asserter.assertEquals(100, 100);
    }

    @Test(order = 7)
    String test3() {
        System.out.println("test3");
        return Asserter.assertEquals(100, 100);
    }

    @Test
    String test4() {
        System.out.println("test4");
        return Asserter.assertEquals(200, 100);
    }

    @Test(order = 1)
    String test5() {
        System.out.println("test5");
        return Asserter.assertEquals(100, 100);
    }

    @Test(order = 1)
    private String test88() {
        return "test88";
    }

    public String test99() {
        return "test99";
    }
}