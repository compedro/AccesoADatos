# AGENTS.md - Guía de Arquitectura, Contexto y Continuación para Agentes de IA

**Proyecto:** Sistema de Gestión de Gasolinera y Tienda con MySQL - Fase 1  
**Módulo:** Acceso a Datos (2.º DAM)  
**Documento base de especificaciones:** `Caso_practico_Gasolinera_MySQL_Fase_1.md`  
**Ubicación del código fuente:** `PersistenciaGasolinera/Gasolinera/`  
**Scripts SQL:** `PersistenciaGasolinera/sql/`  

---

## 1. Visión General del Proyecto y Alcance

Este repositorio contiene la ampliación de una aplicación de gestión de gasolineras (que originalmente almacenaba `Cliente` y `Repostaje` en ficheros CSV) hacia un sistema integral que abarca la actividad diaria de las estaciones y su tienda de conveniencia.

### Alcance de la Fase 1 (100% Completada):
1. **Convivencia Dual de Persistencia de Ficheros (CSV + JSON):** Implementada mediante Jackson Databind + JavaTimeModule, garantizando total compatibilidad hacia atrás mediante la abstracción `ItfGestorArchivo`.
2. **Diseño Relacional en MySQL (InnoDB):** Esquema DDL formal de 20 tablas, todas con **clave primaria técnica autoincremental entera única (`id_* INT PRIMARY KEY AUTO_INCREMENT`)**, desacoplada de los códigos y claves de negocio (`cif`, `dni`, códigos internos, referencias) que se aseguran mediante `UNIQUE NOT NULL`.
3. **Trigger de Auditoría de Precios en MySQL:** Registro automático en `historico_precios` ante cambios reales de `precio_venta` (`OLD.precio_venta <> NEW.precio_venta`), con garantía de atomicidad transaccional bajo InnoDB (un `ROLLBACK` revierte producto e histórico).
4. **Modelo de Dominio Orientado a Objetos en Java (>20 clases/enums):** Paquete `modelo` con modelado polimórfico de productos (`ProductoEnvasado` con unidades enteras vs. `ProductoGranel` con decimales pesados), personal con asignación flexible de funciones temporales (`AsignacionFuncion`) y gestión completa de tickets, pagos fraccionados y devoluciones.
5. **Cero persistencia JDBC activa en el dominio para Fase 1:** Las clases del modelo no ejecutan SQL ni abren conexiones (requisito estricto docente). La persistencia JDBC de la tienda se implementará en la Fase 2.
6. **Demostración en Memoria (`DemoModalidades.java`):** Prueba interactiva sin tocar base de datos que verifica la preparación de tickets, cálculos con `BigDecimal` y redondeo `HALF_UP`, pagos mixtos, rechazo de casos inválidos y devoluciones parciales sucesivas.
7. **Documentación Académica Completa:** Tabular y textual sin diagramas E-R gráficos (prohibidos por el profesor).

---

## 2. Mapa Integral de Archivos y Entregables

