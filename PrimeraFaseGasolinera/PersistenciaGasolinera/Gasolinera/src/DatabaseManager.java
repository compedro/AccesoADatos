import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Locale;

public class DatabaseManager {
    private static final String SQLITE_URL = "jdbc:sqlite:gasolinera.db";

    public static Connection getConnection() throws SQLException {
        return switch (getDatabaseType()) {
            case "sqlite" -> DriverManager.getConnection(SQLITE_URL);
            case "mysql" -> DriverManager.getConnection(getMySqlUrl(), getSetting("gasolinera.mysql.user", "MYSQL_USER", ""),
                    getSetting("gasolinera.mysql.password", "MYSQL_PASSWORD", ""));
            default -> throw new SQLException("Motor de base de datos no admitido. Usa 'sqlite' o 'mysql'.");
        };
    }

    public static String getDatabaseType() {
        return getSetting("gasolinera.db", "GASOLINERA_DB", "sqlite").trim().toLowerCase(Locale.ROOT);
    }

    private static String getMySqlUrl() {
        String configuredUrl = getSetting("gasolinera.mysql.url", "MYSQL_URL", "").trim();
        if (!configuredUrl.isEmpty()) {
            return configuredUrl;
        }

        String host = getSetting("gasolinera.mysql.host", "MYSQL_HOST", "localhost");
        String port = getSetting("gasolinera.mysql.port", "MYSQL_PORT", "3306");
        String database = getSetting("gasolinera.mysql.database", "MYSQL_DATABASE", "gasolinera");
        return "jdbc:mysql://" + host + ":" + port + "/" + database;
    }

    private static String getSetting(String property, String environmentVariable, String defaultValue) {
        String value = System.getProperty(property);
        if (value == null || value.isBlank()) {
            value = System.getenv(environmentVariable);
        }
        return value == null || value.isBlank() ? defaultValue : value;
    }

    public static void inicializarBaseDeDatos() {
        String sqlClientes;
        String sqlRepostajes;
        if ("mysql".equals(getDatabaseType())) {
            sqlClientes = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id_cliente INT PRIMARY KEY AUTO_INCREMENT,
                    nombre VARCHAR(255) NOT NULL,
                    telefono VARCHAR(50) NOT NULL,
                    matricula VARCHAR(50) NOT NULL
                ) ENGINE=InnoDB
                """;
            sqlRepostajes = """
                CREATE TABLE IF NOT EXISTS repostajes (
                    id_repostaje INT PRIMARY KEY AUTO_INCREMENT,
                    id_cliente INT NOT NULL,
                    fecha VARCHAR(10) NOT NULL,
                    importe DOUBLE NOT NULL,
                    litros DOUBLE NOT NULL,
                    combustible VARCHAR(50) NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
                ) ENGINE=InnoDB
                """;
        } else {
            sqlClientes = """
                CREATE TABLE IF NOT EXISTS clientes (
                    id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    telefono TEXT NOT NULL,
                    matricula TEXT NOT NULL
                )
                """;
            sqlRepostajes = """
                CREATE TABLE IF NOT EXISTS repostajes (
                    id_repostaje INTEGER PRIMARY KEY AUTOINCREMENT,
                    id_cliente INTEGER NOT NULL,
                    fecha TEXT NOT NULL,
                    importe REAL NOT NULL,
                    litros REAL NOT NULL,
                    combustible TEXT NOT NULL,
                    FOREIGN KEY (id_cliente) REFERENCES clientes(id_cliente)
                )
                """;
        }

        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sqlClientes);
            stmt.execute(sqlRepostajes);
            System.out.println("Base de datos " + getDatabaseType() + " inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}