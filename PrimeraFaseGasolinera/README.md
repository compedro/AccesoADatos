# Sistema de Gestión de Gasolinera y Tienda (Fase 1)

**Módulo:** Acceso a Datos · **Curso:** 2.º DAM  
**Caso Práctico:** Ampliación con persistencia relacional en MySQL y modelo de dominio Java.

---

## 1. Descripción del Proyecto

Este proyecto amplía una aplicación previa de gestión de gasolineras (que soportaba almacenamiento en ficheros CSV) para:
1. **Convivencia obligatoria de persistencias:** Incorporar soporte para formato **JSON** (mediante Jackson Databind y JavaTimeModule), manteniendo compatibilidad total hacia atrás con **CSV** a través de la interfaz abstracta `ItfGestorArchivo`.
2. **Diseño de Base de Datos en MySQL (InnoDB):**
   * Todas las tablas utilizan una clave primaria técnica autoincremental entera única (`id_* INT PRIMARY KEY AUTO_INCREMENT`).
   * Separación estricta entre identidad técnica y referencias de negocio con restricciones de unicidad (`UNIQUE NOT NULL`).
   * Trigger obligatorio en base de datos para auditoría de cambios de precio en productos.
3. **Modelo de Dominio en Java (> 20 clases/enums):**
   * Modelado completo de entidades del negocio en el paquete `modelo`.
   * Jerarquía polimórfica para productos envasados (unidades enteras) y productos a granel (decimales pesados).
   * Asignación temporal de funciones al personal con atributos específicos (sin herencia rígida de empleados).
   * **Sin llamadas JDBC activas en las entidades de dominio** en esta fase.
4. **Demostración Java (`DemoModalidades.java`):**
   * Validación en memoria y cálculo de importes polimórfico según la modalidad del producto.

---

## 2. Requisitos del Sistema

* **Java Development Kit (JDK):** Versión 17 o superior.
* **Gestor de Construcción:** Gradle (incluido mediante wrapper `gradlew`).
* **Sistema Gestor de Bases de Datos:** MySQL Server 8.0 o superior (con soporte InnoDB).

---

## 3. Estructura del Repositorio

```text
├── sql/                                    # Scripts SQL ordenados (en PersistenciaGasolinera/sql/)
│   ├── 01_esquema.sql                      # DDL: creación de tablas, PKs, FKs y restricciones CHECK
│   ├── 02_triggers.sql                     # Trigger obligatorio de auditoría de precios
│   ├── 03_datos.sql                        # Carga de datos de ejemplo y casos límite
│   └── 04_comprobaciones.sql               # Pruebas, verificación de trigger y transaccionalidad
├── PersistenciaGasolinera/
│   ├── Gasolinera/                         # Proyecto Java con Gradle
│   │   ├── build.gradle                    # Dependencias (Jackson, MySQL Connector, SQLite)
│   │   └── src/
│   │       ├── modelo/                     # Paquete del modelo de dominio OOP (>20 clases/enums)
│   │       │   ├── Estacion.java
│   │       │   ├── FichaConfiguracion.java
│   │       │   ├── Surtidor.java
│   │       │   ├── Empleado.java
│   │       │   ├── Funcion.java
│   │       │   ├── AsignacionFuncion.java
│   │       │   ├── Producto.java           # Clase base abstracta
│   │       │   ├── ProductoEnvasado.java   # Subclase: unidades enteras
│   │       │   ├── ProductoGranel.java     # Subclase: decimales pesados
│   │       │   ├── HistoricoPrecio.java
│   │       │   ├── Cliente.java
│   │       │   ├── TarjetaFidelizacion.java
│   │       │   ├── Proveedor.java
│   │       │   ├── AcuerdoSuministro.java
│   │       │   ├── Campana.java
│   │       │   ├── InscripcionCampana.java
│   │       │   ├── Venta.java
│   │       │   ├── LineaVenta.java
│   │       │   ├── Pago.java
│   │       │   ├── Devolucion.java
│   │       │   ├── Incidencia.java
│   │       │   └── ... (Enums de estado y modalidades)
│   │       ├── DemoModalidades.java        # Demostración ejecutable de modalidades y validaciones
│   │       ├── GestorArchivoCSV.java       # Persistencia en CSV
│   │       ├── GestorArchivoJSON.java      # Persistencia en JSON (Jackson)
│   │       ├── ItfGestorArchivo.java       # Interfaz de desacoplamiento de persistencia
│   │       ├── GestorVentas.java           # Lógica de ventas desacoplada
│   │       ├── DatabaseManager.java        # Configuración de conexiones JDBC
│   │       └── Main.java                   # Punto de entrada de la aplicación
├── DICCIONARIO_DATOS.md                    # Diccionario de datos y modelo relacional tabular (sin E-R)
├── JUSTIFICACION_DISENO_Y_REGLAS.md        # Justificación de diseño, reglas garantizadas vs pendientes y 3 operaciones JDBC
├── AUDITORIA_IA.md                         # Informe de auditoría con análisis crítico del uso de IA
├── AGENTS.md                               # Guía y contexto para agentes de IA
└── README.md                               # Este documento
```

