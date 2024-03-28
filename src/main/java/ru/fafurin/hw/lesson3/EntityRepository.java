package ru.fafurin.hw.lesson3;

import ru.fafurin.hw.lesson3.annotations.Column;
import ru.fafurin.hw.lesson3.annotations.Table;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EntityRepository {
    private final StatementStringCreator statementStringCreator = new StatementStringCreator();
    private final String tableName;
    private String columnsNames;
    private final ConnectionInterface connector;
    private final Class<?> entity;
    private int objectId;

    public EntityRepository(ConnectionInterface connector, Class<?> entity) {
        this.connector = connector;
        this.entity = entity;
        tableName = entity.getAnnotation(Table.class).name();
        getObjectColumnsNames();
    }

    public Object findById(int id) {
        objectId = id;
        return pushDataInEmptyObject(getObjectFromDbById());
    }

    public List<Object> findAll() {
        List<Object> classObjects = new ArrayList<>();
        List<Map<String, Object>> dbObjects = getAllObjectsFromDb();
        for (Map<String, Object> object : dbObjects) {
            classObjects.add(pushDataInEmptyObject(object));
        }
        return classObjects;
    }

    private List<Map<String, Object>> getAllObjectsFromDb() {
        try (Connection dbConnection = connector.getDBConnection(); Statement statement = dbConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(statementStringCreator.prepareSelectAllStatementString(columnsNames, tableName));
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();

            final int columnCount = resultSetMetaData.getColumnCount();
            List<Map<String, Object>> list = new ArrayList<>();
            while (resultSet.next()) {
                Map<String, Object> values = new HashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    String columnNameCamelCase = snakeCaseToCamelCase(resultSetMetaData.getColumnName(i));
                    values.put(columnNameCamelCase, resultSet.getObject(i));
                }
                list.add(values);
            }
            return list;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private Map<String, Object> getObjectFromDbById() {
        try (Connection dbConnection = connector.getDBConnection(); Statement statement = dbConnection.createStatement()) {
            ResultSet resultSet = statement.executeQuery(statementStringCreator.prepareSelectByIdStatementString(columnsNames, tableName, objectId));
            ResultSetMetaData resultSetMetaData = resultSet.getMetaData();
            final int columnCount = resultSetMetaData.getColumnCount();
            Map<String, Object> values = new HashMap<>();
            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String columnNameCamelCase = snakeCaseToCamelCase(resultSetMetaData.getColumnName(i));
                    values.put(columnNameCamelCase, resultSet.getObject(i));
                }
            }
            return values;
        } catch (SQLException e) {
            throw new RuntimeException();
        }
    }

    private void getObjectColumnsNames() {
        List<String> tmpColumnsNames = new ArrayList<>();
        for (Field declaredField : entity.getDeclaredFields()) {
            Column columnAnno = declaredField.getAnnotation(Column.class);
            String columnName = columnAnno.name();
            declaredField.setAccessible(true);
            tmpColumnsNames.add(columnName);
        }
        columnsNames = String.join(", ", tmpColumnsNames);
    }

    private String snakeCaseToCamelCase(String str) {
        while (str.contains("_")) {
            str = str.replaceFirst("_[a-z]", String.valueOf(Character.toUpperCase(str.charAt(str.indexOf("_") + 1))));
        }
        return str;
    }

    protected Object pushDataInEmptyObject(Map<String, Object> data) {
        Object emptyObject = createEmptyObject();
        List<Method> setters = getObjectSetters();

        for (Method setter : setters) {
            for (Map.Entry<String, Object> entry : data.entrySet()) {
                if (setter.getName().toLowerCase().contains(entry.getKey().toLowerCase())) {
                    invokeSetter(emptyObject, entry.getValue(), setter);
                }
            }
        }
        return emptyObject;
    }

    private Object createEmptyObject() {
        Object object;
        try {
            Constructor<?> constructor = entity.getDeclaredConstructor();
            object = constructor.newInstance();
        } catch (NoSuchMethodException | InstantiationException |
                 IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException(e);
        }
        return object;
    }

    private void invokeSetter(Object obj, Object variableValue, Method setter) {
        try {
            setter.invoke(obj, variableValue);
        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private List<Method> getObjectSetters() {
        List<Method> setters = new ArrayList<>();
        for (Method declaredMethod : entity.getDeclaredMethods()) {
            if (declaredMethod.getName().contains("set")) {
                setters.add(declaredMethod);
            }
        }
        return setters;
    }
}
