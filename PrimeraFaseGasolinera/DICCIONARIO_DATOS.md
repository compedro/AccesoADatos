# Modelo Relacional y Diccionario de Datos - Fase 1

**Proyecto:** Sistema de Gestión de Gasolinera y Tienda  
**Módulo:** Acceso a Datos (2.º DAM)  
**Motor de Persistencia:** MySQL (InnoDB)  
**Regla Estructural Global:** Todas las tablas poseen una clave primaria técnica autoincremental entera única (`id_* INT PRIMARY KEY AUTO_INCREMENT`). Se prohíbe el uso de claves compuestas como PK técnica para maximizar el desacoplamiento con las referencias y claves de negocio (`cif`, `dni`, códigos internos, números de serie, etc.).

> **Nota:** Según las especificaciones del docente, este documento presenta el modelo relacional de forma textual y tabular exhaustiva. Se prescinde deliberadamente de diagramas Entidad-Relación gráficos.

---

## 1. Relación Textual de Tablas y Esquema Relacional

* **estaciones** (`id_estacion` [PK], `codigo_estacion` [UQ], `nombre_comercial`, `direccion`, `telefono`, `fecha_apertura`)
* **fichas_configuracion** (`id_ficha_configuracion` [PK], `id_estacion` [FK, UQ], `codigo_configuracion` [UQ], `serie_tickets`, `siguiente_numero`, `pie_justificante`)
* **surtidores** (`id_surtidor` [PK], `id_estacion` [FK], `codigo_equipo` [UQ], `numero_visible`, `tipo_combustible`, `estado_servicio`) — *UQ(`id_estacion`, `numero_visible`)*
* **empleados** (`id_empleado` [PK], `codigo_empleado` [UQ], `dni` [UQ], `nombre`, `telefono`, `email`, `fecha_incorporacion`, `id_estacion` [FK], `id_supervisor` [FK, NULL])
* **funciones** (`id_funcion` [PK], `codigo_funcion` [UQ], `nombre`, `descripcion`)
* **asignaciones_funciones** (`id_asignacion_funcion` [PK], `id_empleado` [FK], `id_funcion` [FK], `fecha_inicio`, `fecha_fin`, `limite_devolucion`, `num_acreditacion_combustible`, `fecha_caducidad_acreditacion`, `presupuesto_compras`, `nivel_autorizacion`)
* **clientes** (`id_cliente` [PK], `codigo_cliente` [UQ], `nombre`, `telefono`, `email`, `matricula`, `fecha_registro`)
* **tarjetas_fidelizacion** (`id_tarjeta` [PK], `numero_tarjeta` [UQ], `id_cliente` [FK, UQ, NULL], `fecha_emision`, `fecha_asignacion`, `puntos`, `estado`)
* **productos** (`id_producto` [PK], `referencia_producto` [UQ], `descripcion`, `precio_venta`, `estado_comercializacion`, `modalidad`, `formato_envase`, `precision_pesada`)
* **historico_precios** (`id_historico_precio` [PK], `id_producto` [FK], `precio_anterior`, `precio_nuevo`, `fecha_modificacion`)
* **proveedores** (`id_proveedor` [PK], `codigo_proveedor` [UQ], `cif` [UQ], `razon_social`, `telefono`, `email`, `direccion`)
* **acuerdos_suministro** (`id_acuerdo` [PK], `id_proveedor` [FK], `id_producto` [FK], `referencia_proveedor`, `precio_compra`, `plazo_entrega_dias`, `vigente`) — *UQ(`id_proveedor`, `id_producto`)*
* **campanas** (`id_campana` [PK], `codigo_campana` [UQ], `nombre`, `descripcion`, `fecha_inicio`, `fecha_fin`)
* **inscripciones_campanas** (`id_inscripcion_campana` [PK], `id_campana` [FK], `id_cliente` [FK], `fecha_inscripcion`) — *UQ(`id_campana`, `id_cliente`)*
* **ventas** (`id_venta` [PK], `referencia_ticket` [UQ], `id_estacion` [FK], `id_empleado` [FK], `id_cliente` [FK, NULL], `fecha_hora`, `importe_total`, `estado_pago`)
* **lineas_venta** (`id_linea_venta` [PK], `id_venta` [FK], `numero_orden`, `id_producto` [FK], `cantidad`, `precio_unitario_aplicado`, `importe`) — *UQ(`id_venta`, `numero_orden`)*
* **pagos** (`id_pago` [PK], `id_venta` [FK], `medio_pago`, `importe`, `fecha_hora`, `referencia_transaccion`)
* **devoluciones** (`id_devolucion` [PK], `id_linea_venta` [FK], `id_empleado` [FK], `cantidad`, `motivo`, `fecha_hora`, `importe_reintegrado`, `apto_para_venta`)
* **incidencias** (`id_incidencia` [PK], `referencia_incidencia` [UQ], `id_estacion` [FK], `id_empleado_responsable` [FK, NULL], `descripcion`, `fecha_hora_apertura`, `estado`)
* **repostajes** (*compatibilidad anterior*: `id_repostaje` [PK], `id_cliente` [FK], `fecha`, `importe`, `litros`, `combustible`)

