-- =============================================================================
-- PROYECTO: Sistema de Gestión de Gasolinera y Tienda (Fase 1)
-- SCRIPT: 03_datos.sql
-- DESCRIPCIÓN: Carga de datos de prueba realistas y cobertura de casos límite.
-- CRITERIOS:
--   - Al menos 5 registros por tabla principal.
--   - Al menos 10 líneas de venta repartidas entre varios tickets.
--   - Cobertura explícita de todos los casos límite del enunciado.
-- =============================================================================

USE gasolinera;

-- Desactivar temporalmente autocommit para carga transaccional segura
SET AUTOCOMMIT = 0;
START TRANSACTION;

-- -----------------------------------------------------------------------------
-- 1. ESTACIONES Y FICHAS DE CONFIGURACIÓN (Relación 1:1 estricta)
-- Caso: Varias estaciones, cada una con su ficha y surtidores.
-- -----------------------------------------------------------------------------
INSERT INTO estaciones (id_estacion, codigo_estacion, nombre_comercial, direccion, telefono, fecha_apertura) VALUES
(1, 'EST-001', 'Gasolinera Central - AutoVía Norte', 'Autovía A-1, Km 23, Madrid', '912345671', '2018-03-15'),
(2, 'EST-002', 'Estación de Servicio Sol y Playa', 'Av. del Mediterráneo 45, Valencia', '963456712', '2019-06-20'),
(3, 'EST-003', 'Gasolinera Ruta de la Plata', 'Ctra. Nacional 630, Km 112, Sevilla', '954567123', '2020-01-10'),
(4, 'EST-004', 'E.S. Pirineos Express', 'Calle Mayor 88, Jaca, Huesca', '974567124', '2021-11-05'),
(5, 'EST-005', 'Gasolinera Bahía Sur', 'Polígono Industrial Las Salinas 12, Cádiz', '956567125', '2022-04-01');

INSERT INTO fichas_configuracion (id_ficha_configuracion, id_estacion, codigo_configuracion, serie_tickets, siguiente_numero, pie_justificante) VALUES
(1, 1, 'CONF-EST-001', 'TKT-2026-MAD', 101, 'Gracias por confiar en Central Norte. Conserve este justificante.'),
(2, 2, 'CONF-EST-002', 'TKT-2026-VAL', 205, 'Estación Sol y Playa: Servicio y calidad. ¡Buen viaje!'),
(3, 3, 'CONF-EST-003', 'TKT-2026-SEV', 54, 'Gasolinera Ruta de la Plata. IVA incluido.'),
(4, 4, 'CONF-EST-004', 'TKT-2026-JAC', 89, 'Pirineos Express. Rogamos revisen su cambio y mercancía.'),
(5, 5, 'CONF-EST-005', 'TKT-2026-CAD', 12, 'Bahía Sur: Abierto 24 horas. ¡Vuelva pronto!');

-- -----------------------------------------------------------------------------
-- 2. SURTIDORES
-- Caso: Estación 1 cuenta con varios equipos (3 surtidores).
-- Número visible se repite entre distintas estaciones (surtidor 1 en EST-001 y EST-002).
-- Estado averiado registrado sin desvincular el equipo de la estación.
-- -----------------------------------------------------------------------------
INSERT INTO surtidores (id_surtidor, id_estacion, codigo_equipo, numero_visible, tipo_combustible, estado_servicio) VALUES
(1, 1, 'SURT-E01-01', 1, 'Gasolina 95 E5', 'ACTIVO'),
(2, 1, 'SURT-E01-02', 2, 'Diesel Premium A+', 'ACTIVO'),
(3, 1, 'SURT-E01-03', 3, 'Gasolina 98 E5', 'AVERIADO'),         -- Equipo temporalmente averiado en estación 1
(4, 2, 'SURT-E02-01', 1, 'Gasolina 95 E5', 'ACTIVO'),           -- Mismo número visible (1) en otra estación
(5, 2, 'SURT-E02-02', 2, 'Diesel Normal', 'ACTIVO'),
(6, 3, 'SURT-E03-01', 1, 'Diesel Normal', 'ACTIVO'),
(7, 4, 'SURT-E04-01', 1, 'Gasolina 95 E5', 'EN_MANTENIMIENTO'),
(8, 5, 'SURT-E05-01', 1, 'Diesel Premium A+', 'ACTIVO');

