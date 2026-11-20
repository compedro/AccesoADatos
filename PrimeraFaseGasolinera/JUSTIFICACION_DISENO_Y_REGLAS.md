# Justificación de Diseño y Reglas de Negocio - Fase 1

**Proyecto:** Sistema de Gestión de Gasolinera y Tienda  
**Módulo:** Acceso a Datos (2.º DAM)  
**Persistencias:** MySQL (InnoDB) · Convivencia con CSV y JSON

---

## 1. Justificación de las Decisiones del Modelo Java y Correspondencia con MySQL

### 1.1. Claves Primarias Técnicas vs. Referencias de Negocio
* **Decisión Relacional:** Todas las tablas de MySQL (incluidas tablas intermedias N:M, líneas de venta, histórico y asignaciones) utilizan como clave primaria un único entero autoincremental (`id_* INT PRIMARY KEY AUTO_INCREMENT`).
* **Justificación:** Los códigos de negocio (`cif`, `dni`, `codigo_estacion`, `referencia_ticket`) son propensos a mutaciones por cambios normativos, fusiones, reestructuraciones corporativas o corrección de erratas de mecanografiado. Utilizar identificadores técnicos numéricos (`surrogate keys`) desacopla las relaciones estructurales de las claves de negocio, evitando actualizaciones en cascada masivas y mejorando el rendimiento de los índices B-Tree en InnoDB. La unicidad e integridad de las referencias de negocio se garantiza de forma independiente mediante restricciones `UNIQUE NOT NULL`.
* **Correspondencia en Java:** En las clases Java, los atributos técnicos son de tipo `Integer id*` (admitiendo valor `null` antes de ser persistidos en base de datos), mientras que la igualdad de negocio (`equals()` y `hashCode()`) se fundamenta en las referencias comerciales únicas.

### 1.2. Jerarquía Polimórfica de Productos: Envasado vs. Granel
* **Decisión en Java:** Se implementa una clase base abstracta `Producto` con dos especializaciones: `ProductoEnvasado` y `ProductoGranel`.
* **Justificación de Comportamiento:** La modalidad de venta es inmutable para cada referencia comercial. El tratamiento de cantidades y cálculos no debe dispersarse en bloques condicionales `if/else` o `instanceof` dentro del flujo de ventas. Se aplica polimorfismo puro:
  * `ProductoEnvasado`: Exige cantidades enteras positivas (`cantidad.stripTrailingZeros().scale() <= 0`). Rechaza cantidades decimales (como 1,5 unidades) mediante `IllegalArgumentException`.
  * `ProductoGranel`: Permite cantidades decimales siempre que no superen la precisión de pesada admitida (`precisionPesada` en kilogramos).
  * El cálculo de importes se centraliza mediante `BigDecimal` y redondeo estandarizado `RoundingMode.HALF_UP` a 2 decimales.
* **Correspondencia en MySQL:** Se modela en una única tabla `productos` con columna discriminadora `modalidad ENUM('ENVASADO', 'GRANEL')` y una restricción `CHECK` de exclusión mutua de atributos (`chk_productos_modalidad_datos`), garantizando que un producto envasado tenga formato de envase y carezca de precisión de pesada, y viceversa.

### 1.3. Representación del Personal y Funciones Cambiantes
* **Decisión en Java y MySQL:** Se descarta crear subclases fijas de `Empleado` (como `class Cajero extends Empleado`) porque en la realidad operativa un empleado puede desempeñar múltiples funciones a la vez (cajero y expendedor), cambiar de responsabilidad, recuperar cargos pasados o estar temporalmente sin funciones durante una incorporación.
* **Solución:** Se implementa el patrón de asignación temporal mediante la entidad `AsignacionFuncion` vinculada a un catálogo maestro `Funcion`. Esta entidad conserva el historial (`fechaInicio`, `fechaFin`) y los atributos específicos según el rol (límite de devolución para cajero, acreditación para expendedor, presupuesto para compras, nivel para jefes).
* **Supervisión Reflexiva:** La supervisión se modela como una autorreferencia `id_supervisor` en `empleados`, con una restricción `CHECK (id_supervisor <> id_empleado)` que impide la autosupervisión inmediata.

### 1.4. Congelación del Precio Histórico en Líneas de Venta
* **Decisión:** Cada registro en `lineas_venta` almacena obligatoriamente `precio_unitario_aplicado` e `importe`.
* **Justificación:** Los precios de venta del catálogo varían con el tiempo. El importe cobrado en una venta pasada es un hecho contable inmutable. No debe recalcularse dinámicamente consultando el precio vigente ni reconstruirse buscando en tablas de histórico, ya que generaría descuadres fiscales y auditorías inconsistentes.

---

## 2. Relación de Reglas de Negocio: Garantizadas vs. Pendientes

