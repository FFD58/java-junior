package ru.fafurin.hw.lesson4;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;

import java.util.List;

public class Main {
    public static void main(String[] args) {
        Department department = new Department(1L, "Math");
        Student student1 = new Student(1L, "First student", "First student second name", 21, department);
        Student student2 = new Student(2L, "Second student", "Second student second name", 18, department);

        Configuration config = new Configuration().configure();
        try (SessionFactory sessionFactory = config.buildSessionFactory()) {

            insertObject(sessionFactory, department);
            insertObject(sessionFactory, student1);
            insertObject(sessionFactory, student2);

            Student student = (Student) findObjectById(sessionFactory, Student.class, 1L);

            updateStudentSecondName(sessionFactory, student, "new second name");

            deleteObject(student, sessionFactory);

            try (Session session = sessionFactory.openSession()) {
                List<Student> students = session.createQuery("select s from Student s where s.age < 20", Student.class).getResultList();
                System.out.println(students);
            }

        }
    }

    private static void deleteObject(Object object, SessionFactory sessionFactory) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.remove(object);
            transaction.commit();
        }
    }

    private static void updateStudentSecondName(SessionFactory sessionFactory, Student student, String secondName) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            student.setSecondName(secondName);
            session.merge(student);
            transaction.commit();
        }
    }

    private static Object findObjectById(SessionFactory sessionFactory, Class<?> className, Long id) {
        try (Session session = sessionFactory.openSession()) {
            return session.find(className, id);
        }
    }

    private static void insertObject(SessionFactory sessionFactory, Object object) {
        try (Session session = sessionFactory.openSession()) {
            Transaction transaction = session.beginTransaction();
            session.persist(object);
            transaction.commit();
        }
    }
}