---

## 2. Diccionario Detallado de Datos por Tabla

### 2.1. Tabla: `estaciones`
Representa cada gasolinera o estación de servicio de la red.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_estacion` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico autoincremental de la estación. |
| `codigo_estacion` | `VARCHAR(50)` | NO | UQ | `uq_estaciones_codigo` | Código interno único de negocio en la empresa (ej. 'EST-001'). |
| `nombre_comercial` | `VARCHAR(100)` | NO | | | Nombre comercial y rótulo de la estación. |
| `direccion` | `VARCHAR(255)` | NO | | | Domicilio completo o punto kilométrico de la estación. |
| `telefono` | `VARCHAR(50)` | NO | | | Teléfono de contacto de la estación. |
| `fecha_apertura` | `DATE` | NO | | | Fecha oficial de apertura al público. |

* **Reglas de Integridad Referencial:**
  * Referenciada por `fichas_configuracion` (`ON UPDATE CASCADE ON DELETE RESTRICT`).
  * Referenciada por `surtidores` (`ON UPDATE CASCADE ON DELETE RESTRICT`).
  * Referenciada por `empleados` (`ON UPDATE CASCADE ON DELETE RESTRICT`).
  * Referenciada por `ventas` (`ON UPDATE CASCADE ON DELETE RESTRICT`).
  * Referenciada por `incidencias` (`ON UPDATE CASCADE ON DELETE RESTRICT`).

---

### 2.2. Tabla: `fichas_configuracion`
Ficha técnica de configuración para facturación y emisión de tickets. Forma una relación 1:1 estricta con `estaciones`.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_ficha_configuracion` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico autoincremental de la ficha. |
| `id_estacion` | `INT` | NO | FK, UQ | `uq_fichas_id_estacion`, FK a `estaciones(id_estacion)` | Garantiza que una estación tiene como máximo una ficha. |
| `codigo_configuracion` | `VARCHAR(50)` | NO | UQ | `uq_fichas_codigo` | Código único usado por soporte técnico. |
| `serie_tickets` | `VARCHAR(20)` | NO | | | Serie alfanumérica para los tickets (ej. 'TKT-2026-MAD'). |
| `siguiente_numero` | `INT` | NO | | `DEFAULT 1`, `CHECK (siguiente_numero > 0)` | Siguiente correlativo correlativo a emitir. |
| `pie_justificante` | `TEXT` | SÍ | | | Texto legal o promocional al pie de los tickets. |

* **Reglas de Integridad Referencial:**
  * FK a `estaciones(id_estacion)` con `ON UPDATE CASCADE ON DELETE RESTRICT`.

---

