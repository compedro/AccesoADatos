-- =============================================================================
-- PROYECTO: Sistema de Gestión de Gasolinera y Tienda (Fase 1)
-- SCRIPT: 01_esquema.sql
-- DESCRIPCIÓN: Creación de base de datos, tablas, claves primarias técnicas,
--              restricciones de unicidad de negocio, claves foráneas y CHECKs.
-- MOTOR: InnoDB en todas las tablas.
-- REGLA DE ARQUITECTURA: Todas las tablas poseen una PK autoincremental única entera.
-- =============================================================================

DROP DATABASE IF EXISTS gasolinera;
CREATE DATABASE gasolinera CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci;
USE gasolinera;

-- -----------------------------------------------------------------------------
-- 1. ESTACIONES Y CONFIGURACIÓN DE FACTURACIÓN
-- -----------------------------------------------------------------------------

-- Entidad: estaciones
-- Propósito: Representa cada una de las estaciones de servicio operadas por la empresa.
CREATE TABLE estaciones (
    id_estacion INT AUTO_INCREMENT PRIMARY KEY,
    codigo_estacion VARCHAR(50) NOT NULL,
    nombre_comercial VARCHAR(100) NOT NULL,
    direccion VARCHAR(255) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    fecha_apertura DATE NOT NULL,
    CONSTRAINT uq_estaciones_codigo UNIQUE (codigo_estacion)
) ENGINE=InnoDB;

