-- =============================================================================
-- PROYECTO: Sistema de Gestión de Gasolinera y Tienda (Fase 1)
-- SCRIPT: 04_comprobaciones.sql
-- DESCRIPCIÓN: Consultas de verificación del modelo relacional, pruebas completas
--              del trigger de auditoría de precios (incluyendo transaccionalidad
--              con ROLLBACK) y casos de error esperados debidamente documentados.
-- =============================================================================

USE gasolinera;

-- =============================================================================
-- BLOQUE A: CONSULTAS DE VERIFICACIÓN DE REGLAS DE NEGOCIO Y ESTRUCTURA
-- =============================================================================

-- 1. Verificación de Estaciones, Configuración 1:1 y Surtidores
-- Resultado esperado: 5 estaciones, cada una con su ficha y surtidores (EST-001 con 3 surtidores).
SELECT 
    e.codigo_estacion,
    e.nombre_comercial,
    fc.codigo_configuracion,
    fc.serie_tickets,
    COUNT(s.id_surtidor) AS total_surtidores
FROM estaciones e
INNER JOIN fichas_configuracion fc ON e.id_estacion = fc.id_estacion
LEFT JOIN surtidores s ON e.id_estacion = s.id_estacion
GROUP BY e.id_estacion, e.codigo_estacion, e.nombre_comercial, fc.codigo_configuracion, fc.serie_tickets;

-- 2. Verificación de Surtidores con mismo número visible en diferentes estaciones
-- Resultado esperado: Surtidor número 1 existe tanto en EST-001 como en EST-002 sin conflicto.
SELECT 
    s.id_surtidor,
    e.codigo_estacion,
    s.codigo_equipo,
    s.numero_visible,
    s.tipo_combustible,
    s.estado_servicio
FROM surtidores s
INNER JOIN estaciones e ON s.id_estacion = e.id_estacion
WHERE s.numero_visible = 1;

-- 3. Verificación de Personal y Jerarquía Reflexiva de Supervisión
-- Resultado esperado:
--   - EMP-001 sin supervisor (Dirección).
--   - EMP-002 y EMP-003 supervisados por EMP-001.
--   - EMP-004 y EMP-005 supervisados por EMP-002.
--   - EMP-006 sin supervisor asignado.
SELECT 
    emp.codigo_empleado,
    emp.nombre AS empleado,
    COALESCE(sup.nombre, '--> Sin supervisor (Dirección / Libre)') AS supervisor_directo
FROM empleados emp
LEFT JOIN empleados sup ON emp.id_supervisor = sup.id_empleado
ORDER BY emp.id_empleado;

-- 4. Verificación de Historial de Funciones del Personal
-- Resultado esperado:
--   - EMP-002 tiene 2 funciones simultáneas activas (Cajero y Expendedor).
--   - EMP-003 ejerció de Cajero (finalizado) y recuperó la función con nuevas condiciones.
--   - EMP-006 aparece sin funciones asignadas.
SELECT 
    emp.codigo_empleado,
    emp.nombre AS empleado,
    f.codigo_funcion,
    af.fecha_inicio,
    af.fecha_fin,
    CASE 
        WHEN af.fecha_fin IS NULL THEN 'ACTIVA'
        ELSE 'FINALIZADA'
    END AS estado_asignacion,
    af.limite_devolucion,
    af.num_acreditacion_combustible,
    af.presupuesto_compras,
    af.nivel_autorizacion
FROM empleados emp
LEFT JOIN asignaciones_funciones af ON emp.id_empleado = af.id_empleado
LEFT JOIN funciones f ON af.id_funcion = f.id_funcion
ORDER BY emp.id_empleado, af.fecha_inicio;

-- 5. Verificación de Clientes y Tarjetas de Fidelización (Relación 1:1 Opcional)
-- Resultado esperado:
--   - Clientes con tarjeta (CLI-001, CLI-002, CLI-005).
--   - Clientes sin tarjeta (CLI-003, CLI-004, CLI-006).
--   - Tarjetas en stock sin titular (TARJ-FID-10004, TARJ-FID-10005).
SELECT 
    c.codigo_cliente,
    c.nombre AS cliente,
    tf.numero_tarjeta,
    tf.puntos,
    tf.estado AS estado_tarjeta
