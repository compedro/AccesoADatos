# Registro de Auditoría de Uso de IA

**Proyecto:** Caso Práctico Gasolinera con MySQL - Fase 1  
**Módulo:** Acceso a Datos (2.º DAM)  
**Herramientas analizadas:** Asistentes de IA (OpenCode / Antigravity / GitHub Copilot)

---

## 1. Introducción
Durante el desarrollo de la Fase 1, se utilizó asistencia de Inteligencia Artificial para el análisis de requisitos, modelado relacional y generación de código Java y SQL. A continuación, se documentan de forma crítica tres intervenciones relevantes: una relativa a una decisión estructural de diseño, otra sobre compatibilidad de persistencia y otra sobre una comprobación de comportamiento en base de datos.

---

## 2. Intervenciones Relevantes Analizadas

### Intervención 1: Decisión de Diseño del Personal y sus Funciones
* **Qué se pidió:**  
  Modelar en Java y en MySQL la estructura del personal descrita en el caso práctico: cajeros (con límite de devoluciones), expendedores (con acreditación de combustible), responsables de tienda (con presupuesto de compras) y jefes de estación (con nivel de autorización), teniendo en cuenta que una persona puede realizar varias funciones a la vez o cambiar de función en el tiempo.
* **Qué propuso la IA inicialmente:**  
  La IA propuso una jerarquía clásica de herencia en Java con una clase abstracta `Empleado` y subclases fijas `Cajero`, `Expendedor`, `ResponsableTienda` y `JefeEstacion`. En la base de datos proponía una tabla por clase (`TABLE empleados_cajeros`, etc.) o una columna `tipo_empleado ENUM(...)`.
* **Análisis crítico (Aciertos y Errores):**  
  * *Lo que hizo mal:* El modelo de herencia rígido violaba las reglas de negocio del caso práctico: en una gasolinera real, un empleado puede ser cajero y expendedor a la vez en estaciones pequeñas, o cambiar de función manteniendo su identidad y antigüedad sin tener que destruir y recrear el objeto `Empleado`.
  * *Lo que se corrigió / modificó:* Se rechazó la herencia en `Empleado` y se forzó la creación de una entidad intermedia `AsignacionFuncion` vinculada a un catálogo maestro `Funcion`. De este modo, un `Empleado` tiene una lista de asignaciones vigentes e históricas, conteniendo cada asignación sus atributos específicos (límite, carné, presupuesto, nivel).
* **Cómo se verificó:**  
  Se verificó en `03_datos.sql` insertando al empleado `EMP-002` con dos funciones simultáneas activas (`CAJERO` y `EXPENDEDOR`) y al empleado `EMP-003` recuperando la función de `CAJERO` con un límite de devolución diferente tras un periodo previo finalizado. En Java se verificó mediante el método `empleado.obtenerFuncionesActivas()`.

---

### Intervención 2: Convivencia y Desacoplamiento de Persistencias (CSV y JSON)
* **Qué se pidió:**  
  Incorporar persistencia en JSON usando la librería Jackson manteniendo plena compatibilidad hacia atrás con el código existente que usaba CSV.
* **Qué propuso la IA inicialmente:**  
  Propuso implementar `GestorArchivoJSON` y sustituir directamente en `GestorVentas` y `Main` la clase `GestorArchivoCSV` por `GestorArchivoJSON`, dejando el nuevo gestor prefijado en el código fuente.