### 2.3. Tabla: `surtidores`
Equipos de dispensación de combustible adscritos a una estación.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_surtidor` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico autoincremental del surtidor. |
| `id_estacion` | `INT` | NO | FK | FK a `estaciones(id_estacion)` | Estación a la que pertenece el equipo. |
| `codigo_equipo` | `VARCHAR(50)` | NO | UQ | `uq_surtidores_codigo_equipo` | Código técnico único en toda la empresa. |
| `numero_visible` | `INT` | NO | | `CHECK (numero_visible > 0)` | Número visible en pista (ej. 1, 2, 3). |
| `tipo_combustible` | `VARCHAR(50)` | NO | | | Tipo de combustible dispensado. |
| `estado_servicio` | `ENUM` | NO | | `'ACTIVO', 'AVERIADO', 'EN_MANTENIMIENTO', 'BAJA'` | Estado operativo del equipo. |

* **Restricción Compuesta de Negocio:**
  * `uq_surtidores_estacion_numero UNIQUE (id_estacion, numero_visible)`: Dos surtidores de una misma estación no pueden compartir número visible; sí puede repetirse entre distintas estaciones.

---

### 2.4. Tabla: `empleados`
Trabajadores adscritos a una estación. Incluye relación reflexiva de supervisión.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_empleado` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico autoincremental del empleado. |
| `codigo_empleado` | `VARCHAR(50)` | NO | UQ | `uq_empleados_codigo` | Número interno corporativo del trabajador. |
| `dni` | `VARCHAR(20)` | NO | UQ | `uq_empleados_dni` | Documento nacional de identidad. |
| `nombre` | `VARCHAR(100)` | NO | | | Nombre y apellidos. |
| `telefono` | `VARCHAR(50)` | SÍ | | | Teléfono de contacto personal. |
| `email` | `VARCHAR(100)` | SÍ | | | Correo electrónico de contacto. |
| `fecha_incorporacion`| `DATE` | NO | | | Fecha de alta laboral en la empresa. |
| `id_estacion` | `INT` | NO | FK | FK a `estaciones(id_estacion)` | Estación a la que está adscrito. |
| `id_supervisor` | `INT` | SÍ | FK | FK a `empleados(id_empleado)`, `CHECK (id_supervisor <> id_empleado)` | Empleado que actúa como supervisor directo. Nulo si no tiene. |

* **Integridad Referencial:**
  * FK reflexiva a `empleados(id_empleado)` con `ON UPDATE CASCADE ON DELETE RESTRICT`.

---

### 2.5. Tabla: `funciones`
Catálogo maestro de funciones o roles laborales.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_funcion` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la función. |
| `codigo_funcion` | `VARCHAR(50)` | NO | UQ | `uq_funciones_codigo` | Código de la función: 'CAJERO', 'EXPENDEDOR', 'RESPONSABLE_TIENDA', 'JEFE_ESTACION'. |
| `nombre` | `VARCHAR(100)` | NO | | | Denominación del cargo. |
| `descripcion` | `VARCHAR(255)` | SÍ | | | Descripción y alcance de la responsabilidad. |

---

### 2.6. Tabla: `asignaciones_funciones`
Historial de desempeño de funciones por parte del personal, con sus atributos específicos.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_asignacion_funcion`| `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la asignación. |
| `id_empleado` | `INT` | NO | FK | FK a `empleados(id_empleado)` | Empleado asignado. |
| `id_funcion` | `INT` | NO | FK | FK a `funciones(id_funcion)` | Función asignada. |
| `fecha_inicio` | `DATE` | NO | | | Fecha en que asume la función. |
| `fecha_fin` | `DATE` | SÍ | | `CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio)` | Fecha de finalización (nulo si sigue activa). |
| `limite_devolucion` | `DECIMAL(10,2)` | SÍ | | `CHECK (limite_devolucion >= 0)` | Límite monetario autorizado para devoluciones (Cajeros). |
| `num_acreditacion_combustible` | `VARCHAR(50)` | SÍ | | | Número oficial de acreditación para manipular combustibles (Expendedores). |
| `fecha_caducidad_acreditacion` | `DATE` | SÍ | | | Fecha de caducidad del carné de manipulación (Expendedores). |
| `presupuesto_compras`| `DECIMAL(10,2)`| SÍ | | `CHECK (presupuesto_compras >= 0)` | Presupuesto máximo autorizado de compras (Responsables de tienda). |
| `nivel_autorizacion` | `INT` | SÍ | | `CHECK (nivel_autorizacion >= 1)` | Nivel para operaciones excepcionales (Jefes de estación). |

---

### 2.7. Tabla: `clientes`
Clientes dados de alta en el sistema.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_cliente` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del cliente. |
| `codigo_cliente` | `VARCHAR(50)` | NO | UQ | `uq_clientes_codigo` | Código de cliente en el sistema (ej. 'CLI-001'). |
| `nombre` | `VARCHAR(100)` | NO | | | Nombre o razón social del cliente. |
| `telefono` | `VARCHAR(50)` | SÍ | | | Teléfono de contacto (puede faltar si tiene email). |
| `email` | `VARCHAR(100)` | SÍ | | | Correo electrónico (puede faltar si tiene teléfono). |
| `matricula` | `VARCHAR(50)` | SÍ | | | Matrícula de vehículo habitual (mantiene compatibilidad anterior). |
| `fecha_registro` | `DATE` | NO | | | Fecha de registro en el sistema. |

