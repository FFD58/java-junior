package ru.fafurin.hw.lesson3;

public class StatementStringCreator {
    public String prepareSelectByIdStatementString(String columnsNames, String tableName, long id) {
        return "select " + columnsNames + " from \"" + tableName + "\" where id=" + id + ";";
    }

    public String prepareInsertStatementString(String columnsNames, String tableName, String columnsValues) {
        return "insert into " + tableName + " (" + columnsNames + ") values (" + columnsValues + ");";
    }

    public String prepareDeleteByIdStatementString(String tableName, long id) {
        return "delete from " + tableName + " where id=" + id + ";";
    }

    public String prepareSelectAllStatementString(String columnsNames, String tableName) {
        return "select " + columnsNames + " from \"" + tableName + "\";";
    }
}
