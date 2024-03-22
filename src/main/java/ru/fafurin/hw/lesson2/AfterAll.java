package ru.fafurin.hw.lesson2;

import java.lang.annotation.*;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface AfterAll {
}