-- -----------------------------------------------------------------------------
-- 3. EMPLEADOS Y SUPERVISIÓN REFLEXIVA
-- Caso:
--   - EMP-001: Director regional (sin supervisor, id_supervisor = NULL).
--   - EMP-002 y EMP-003: Supervisados por EMP-001 (EMP-001 supervisa a varias personas).
--   - EMP-004 y EMP-005: Supervisados por EMP-002 (EMP-002 es supervisor de otros).
--   - EMP-006: Nueva incorporación sin supervisor asignado aún.
-- -----------------------------------------------------------------------------
INSERT INTO empleados (id_empleado, codigo_empleado, dni, nombre, telefono, email, fecha_incorporacion, id_estacion, id_supervisor) VALUES
(1, 'EMP-001', '12345678A', 'Carlos Mendoza Alarcón', '600111222', 'carlos.mendoza@gasolinera.es', '2018-03-15', 1, NULL),
(2, 'EMP-002', '23456789B', 'Beatriz Romero Soler',   '600222333', 'beatriz.romero@gasolinera.es', '2019-01-10', 1, 1),
(3, 'EMP-003', '34567890C', 'David Navarro Sanz',     '600333444', 'david.navarro@gasolinera.es',   '2019-06-01', 2, 1),
(4, 'EMP-004', '45678901D', 'Elena Gil Prieto',       '600444555', 'elena.gil@gasolinera.es',       '2021-02-15', 1, 2),
(5, 'EMP-005', '56789012E', 'Fernando Ortiz Ruiz',    '600555666', 'fernando.ortiz@gasolinera.es',  '2022-09-01', 1, 2),
(6, 'EMP-006', '67890123F', 'Gloria Marín Campos',    '600666777', 'gloria.marin@gasolinera.es',    '2026-11-01', 3, NULL);

-- -----------------------------------------------------------------------------
-- 4. CATÁLOGO DE FUNCIONES Y ASIGNACIONES TEMPORALES
-- Caso:
--   - EMP-002: Desempeña dos funciones simultáneas vigentes (Cajero y Expendedor).
--   - EMP-003: Ejerció de Cajero (periodo finalizado) y recuperó la función posteriormente con nuevo límite.
--   - EMP-006: Empleado recién incorporado SIN funciones asignadas.
-- -----------------------------------------------------------------------------
INSERT INTO funciones (id_funcion, codigo_funcion, nombre, descripcion) VALUES
(1, 'CAJERO', 'Cajero de Tienda', 'Atención en caja y cobro con límite autorizado de devoluciones'),
(2, 'EXPENDEDOR', 'Expendedor de Pista', 'Manipulación y repostaje de combustibles con acreditación oficial'),
(3, 'RESPONSABLE_TIENDA', 'Responsable de Tienda', 'Gestión de mercancías y stock con presupuesto autorizado de compras'),
(4, 'JEFE_ESTACION', 'Jefe de Estación', 'Gestión integral y operaciones excepcionales con nivel de autorización');

INSERT INTO asignaciones_funciones (id_asignacion_funcion, id_empleado, id_funcion, fecha_inicio, fecha_fin, limite_devolucion, num_acreditacion_combustible, fecha_caducidad_acreditacion, presupuesto_compras, nivel_autorizacion) VALUES
-- EMP-001: Jefe de estación
(1, 1, 4, '2018-03-15', NULL, NULL, NULL, NULL, NULL, 5),

-- EMP-002: Dos funciones simultáneas activas (Cajero y Expendedor)
(2, 2, 1, '2024-01-01', NULL, 150.00, NULL, NULL, NULL, NULL),
(3, 2, 2, '2024-01-01', NULL, NULL, 'ACR-ESP-2024-88', '2028-12-31', NULL, NULL),