FROM clientes c
LEFT JOIN tarjetas_fidelizacion tf ON c.id_cliente = tf.id_cliente
UNION
SELECT 
    '-- Sin cliente asignado --' AS codigo_cliente,
    '-- En Stock --' AS cliente,
    tf.numero_tarjeta,
    tf.puntos,
    tf.estado AS estado_tarjeta
FROM tarjetas_fidelizacion tf
WHERE tf.id_cliente IS NULL;

-- 6. Verificación de Productos de Ambas Modalidades
-- Resultado esperado:
--   - Envasados con formato_envase y sin precisión de pesada.
--   - Granel con precisión de pesada y sin formato de envase.
SELECT 
    referencia_producto,
    descripcion,
    precio_venta,
    modalidad,
    formato_envase,
    precision_pesada
FROM productos
ORDER BY modalidad, id_producto;

-- 7. Verificación de Proveedores y Acuerdos N:M (Sin aislados)
-- Resultado esperado: Todos los proveedores y productos participan en al menos un acuerdo.
SELECT 
    p.razon_social AS proveedor,
    pr.referencia_producto,
    pr.descripcion AS producto,
    a.referencia_proveedor,
    a.precio_compra,
    a.plazo_entrega_dias
FROM acuerdos_suministro a
INNER JOIN proveedores p ON a.id_proveedor = p.id_proveedor
INNER JOIN productos pr ON a.id_producto = pr.id_producto
ORDER BY p.id_proveedor, pr.id_producto;

-- 8. Verificación de Campañas e Inscripciones
-- Resultado esperado:
--   - Campaña 5 sin inscritos.
--   - Cliente 4 sin campañas.
--   - Cliente 1 en múltiples campañas (1 y 2).
SELECT 
    c.codigo_campana,
    c.nombre AS campana,
    COUNT(ic.id_cliente) AS total_inscritos
FROM campanas c
LEFT JOIN inscripciones_campanas ic ON c.id_campana = ic.id_campana
GROUP BY c.id_campana, c.codigo_campana, c.nombre;

-- 9. Verificación de Ventas, Líneas y Pagos (Incluyendo Pagos Mixtos)
-- Resultado esperado:
--   - Venta 1 con 2 pagos (Tarjeta + Efectivo) que suman exactamente el importe total (36.45).
--   - Ventas anónimas e identificadas con sus líneas.
SELECT 
    v.referencia_ticket,
    COALESCE(c.nombre, 'ANÓNIMO') AS cliente,
    v.fecha_hora,
    v.importe_total AS importe_ticket,
    COALESCE(SUM(p.importe), 0.00) AS total_pagado,
    GROUP_CONCAT(CONCAT(p.medio_pago, ': ', p.importe, '€') SEPARATOR ' | ') AS desglose_pagos
FROM ventas v
LEFT JOIN clientes c ON v.id_cliente = c.id_cliente
LEFT JOIN pagos p ON v.id_venta = p.id_venta
GROUP BY v.id_venta, v.referencia_ticket, c.nombre, v.fecha_hora, v.importe_total;

-- 10. Verificación de Devoluciones Parciales Sucesivas
-- Resultado esperado:
--   - Línea 8 tenía 3 unidades compradas a 2.20€ (6.60€).
--   - Dos devoluciones de 1 unidad cada una (2.20€ c/u).
--   - Total devuelto: 2 unidades (4.40€) <= 3 unidades (6.60€). La venta original permanece intacta.
SELECT 
    v.referencia_ticket,
    lv.numero_orden,
    pr.descripcion AS producto,
    lv.cantidad AS cantidad_comprada,
    lv.precio_unitario_aplicado,
    d.id_devolucion,
    d.cantidad AS cantidad_devuelta,
    d.importe_reintegrado,
    d.motivo,
    d.apto_para_venta
FROM lineas_venta lv
INNER JOIN ventas v ON lv.id_venta = v.id_venta
INNER JOIN productos pr ON lv.id_producto = pr.id_producto
INNER JOIN devoluciones d ON lv.id_linea_venta = d.id_linea_venta
WHERE lv.id_linea_venta = 8;

