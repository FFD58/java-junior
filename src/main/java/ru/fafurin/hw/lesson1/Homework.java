package ru.fafurin.hw.lesson1;

import com.github.javafaker.Faker;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Homework {
    public static void main(String[] args) {
        Faker faker = new Faker();

        List<Department> departments = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            departments.add(new Department("Department № " + i));
        }

        List<Person> persons = new ArrayList<>();
        for (int i = 0; i < 50; i++) {
            persons.add(new Person(
                    faker.name().name(),
                    ThreadLocalRandom.current().nextInt(19, 55),
                    ThreadLocalRandom.current().nextInt(65000, 155000),
                    departments.get(ThreadLocalRandom.current().nextInt(departments.size()))
            ));
        }

// 1.
        printNamesOrdered(persons);

// 2.
        System.out.println(printDepartmentOldestPerson(persons));

// 3.
        System.out.println(findFirstPersons(persons));

// 4.
        System.out.println(findTopDepartment(persons));
    }

    // 1. Вывести на консоль отсортированные (по алфавиту) имена персонов
    private static void printNamesOrdered(List<Person> persons) {
        persons.stream()
                .sorted(Comparator.comparing(Person::name))
                .forEach(System.out::println);
    }

    // 2. В каждом департаменте найти самого взрослого сотрудника.
    //    Вывести на консоль мапипнг department -> personName
    //    Map<Department, Person>
    private static Map<Department, Person> printDepartmentOldestPerson(List<Person> persons) {
        Comparator<Person> personAgeComparator = Comparator.comparing(Person::age);
        return persons.stream()
                .collect(Collectors.toMap(Person::department, Function.identity(),
                        (first, second) -> personAgeComparator.compare(first, second) > 0 ? first : second));
    }

    // 3. Найти 10 первых сотрудников, младше 30 лет, у которых зарплата выше 50_000
    private static List<Person> findFirstPersons(List<Person> persons) {
        return persons.stream()
                .filter(person -> person.age() < 30)
                .filter(person -> person.salary() > 50_000)
                .limit(10)
                .toList();
    }

    // 4. Найти департамент, чья суммарная зарплата всех сотрудников максимальна
    private static Department findTopDepartment(List<Person> persons) {
        return persons.stream()
                .collect(Collectors.groupingBy(Person::department,
                        Collectors.summingInt(Person::salary)))
                .entrySet().stream()
                .max(Map.Entry.comparingByValue()).get().getKey();
    }

    record Person(String name, int age, int salary, Department department) {
    }

    record Department(String name) {
    }
}