-- EMP-003: Historial con recuperación de función (Cajero finalizado en 2023, recuperado en 2024 con nuevo límite)
(4, 3, 1, '2021-01-01', '2023-12-31', 80.00, NULL, NULL, NULL, NULL),
(5, 3, 1, '2024-06-01', NULL, 200.00, NULL, NULL, NULL, NULL),

-- EMP-004: Responsable de tienda
(6, 4, 3, '2023-01-15', NULL, NULL, NULL, NULL, 5000.00, NULL),

-- EMP-005: Expendedor
(7, 5, 2, '2022-09-01', NULL, NULL, 'ACR-ESP-2022-12', '2026-08-31', NULL, NULL);
-- EMP-006 no tiene inserciones: permanece temporalmente sin funciones.

-- -----------------------------------------------------------------------------
-- 5. CLIENTES Y TARJETAS DE FIDELIZACIÓN (Relación 1:1 opcional)
-- Caso:
--   - CLI-001, CLI-002, CLI-005: Clientes con tarjeta de fidelización asignada.
--   - CLI-003, CLI-004, CLI-006: Clientes registrados SIN tarjeta.
--   - TARJ-004 y TARJ-005: Tarjetas emitidas en stock SIN cliente asignado.
-- -----------------------------------------------------------------------------
INSERT INTO clientes (id_cliente, codigo_cliente, nombre, telefono, email, matricula, fecha_registro) VALUES
(1, 'CLI-001', 'Antonio Banderas Cruz', '611222333', 'antonio.b@correo.es', '1234-XYZ', '2022-01-15'),
(2, 'CLI-002', 'Carmen Maura Pol',      '622333444', 'carmen.m@correo.es',  '5678-ABC', '2022-05-20'),
(3, 'CLI-003', 'Javier Bardem Encinas',  '633444555', NULL,                  '9012-DEF', '2023-03-10'),
(4, 'CLI-004', 'Penélope Cruz Sánchez',  NULL,        'penelope.c@correo.es','3456-GHI', '2023-08-12'),
(5, 'CLI-005', 'Santiago Segura Silva',  '655666777', 'santiago.s@correo.es','7890-JKL', '2024-02-01'),
(6, 'CLI-006', 'Maribel Verdú Rollán',   '666777888', NULL,                  '2345-MNO', '2024-07-15');

INSERT INTO tarjetas_fidelizacion (id_tarjeta, numero_tarjeta, id_cliente, fecha_emision, fecha_asignacion, puntos, estado) VALUES
(1, 'TARJ-FID-10001', 1,    '2022-01-01', '2022-01-15', 350, 'ASIGNADA'),
(2, 'TARJ-FID-10002', 2,    '2022-01-01', '2022-05-20', 120, 'ASIGNADA'),
(3, 'TARJ-FID-10003', 5,    '2022-01-01', '2024-02-01', 50,  'ASIGNADA'),
(4, 'TARJ-FID-10004', NULL, '2023-06-01', NULL,         0,   'EMITIDA'),  -- Stock sin titular
(5, 'TARJ-FID-10005', NULL, '2023-06-01', NULL,         0,   'EMITIDA');  -- Stock sin titular

-- -----------------------------------------------------------------------------
-- 6. CATÁLOGO DE PRODUCTOS (ENVASADOS Y GRANEL)
-- Caso:
--   - Envasados: formato_envase informado, precisión_pesada nulo.
--   - Granel: precision_pesada informada (decimales: 2 o 3), formato_envase nulo.
-- -----------------------------------------------------------------------------
INSERT INTO productos (id_producto, referencia_producto, descripcion, precio_venta, estado_comercializacion, modalidad, formato_envase, precision_pesada) VALUES
(1, 'PROD-ENV-001', 'Aceite Motor Sintético 5W30', 29.95, 'ACTIVO',   'ENVASADO', 'Garrafa 5 Litros', NULL),
(2, 'PROD-ENV-002', 'Líquido Limpiaparabrisas Anticongelante', 6.50, 'ACTIVO', 'ENVASADO', 'Botella 2 Litros', NULL),
(3, 'PROD-ENV-003', 'Bebida Energética 500ml', 2.20, 'ACTIVO',       'ENVASADO', 'Lata 500 ml', NULL),
(4, 'PROD-GRA-001', 'Almendras Tostadas Dispensador', 14.50, 'ACTIVO', 'GRANEL',   NULL, 3), -- Precisión 3 decimales (Kg)
(5, 'PROD-GRA-002', 'Mezcla Frutos Secos Natural', 12.00, 'ACTIVO',    'GRANEL',   NULL, 3), -- Precisión 3 decimales (Kg)
(6, 'PROD-GRA-003', 'Gominolas Variadas Tienda', 9.80, 'ACTIVO',       'GRANEL',   NULL, 2); -- Precisión 2 decimales (Kg)

