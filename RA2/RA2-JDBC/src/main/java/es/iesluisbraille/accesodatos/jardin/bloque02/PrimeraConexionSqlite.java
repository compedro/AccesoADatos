package es.iesluisbraille.accesodatos.jardin.bloque02;

import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

/** Primera conexión: todo el proceso cabe en un método main. */
public final class PrimeraConexionSqlite {
    private PrimeraConexionSqlite() {
    }

    public static void main(String[] args) {

        /*  Hardcodeado (NO recomendado)
        Path archivo = args.length == 0 ? Path.of("data", "jardin-botanico.db") : Path.of(args[0]);
        */

        // Utilizando variables de entorno es la forma mas segura
        String dbPath = System.getenv().getOrDefault("SQLITE_DB_PATH", "data/jardin-botanico.db");
        Path archivo = Path.of(dbPath);

        if (!Files.isRegularFile(archivo)) {
            System.err.println("No existe la base SQLite: " + archivo.toAbsolutePath());
            System.err.println("Prepárala con: python tools/preparar_sqlite.py");
            return;
        }

        String url = "jdbc:sqlite:" + archivo.toAbsolutePath();
        try (Connection conexion = DriverManager.getConnection(url)) {
            try (Statement configuracion = conexion.createStatement()) {
                configuracion.execute("PRAGMA foreign_keys = ON"); // SQLite exige activar las claves foráneas en cada conexión nueva.
            }
            System.out.println("Conexión SQLite abierta: " + archivo.toAbsolutePath());
            System.out.println("Conexión válida: " + conexion.isValid(2));
            System.out.println("Auto-commit: " + conexion.getAutoCommit());
        } catch (SQLException e) {
            System.err.println("Error al conectar con SQLite: " + e.getMessage());
//            return;
        }
        System.out.println("La conexión se ha cerrado al salir de try-with-resources.");
    }
}