A continuación se detalla dónde queda asegurada cada regla exigida por el caso de uso:

| Regla de Negocio del Relato | Estado | Mecanismo de Garantía en Fase 1 | Tratamiento Propuesto para Fase 2 (JDBC) |
| :--- | :---: | :--- | :--- |
| **Unicidad de códigos de negocio** (`codigo_estacion`, `dni`, etc.) | **Garantizada** | Restricciones `UNIQUE` en MySQL. | Captura de `SQLException` (código 1062) en capa DAO y traducción a excepciones de dominio. |
| **Precios mayores que cero y no nulos** | **Garantizada** | `NOT NULL` y `CHECK (precio_venta > 0)`. Validación en constructor de `Producto`. | Validación en DTO previo a sentencia `PreparedStatement`. |
| **Auditoría automática de cambios de precio** | **Garantizada** | Trigger `trg_auditoria_precio_producto` en `02_triggers.sql`. Ignora INSERT y actualizaciones sin variación de precio. | Ninguna acción necesaria en Java; la base de datos es autónoma. |
| **Atomicidad del histórico ante cancelaciones** | **Garantizada** | Motor `InnoDB` y transaccionalidad (`ROLLBACK` revierte producto e histórico simultáneamente). | Control de transacciones con `conn.commit()` y `conn.rollback()`. |
| **Exclusión de atributos según modalidad de producto** | **Garantizada** | `CHECK chk_productos_modalidad_datos` en MySQL. Jerarquía polimórfica en Java. | Instanciación tipada por factoría polimórfica según columna discriminadora. |
| **Validación de enteros en envasados y decimales en granel** | **Garantizada** | Lógica de validación en `ProductoEnvasado` y `ProductoGranel` mediante `BigDecimal`. | Invocación de `producto.validarCantidad(...)` antes de insertar la línea de venta. |
| **Una tarjeta como máximo por cliente (1:1 opcional)** | **Garantizada** | `UNIQUE(id_cliente)` en `tarjetas_fidelizacion`. | Verificación previa en servicio de fidelización. |
| **Surtidores únicos por estación** | **Garantizada** | `UNIQUE(id_estacion, numero_visible)` en `surtidores`. | Verificación en capa de servicio antes del INSERT. |
| **No autosupervisión inmediata** | **Garantizada** | `CHECK (id_supervisor <> id_empleado)` y validación en `Empleado.asignarSupervisor()`. | Verificación previa al seteo del supervisor. |
| **Alta conjunta obligatoria Estación + Ficha** | *Pendiente* | El esquema permite la FK, pero no impide transitoriamente que una estación exista sin ficha antes de confirmar. | **Transacción JDBC:** Bloque con `setAutoCommit(false)`, inserción de estación, obtención de `id_estacion` generado y posterior inserción de ficha. Si falla, `rollback()`. |
| **Al menos un surtidor por estación y no eliminar el último** | *Pendiente* | El esquema relacional estándar no puede verificar cardinalidad mínima (1..N) en el borrado sin ciclos. | **JDBC / Servicio:** Comprobación `SELECT COUNT(*) FROM surtidores WHERE id_estacion = ?` antes de DELETE. Si el conteo es 1, lanzar `ReglaNegocioException`. |
| **Ningún proveedor ni producto aislado** | *Pendiente* | Las tablas base no pueden forzar la existencia simultánea de una fila en la tabla intermedia durante el primer INSERT. | **Transacción JDBC:** Alta obligatoria conjunta de producto y su primer acuerdo de suministro en la misma transacción atómica. |
| **Ausencia de solapes temporales de la misma función** | *Pendiente* | MySQL no soporta restricciones `EXCLUDE USING gist` sobre rangos temporales como PostgreSQL. | **JDBC / Trigger:** Consulta de verificación previa: `SELECT COUNT(*) FROM asignaciones_funciones WHERE id_empleado = ? AND id_funcion = ? AND (fecha_fin IS NULL OR fecha_fin >= ?)` antes de registrar una nueva asignación. |
| **Prevención de ciclos indirectos de supervisión** | *Pendiente* | El CHECK previene $A \rightarrow A$, pero no ciclos $A \rightarrow B \rightarrow C \rightarrow A$. | **JDBC / CTE Recursiva:** Consulta recursiva con `WITH RECURSIVE` en MySQL para verificar que el nuevo supervisor no dependa previamente del supervisado. |
| **No devolver más de lo comprado originalmente** | *Pendiente* | El diseño incluye la tabla `devoluciones` vinculada a `lineas_venta`, pero la suma histórica acumulada entre varias transacciones requiere control. | **JDBC Transaccional con Bloqueo Pesimista:** `SELECT cantidad, (SELECT COALESCE(SUM(cantidad),0) FROM devoluciones WHERE id_linea_venta = ?) FROM lineas_venta WHERE id_linea_venta = ? FOR UPDATE`. |
| **Pago completo de la venta antes de su confirmación** | *Pendiente* | El estado `PAGADA` debe garantizar que `SUM(pagos) == importe_total`. | **Transacción de Cierre en JDBC:** Bloquear ticket, sumar pagos registrados y validar igualdad matemática antes de actualizar `estado_pago = 'PAGADA'`. |

