package es.iesluisbraille.accesodatos.jardin.bloque02;


import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/** Ejecuta el esquema SQL en TiDB Cloud */
public final class SubirArchivosTiDB  {
    private SubirArchivosTiDB () {
    }

    public static void main(String[] args) {
//        String url = "jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/sys?useSSL=true&requireSSL=true";
        String url = "jdbc:mysql://gateway01.eu-central-1.prod.aws.tidbcloud.com:4000/jardin_botanico?useSSL=true&requireSSL=true";
        String usuario = "49VwnBjuqdwVwpc.root";
        String clave = "y8Jj3rF5s4f0gafm";

//        Path archivoSql = Path.of("sql/mysql/01-esquema.sql");
        Path archivoSql = Path.of("sql/mysql/02-datos.sql");

        if (!Files.isRegularFile(archivoSql)) {
            System.err.println("No existe el archivo SQL: " + archivoSql.toAbsolutePath());
            return;
        }

        try {
            String contenido = Files.readString(archivoSql);
            List<String> sentencias = parsearSentencias(contenido);

            try (Connection conexion = DriverManager.getConnection(url, usuario, clave)) {
                conexion.setAutoCommit(false);

                try (Statement stmt = conexion.createStatement()) {
                    for (String sentencia : sentencias) {
                        System.out.println("Ejecutando: " + sentencia.substring(0, Math.min(50, sentencia.length())) + "...");
                        stmt.execute(sentencia);
                    }
                    conexion.commit();
                    System.out.println("Esquema ejecutado correctamente en TiDB Cloud");
                } catch (SQLException e) {
                    conexion.rollback();
                    System.err.println("Error ejecutando sentencia. Rollback realizado.");
                    throw e;
                }
            }
        } catch (IOException e) {
            System.err.println("Error leyendo archivo SQL: " + e.getMessage());
        } catch (SQLException e) {
            System.err.println("Error SQL: " + e.getMessage());
            System.err.println("SQLState: " + e.getSQLState());
        }
    }

    private static List<String> parsearSentencias(String contenido) {
        List<String> sentencias = new ArrayList<>();
        StringBuilder actual = new StringBuilder();
        boolean enDelimitador = false;
        boolean ignorarBloque = false; // Ignorar triggers en TiDB

        for (String linea : contenido.split("\n")) {
            String lineaTrim = linea.trim();

            if (lineaTrim.startsWith("DELIMITER")) {
                if (lineaTrim.contains("$$")) {
                    enDelimitador = true;
                    ignorarBloque = true; // Ignorar bloques DELIMITER $$
                } else if (lineaTrim.contains(";")) {
                    enDelimitador = false;
                    ignorarBloque = false;
                }
                continue;
            }

            if (lineaTrim.isEmpty() || lineaTrim.startsWith("--")) {
                continue;
            }

            // Si estamos en un bloque a ignorar (triggers), saltar
            if (ignorarBloque) {
                continue;
            }

            actual.append(linea).append("\n");

            if (!enDelimitador && lineaTrim.endsWith(";")) {
                sentencias.add(actual.toString().trim());
                actual = new StringBuilder();
            }
        }

        return sentencias;
    }
}