---

### 2.8. Tabla: `tarjetas_fidelizacion`
Tarjetas de fidelización emitidas. Permite existencia en stock (sin titular).
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_tarjeta` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la tarjeta. |
| `numero_tarjeta` | `VARCHAR(50)` | NO | UQ | `uq_tarjetas_numero` | Número grabado en el plástico de la tarjeta. |
| `id_cliente` | `INT` | SÍ | FK, UQ | `uq_tarjetas_id_cliente`, FK a `clientes(id_cliente)` | Titular (relación 1:1 opcional; NULL si está en stock). |
| `fecha_emision` | `DATE` | NO | | | Fecha de fabricación o recepción del lote. |
| `fecha_asignacion` | `DATE` | SÍ | | | Fecha de entrega al cliente. |
| `puntos` | `INT` | NO | | `DEFAULT 0, CHECK (puntos >= 0)` | Saldo de puntos acumulados. |
| `estado` | `ENUM` | NO | | `'EMITIDA', 'ASIGNADA', 'BLOQUEADA', 'BAJA'` | Estado de la tarjeta. |

* **CHECK de Coherencia de Titularidad:**
  * `CHECK ((id_cliente IS NULL AND fecha_asignacion IS NULL) OR (id_cliente IS NOT NULL AND fecha_asignacion IS NOT NULL AND fecha_asignacion >= fecha_emision))`

---

### 2.9. Tabla: `productos`
Artículos comercializados en la tienda y pista.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_producto` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del producto. |
| `referencia_producto`| `VARCHAR(50)` | NO | UQ | `uq_productos_referencia` | Código comercial único (SKU). |
| `descripcion` | `VARCHAR(255)` | NO | | | Nombre comercial y descripción del artículo. |
| `precio_venta` | `DECIMAL(10,2)`| NO | | `CHECK (precio_venta > 0)` | Precio de venta vigente al público. |
| `estado_comercializacion`| `ENUM` | NO | | `'ACTIVO', 'RETIRADO'` | Disponibilidad comercial del producto. |
| `modalidad` | `ENUM` | NO | | `'ENVASADO', 'GRANEL'` | Modalidad de venta inmutable. |
| `formato_envase` | `VARCHAR(100)` | SÍ | | | Formato obligatorio si modalidad = 'ENVASADO' (ej. 'Garrafa 5L'). |
| `precision_pesada` | `INT` | SÍ | | `CHECK (precision_pesada >= 0)` | Decimales admitidos si modalidad = 'GRANEL' (ej. 2 o 3). |

* **CHECK de Exclusión Mutua de Modalidad:**
  * `CHECK ((modalidad = 'ENVASADO' AND formato_envase IS NOT NULL AND precision_pesada IS NULL) OR (modalidad = 'GRANEL' AND precision_pesada IS NOT NULL AND formato_envase IS NULL))`

---

### 2.10. Tabla: `historico_precios`
Auditoría automática de variaciones de precio en catálogo. Alimentada exclusivamente por trigger.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_historico_precio`| `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del cambio de precio. |
| `id_producto` | `INT` | NO | FK | FK a `productos(id_producto)` | Producto afectado por el cambio de precio. |
| `precio_anterior` | `DECIMAL(10,2)`| NO | | | Precio antes de la modificación. |
| `precio_nuevo` | `DECIMAL(10,2)`| NO | | `CHECK (precio_anterior <> precio_nuevo)` | Nuevo precio confirmado. |
| `fecha_modificacion`| `DATETIME` | NO | | | Momento exacto de confirmación del cambio. |

---

