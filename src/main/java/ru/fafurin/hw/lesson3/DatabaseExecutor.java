package ru.fafurin.hw.lesson3;

import ru.fafurin.hw.lesson3.annotations.Column;
import ru.fafurin.hw.lesson3.annotations.Table;

import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class DatabaseExecutor {
    private final ConnectionInterface connector;
    private final StatementStringCreator statementStringCreator = new StatementStringCreator();
    private final Map<String, Object> columnsData = new LinkedHashMap<>();
    private long objectId;
    private String tableName;
    private String columnsNames;
    private String columnsValues;

    public DatabaseExecutor(ConnectionInterface connector) {
        this.connector = connector;
    }

    public DatabaseExecutor persist(Object object) {
        getObjectData(object);
        prepareStatementStrings();
        return this;
    }

    public void saveOrUpdate() {
        try (Connection dbConnection = connector.getDBConnection(); Statement statement = dbConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(statementStringCreator.prepareSelectByIdStatementString(columnsNames, tableName, objectId));
            if (!resultSet.next()) {
                statement.execute(statementStringCreator.prepareInsertStatementString(columnsNames, tableName, columnsValues));
                System.out.println("Record successfully added to database");
            } else {
                statement.executeUpdate(prepareUpdateByIdStatementString());
                System.out.println("Record successfully updated");
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    public void delete() {
        try (Connection dbConnection = connector.getDBConnection(); Statement statement = dbConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(statementStringCreator.prepareSelectByIdStatementString(columnsNames, tableName, objectId));
            if (resultSet.next()) {
                statement.executeUpdate(statementStringCreator.prepareDeleteByIdStatementString(tableName, objectId));
                System.out.println("Record successfully deleted");
            } else {
                System.out.printf("Record with id: %d not found\n", objectId);
            }
        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }
    }

    private void prepareStatementStrings() {
        StringBuilder names = new StringBuilder();
        StringBuilder values = new StringBuilder();
        for (Map.Entry<String, Object> entry : columnsData.entrySet()) {
            names.append(entry.getKey()).append(", ");
            values.append(entry.getValue()).append(", ");
        }
        columnsNames = formatStatementString(names);
        columnsValues = formatStatementString(values);
    }

    private void getObjectData(Object object) {
        Class<?> objectClass = object.getClass();
        Table tableAnno = objectClass.getAnnotation(Table.class);
        tableName = tableAnno.name();
        for (Field declaredField : objectClass.getDeclaredFields()) {
            Column columnAnno = declaredField.getAnnotation(Column.class);
            String columnName = columnAnno.name();
            declaredField.setAccessible(true);
            Object value;
            try {
                value = declaredField.get(object);
                if (value.getClass().getName().equals("java.lang.String")) {
                    value = "'" + value + "'";
                }
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
            }
            columnsData.put(columnName, value);
            objectId = (long) columnsData.get("id");
        }
    }

    private String formatStatementString(StringBuilder inputString) {
        inputString.delete(inputString.length() - 2, inputString.length());
        return inputString.toString();
    }

    private String prepareUpdateByIdStatementString() {
        StringBuilder statementString = new StringBuilder("update ");
        statementString.append(tableName).append(" set ");
        for (Map.Entry<String, Object> entry : columnsData.entrySet()) {
            if (!entry.getKey().equals("id")) {
                statementString.append(entry.getKey()).append("=").append(entry.getValue()).append(", ");
            }
        }
        statementString.append("where id=").append(objectId).append(";");
        return statementString.toString().replace(", where", " where");
    }
}