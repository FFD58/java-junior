package ru.fafurin.hw.lesson2;

import java.util.HashMap;
import java.util.Map;

public class TestLogger {
    static Map<String, Object> log = new HashMap<>();
    final static char SUCCESS_SYMBOL = (int) 149;
    final static char FAILURE_SYMBOL = '-';

    public static void setMethodLog(String methodName, Object result) {
        log.put(methodName, result);
    }

    public static void printLogs() {
        StringBuilder success = new StringBuilder("Completed tests:\n");
        StringBuilder failure = new StringBuilder("Failed tests:\n");
        for (Map.Entry<String, Object> entry : log.entrySet()) {
            String entryString = "  " + entry.getKey() + ". " + entry.getValue() + "\n";
            if (entry.getValue().toString().contains(TestRunner.TEST_COMPLETED)) {
                success.append(SUCCESS_SYMBOL).append(entryString);
            } else {
                failure.append(FAILURE_SYMBOL).append(entryString);
            }
        }
        System.out.println(success);
        System.err.println(failure);
    }
}
