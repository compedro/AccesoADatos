-- =============================================================================
-- PROYECTO: Sistema de Gestión de Gasolinera y Tienda (Fase 1)
-- SCRIPT: 02_triggers.sql
-- DESCRIPCIÓN: Trigger obligatorio para auditoría de cambios de precio en MySQL.
-- COMPORTAMIENTO:
--   - Se dispara AFTER UPDATE en la tabla productos.
--   - Compara OLD.precio_venta <> NEW.precio_venta.
--   - No genera registros ante altas (INSERT) ni modificaciones de otros campos
--     (descripción, estado, formato, etc.) si el precio_venta no varía.
--   - Al ejecutarse sobre tablas InnoDB, garantiza atomicidad: un ROLLBACK
--     revierte tanto la actualización del producto como la fila del histórico.
-- =============================================================================

USE gasolinera;

DROP TRIGGER IF EXISTS trg_auditoria_precio_producto;

DELIMITER $$

CREATE TRIGGER trg_auditoria_precio_producto
AFTER UPDATE ON productos
FOR EACH ROW
BEGIN
    -- Comprobar si ha existido un cambio real en el precio de venta
    IF OLD.precio_venta <> NEW.precio_venta THEN
        INSERT INTO historico_precios (
            id_producto,
            precio_anterior,
            precio_nuevo,
            fecha_modificacion
        ) VALUES (
            NEW.id_producto,
            OLD.precio_venta,
            NEW.precio_venta,
            NOW()
        );
    END IF;
END$$

DELIMITER ;
