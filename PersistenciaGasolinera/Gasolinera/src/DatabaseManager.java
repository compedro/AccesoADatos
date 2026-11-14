import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class DatabaseManager {
    private static final String URL = "jdbc:sqlite:gasolinera.db";

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL);
    }

    public static void inicializarBaseDeDatos() {
        String sqlClientes = """
            CREATE TABLE IF NOT EXISTS clientes (
                id_cliente INTEGER PRIMARY KEY AUTOINCREMENT,
                nombre TEXT NOT NULL,
                telefono TEXT NOT NULL,
                matricula TEXT NOT NULL
            )
            """;

        String sqlRepostajes = """
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

        try (Connection conn = getConnection();             //crea la conexión "conn" de la BBDD
             Statement stmt = conn.createStatement()) {     //crea el objeto stmt para ejecutar instrucciones SQL en la BBDD
            stmt.execute(sqlClientes);
            stmt.execute(sqlRepostajes);
            System.out.println("Base de datos inicializada correctamente.");
        } catch (SQLException e) {
            System.err.println("Error al inicializar la base de datos: " + e.getMessage());
        }
    }
}