```text
├── Caso_practico_Gasolinera_MySQL_Fase_1.md # Especificaciones y enunciado original del docente
├── DICCIONARIO_DATOS.md                     # Diccionario de datos y modelo relacional tabular (sin E-R)
├── JUSTIFICACION_DISENO_Y_REGLAS.md         # Decisiones de diseño, matriz garantizadas vs pendientes y 3 transacciones JDBC
├── AUDITORIA_IA.md                          # Registro crítico de intervenciones de IA (diseño, persistencia, triggers)
├── README.md                                # Guía general de instalación, configuración y ejecución
├── AGENTS.md                                # Este documento (guía de contexto para cualquier agente/máquina)
├── PersistenciaGasolinera/
│   ├── sql/                                 # Scripts SQL ordenados y verificados
│   │   ├── 01_esquema.sql                   # DDL: creación de base de datos, 20 tablas InnoDB, PKs, FKs y CHECKs
│   │   ├── 02_triggers.sql                  # Trigger trg_auditoria_precio_producto
│   │   ├── 03_datos.sql                     # Datos maestros (mínimo 5 por tabla, >10 líneas venta, casos límite)
│   │   └── 04_comprobaciones.sql            # Verificaciones, prueba de trigger con ROLLBACK y errores comentados
│   └── Gasolinera/                          # Proyecto Java con Gradle
│       ├── build.gradle                     # Configuración Gradle, dependencias y tarea 'runDemo'
│       ├── gradlew / gradlew.bat            # Gradle Wrapper
│       ├── clientes.csv / repostajes.csv    # Datos originales de persistencia CSV
│       ├── clientes.json / repostajes.json  # Datos generados por la persistencia JSON
│       └── src/
│           ├── GestorArchivoCSV.java        # Implementación CSV
│           ├── GestorArchivoJSON.java       # Implementación JSON (Jackson ObjectMapper + JavaTimeModule)
│           ├── ItfGestorArchivo.java        # Interfaz común de persistencia en archivo
│           ├── GestorVentas.java            # Lógica de ventas desacoplada a través de la interfaz
│           ├── DatabaseManager.java         # Gestor de conexiones JDBC configurables (SQLite / MySQL)
│           ├── ClienteDAO.java              # DAO previo de clientes (SQLite/MySQL básico)
│           ├── Cliente.java                 # Entidad heredada para compatibilidad CSV/JSON/DAO previo
│           ├── Repostaje.java               # Entidad heredada para compatibilidad CSV/JSON/DAO previo
│           ├── Main.java                    # Punto de entrada de la aplicación heredada (modo consola)
│           ├── DemoModalidades.java         # Demostración ejecutable del modelo de dominio de la Fase 1
│           └── modelo/                      # Paquete del nuevo Modelo de Dominio OOP (Fase 1)
│               ├── Estacion.java
│               ├── FichaConfiguracion.java
│               ├── Surtidor.java
│               ├── Empleado.java
│               ├── Funcion.java
│               ├── AsignacionFuncion.java
│               ├── Producto.java            # Clase base abstracta polimórfica
│               ├── ProductoEnvasado.java    # Subclase: unidades enteras
│               ├── ProductoGranel.java      # Subclase: cantidades decimales pesadas
│               ├── HistoricoPrecio.java
│               ├── Cliente.java             # Entidad de dominio de tienda y fidelización
│               ├── TarjetaFidelizacion.java
│               ├── Proveedor.java
│               ├── AcuerdoSuministro.java
│               ├── Campana.java
│               ├── InscripcionCampana.java
│               ├── Venta.java
│               ├── LineaVenta.java
│               ├── Pago.java
│               ├── Devolucion.java
│               ├── Incidencia.java
│               ├── EstadoServicio.java       (Enum)
│               ├── TipoFuncion.java         (Enum)
│               ├── ModalidadProducto.java   (Enum)
│               ├── EstadoComercializacion.java (Enum)
│               ├── EstadoTarjeta.java       (Enum)
│               ├── EstadoPago.java          (Enum)
│               ├── MedioPago.java           (Enum)
│               └── EstadoIncidencia.java    (Enum)
```

---

## 3. Decisiones Críticas de Arquitectura y Negocio

### 3.1. Convivencia entre Modelos y Clases de Cliente
* **`src/Cliente.java` (Raíz):** Contiene los campos estrictos de la aplicación previa (`idCliente`, `nombre`, `telefono`, `matricula`) necesarios para mantener inalterados los ficheros CSV/JSON y el `ClienteDAO` existente.
* **`src/modelo/Cliente.java` (Dominio Fase 1):** Modelo ampliado con `codigoCliente`, datos de contacto opcionales, fecha de registro y vínculo 1:1 con `TarjetaFidelizacion`.
* **Regla de importación:** En clases de raíz (como `DemoModalidades.java`), se debe importar explícitamente `import modelo.Cliente;` y cualificar `modelo.Cliente` para evitar que el compilador resuelva por defecto hacia la clase del mismo paquete raíz.

### 3.2. Claves Técnicas Autoincrementales vs. Códigos Comerciales
* **Prohibición:** Ninguna tabla utiliza claves compuestas o códigos de negocio (`cif`, `dni`, `codigo_estacion`, `referencia_ticket`) como clave primaria técnica.
* **Justificación:** Todas las tablas utilizan `id_* INT PRIMARY KEY AUTO_INCREMENT`. Los códigos de negocio son volubles (modificaciones fiscales, correcciones de erratas). La unicidad e integridad se garantiza con `UNIQUE NOT NULL`.

### 3.3. Personal y Funciones Dinámicas (Sin Herencia Rígida)
* Un empleado no es subclase de `Cajero` o `Expendedor`.
* Se utiliza el patrón de asociación temporal `AsignacionFuncion` vinculada al catálogo `Funcion`. Permite:
  * Ejercer múltiples funciones simultáneas (cajero + expendedor en gasolineras pequeñas).
  * Recuperar funciones pasadas con condiciones diferentes tras periodos finalizados.
  * Empleados temporales sin funciones durante incorporaciones.
  * Atributos específicos por función (`limite_devolucion`, `num_acreditacion_combustible`, `presupuesto_compras`, `nivel_autorizacion`).

### 3.4. Modalidad de Producto y Criterio Financiero
* Polimorfismo puro con clase abstracta `Producto`:
  * `ProductoEnvasado`: Rechaza cantidades con decimales (`cantidad.stripTrailingZeros().scale() > 0`).
  * `ProductoGranel`: Valida que la escala decimal no supere la `precisionPesada` admitida en kilogramos.
* Todos los importes monetarios usan `BigDecimal` con redondeo estandarizado `RoundingMode.HALF_UP` a 2 decimales.
* En `lineas_venta`, el precio realmente cobrado (`precio_unitario_aplicado`) queda congelado en la fila. Los cambios futuros en el catálogo no alteran ventas pasadas.