### 2.11. Tabla: `proveedores`
Empresas proveedoras del catálogo.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_proveedor` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del proveedor. |
| `codigo_proveedor` | `VARCHAR(50)` | NO | UQ | `uq_proveedores_codigo` | Código interno corporativo (ej. 'PRV-001'). |
| `cif` | `VARCHAR(20)` | NO | UQ | `uq_proveedores_cif` | Identificación fiscal (CIF / NIF). |
| `razon_social` | `VARCHAR(150)` | NO | | | Razón social de la empresa. |
| `telefono` | `VARCHAR(50)` | NO | | | Teléfono de contacto de pedidos. |
| `email` | `VARCHAR(100)` | SÍ | | | Correo electrónico de facturación/pedidos. |
| `direccion` | `VARCHAR(255)` | SÍ | | | Domicilio social o almacén. |

---

### 2.12. Tabla: `acuerdos_suministro`
Relación N:M entre `proveedores` y `productos` con condiciones contractuales vigentes.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_acuerdo` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del acuerdo. |
| `id_proveedor` | `INT` | NO | FK | FK a `proveedores(id_proveedor)` | Proveedor suministrador. |
| `id_producto` | `INT` | NO | FK | FK a `productos(id_producto)` | Producto suministrado. |
| `referencia_proveedor`| `VARCHAR(50)` | NO | | | Referencia/código del artículo usado por el proveedor. |
| `precio_compra` | `DECIMAL(10,2)`| NO | | `CHECK (precio_compra > 0)` | Precio unitario de coste acordado. |
| `plazo_entrega_dias`| `INT` | NO | | `CHECK (plazo_entrega_dias >= 0)` | Días habituales de entrega. |
| `vigente` | `BOOLEAN` | NO | | `DEFAULT TRUE` | Indica si el acuerdo está activo. |

* **Restricción de Negocio:**
  * `uq_acuerdos_proveedor_producto UNIQUE (id_proveedor, id_producto)`: No se permite duplicar acuerdos entre el mismo proveedor y producto.

---

### 2.13. Tabla: `campanas`
Campañas comerciales de fidelización o promociones.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_campana` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la campaña. |
| `codigo_campana` | `VARCHAR(50)` | NO | UQ | `uq_campanas_codigo` | Código único de la promoción (ej. 'CAMP-VER-2026'). |
| `nombre` | `VARCHAR(100)` | NO | | | Título de la campaña. |
| `descripcion` | `TEXT` | SÍ | | | Bases o detalle de las promociones. |
| `fecha_inicio` | `DATE` | NO | | | Fecha de inicio de la campaña. |
| `fecha_fin` | `DATE` | NO | | `CHECK (fecha_fin >= fecha_inicio)` | Fecha de finalización. |

---

### 2.14. Tabla: `inscripciones_campanas`
Participación de clientes registrados en campañas promocionales.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_inscripcion_campana`| `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la inscripción. |
| `id_campana` | `INT` | NO | FK | FK a `campanas(id_campana)` | Campaña en la que participa. |
| `id_cliente` | `INT` | NO | FK | FK a `clientes(id_cliente)` | Cliente inscrito. |
| `fecha_inscripcion` | `DATE` | NO | | | Fecha en que se apuntó el cliente. |

* **Restricción de Negocio:**
  * `uq_inscripcion_campana_cliente UNIQUE (id_campana, id_cliente)`: Impide inscribir dos veces al mismo cliente en la misma campaña.

---

### 2.15. Tabla: `ventas`
Cabecera de tickets de compra de tienda y pista.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_venta` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la venta. |
| `referencia_ticket` | `VARCHAR(50)` | NO | UQ | `uq_ventas_referencia_ticket` | Referencia única global de facturación del ticket. |
| `id_estacion` | `INT` | NO | FK | FK a `estaciones(id_estacion)` | Estación donde se efectúa la venta. |
| `id_empleado` | `INT` | NO | FK | FK a `empleados(id_empleado)` | Empleado que atiende la caja. |
| `id_cliente` | `INT` | SÍ | FK | FK a `clientes(id_cliente)` | Cliente identificado (NULL para compras anónimas). |
| `fecha_hora` | `DATETIME` | NO | | | Momento de emisión del ticket. |
| `importe_total` | `DECIMAL(10,2)`| NO | | `CHECK (importe_total >= 0)` | Suma total de los importes de las líneas. |
| `estado_pago` | `ENUM` | NO | | `'PENDIENTE', 'PAGADA', 'CANCELADA'` | Estado del cobro del ticket. |

---