-- -----------------------------------------------------------------------------
-- 7. PROVEEDORES Y ACUERDOS DE SUMINISTRO (Relación N:M)
-- Caso:
--   - Proveedor PROV-001 suministra PROD-001 y PROD-002.
--   - Producto PROD-001 es suministrado por PROV-001 y PROV-002 (múltiples proveedores para un producto).
--   - Ningún proveedor ni producto queda aislado (todos con al menos 1 acuerdo).
-- -----------------------------------------------------------------------------
INSERT INTO proveedores (id_proveedor, codigo_proveedor, cif, razon_social, telefono, email, direccion) VALUES
(1, 'PRV-001', 'A11223344', 'Lubricantes y Químicos del Sur S.A.', '918881122', 'pedidos@lubrisur.es', 'Pol. Ind. Cobo Calleja 14, Fuenlabrada'),
(2, 'PRV-002', 'B22334455', 'Distribuciones AutoRepuestos Iberia S.L.', '934442233', 'ventas@autorepuestos.com', 'Carrer de la Industria 8, Martorell'),
(3, 'PRV-003', 'B33445566', 'Bebidas y Aperitivos Global S.L.', '961113344', 'contacto@aperitivosglobal.es', 'Avda. Real Monasterio 3, Paterna'),
(4, 'PRV-004', 'A44556677', 'Frutos Secos y Aperitivos del Valle S.A.', '953334455', 'comercial@frutosdelvalle.com', 'Pol. La Torrecilla, Córdoba'),
(5, 'PRV-005', 'B55667788', 'Confiterías Artesanas y Dulces S.L.', '985556677', 'ventas@confiteriasartesanas.es', 'Calle Carbón 4, Gijón');

INSERT INTO acuerdos_suministro (id_acuerdo, id_proveedor, id_producto, referencia_proveedor, precio_compra, plazo_entrega_dias, vigente) VALUES
-- PROD-ENV-001 suministrado por PROV-001 y PROV-002 (múltiples proveedores para un producto)
(1, 1, 1, 'REF-LUB-5W30', 18.50, 3, TRUE),
(2, 2, 1, 'REP-ACE-01',   19.20, 2, TRUE),

-- PROD-ENV-002 suministrado por PROV-001
(3, 1, 2, 'REF-LUB-LIMP', 3.80, 2, TRUE),

-- PROD-ENV-003 suministrado por PROV-003
(4, 3, 3, 'BEB-ENERG-500', 1.10, 1, TRUE),

-- PROD-GRA-001 suministrado por PROV-004
(5, 4, 4, 'FS-ALM-GRA', 8.50, 4, TRUE),

-- PROD-GRA-002 suministrado por PROV-003 y PROV-004 (PROV-003 suministra varios productos)
(6, 3, 5, 'AP-MEZ-GRAN', 7.20, 2, TRUE),
(7, 4, 5, 'FS-MEZ-002',  7.00, 3, TRUE),

-- PROD-GRA-003 suministrado por PROV-005
(8, 5, 6, 'CONF-GOM-VAR', 5.30, 3, TRUE);