-- 11. Verificación de Incidencias Operativas
-- Resultado esperado: INC-001 sin asignar, INC-002 e INC-003 asignadas a EMP-002.
SELECT 
    inc.referencia_incidencia,
    e.nombre_comercial AS estacion,
    COALESCE(emp.nombre, '--> Sin responsable asignado') AS responsable,
    inc.descripcion,
    inc.estado
FROM incidencias inc
INNER JOIN estaciones e ON inc.id_estacion = e.id_estacion
LEFT JOIN empleados emp ON inc.id_empleado_responsable = emp.id_empleado;


-- =============================================================================
-- BLOQUE B: PRUEBAS EXHAUSTIVAS DEL TRIGGER DE AUDITORÍA DE PRECIOS
-- =============================================================================

-- Paso B.1: Estado inicial del producto 1 y del histórico
SELECT id_producto, referencia_producto, precio_venta, descripcion FROM productos WHERE id_producto = 1;
SELECT * FROM historico_precios WHERE id_producto = 1; -- Esperado: Vacío

-- Paso B.2: Modificación de descripción SIN cambio de precio
-- Resultado esperado: El trigger NO debe registrar ninguna entrada en historico_precios.
UPDATE productos 
SET descripcion = 'Aceite Motor Sintético 5W30 Fórmula Premium' 
WHERE id_producto = 1;

SELECT * FROM historico_precios WHERE id_producto = 1; -- Esperado: Sigue vacío

-- Paso B.3: Actualización al MISMO precio de venta (29.95)
-- Resultado esperado: El trigger NO debe registrar ninguna entrada.
UPDATE productos 
SET precio_venta = 29.95 
WHERE id_producto = 1;

SELECT * FROM historico_precios WHERE id_producto = 1; -- Esperado: Sigue vacío

-- Paso B.4: CAMBIO REAL de precio (de 29.95 a 32.50)
-- Resultado esperado: El trigger genera 1 registro (anterior: 29.95, nuevo: 32.50).
UPDATE productos 
SET precio_venta = 32.50 
WHERE id_producto = 1;

SELECT * FROM historico_precios WHERE id_producto = 1; -- Esperado: 1 registro generado

-- Paso B.5: Comprobación de que las ventas anteriores conservan el precio cobrado intacto
-- Resultado esperado: La línea 1 de la venta 1 conserva precio_unitario_aplicado = 29.95 a pesar de que el catálogo vale 32.50.
SELECT 
    v.referencia_ticket,
    lv.numero_orden,
    pr.descripcion,
    lv.precio_unitario_aplicado AS precio_cobrado_historico,
    pr.precio_venta AS precio_vigente_catalogo
FROM lineas_venta lv
INNER JOIN ventas v ON lv.id_venta = v.id_venta
INNER JOIN productos pr ON lv.id_producto = pr.id_producto
WHERE lv.id_producto = 1;

-- Paso B.6: PRUEBA DE ATOMICIDAD Y ROLLBACK CON INNODB
-- Escenario: Se modifica el precio dentro de una transacción que luego se aborta.
-- Resultado esperado: Tanto el precio del producto como el registro del histórico deben revertirse.
START TRANSACTION;

-- Cambiamos el precio a 45.00 dentro de la transacción
UPDATE productos 
SET precio_venta = 45.00 
WHERE id_producto = 1;

-- Comprobación dentro de la transacción (debe verse el precio 45.00 y 2 registros en el histórico)
SELECT precio_venta AS precio_en_transaccion FROM productos WHERE id_producto = 1;
SELECT COUNT(*) AS total_historico_en_transaccion FROM historico_precios WHERE id_producto = 1;

-- Deshacemos la transacción
ROLLBACK;

-- Comprobación tras ROLLBACK:
-- Esperado: precio_venta vuelve a 32.50 y el histórico vuelve a tener exactamente 1 registro.
SELECT precio_venta AS precio_tras_rollback FROM productos WHERE id_producto = 1;
SELECT * FROM historico_precios WHERE id_producto = 1;


