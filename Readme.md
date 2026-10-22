# Sistema de Gestión de Gasolinera

Aplicación de consola desarrollada en Java para la gestión de clientes y el registro de pagos de repostajes en una gasolinera mediante archivos CSV.

---

## ️ Requisitos del Entorno

* **Lenguaje:** Java (JDK 21)
* **Codificación:** UTF-8
* **Bibliotecas I/O:** `java.nio.file.Files` y `java.nio.file.Path`

---

##  Compilación y Ejecución

### Desde un IDE (IntelliJ IDEA)
1. Abrir la carpeta del proyecto en el IDE.
2. Asegurarse de que el SDK del proyecto esté configurado en **Java 21**.
3. Ejecutar la clase principal `Main.java` situada en la carpeta de código fuente.

---

##  Ubicación y Formato de Ficheros

Los datos se guardan de forma automática en la raíz del proyecto mediante dos archivos de texto formateados en CSV con codificación UTF-8:
* `clientes.csv`: Almacena los registros de los clientes.
* `repostajes.csv`: Almacena las operaciones de pago realizadas.

### Reglas del Formato CSV
* **Separador de campos:** Coma `,`.
* **Presencia de cabecera:** La primera línea contiene los nombres de las columnas y es ignorada durante la carga de datos al iniciar el programa.
* **Formato de fechas:** `dd/MM/yyyy` (ej. 11/09/2026).
* **Cantidades e importes:** Números con decimales en formato estándar/inglés (separados por punto `.`).
* **Soporte de mayúsculas/minúsculas:** Las matrículas se almacenan siempre en mayúsculas. Los tipos de combustible corresponden a los valores exactos del tipo enumerado `TipoCombustible` (`DIESEL`, `GASOLINA_95`, `GASOLINA_98`).

### Estructura de las Cabeceras
* **`clientes.csv`:** `id,nombre,telefono,matricula`
* **`repostajes.csv`:** `idRepostaje,idCliente,Fecha,Importe,litros,combustible`

---

##  Decisiones de Arquitectura y Diseño

El proyecto aplica una arquitectura en capas con bajo acoplamiento y alta modularidad:

* **Capa de Presentación / Interfaz (`Main`):** Encargada exclusivamente de la interacción con el usuario (mostrar menús, solicitar datos por teclado y gestionar el formato de los listados en consola).
* **Capa de Lógica de Negocio (`GestorVentas`):** Contiene los mapas en memoria (`Map<Integer, Cliente>` y `Map<Integer, Repostaje>`). Coordina las operaciones de alta de clientes, procesamiento de pagos, ordenación de listados y búsquedas parciales. No interactúa directamente con la consola ni con los archivos del disco.
* **Capa de Persistencia e Interfaz (`ItfGestorArchivos` y `GestorArchivoCSV`):**
  * `ItfGestorArchivos`: Interfaz Java que define el contrato de operaciones de lectura, escritura y carga de datos.
  * `GestorArchivoCSV`: Implementación concreta que utiliza `java.nio.file.Files` para leer y escribir el formato CSV.

### Respuesta ante Cambios de Almacenamiento
Gracias al uso de la interfaz `ItfGestorArchivos`, la aplicación cumple con el principio de inversión de dependencias y bajo acoplamiento:
* Si en el futuro se necesitase cambiar el formato de almacenamiento (por ejemplo, pasar de CSV a archivos JSON, XML o una Base de Datos SQL), el **único cambio necesario** será crear una nueva clase (por ejemplo, `GestorBaseDatos`) que implemente la interfaz `ItfGestorArchivos`.
* Las clases `GestorVentas`, `Cliente`, `Repostaje` y `Main` **permanecerán intactas**, garantizando que los cambios en la infraestructura no afecten a la lógica de negocio ni a la interfaz del usuario.

---

## ✍️ Autoría
* **Autor:** Pedro A. Lima
* **Proyecto:** Práctica 1 - Gestión de Clientes y Repostajes de una Gasolinera
* **Módulo:** Acceso a Datos
