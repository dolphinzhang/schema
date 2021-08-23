package org.dol.database.schema;


import lombok.SneakyThrows;
import org.dol.database.utils.Utils;

import java.nio.charset.StandardCharsets;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class DatabaseSchemaLoader {

    private static final String                      EMPTY_STRING      = "";
    private static       Map<String, DatabaseSchema> databaseSchemaMap = new HashMap<>();
    private final        String                      driverClassName;
    private final        String                      jdbcUrl;
    private final        String                      userName;
    private final        String                      password;
    private final        String                      tablePrefix;

    public DatabaseSchemaLoader(String driverClassName,
                                String jdbcUrl,
                                String userName,
                                String password, String tablePrefix) {

        this.driverClassName = driverClassName;
        this.jdbcUrl = jdbcUrl;
        this.userName = userName;
        this.password = password;
        this.tablePrefix = tablePrefix;
    }

    public static DatabaseSchemaLoader newLoader(String driverClassName,
                                                 String jdbcUrl,
                                                 String userName,
                                                 String password, String tablePrefix) {
        return new DatabaseSchemaLoader(driverClassName, jdbcUrl, userName, password, tablePrefix);

    }

    /**
     * 参照方法名.
     *
     * @param tableSchema the table schema
     * @return the indexes
     * @throws SQLException the SQL exception
     */
    private static List<IndexSchema> getIndexes(Connection connection, TableSchema tableSchema) throws SQLException {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        final ResultSet rs = databaseMetaData.getIndexInfo(null, null, tableSchema.getTableName(), false, false);

        final List<IndexSchema> indexSchemas = new ArrayList<>();
        final Map<String, IndexSchema> indexColumns = new HashMap<>();
        // List<ColumnSchema> memberColumns = new ArrayList<ColumnSchema>();
        // keySchema.setMemberColumns(memberColumns);
        addIndex(tableSchema, rs, indexColumns, indexSchemas);
        // rs = databaseMetaData.getIndexInfo(tableSchema.getTableCatalog(),
        // null, tableSchema.getTableName(), false, true);
        // addIndex(tableSchema, rs, indexColumns, indexSchemas);
        return indexSchemas;
    }

    /**
     * 添加索引到tableSchema.
     *
     * @param tableSchema  the table schema
     * @param rs           the rs
     * @param indexColumns the index columns
     * @param indexSchemas the index schemas
     * @throws SQLException the SQL exception
     */
    private static void addIndex(TableSchema tableSchema, ResultSet rs, Map<String, IndexSchema> indexColumns, List<IndexSchema> indexSchemas) throws SQLException {
        while (rs.next()) {
            final String indexName = rs.getString("INDEX_NAME");// 索引的名称
            if (indexName == null || indexName.equalsIgnoreCase("PRIMARY")) {
                continue;
            }
            final String columnName = rs.getString("COLUMN_NAME");// 列名
            IndexSchema indexSchema;
            if (!indexColumns.containsKey(indexName)) {
                indexSchema = new IndexSchema();
                indexSchema.setIndexName(indexName);
                indexSchema.setUnique(!rs.getBoolean("NON_UNIQUE"));
                indexSchema.setType(rs.getShort("TYPE"));
                indexSchema.setOrder(rs.getString("ASC_OR_DESC"));
                List<ColumnSchema> columnSchemas = new ArrayList<>();
                indexSchema.setMemberColumns(columnSchemas);
                indexSchemas.add(indexSchema);
                indexColumns.put(indexName, indexSchema);
            } else {
                indexSchema = indexColumns.get(indexName);
            }
            for (final ColumnSchema column : tableSchema.getColumns()) {
                if (columnName.equalsIgnoreCase(column.getColumnName())) {
                    indexSchema.getMemberColumns().add(column);
                    break;
                }
            }
        }
    }

    /**
     * 关闭数据库连接.
     *
     * @param connection the connection
     */
    private static void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (final Exception e) {
            // TODO: handle exception
        }

    }

    /**
     * Gets the columns.
     *
     * @param tableSchema the table schema
     * @return the columns
     * @throws SQLException the SQL exception
     */
    private static List<ColumnSchema> getColumns(Connection connection, TableSchema tableSchema) throws SQLException {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        final ResultSet rs = databaseMetaData.getColumns(tableSchema.getTableCatalog(), null, tableSchema.getTableName(), "%");
        final List<ColumnSchema> columnSchemas = new ArrayList<>();
        while (rs.next()) {
            // String tableCat = rs.getString("TABLE_CAT");// 表目录（可能为空）
            // String tableSchemaName = rs.getString("TABLE_SCHEM");//
            // 表的架构（可能为空）
            // String tableName_ = rs.getString("TABLE_NAME");// 表名
            final String columnName = rs.getString("COLUMN_NAME");// 列名
            final int dataType = rs.getInt("DATA_TYPE"); // 对应的java.sql.Types类型
            String dataTypeName = rs.getString("TYPE_NAME");// java.sql.Types类型
            String[] parts = dataTypeName.split("\\s");
            dataTypeName = parts[0];

            final int columnSize = rs.getInt("COLUMN_SIZE");// 列大小
            final int decimalDigits = rs.getInt("DECIMAL_DIGITS");// 小数位数
            final int nullAble = rs.getInt("NULLABLE");// 是否允许为null
            // String isNullable = rs.getString("IS_NULLABLE");
            final String remarks = rs.getString("REMARKS");// 列描述
            final String defaultValue = rs.getString("COLUMN_DEF");// 默认值
            // int sqlDataType = rs.getInt("SQL_DATA_TYPE");// sql数据类型

            final String isAutoincrement = rs.getString("IS_AUTOINCREMENT");
            // String isGeneratedColumn =
            // rs.getString("IS_GENERATEDCOLUMN");

            final ColumnSchema columnSchema = new ColumnSchema();
            columnSchema.setTableSchema(tableSchema);
            columnSchema.setColumnName(columnName);
            columnSchema.setRemarks(remarks);
            columnSchema.setColumnSize(columnSize);
            columnSchema.setDataType(dataType);
            columnSchema.setDataTypeName(dataTypeName);
            columnSchema.setAutoIncrement(isAutoincrement.equalsIgnoreCase("YES"));
            columnSchema.setDecimalDigits(decimalDigits);
            columnSchema.setDefaultValue(defaultValue);
            columnSchema.setNullable(nullAble == 1);
            columnSchemas.add(columnSchema);
        }
        return columnSchemas;
    }

    @SneakyThrows
    public static DatabaseSchema load(String driverClassName,
                                      String jdbcUrl,
                                      String userName,
                                      String password, String tablePrefix) {
        DatabaseSchema databaseSchema = databaseSchemaMap.get(jdbcUrl);
        if (databaseSchema == null) {
            databaseSchema = doLoad(driverClassName, jdbcUrl, userName, password, tablePrefix);
            databaseSchemaMap.put(jdbcUrl, databaseSchema);
        }
        return databaseSchema;
    }

    @SneakyThrows
    public static DatabaseSchema load(Connection connection, String tablePrefix) {
        DatabaseSchema databaseSchema = databaseSchemaMap.get(connection.getSchema());
        if (databaseSchema == null) {
            databaseSchema = doLoad(connection, tablePrefix);
            databaseSchemaMap.put(connection.getSchema(), databaseSchema);
        }
        return databaseSchema;
    }

    /**
     * 获取主键.
     *
     * @param tableSchema the table schema
     * @return the primary key
     * @throws SQLException the SQL exception
     */
    private static KeySchema getPrimaryKey(Connection connection, TableSchema tableSchema) throws Exception {
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        final ResultSet rs = databaseMetaData.getPrimaryKeys(tableSchema.getTableCatalog(), null, tableSchema.getTableName());
        final KeySchema keySchema = new KeySchema();
        final List<ColumnSchema> memberColumns = new ArrayList<>();
        keySchema.setMemberColumns(memberColumns);
        while (rs.next()) {
            final String columnName = rs.getString("COLUMN_NAME");// 列名
            for (final ColumnSchema column : tableSchema.getColumns()) {
                if (columnName.equalsIgnoreCase(column.getColumnName())) {
                    column.setPrimary(true);
                    tableSchema.setPrimaryColumn(column);
                    memberColumns.add(column);
                    break;
                }
            }
            final String keyName = rs.getString("PK_NAME"); // 对应的java.sql.Types类型
            keySchema.setKeyName(keyName);
        }
        return keySchema;

    }

    @SneakyThrows
    private static DatabaseSchema doLoad(String driverClassName,
                                         String jdbcUrl,
                                         String userName,
                                         String password,
                                         String tablePrefix) {
        Connection connection = null;
        try {
            connection = getConnection(driverClassName, jdbcUrl, userName, password);
            return doLoad(connection, tablePrefix);
        } finally {
            closeConnection(connection);
        }
    }

    private static DatabaseSchema doLoad(Connection connection, String tablePrefix) throws Exception {
        DatabaseSchema databaseSchema = new DatabaseSchema();
        final DatabaseMetaData databaseMetaData = connection.getMetaData();
        databaseMetaData.getSchemaTerm();
        final String[] types = {"table", "view"};
        final ResultSet rs = databaseMetaData.getTables(null, "dbo", null, types);
        final List<TableSchema> tableSchemas = new ArrayList<>();
        while (rs.next()) {
            final TableSchema tableSchema = new TableSchema(tablePrefix);
            tableSchema.setTableCatalog(rs.getString("TABLE_CAT"));
            tableSchema.setTableName(rs.getString("TABLE_NAME"));
            tableSchema.setComment(rs.getString("REMARKS"));
            final List<ColumnSchema> columnSchemas = getColumns(connection, tableSchema);
            tableSchema.setColumns(columnSchemas);
            tableSchema.setIndexes(getIndexes(connection, tableSchema));
            tableSchema.setPrimaryKey(getPrimaryKey(connection, tableSchema));
            tableSchema.setView(rs.getString(4).equals("VIEW"));
            tableSchemas.add(tableSchema);
        }
        setComments(connection, tableSchemas);
        databaseSchema.setTables(tableSchemas);
        return databaseSchema;
    }

    private static Connection getConnection(String driverClassName,
                                            String jdbcUrl,
                                            String userName,
                                            String password) {
        try {
            Class.forName(driverClassName);
            return DriverManager.getConnection(jdbcUrl,
                    userName,
                    password
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Parses the.
     *
     * @param all the all
     * @return the string
     */
    private static String parse(String all) {
        String comment;
        final int index = all.indexOf("COMMENT='");
        if (index < 0) {
            return EMPTY_STRING;
        }
        comment = all.substring(index + 9);
        comment = comment.substring(0, comment.length() - 1);
        comment = new String(comment.getBytes(StandardCharsets.UTF_8));
        return comment;
    }

    /**
     * Sets the comments.
     *
     * @param connection   the connection
     * @param tableSchemas the table schemas
     * @throws SQLException the SQL exception
     */
    private static void setComments(Connection connection, List<TableSchema> tableSchemas) throws SQLException {
        final Statement statement = connection.createStatement();
        for (final TableSchema tableSchema : tableSchemas) {
            if (!Utils.hasText(tableSchema.getComment())) {
                String comment = getComment(statement, tableSchema);
                tableSchema.setComment(comment);
            }
        }
    }

    private static String getComment(Statement statement, TableSchema tableSchema) {
        try {
            String comment = "";
            final ResultSet rs = statement.executeQuery("SHOW CREATE TABLE " + tableSchema.getTableName());
            if (rs != null && rs.next()) {
                final String create = rs.getString(2);
                comment = parse(create);
                rs.close();
            }
            return comment;
        } catch (Exception ex) {
            return "";
        }

    }


    public DatabaseSchema loadSchema() {
        return load(driverClassName, jdbcUrl, userName, password, tablePrefix);
    }
}