-- -----------------------------------------------------------------------------
-- 8. CAMPAÑAS PROMOCIONALES E INSCRIPCIONES
-- Caso:
--   - CAMP-005: Campaña sin clientes inscritos todavía.
--   - CLI-004: Cliente registrado sin ninguna campaña inscrita.
--   - Participaciones múltiples: CLI-001 inscrito en CAMP-001 y CAMP-002.
-- -----------------------------------------------------------------------------
INSERT INTO campanas (id_campana, codigo_campana, nombre, descripcion, fecha_inicio, fecha_fin) VALUES
(1, 'CAMP-VER-2026', 'Operación Salida de Verano', 'Promoción especial en lubricantes y refrigerantes', '2026-06-15', '2026-08-31'),
(2, 'CAMP-FID-2026', 'Doble Puntuación Fidelidad', 'El doble de puntos en compras de tienda', '2026-01-01', '2026-12-31'),
(3, 'CAMP-OTO-2026', 'Puesta a Punto Otoño', 'Revisión y líquidos con descuento', '2026-09-01', '2026-10-31'),
(4, 'CAMP-NAVIDAD',   'Especial Snacks Navideños', 'Descuento en frutos secos y dulces', '2026-12-01', '2027-01-06'),
(5, 'CAMP-PRIMAVERA', 'Bienvenida Primavera 2027', 'Campaña futura sin inscritos', '2027-03-21', '2027-04-30');

INSERT INTO inscripciones_campanas (id_inscripcion_campana, id_campana, id_cliente, fecha_inscripcion) VALUES
(1, 1, 1, '2026-06-16'),
(2, 2, 1, '2026-01-05'),  -- Cliente 1 en dos campañas
(3, 1, 2, '2026-06-20'),  -- Campaña 1 con múltiples clientes
(4, 2, 3, '2026-02-10'),
(5, 3, 5, '2026-09-05');
-- CAMP-005 queda sin inscripciones. CLI-004 queda sin campañas.

-- -----------------------------------------------------------------------------
-- 9. VENTAS, LÍNEAS DE VENTA (>10 LÍNEAS) Y PAGOS
-- Casos:
--   - Ventas identificadas (con cliente) y ventas anónimas (id_cliente = NULL).
--   - Tickets con múltiples líneas que combinan envasados (unidades) y granel (decimales).
--   - Venta 1 cobrada mediante pago mixto (parte en efectivo y parte en tarjeta).
-- -----------------------------------------------------------------------------
INSERT INTO ventas (id_venta, referencia_ticket, id_estacion, id_empleado, id_cliente, fecha_hora, importe_total, estado_pago) VALUES
(1, 'TKT-2026-MAD-000101', 1, 2, 1,    '2026-11-10 10:15:00', 36.45, 'PAGADA'),  -- Identificada
(2, 'TKT-2026-MAD-000102', 1, 2, NULL, '2026-11-10 11:30:00', 10.90, 'PAGADA'),  -- Anónima
(3, 'TKT-2026-VAL-000205', 2, 3, 2,    '2026-11-12 16:45:00', 35.75, 'PAGADA'),  -- Identificada
(4, 'TKT-2026-SEV-000054', 3, 3, NULL, '2026-11-15 09:20:00', 17.40, 'PAGADA'),  -- Anónima
(5, 'TKT-2026-MAD-000103', 1, 4, 5,    '2026-11-18 18:00:00', 44.45, 'PAGADA');  -- Identificada

INSERT INTO lineas_venta (id_linea_venta, id_venta, numero_orden, id_producto, cantidad, precio_unitario_aplicado, importe) VALUES
-- Ticket 1 (id_venta 1, total 36.45):
(1,  1, 1, 1, 1.000, 29.95, 29.95), -- Aceite 5W30
(2,  1, 2, 2, 1.000,  6.50,  6.50), -- Limpiaparabrisas

-- Ticket 2 (id_venta 2, total 10.90):
(3,  2, 1, 3, 2.000,  2.20,  4.40), -- 2 latas bebida
(4,  2, 2, 6, 0.663,  9.80,  6.50), -- Gominolas a granel (0.663 kg x 9.80 = 6.50)

-- Ticket 3 (id_venta 3, total 35.75):
(5,  3, 1, 2, 1.000,  6.50,  6.50), -- Limpiaparabrisas
(6,  3, 2, 5, 1.500, 12.00, 18.00), -- 1.500 Kg Frutos Secos Natural
(7,  3, 3, 6, 1.148,  9.80, 11.25), -- 1.148 Kg Gominolas