---

## 3. Descripción de Tres Operaciones Futuras mediante JDBC

Para la Fase 2, se definen las especificaciones de las tres transacciones de negocio críticas:

### Operación 1: Alta Conjunta de Estación con Ficha de Configuración y Primer Surtidor
* **Propósito:** Cumplir el requisito de negocio que prohíbe estaciones sin ficha y sin al menos un surtidor registrado.
* **Datos de Entrada:**
  * Estación: `codigoEstacion`, `nombreComercial`, `direccion`, `telefono`, `fechaApertura`.
  * Ficha: `codigoConfiguracion`, `serieTickets`, `pieJustificante`.
  * Surtidor inicial: `codigoEquipo`, `numeroVisible` (1), `tipoCombustible`.
* **Reglas a Respetar:**
  1. Ejecución atómica en una única transacción (`connection.setAutoCommit(false)`).
  2. Inserción de la estación y recuperación de su clave generada (`Statement.RETURN_GENERATED_KEYS`).
  3. Inserción de la ficha de facturación asignando el `id_estacion` obtenido.
  4. Inserción del primer surtidor adscrito a dicho `id_estacion`.
  5. Si cualquiera de las tres operaciones falla por duplicidad de códigos o error de formato, invocar `connection.rollback()` para no dejar ningún registro huérfano.

### Operación 2: Venta de Mostrador con Múltiples Líneas y Pago Fraccionado
* **Propósito:** Registrar un ticket de venta completo que combine productos envasados y a granel, congelando los precios históricos y admitiendo pagos mixtos (tarjeta y efectivo).
* **Datos de Entrada:**
  * Cabecera: `idEstacion`, `idEmpleado`, `idCliente` (opcional, null si es anónima).
  * Lista de líneas: Lista de tuplas `(idProducto, cantidad)`.
  * Lista de cobros: Lista de tuplas `(medioPago, importe, referenciaTransaccion)`.
* **Reglas a Respetar:**
  1. Obtener de la ficha de configuración de la estación la serie y el correlativo siguiente mediante `SELECT siguiente_numero FROM fichas_configuracion WHERE id_estacion = ? FOR UPDATE`.
  2. Incrementar el correlativo en la ficha.
  3. Crear la cabecera de `ventas` con la referencia de ticket construida (`serie - numero`).
  4. Para cada línea, consultar el producto:
     * Si es envasado, validar que `cantidad` sea entero.
     * Si es granel, validar que `cantidad` respete la `precision_pesada`.
     * Tomar el `precio_venta` vigente del producto y guardarlo explícitamente en `precio_unitario_aplicado`.
     * Calcular `importe = cantidad * precio_unitario_aplicado`.
  5. Acumular el importe total en la cabecera.
  6. Insertar cada pago en `pagos`. Validar que `SUM(pagos) == importe_total`. Si coincide, marcar estado `PAGADA`.
  7. Confirmar la transacción con `commit()`.

### Operación 3: Devolución Parcial Segura con Control de Concurrencia
* **Propósito:** Procesar una devolución de producto asociada a una línea de venta, garantizando que devoluciones concurrentes o sucesivas no superen la cantidad ni el importe original.
* **Datos de Entrada:**
  * `idLineaVenta`, `idEmpleado`, `cantidadADevolver`, `motivo`, `aptoParaVenta`.
* **Reglas a Respetar:**
  1. Abrir transacción JDBC con nivel de aislamiento `SERIALIZABLE` o aplicar bloqueo pesimista `SELECT ... FOR UPDATE` sobre la fila de `lineas_venta`.
  2. Consultar la cantidad comprada y el precio unitario cobrado originalmente.
  3. Consultar la suma acumulada de devoluciones anteriores de esa línea: `SELECT COALESCE(SUM(cantidad), 0) FROM devoluciones WHERE id_linea_venta = ?`.
  4. Validar que `cantidadDevueltaPrevia + cantidadADevolver <= cantidadComprada`. Si se supera, abortar la transacción lanzando `DevolucionExcedidaException`.
  5. Validar que la cantidad a devolver cumpla las restricciones de modalidad del producto (entero para envasado, decimal admisible para granel).
  6. Calcular el reintegro proporcional exacto: `importeReintegrado = cantidadADevolver * precio_unitario_aplicado`.
  7. Insertar el registro en `devoluciones` y hacer `commit()`.
