package com.zetyun.daemon.db;

import com.zetyun.daemon.config.ConfigLoader;
import com.zetyun.daemon.model.Tenant;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.sql.*;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

public class DatabaseManager {
    private HikariDataSource dataSource;
    private ConfigLoader config;

    public DatabaseManager(ConfigLoader config) {
        this.config = config;
        initializeDataSource();
    }

    private void initializeDataSource() {
        HikariConfig hikariConfig = new HikariConfig();
        hikariConfig.setJdbcUrl(config.getProperty("db.url"));
        hikariConfig.setUsername(config.getProperty("db.username"));
        hikariConfig.setPassword(config.getProperty("db.password"));
        dataSource = new HikariDataSource(hikariConfig);
    }

    public void initializeTables() throws SQLException {
        try (Connection conn = dataSource.getConnection();
             Statement stmt = conn.createStatement()) {

            // 执行SQL脚本创建宽表
            executeSqlScript(conn, "create_wide_table.sql");
        }
    }

    private void executeSqlScript(Connection conn, String scriptFileName) throws SQLException {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(
                getClass().getClassLoader().getResourceAsStream(scriptFileName)))) {

            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                sb.append(line);
                if (line.endsWith(";")) {
                    try (Statement stmt = conn.createStatement()) {
                        stmt.execute(sb.toString());
                    }
                    sb.setLength(0);
                }
            }
        } catch (Exception e) {
            throw new SQLException("Error executing SQL script", e);
        }
    }

    public List<Tenant> getTenants() throws SQLException {
        List<Tenant> tenants = new ArrayList<>();
        String sql = "SELECT id, name FROM public.\"" + config.getProperty("table.name") + "\" where domain='training'";
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                String id = rs.getObject("id", UUID.class).toString();
                String name = rs.getObject("name", String.class);
                System.out.println("id: " + id + " name: " + name);
                tenants.add(new Tenant(id, name));
            }
        }
        return tenants;
    }

    public void insertOrupdateStats(String tenantId, Map<String, Object> stats) throws SQLException {
        String sqlInsert = generateInsertSQL(tenantId, stats, config.getProperty("wide.table.name"));
        try (Connection conn = dataSource.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sqlInsert)) {
            pstmt.executeUpdate();
        }
    }

    public static String generateInsertSQL(String tenantId, Map<String, Object> modules, String tableName) {
        StringBuilder sqlBuilder = new StringBuilder();
        sqlBuilder.append("INSERT INTO ").append(tableName).append(" (tenant_id, ");

        // 添加字段名
        String columns = modules.keySet().stream()
                .map(DatabaseManager::sanitizeColumnName)
                .collect(Collectors.joining(", "));
        sqlBuilder.append(columns);
        sqlBuilder.append(", updated_at");


        sqlBuilder.append(") VALUES (").append("'" + tenantId + "'").append(", ");

        // 添加值
        String values = modules.values().stream()
                .map(Object::toString)
                .collect(Collectors.joining(", "));
        sqlBuilder.append(values);
        Timestamp updated_at = Timestamp.from(Instant.now());
        sqlBuilder.append(", '" + updated_at + "'");
        sqlBuilder.append(") ON CONFLICT (tenant_id) DO UPDATE SET ");

        String updateColumns = modules.keySet().stream()
                .map(DatabaseManager::sanitizeColumnName1)
                .collect(Collectors.joining(", "));
        sqlBuilder.append(updateColumns);
        sqlBuilder.append(", updated_at=EXCLUDED.updated_at");
        sqlBuilder.append(";");
        System.out.println(sqlBuilder.toString());
        return sqlBuilder.toString();
    }

    // 防止 SQL 注入，确保列名是有效的
    private static String sanitizeColumnName(String columnName) {
        // 移除所有非字母数字字符，并转换为小写
        return columnName.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
    }

    private static String sanitizeColumnName1(String columnName) {
        // 移除所有非字母数字字符，并转换为小写
        String columnNameLower = columnName.replaceAll("[^a-zA-Z0-9_]", "").toLowerCase();
        return columnNameLower + "=EXCLUDED." + columnNameLower;
    }

    public void updateStats(String id, Map<String, Integer> statsMap1) {
        StringBuilder sqlBuilder = new StringBuilder();


    }
}