-- Ticket 4 (id_venta 4, total 17.40):
(8,  4, 1, 3, 3.000,  2.20,  6.60), -- 3 latas bebida
(9,  4, 2, 4, 0.745, 14.50, 10.80), -- 0.745 Kg Almendras a granel

-- Ticket 5 (id_venta 5, total 44.45):
(10, 5, 1, 1, 1.000, 29.95, 29.95), -- Aceite
(11, 5, 2, 3, 2.000,  2.20,  4.40), -- Bebidas
(12, 5, 3, 5, 0.842, 12.00, 10.10); -- Frutos secos granel

-- Pagos correspondientes:
-- Caso: Venta 1 se cobra mediante pago mixto (20.00 EUR en Tarjeta + 16.45 EUR en Efectivo = 36.45 EUR)
INSERT INTO pagos (id_pago, id_venta, medio_pago, importe, fecha_hora, referencia_transaccion) VALUES
(1, 1, 'TARJETA',  20.00, '2026-11-10 10:15:30', 'TPV-MAD-449102'),
(2, 1, 'EFECTIVO', 16.45, '2026-11-10 10:16:00', NULL),
(3, 2, 'EFECTIVO', 10.90, '2026-11-10 11:30:45', NULL),
(4, 3, 'TARJETA',  35.75, '2026-11-12 16:46:10', 'TPV-VAL-882194'),
(5, 4, 'EFECTIVO', 17.40, '2026-11-15 09:21:05', NULL),
(6, 5, 'TARJETA',  44.45, '2026-11-18 18:01:20', 'TPV-MAD-991203');

-- -----------------------------------------------------------------------------
-- 10. DEVOLUCIONES
-- Caso:
--   - Devoluciones parciales sucesivas sobre la misma línea de venta (Línea 8: compradas 3 unidades de bebida).
--   - Devolución 1: 1 unidad devuelta (2.20 EUR).
--   - Devolución 2: 1 unidad adicional devuelta (2.20 EUR).
--   - Cantidad total devuelta: 2 unidades <= 3 compradas. Reintegro total: 4.40 <= 6.60.
--   - Venta original y línea original permanecen intactas.
-- -----------------------------------------------------------------------------
INSERT INTO devoluciones (id_devolucion, id_linea_venta, id_empleado, cantidad, motivo, fecha_hora, importe_reintegrado, apto_para_venta) VALUES
(1, 8, 3, 1.000, 'Cliente compró sabor erróneo por error, bote intacto', '2026-11-15 10:00:00', 2.20, TRUE),
(2, 8, 3, 1.000, 'Lata abollada observada antes de abrir',         '2026-11-15 11:15:00', 2.20, FALSE);

-- -----------------------------------------------------------------------------
-- 11. INCIDENCIAS DE TRABAJO
-- Caso:
--   - INC-001: Incidencia sin asignar (id_empleado_responsable = NULL).
--   - INC-002 e INC-003: Asignadas al mismo empleado (EMP-002 es responsable de varias).
--   - EMP-005: Empleado sin incidencias asignadas.
-- -----------------------------------------------------------------------------
INSERT INTO incidencias (id_incidencia, referencia_incidencia, id_estacion, id_empleado_responsable, descripcion, fecha_hora_apertura, estado) VALUES
(1, 'INC-2026-001', 1, NULL, 'Fuga leve detectada en la manguera de agua del lavadero', '2026-11-10 08:30:00', 'ABIERTA'),
(2, 'INC-2026-002', 1, 2,    'Papel atascado en impresora de tickets del TPV 1',       '2026-11-11 12:00:00', 'EN_PROCESO'),
(3, 'INC-2026-003', 1, 2,    'Fallo de iluminación en poste de precios nocturno',      '2026-11-13 19:45:00', 'ABIERTA'),
(4, 'INC-2026-004', 2, 3,    'Surtidor 3 dispensa con caudal lento',                   '2026-11-14 09:15:00', 'EN_PROCESO'),
(5, 'INC-2026-005', 4, 1,    'Revisión anual obligatoria de extintores completada',    '2026-11-01 10:00:00', 'CERRADA');

COMMIT;
SET AUTOCOMMIT = 1;