### 2.16. Tabla: `lineas_venta`
Detalle de artículos facturados en cada ticket.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_linea_venta` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la línea. |
| `id_venta` | `INT` | NO | FK | FK a `ventas(id_venta)` | Ticket de venta al que pertenece. |
| `numero_orden` | `INT` | NO | | | Número correlativo de línea dentro del ticket (1, 2, 3...). |
| `id_producto` | `INT` | NO | FK | FK a `productos(id_producto)` | Artículo del catálogo vendido. |
| `cantidad` | `DECIMAL(10,3)`| NO | | `CHECK (cantidad > 0)` | Cantidad vendida (unidades enteras o kg pesados). |
| `precio_unitario_aplicado`| `DECIMAL(10,2)`| NO | | `CHECK (precio_unitario_aplicado > 0)` | **Precio histórico realmente cobrado** en el momento de la venta. |
| `importe` | `DECIMAL(10,2)`| NO | | `CHECK (importe >= 0)` | Importe total de la línea (`cantidad * precio_unitario_aplicado`). |

* **Restricción de Negocio:**
  * `uq_lineas_venta_orden UNIQUE (id_venta, numero_orden)`: El número de orden no puede repetirse dentro del mismo ticket.

---

### 2.17. Tabla: `pagos`
Registro de cobros asociados a un ticket de venta.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_pago` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del cobro. |
| `id_venta` | `INT` | NO | FK | FK a `ventas(id_venta)` | Venta a la que se aplica el cobro. |
| `medio_pago` | `ENUM` | NO | | `'EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'FIDELIZACION', 'OTRO'` | Medio de pago utilizado. |
| `importe` | `DECIMAL(10,2)`| NO | | `CHECK (importe > 0)` | Importe abonado con este medio. |
| `fecha_hora` | `DATETIME` | NO | | | Momento del abono. |
| `referencia_transaccion`| `VARCHAR(100)`| SÍ | | | Código de resguardo bancario o autorización TPV. |

---

### 2.18. Tabla: `devoluciones`
Reintegros de productos comprados asociados a una línea concreta de venta.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_devolucion` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la devolución. |
| `id_linea_venta` | `INT` | NO | FK | FK a `lineas_venta(id_linea_venta)` | Línea específica del ticket original que se devuelve. |
| `id_empleado` | `INT` | NO | FK | FK a `empleados(id_empleado)` | Empleado que autoriza y recepciona la devolución. |
| `cantidad` | `DECIMAL(10,3)`| NO | | `CHECK (cantidad > 0)` | Cantidad reintegrada. |
| `motivo` | `VARCHAR(255)` | NO | | | Motivo declarado para la devolución. |
| `fecha_hora` | `DATETIME` | NO | | | Momento en que se registra el reintegro. |
| `importe_reintegrado`| `DECIMAL(10,2)`| NO | | `CHECK (importe_reintegrado > 0)` | Importe total devuelto al cliente (calculado según precio histórico cobrado). |
| `apto_para_venta` | `BOOLEAN` | NO | | `DEFAULT FALSE` | Indica si el producto puede devolverse a la estantería/dispensador. |

---

### 2.19. Tabla: `incidencias`
Incidencias técnicas y operativas registradas en cada estación.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_incidencia` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico de la incidencia. |
| `referencia_incidencia`| `VARCHAR(50)` | NO | UQ | `uq_incidencias_referencia` | Código único de ticket de incidencia (ej. 'INC-2026-001'). |
| `id_estacion` | `INT` | NO | FK | FK a `estaciones(id_estacion)` | Estación donde se produjo la anomalía. |
| `id_empleado_responsable`| `INT` | SÍ | FK | FK a `empleados(id_empleado)` | Empleado asignado para su resolución (NULL si está sin asignar). |
| `descripcion` | `TEXT` | NO | | | Explicación detallada del problema. |
| `fecha_hora_apertura`| `DATETIME` | NO | | | Momento de apertura de la incidencia. |
| `estado` | `ENUM` | NO | | `'ABIERTA', 'EN_PROCESO', 'RESUELTA', 'CERRADA'` | Estado actual de seguimiento. |

---

### 2.20. Tabla: `repostajes` (Compatibilidad Hacia Atrás)
Tabla de compatibilidad para el almacenamiento relacional de repostajes provenientes de la versión anterior.
* **Motor:** `InnoDB`

| Columna | Tipo de Dato | Nulo | Clave | Restricciones / Validación | Descripción |
| :--- | :--- | :--- | :--- | :--- | :--- |
| `id_repostaje` | `INT` | NO | PK | `AUTO_INCREMENT` | Identificador técnico del repostaje. |
| `id_cliente` | `INT` | NO | FK | FK a `clientes(id_cliente)` | Cliente que repostó. |
| `fecha` | `VARCHAR(20)` | NO | | | Fecha del repostaje. |
| `importe` | `DOUBLE` | NO | | | Importe pagado en el repostaje. |
| `litros` | `DOUBLE` | NO | | | Litros dispensados. |
| `combustible` | `VARCHAR(50)` | NO | | | Tipo de combustible suministrado. |
