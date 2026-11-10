package es.iesluisbraille.accesodatos.jardin.bloque02;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.SQLException;

/** Conexión a un servidor MySQL de prácticas, sin credenciales en el código. */
public final class PrimeraConexionMysql {
    private PrimeraConexionMysql() {
    }

    public static void main(String[] args) {
/* Utilizando variables de entorno es la forma mas segura
* Las credenciales no quedan en el historial de comandos
* Configuración global: Se configuran una vez en el sistema
* Ejecución: java PrimeraConexionMysql */
//        String url = System.getenv("JARDIN_MYSQL_URL");
//        String usuario = System.getenv("JARDIN_MYSQL_USER");
//        String clave = System.getenv("JARDIN_MYSQL_PASSWORD");
/*Argumentos de línea de comandos (CLI)
* Más flexible: Puedes cambiar credenciales en cada ejecución
* Menos segura: Las credenciales quedan en el historial de comandos
* Ejecución: java PrimeraConexionMysql "jdbc:mysql://localhost:3306/ra2_botanico" "botanico" "1234"*/

//        if (args != null && args.length == 3) {
//            url = args[0];
//            usuario = args[1];
//            clave = args[2];
//        }
/* Hardcodear (NO recomendado)
*url = "jdbc:mysql://localhost:3306/ra2_botanico";
* usuario = "botanico";
* clave = "1234"; */
//        String url= "jdbc:mysql://localhost:3306/ra2_botanico";
//        String usuario = "botanico";
//        String clave = "1234";

        String url = "jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/sys?useSSL=true&requireSSL=true";
        String usuario = "49VwnBjuqdwVwpc.root";
//        String clave = "y8Jj3rF5s4f0gafm";
        String clave = "6mGG0kAHescXIXF4";

        if (url == null || url.isBlank() || usuario == null || usuario.isBlank()) {
            System.err.println("Configura JARDIN_MYSQL_URL y JARDIN_MYSQL_USER.");
            return;
        }
        if (clave == null) {
            clave = "";
        }

        try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
            DatabaseMetaData datos = conexion.getMetaData();
            System.out.println("Gestor: " + datos.getDatabaseProductName());
            System.out.println("Versión del gestor: " + datos.getDatabaseProductVersion());
            System.out.println("Driver: " + datos.getDriverName());
            System.out.println("Conexión válida: " + conexion.isValid(2));
        } catch (SQLException e) {
            System.err.println("Error al conectar con MySQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }
}