-- =============================================================================
-- BLOQUE C: PRUEBAS DE RESTRICCIONES Y ERRORES PROVOCADOS DELIBERADAMENTE
-- (Se encuentran comentadas para no abortar la ejecución secuencial de scripts)
-- =============================================================================

/*
-- ERROR ESPERADO 1: Intento de insertar código de estación duplicado
-- Causa: Violación de la restricción uq_estaciones_codigo
INSERT INTO estaciones (codigo_estacion, nombre_comercial, direccion, telefono, fecha_apertura)
VALUES ('EST-001', 'Estación Duplicada', 'Calle Falsa 123', '900000000', '2026-01-01');
-- Mensaje MySQL esperado: Error Code 1062: Duplicate entry 'EST-001' for key 'estaciones.uq_estaciones_codigo'

-- ERROR ESPERADO 2: Intento de asignar una segunda tarjeta a un cliente que ya tiene una
-- Causa: Violación de la restricción uq_tarjetas_id_cliente (Relación 1:1 opcional)
INSERT INTO tarjetas_fidelizacion (numero_tarjeta, id_cliente, fecha_emision, fecha_asignacion, puntos, estado)
VALUES ('TARJ-FID-DUPLICADA', 1, '2026-01-01', '2026-01-02', 0, 'ASIGNADA');
-- Mensaje MySQL esperado: Error Code 1062: Duplicate entry '1' for key 'tarjetas_fidelizacion.uq_tarjetas_id_cliente'

-- ERROR ESPERADO 3: Intento de duplicar número visible de surtidor en la misma estación
-- Causa: Violación de la restricción uq_surtidores_estacion_numero
INSERT INTO surtidores (id_estacion, codigo_equipo, numero_visible, tipo_combustible, estado_servicio)
VALUES (1, 'SURT-NUEVO-DUP', 1, 'Gasolina 95 E5', 'ACTIVO');
-- Mensaje MySQL esperado: Error Code 1062: Duplicate entry '1-1' for key 'surtidores.uq_surtidores_estacion_numero'

-- ERROR ESPERADO 4: Intento de insertar producto granel sin informar precisión de pesada
-- Causa: Violación del CHECK chk_productos_modalidad_datos
INSERT INTO productos (referencia_producto, descripcion, precio_venta, estado_comercializacion, modalidad, formato_envase, precision_pesada)
VALUES ('PROD-ERROR-GRA', 'Cacahuetes a granel sin precisión', 5.00, 'ACTIVO', 'GRANEL', NULL, NULL);
-- Mensaje MySQL esperado: Error Code 3819: Check constraint 'chk_productos_modalidad_datos' is violated.

-- ERROR ESPERADO 5: Intento de auto-supervisión de un empleado sobre sí mismo
-- Causa: Violación del CHECK chk_empleados_no_autosupervision
UPDATE empleados SET id_supervisor = id_empleado WHERE id_empleado = 1;
-- Mensaje MySQL esperado: Error Code 3819: Check constraint 'chk_empleados_no_autosupervision' is violated.

-- ERROR ESPERADO 6: Intento de insertar fecha_fin anterior a fecha_inicio en campaña
-- Causa: Violación del CHECK chk_campanas_fechas
INSERT INTO campanas (codigo_campana, nombre, fecha_inicio, fecha_fin)
VALUES ('CAMP-ERR', 'Campaña con fechas invertidas', '2026-12-31', '2026-01-01');
-- Mensaje MySQL esperado: Error Code 3819: Check constraint 'chk_campanas_fechas' is violated.

-- ERROR ESPERADO 7: Intento de registrar precio de venta negativo o cero
-- Causa: Violación del CHECK chk_productos_precio
INSERT INTO productos (referencia_producto, descripcion, precio_venta, estado_comercializacion, modalidad, formato_envase)
VALUES ('PROD-NEG', 'Producto Precio Negativo', -2.50, 'ACTIVO', 'ENVASADO', 'Bote 1L');
-- Mensaje MySQL esperado: Error Code 3819: Check constraint 'chk_productos_precio' is violated.
*/
