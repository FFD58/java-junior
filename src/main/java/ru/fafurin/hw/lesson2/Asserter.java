package ru.fafurin.hw.lesson2;

public class Asserter {
    public static String assertEquals(int expected, int actual) {
        if (expected != actual) return String.format("Test failed. Expected value %d not equals actual value %d", expected, actual);
        return TestRunner.TEST_COMPLETED;
    }
}