-- Entidad: fichas_configuracion
-- Propósito: Ficha propia de configuración de facturación para emisión de tickets.
-- Regla de negocio: Relación 1:1 estricta con estación. id_estacion es UNIQUE.
CREATE TABLE fichas_configuracion (
    id_ficha_configuracion INT AUTO_INCREMENT PRIMARY KEY,
    id_estacion INT NOT NULL,
    codigo_configuracion VARCHAR(50) NOT NULL,
    serie_tickets VARCHAR(20) NOT NULL,
    siguiente_numero INT NOT NULL DEFAULT 1,
    pie_justificante TEXT NULL,
    CONSTRAINT uq_fichas_id_estacion UNIQUE (id_estacion),
    CONSTRAINT uq_fichas_codigo UNIQUE (codigo_configuracion),
    CONSTRAINT fk_fichas_estaciones FOREIGN KEY (id_estacion)
        REFERENCES estaciones(id_estacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_fichas_siguiente_num CHECK (siguiente_numero > 0)
) ENGINE=InnoDB;

-- Entidad: surtidores
-- Propósito: Equipos de dispensación pertenecientes a una estación.
-- Regla de negocio: Código de equipo único global; número visible único dentro de la estación.
CREATE TABLE surtidores (
    id_surtidor INT AUTO_INCREMENT PRIMARY KEY,
    id_estacion INT NOT NULL,
    codigo_equipo VARCHAR(50) NOT NULL,
    numero_visible INT NOT NULL,
    tipo_combustible VARCHAR(50) NOT NULL,
    estado_servicio ENUM('ACTIVO', 'AVERIADO', 'EN_MANTENIMIENTO', 'BAJA') NOT NULL DEFAULT 'ACTIVO',
    CONSTRAINT uq_surtidores_codigo_equipo UNIQUE (codigo_equipo),
    CONSTRAINT uq_surtidores_estacion_numero UNIQUE (id_estacion, numero_visible),
    CONSTRAINT fk_surtidores_estaciones FOREIGN KEY (id_estacion)
        REFERENCES estaciones(id_estacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_surtidores_numero_visible CHECK (numero_visible > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 2. PERSONAL, SUPERVISIÓN Y ASIGNACIÓN DE FUNCIONES
-- -----------------------------------------------------------------------------

-- Entidad: empleados
-- Propósito: Personal adscrito a una estación. Incluye relación reflexiva de supervisión.
-- Regla de negocio: id_supervisor hace referencia a otro empleado (no a sí mismo).
CREATE TABLE empleados (
    id_empleado INT AUTO_INCREMENT PRIMARY KEY,
    codigo_empleado VARCHAR(50) NOT NULL,
    dni VARCHAR(20) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(50) NULL,
    email VARCHAR(100) NULL,
    fecha_incorporacion DATE NOT NULL,
    id_estacion INT NOT NULL,
    id_supervisor INT NULL,
    CONSTRAINT uq_empleados_codigo UNIQUE (codigo_empleado),
    CONSTRAINT uq_empleados_dni UNIQUE (dni),
    CONSTRAINT fk_empleados_estaciones FOREIGN KEY (id_estacion)
        REFERENCES estaciones(id_estacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_empleados_supervisor FOREIGN KEY (id_supervisor)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- Entidad: funciones
-- Propósito: Catálogo maestro de tipos de responsabilidad o cargos desempeñables.
CREATE TABLE funciones (
    id_funcion INT AUTO_INCREMENT PRIMARY KEY,
    codigo_funcion VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion VARCHAR(255) NULL,
    CONSTRAINT uq_funciones_codigo UNIQUE (codigo_funcion)
) ENGINE=InnoDB;

-- Entidad: asignaciones_funciones
-- Propósito: Historial de responsabilidades desempeñadas por un empleado a lo largo del tiempo.
-- Permite múltiples funciones simultáneas y reasignaciones con diferentes condiciones.
CREATE TABLE asignaciones_funciones (
    id_asignacion_funcion INT AUTO_INCREMENT PRIMARY KEY,
    id_empleado INT NOT NULL,
    id_funcion INT NOT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NULL,
    -- Atributos específicos según tipo de función:
    limite_devolucion DECIMAL(10,2) NULL,             -- Específico de CAJERO
    num_acreditacion_combustible VARCHAR(50) NULL,    -- Específico de EXPENDEDOR
    fecha_caducidad_acreditacion DATE NULL,           -- Específico de EXPENDEDOR
    presupuesto_compras DECIMAL(10,2) NULL,           -- Específico de RESPONSABLE_TIENDA
    nivel_autorizacion INT NULL,                      -- Específico de JEFE_ESTACION
    CONSTRAINT fk_asig_empleados FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_asig_funciones FOREIGN KEY (id_funcion)
        REFERENCES funciones(id_funcion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_asig_fechas CHECK (fecha_fin IS NULL OR fecha_fin >= fecha_inicio),
    CONSTRAINT chk_asig_limite_devolucion CHECK (limite_devolucion IS NULL OR limite_devolucion >= 0),
    CONSTRAINT chk_asig_presupuesto CHECK (presupuesto_compras IS NULL OR presupuesto_compras >= 0),
    CONSTRAINT chk_asig_nivel_autorizacion CHECK (nivel_autorizacion IS NULL OR nivel_autorizacion >= 1)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 3. CLIENTES Y TARJETAS DE FIDELIZACIÓN
-- -----------------------------------------------------------------------------

-- Entidad: clientes
-- Propósito: Registro de clientes habituales o dados de alta.
CREATE TABLE clientes (
    id_cliente INT AUTO_INCREMENT PRIMARY KEY,
    codigo_cliente VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    telefono VARCHAR(50) NULL,
    email VARCHAR(100) NULL,
    matricula VARCHAR(50) NULL,
    fecha_registro DATE NOT NULL,
    CONSTRAINT uq_clientes_codigo UNIQUE (codigo_cliente)
) ENGINE=InnoDB;

-- Entidad: tarjetas_fidelizacion
-- Propósito: Tarjetas de fidelización emitidas. Pueden existir en stock (sin titular).
-- Regla de negocio: Relación 1:1 opcional con cliente (id_cliente UNIQUE).
CREATE TABLE tarjetas_fidelizacion (
    id_tarjeta INT AUTO_INCREMENT PRIMARY KEY,
    numero_tarjeta VARCHAR(50) NOT NULL,
    id_cliente INT NULL,
    fecha_emision DATE NOT NULL,
    fecha_asignacion DATE NULL,
    puntos INT NOT NULL DEFAULT 0,
    estado ENUM('EMITIDA', 'ASIGNADA', 'BLOQUEADA', 'BAJA') NOT NULL DEFAULT 'EMITIDA',
    CONSTRAINT uq_tarjetas_numero UNIQUE (numero_tarjeta),
    CONSTRAINT uq_tarjetas_id_cliente UNIQUE (id_cliente),
    CONSTRAINT fk_tarjetas_clientes FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE RESTRICT ON DELETE RESTRICT,
    CONSTRAINT chk_tarjetas_coherencia_cliente CHECK (
        (id_cliente IS NULL AND fecha_asignacion IS NULL) OR
        (id_cliente IS NOT NULL AND fecha_asignacion IS NOT NULL AND fecha_asignacion >= fecha_emision)
    ),
    CONSTRAINT chk_tarjetas_puntos CHECK (puntos >= 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 4. CATÁLOGO DE PRODUCTOS Y AUDITORÍA DE PRECIOS
-- -----------------------------------------------------------------------------

-- Entidad: productos
-- Propósito: Artículos comercializados en la tienda.
-- Regla de negocio: Modalidad inmutable ('ENVASADO' o 'GRANEL').
CREATE TABLE productos (
    id_producto INT AUTO_INCREMENT PRIMARY KEY,
    referencia_producto VARCHAR(50) NOT NULL,
    descripcion VARCHAR(255) NOT NULL,
    precio_venta DECIMAL(10,2) NOT NULL,
    estado_comercializacion ENUM('ACTIVO', 'RETIRADO') NOT NULL DEFAULT 'ACTIVO',
    modalidad ENUM('ENVASADO', 'GRANEL') NOT NULL,
    formato_envase VARCHAR(100) NULL,     -- Obligatorio si modalidad = 'ENVASADO'
    precision_pesada INT NULL,            -- Obligatorio si modalidad = 'GRANEL' (número de decimales: ej. 2 o 3)
    CONSTRAINT uq_productos_referencia UNIQUE (referencia_producto),
    CONSTRAINT chk_productos_precio CHECK (precio_venta > 0),
    CONSTRAINT chk_productos_modalidad_datos CHECK (
        (modalidad = 'ENVASADO' AND formato_envase IS NOT NULL AND precision_pesada IS NULL) OR
        (modalidad = 'GRANEL' AND precision_pesada IS NOT NULL AND precision_pesada >= 0 AND formato_envase IS NULL)
    )
) ENGINE=InnoDB;

-- Entidad: historico_precios
-- Propósito: Registro de auditoría alimentado exclusivamente por trigger tras cambios de precio.
CREATE TABLE historico_precios (
    id_historico_precio INT AUTO_INCREMENT PRIMARY KEY,
    id_producto INT NOT NULL,
    precio_anterior DECIMAL(10,2) NOT NULL,
    precio_nuevo DECIMAL(10,2) NOT NULL,
    fecha_modificacion DATETIME NOT NULL,
    CONSTRAINT fk_historico_productos FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_historico_precios_distintos CHECK (precio_anterior <> precio_nuevo)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 5. PROVEEDORES Y ACUERDOS DE SUMINISTRO (RELACIÓN N:M)
-- -----------------------------------------------------------------------------

-- Entidad: proveedores
-- Propósito: Empresas proveedoras de productos del catálogo.
CREATE TABLE proveedores (
    id_proveedor INT AUTO_INCREMENT PRIMARY KEY,
    codigo_proveedor VARCHAR(50) NOT NULL,
    cif VARCHAR(20) NOT NULL,
    razon_social VARCHAR(150) NOT NULL,
    telefono VARCHAR(50) NOT NULL,
    email VARCHAR(100) NULL,
    direccion VARCHAR(255) NULL,
    CONSTRAINT uq_proveedores_codigo UNIQUE (codigo_proveedor),
    CONSTRAINT uq_proveedores_cif UNIQUE (cif)
) ENGINE=InnoDB;

-- Entidad: acuerdos_suministro
-- Propósito: Tabla intermedia N:M entre proveedores y productos con condiciones comerciales.
CREATE TABLE acuerdos_suministro (
    id_acuerdo INT AUTO_INCREMENT PRIMARY KEY,
    id_proveedor INT NOT NULL,
    id_producto INT NOT NULL,
    referencia_proveedor VARCHAR(50) NOT NULL,
    precio_compra DECIMAL(10,2) NOT NULL,
    plazo_entrega_dias INT NOT NULL DEFAULT 1,
    vigente BOOLEAN NOT NULL DEFAULT TRUE,
    CONSTRAINT uq_acuerdos_proveedor_producto UNIQUE (id_proveedor, id_producto),
    CONSTRAINT fk_acuerdos_proveedores FOREIGN KEY (id_proveedor)
        REFERENCES proveedores(id_proveedor)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_acuerdos_productos FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_acuerdos_precio_compra CHECK (precio_compra > 0),
    CONSTRAINT chk_acuerdos_plazo CHECK (plazo_entrega_dias >= 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 6. CAMPAÑAS PROMOCIONALES E INSCRIPCIONES
-- -----------------------------------------------------------------------------

-- Entidad: campanas
-- Propósito: Campañas de marketing y fidelización con vigencia temporal.
CREATE TABLE campanas (
    id_campana INT AUTO_INCREMENT PRIMARY KEY,
    codigo_campana VARCHAR(50) NOT NULL,
    nombre VARCHAR(100) NOT NULL,
    descripcion TEXT NULL,
    fecha_inicio DATE NOT NULL,
    fecha_fin DATE NOT NULL,
    CONSTRAINT uq_campanas_codigo UNIQUE (codigo_campana),
    CONSTRAINT chk_campanas_fechas CHECK (fecha_fin >= fecha_inicio)
) ENGINE=InnoDB;

-- Entidad: inscripciones_campanas
-- Propósito: Inscripción de clientes registrados a campañas promocionales.
CREATE TABLE inscripciones_campanas (
    id_inscripcion_campana INT AUTO_INCREMENT PRIMARY KEY,
    id_campana INT NOT NULL,
    id_cliente INT NOT NULL,
    fecha_inscripcion DATE NOT NULL,
    CONSTRAINT uq_inscripcion_campana_cliente UNIQUE (id_campana, id_cliente),
    CONSTRAINT fk_inscripciones_campanas FOREIGN KEY (id_campana)
        REFERENCES campanas(id_campana)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_inscripciones_clientes FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 7. VENTAS, LÍNEAS DE VENTA Y PAGOS
-- -----------------------------------------------------------------------------

-- Entidad: ventas
-- Propósito: Cabecera de tickets de compra de tienda y repostajes.
-- Permite compras anónimas (id_cliente NULL).
CREATE TABLE ventas (
    id_venta INT AUTO_INCREMENT PRIMARY KEY,
    referencia_ticket VARCHAR(50) NOT NULL,
    id_estacion INT NOT NULL,
    id_empleado INT NOT NULL,
    id_cliente INT NULL,
    fecha_hora DATETIME NOT NULL,
    importe_total DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    estado_pago ENUM('PENDIENTE', 'PAGADA', 'CANCELADA') NOT NULL DEFAULT 'PENDIENTE',
    CONSTRAINT uq_ventas_referencia_ticket UNIQUE (referencia_ticket),
    CONSTRAINT fk_ventas_estaciones FOREIGN KEY (id_estacion)
        REFERENCES estaciones(id_estacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_ventas_empleados FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_ventas_clientes FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_ventas_importe_total CHECK (importe_total >= 0)
) ENGINE=InnoDB;

-- Entidad: lineas_venta
-- Propósito: Detalle de productos vendidos en cada ticket.
-- Conserva el precio histórico realmente cobrado.
CREATE TABLE lineas_venta (
    id_linea_venta INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    numero_orden INT NOT NULL,
    id_producto INT NOT NULL,
    cantidad DECIMAL(10,3) NOT NULL,
    precio_unitario_aplicado DECIMAL(10,2) NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    CONSTRAINT uq_lineas_venta_orden UNIQUE (id_venta, numero_orden),
    CONSTRAINT fk_lineas_ventas FOREIGN KEY (id_venta)
        REFERENCES ventas(id_venta)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_lineas_productos FOREIGN KEY (id_producto)
        REFERENCES productos(id_producto)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_lineas_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_lineas_precio_unitario CHECK (precio_unitario_aplicado > 0),
    CONSTRAINT chk_lineas_importe CHECK (importe >= 0)
) ENGINE=InnoDB;

-- Entidad: pagos
-- Propósito: Desglose de cobros asociados a un ticket (permite cobros fraccionados o múltiples).
CREATE TABLE pagos (
    id_pago INT AUTO_INCREMENT PRIMARY KEY,
    id_venta INT NOT NULL,
    medio_pago ENUM('EFECTIVO', 'TARJETA', 'TRANSFERENCIA', 'FIDELIZACION', 'OTRO') NOT NULL,
    importe DECIMAL(10,2) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    referencia_transaccion VARCHAR(100) NULL,
    CONSTRAINT fk_pagos_ventas FOREIGN KEY (id_venta)
        REFERENCES ventas(id_venta)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_pagos_importe CHECK (importe > 0)
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 8. DEVOLUCIONES E INCIDENCIAS
-- -----------------------------------------------------------------------------

-- Entidad: devoluciones
-- Propósito: Reintegros de productos comprados asociados a una línea concreta de venta.
CREATE TABLE devoluciones (
    id_devolucion INT AUTO_INCREMENT PRIMARY KEY,
    id_linea_venta INT NOT NULL,
    id_empleado INT NOT NULL,
    cantidad DECIMAL(10,3) NOT NULL,
    motivo VARCHAR(255) NOT NULL,
    fecha_hora DATETIME NOT NULL,
    importe_reintegrado DECIMAL(10,2) NOT NULL,
    apto_para_venta BOOLEAN NOT NULL DEFAULT FALSE,
    CONSTRAINT fk_devoluciones_lineas FOREIGN KEY (id_linea_venta)
        REFERENCES lineas_venta(id_linea_venta)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_devoluciones_empleados FOREIGN KEY (id_empleado)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT chk_devoluciones_cantidad CHECK (cantidad > 0),
    CONSTRAINT chk_devoluciones_importe CHECK (importe_reintegrado > 0)
) ENGINE=InnoDB;

-- Entidad: incidencias
-- Propósito: Registro de incidencias operativas por estación y asignación de empleado responsable.
CREATE TABLE incidencias (
    id_incidencia INT AUTO_INCREMENT PRIMARY KEY,
    referencia_incidencia VARCHAR(50) NOT NULL,
    id_estacion INT NOT NULL,
    id_empleado_responsable INT NULL,
    descripcion TEXT NOT NULL,
    fecha_hora_apertura DATETIME NOT NULL,
    estado ENUM('ABIERTA', 'EN_PROCESO', 'RESUELTA', 'CERRADA') NOT NULL DEFAULT 'ABIERTA',
    CONSTRAINT uq_incidencias_referencia UNIQUE (referencia_incidencia),
    CONSTRAINT fk_incidencias_estaciones FOREIGN KEY (id_estacion)
        REFERENCES estaciones(id_estacion)
        ON UPDATE CASCADE ON DELETE RESTRICT,
    CONSTRAINT fk_incidencias_empleados FOREIGN KEY (id_empleado_responsable)
        REFERENCES empleados(id_empleado)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;

-- -----------------------------------------------------------------------------
-- 9. TABLA DE COMPATIBILIDAD CON PROYECTO PREVIO (REPOSTAJES)
-- -----------------------------------------------------------------------------
CREATE TABLE IF NOT EXISTS repostajes (
    id_repostaje INT AUTO_INCREMENT PRIMARY KEY,
    id_cliente INT NOT NULL,
    fecha VARCHAR(20) NOT NULL,
    importe DOUBLE NOT NULL,
    litros DOUBLE NOT NULL,
    combustible VARCHAR(50) NOT NULL,
    CONSTRAINT fk_repostajes_clientes FOREIGN KEY (id_cliente)
        REFERENCES clientes(id_cliente)
        ON UPDATE CASCADE ON DELETE RESTRICT
) ENGINE=InnoDB;
