package ru.fafurin.hw.lesson3;

public class Main {
    public static void main(String[] args) {
        DatabaseExecutor databaseExecutor = new DatabaseExecutor(new PostgresConnector());
        EntityRepository repository = new EntityRepository(new PostgresConnector(), Student.class);

        Student student = (Student) repository.findById(4);

        System.out.println(student);

        databaseExecutor.persist(new Student(10, "Ivan", "Ivanov", 33)).saveOrUpdate();

        databaseExecutor.persist(student).delete();
    }
}