---

## 4. Preparación y Ejecución de la Base de Datos MySQL

Los scripts SQL deben ejecutarse **estrictamente en el siguiente orden secuencial** sobre un servidor MySQL en blanco o existente:

1. **`PersistenciaGasolinera/sql/01_esquema.sql`:** Crea la base de datos `gasolinera`, todas las tablas bajo motor `InnoDB`, sus claves primarias técnicas enteras autoincrementales, restricciones de unicidad de negocio (`UNIQUE`) y restricciones de integridad (`CHECK`).
2. **`PersistenciaGasolinera/sql/02_triggers.sql`:** Crea el trigger `trg_auditoria_precio_producto` que registra automáticamente las variaciones reales de `precio_venta` en `historico_precios`.
3. **`PersistenciaGasolinera/sql/03_datos.sql`:** Carga los datos maestros de prueba (al menos 5 registros por tabla principal, más de 10 líneas de venta y cobertura de todos los casos límite del relato).
4. **`PersistenciaGasolinera/sql/04_comprobaciones.sql`:** Ejecuta las consultas de verificación, pruebas del trigger ante altas, modificaciones de descripción, cambios de precio, mantenimiento del precio cobrado en ventas pasadas, prueba de atomicidad con `ROLLBACK`, y casos de error esperados (comentados).

### Ejecución desde terminal (PowerShell / Bash):
```bash
mysql -u [TU_USUARIO] -p < PersistenciaGasolinera/sql/01_esquema.sql
mysql -u [TU_USUARIO] -p < PersistenciaGasolinera/sql/02_triggers.sql
mysql -u [TU_USUARIO] -p < PersistenciaGasolinera/sql/03_datos.sql
mysql -u [TU_USUARIO] -p < PersistenciaGasolinera/sql/04_comprobaciones.sql
```

---

## 5. Configuración de la Conexión JDBC (Sin Credenciales Personales)

El archivo `DatabaseManager.java` permite configurar la conexión a MySQL de manera segura sin exponer credenciales en el código fuente mediante variables de entorno o propiedades del sistema:

### Variables de entorno admitidas:
* `GASOLINERA_DB`: Motor a utilizar (`mysql` o `sqlite`).
* `MYSQL_HOST`: Servidor de base de datos (por defecto: `localhost`).
* `MYSQL_PORT`: Puerto de conexión (por defecto: `3306`).
* `MYSQL_DATABASE`: Nombre de la base de datos (por defecto: `gasolinera`).
* `MYSQL_USER`: Usuario con permisos sobre la base de datos.
* `MYSQL_PASSWORD`: Contraseña del usuario.
* `MYSQL_URL`: Alternativamente, URL JDBC completa (ej. `jdbc:mysql://localhost:3306/gasolinera`).

---

## 6. Compilación y Ejecución del Proyecto Java

Desde el directorio `PersistenciaGasolinera/Gasolinera/`:

### 6.1. Compilación del proyecto:
```powershell
cd PersistenciaGasolinera\Gasolinera
.\gradlew build
```

### 6.2. Ejecución de la Demostración de Modalidades (`DemoModalidades`):
Muestra el tratamiento polimórfico de productos envasados y a granel, el cálculo de importes con `BigDecimal`, pagos mixtos, rechazo de cantidades decimales en productos envasados y devoluciones parciales sucesivas en memoria:
```powershell
cd PersistenciaGasolinera\Gasolinera
.\gradlew runDemo
```
O directamente mediante compilación directa:
```powershell
java -cp "build/classes/java/main" DemoModalidades
```

### 6.3. Ejecución de la Aplicación Principal:
```powershell
cd PersistenciaGasolinera\Gasolinera
.\gradlew run
```

---

## 7. Documentación Complementaria Entregada

* **[Diccionario de Datos y Modelo Relacional](DICCIONARIO_DATOS.md):** Tablas detalladas con tipos, claves, restricciones e integridades referenciales (sin diagramas E-R gráficos).
* **[Justificación de Diseño y Reglas de Negocio](JUSTIFICACION_DISENO_Y_REGLAS.md):** Análisis de decisiones arquitectónicas, correspondencia Java-MySQL, matriz de reglas garantizadas vs. pendientes y especificación de 3 operaciones JDBC transaccionales futuras.
* **[Informe de Auditoría de IA](AUDITORIA_IA.md):** Evaluación crítica de tres intervenciones de IA en diseño, compatibilidad de persistencia y triggers SQL.
* **[Guía de Continuación para Agentes](AGENTS.md):** Estado de avance y hoja de ruta del proyecto.