* **Análisis crítico (Aciertos y Errores):**  
  * *Lo que hizo mal:*  
    1. **Falso desacoplamiento:** Sustituyó un acoplamiento concreto (`GestorArchivoCSV`) por otro (`GestorArchivoJSON`), en lugar de utilizar la abstracción `ItfGestorArchivo` ya disponible.
    2. **Fallo de deserialización Jackson:** No contempló que las entidades de dominio (`Cliente` y `Repostaje`) carecían de constructores por defecto sin argumentos, provocando excepciones en tiempo de ejecución (`InvalidDefinitionException: Cannot construct instance of Cliente`).
    3. **Imposibilidad de alternancia en tiempo de ejecución:** Al dejar `gestorArchivos = new GestorArchivoJSON();` estático en `Main.java`, la aplicación quedó atrapada en el formato JSON. Al ser soportes físicos independientes (`.csv` vs. `.json`), las operaciones en un formato no se reflejan automáticamente en el otro. Si el usuario ejecutaba el programa, no podía auditar ni comparar las lecturas del CSV heredado sin modificar manualmente el código fuente y recompilar.
  * *Lo que se corrigió / modificó:*  
    1. Se incorporaron constructores sin argumentos en `Cliente.java` y `Repostaje.java` y se registró `JavaTimeModule` en `ObjectMapper` para serializar fechas `LocalDate` legibles.
    2. Se refactorizó `GestorVentas` para depender exclusivamente de `ItfGestorArchivo` (Principio de Inversión de Dependencias de SOLID).
    3. Se implementó un **selector interactivo al inicio de `Main.java`** que permite al usuario escoger en tiempo de ejecución entre `1. Ficheros CSV` o `2. Ficheros JSON`.
    4. Se sincronizó el catálogo inicial de prueba en ambos formatos (`clientes.json` / `repostajes.json` reflejando los registros de los CSV) para que el comportamiento observable en la aplicación sea idéntico al alternar de motor.
* **Cómo se verificó:**  
  Se ejecutó `Main.java` en dos sesiones sucesivas: primero seleccionando la opción 1 (CSV) y comprobando la carga de clientes y repostajes desde `clientes.csv` y `repostajes.csv`; y después seleccionando la opción 2 (JSON) comprobando la misma carga desde `clientes.json` y `repostajes.json`. En ambas modalidades se realizaron altas y pagos de prueba, verificando que cada gestor persiste correctamente en su propio formato (CSV delimitado por comas vs. JSON estructurado *pretty-print*) manteniendo intacta la lógica de la aplicación.

---

### Intervención 3: Comprobación de Comportamiento del Trigger y Transaccionalidad
* **Qué se pidió:**  
  Generar el trigger de MySQL para auditar los cambios reales de precio en los productos del catálogo y diseñar las pruebas de comprobación en `04_comprobaciones.sql`, incluyendo la reversión por `ROLLBACK`.
* **Qué propuso la IA:**  
  Un trigger `AFTER UPDATE ON productos` que compara `OLD.precio_venta <> NEW.precio_venta`. En las pruebas propuso actualizar la descripción, actualizar al mismo precio y actualizar a un precio distinto.
* **Análisis crítico (Aciertos y Errores):**  
  * *Lo que hizo bien:* El condicional `IF OLD.precio_venta <> NEW.precio_venta THEN` descartó correctamente tanto las altas iniciales (`INSERT`) como las actualizaciones inocuas donde el precio no varía o solo cambia la descripción.
  * *Lo que hizo mal:* En la prueba del `ROLLBACK`, la IA asumió que un trigger MySQL siempre se deshace sin verificar el motor de almacenamiento, omitiendo mencionar explícitamente que las tablas deben ser obligatoriamente `ENGINE=InnoDB`. Si las tablas hubieran sido `MyISAM`, el `ROLLBACK` habría fallado silenciosamente dejando datos huérfanos en el histórico. Además, no comprobó si una venta anterior modificaba o no su precio tras el disparo del trigger.
  * *Lo que se corrigió / modificó:* Se añadió en `01_esquema.sql` la especificación explícita de `ENGINE=InnoDB` en todas las tablas, se añadió en `04_comprobaciones.sql` la consulta que demuestra que las líneas de tickets emitidos con anterioridad conservan intacto su `precio_unitario_aplicado`, y se implementó el bloque de transacción con `START TRANSACTION -> UPDATE -> ROLLBACK -> SELECT` para evidenciar que tanto el precio del producto como el registro del histórico vuelven exactamente a su estado previo.
* **Cómo se verificó:**  
  Se ejecutó el script de comprobaciones en MySQL, observando que antes del cambio el histórico estaba vacío, tras el cambio real contenía una fila, y tras el bloque con `ROLLBACK` la fila temporal fue eliminada de forma atómica.

---

## 3. Conclusión
La IA demostró ser muy eficiente acelerando la escritura de estructuras repetitivas (DDL, métodos getter/setter, consultas SELECT), pero propensa a aplicar patrones estereotipados (herencia estricta en lugar de composición, acoplamiento directo en lugar de inversión de dependencias) que violaban restricciones sutiles del negocio. La intervención humana crítica fue indispensable para exigir el cumplimiento estricto del caso práctico.