### 3.5. Trigger y Motor InnoDB
* El trigger `trg_auditoria_precio_producto` en `02_triggers.sql` registra cambios en `historico_precios` solo si `OLD.precio_venta <> NEW.precio_venta`.
* El uso de `ENGINE=InnoDB` garantiza que un `ROLLBACK` anule tanto la actualización del producto como la entrada histórica.

---

## 4. Guía de Ejecución en Cualquier Equipo

### 4.1. Requisitos Previos
* JDK 17 o superior instalado y configurado en `PATH` (o accesible vía `JAVA_HOME`).
* Servidor MySQL 8.0 o superior en ejecución (puerto 3306 por defecto).

### 4.2. Preparación de la Base de Datos MySQL
Ejecutar los scripts en el orden exacto:
```bash
# En Windows PowerShell o Linux Bash:
mysql -u [USUARIO] -p < PersistenciaGasolinera/sql/01_esquema.sql
mysql -u [USUARIO] -p < PersistenciaGasolinera/sql/02_triggers.sql
mysql -u [USUARIO] -p < PersistenciaGasolinera/sql/03_datos.sql
mysql -u [USUARIO] -p < PersistenciaGasolinera/sql/04_comprobaciones.sql
```

### 4.3. Configuración de Variables de Entorno (Sin Credenciales en Código)
En PowerShell:
```powershell
$env:GASOLINERA_DB = "mysql"
$env:MYSQL_HOST = "localhost"
$env:MYSQL_PORT = "3306"
$env:MYSQL_DATABASE = "gasolinera"
$env:MYSQL_USER = "tu_usuario"
$env:MYSQL_PASSWORD = "tu_password"
```
En Linux / macOS:
```bash
export GASOLINERA_DB="mysql"
export MYSQL_HOST="localhost"
export MYSQL_PORT="3306"
export MYSQL_DATABASE="gasolinera"
export MYSQL_USER="tu_usuario"
export MYSQL_PASSWORD="tu_password"
```

### 4.4. Compilación y Ejecución de la Demostración Java
Desde el directorio `PersistenciaGasolinera/Gasolinera`:
```powershell
# Compilar todo el proyecto:
.\gradlew build

# Ejecutar la Demostración de Modalidades de Producto (Fase 1):
.\gradlew runDemo

# Ejecutar la aplicación principal existente (modo interactivo por consola):
.\gradlew run
```

---

## 5. Casos de Borde y Peculiaridades Técnicas Ya Resueltas

Si un nuevo agente revisa el código, debe conocer estos ajustes técnicos previamente aplicados:
1. **Constructores de conveniencia:** `Empleado.java` dispone de sobrecarga con 6 argumentos (`idEmpleado`, `codigoEmpleado`, `dni`, `nombre`, `fechaIncorporacion`, `estacion`) y `modelo/Cliente.java` con 4 argumentos (`idCliente`, `codigoCliente`, `nombre`, `fechaRegistro`) para instanciación ágil en tests y demostraciones.
2. **Desacoplamiento de `Main.java`:** Se eliminó la doble inicialización de variables de persistencia en `Main.java`. Actualmente inicializa limpiamente `ItfGestorArchivo gestorArchivos = new GestorArchivoJSON();`. Para alternar a CSV, solo se cambia la instancia a `new GestorArchivoCSV()`.
3. **Serialización Jackson:** `Cliente.java` y `Repostaje.java` poseen constructores por defecto (`public Cliente() {}` y `public Repostaje() {}`), indispensables para evitar fallos de instanciación con Jackson.

---

## 6. Hoja de Ruta para la Fase 2 (Alcance Futuro)

Cualquier agente que retome el proyecto para la **Fase 2** debe centrarse en implementar la capa JDBC para la tienda:
1. **Creación de DAOs para las entidades del dominio:**
   * `EstacionDAO`, `ProductoDAO`, `EmpleadoDAO`, `VentaDAO`, `DevolucionDAO`.
2. **Implementación de las 3 Transacciones JDBC Críticas** (especificadas en detalle en `JUSTIFICACION_DISENO_Y_REGLAS.md`):
   * **Operación 1:** Alta conjunta atómica de Estación + Ficha de Configuración + Primer Surtidor con `connection.setAutoCommit(false)`, recuperación de ID generado mediante `Statement.RETURN_GENERATED_KEYS` y `rollback()` ante fallos.
   * **Operación 2:** Registro y cobro de ticket de tienda con múltiples líneas, cálculo polimórfico de importes, congelación de precios cobrados, cobro fraccionado multitarjeta/efectivo y validación de `SUM(pagos) == total`.
   * **Operación 3:** Devolución parcial concurrente con bloqueo pesimista (`SELECT ... FOR UPDATE`), validación de acumulado (`totalDevuelto <= totalComprado`) y cálculo del reintegro exacto según el precio histórico original.
3. **Manejo de Transacciones y Excepciones:** Control de aislamiento (`SERIALIZABLE` o bloqueos selectivos), gestión de `SQLException` y traducción a excepciones de dominio.
