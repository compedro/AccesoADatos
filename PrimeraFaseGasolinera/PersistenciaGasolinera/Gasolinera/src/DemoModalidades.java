import modelo.*;
import modelo.Cliente;
import modelo.Empleado;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Demostración de validación de cantidades y cálculo polimórfico de importes
 * para las dos modalidades de producto (Envasado vs Granel) sin tocar base de datos.
 */
public class DemoModalidades {

    public static void main(String[] args) {
        System.out.println("======================================================================");
        System.out.println(" DEMOSTRACIÓN: MODALIDADES DE PRODUCTO Y POLIMORFISMO DE DOMINIO");
        System.out.println("======================================================================\n");

        // 1. Instanciación de productos del catálogo
        ProductoEnvasado aceite = new ProductoEnvasado(
                1, "PROD-ENV-001", "Aceite Motor Sintético 5W30",
                new BigDecimal("29.95"), EstadoComercializacion.ACTIVO, "Garrafa 5 Litros"
        );

        ProductoEnvasado bebida = new ProductoEnvasado(
                2, "PROD-ENV-003", "Bebida Energética 500ml",
                new BigDecimal("2.20"), EstadoComercializacion.ACTIVO, "Lata 500 ml"
        );

        ProductoGranel almendras = new ProductoGranel(
                3, "PROD-GRA-001", "Almendras Tostadas Dispensador",
                new BigDecimal("14.50"), EstadoComercializacion.ACTIVO, 3 // Precisión de 3 decimales
        );

        ProductoGranel gominolas = new ProductoGranel(
                4, "PROD-GRA-003", "Gominolas Variadas Tienda",
                new BigDecimal("9.80"), EstadoComercializacion.ACTIVO, 2 // Precisión de 2 decimales
        );

        // 2. Simulación de un ticket de venta polimórfico
        System.out.println("--- 1. PREPARACIÓN DE TICKET CON TRATAMIENTO POLIMÓRFICO ---");
        Estacion estacion = new Estacion(1, "EST-001", "Central Norte", "Autovía A-1 Km 23", "912345671", LocalDate.now());
        Empleado empleado = new Empleado(1, "EMP-002", "23456789B", "Beatriz Romero", LocalDate.now(), estacion);
        modelo.Cliente cliente = new modelo.Cliente(1, "CLI-001", "Antonio Banderas", LocalDate.now());

        Venta ticket = new Venta("TKT-DEMO-001", estacion, empleado, cliente, LocalDateTime.now());

        // El proceso que prepara el ticket no necesita bifurcar por tipo de producto (if / instanceof)
        agregarLineaSegura(ticket, aceite, new BigDecimal("2"));          // 2 garrafas (entero)
        agregarLineaSegura(ticket, bebida, new BigDecimal("3"));         // 3 latas (entero)
        agregarLineaSegura(ticket, almendras, new BigDecimal("0.448"));  // 0.448 Kg (3 decimales permitidos)
        agregarLineaSegura(ticket, gominolas, new BigDecimal("0.65"));   // 0.65 Kg (2 decimales permitidos)

        System.out.printf("%-6s %-32s %-12s %-10s %-10s %-10s%n", "ORDEN", "DESCRIPCIÓN", "MODALIDAD", "CANTIDAD", "UNITARIO", "IMPORTE");
        System.out.println("---------------------------------------------------------------------------------------");
        for (LineaVenta lv : ticket.getLineas()) {
            System.out.printf("%-6d %-32s %-12s %-10s %-10.2f€ %-10.2f€%n",
                    lv.getNumeroOrden(),
                    lv.getProducto().getDescripcion(),
                    lv.getProducto().getModalidad(),
                    lv.getCantidad(),
                    lv.getPrecioUnitarioAplicado(),
                    lv.getImporte()
            );
        }
        System.out.println("---------------------------------------------------------------------------------------");
        System.out.printf("TOTAL TICKET: %.2f€%n%n", ticket.calcularTotal());

        // 3. Probar pagos mixtos
        System.out.println("--- 2. PRUEBA DE PAGO MIXTO ---");
        ticket.registrarPago(MedioPago.TARJETA, new BigDecimal("50.00"), LocalDateTime.now(), "TPV-123456");
        System.out.println("Pago 1 registrado: 50.00€ con TARJETA.");
        System.out.printf("Saldo pendiente: %.2f€ | ¿Pagada?: %s%n", ticket.getSaldoPendiente(), ticket.estaCompletamentePagada());

        BigDecimal saldoRestante = ticket.getSaldoPendiente();
        ticket.registrarPago(MedioPago.EFECTIVO, saldoRestante, LocalDateTime.now(), null);
        System.out.printf("Pago 2 registrado: %.2f€ en EFECTIVO.%n", saldoRestante);
        System.out.printf("Saldo pendiente: %.2f€ | ¿Pagada?: %s | Estado: %s%n%n",
                ticket.getSaldoPendiente(), ticket.estaCompletamentePagada(), ticket.getEstadoPago());

        // 4. Pruebas de validación y rechazo controlado
        System.out.println("--- 3. PRUEBAS DE CASOS INVÁLIDOS (RECHAZO POR REGLAS DE DOMINIO) ---");

        probarValidacionRechazada("Envasado con cantidad decimal (1.5 unidades de aceite)",
                aceite, new BigDecimal("1.5"));

        probarValidacionRechazada("Envasado con cantidad negativa (-1 unidad de bebida)",
                bebida, new BigDecimal("-1"));

        probarValidacionRechazada("Envasado con cantidad cero (0 unidades de bebida)",
                bebida, BigDecimal.ZERO);

        probarValidacionRechazada("Granel con exceso de decimales (0.1234 Kg en gominolas de máx 2 decimales)",
                gominolas, new BigDecimal("0.1234"));

        probarValidacionRechazada("Granel con cantidad negativa (-0.500 Kg de almendras)",
                almendras, new BigDecimal("-0.500"));

        // 5. Demostración de devoluciones sucesivas respetando límites
        System.out.println("\n--- 4. PRUEBA DE DEVOLUCIÓN PARCIAL SUCESIVA ---");
        LineaVenta lineaBebida = ticket.getLineas().get(1); // 3 latas compradas
        System.out.printf("Línea seleccionada: %s | Cantidad comprada: %s | Importe original: %.2f€%n",
                lineaBebida.getProducto().getDescripcion(), lineaBebida.getCantidad(), lineaBebida.getImporte());

        // Devolución 1: 1 lata
        Devolucion dev1 = lineaBebida.registrarDevolucion(empleado, new BigDecimal("1"), "Cliente equivocó sabor", LocalDateTime.now(), true);
        System.out.printf("Devolución 1 aceptada: %s unid. Reintegro: %.2f€ (Apto venta: %s). Restan disponibles: %s%n",
                dev1.getCantidad(), dev1.getImporteReintegrado(), dev1.isAptoParaVenta(), lineaBebida.getCantidadDisponibleDevolucion());

        // Devolución 2: 1 lata
        Devolucion dev2 = lineaBebida.registrarDevolucion(empleado, new BigDecimal("1"), "Lata golpeada", LocalDateTime.now(), false);
        System.out.printf("Devolución 2 aceptada: %s unid. Reintegro: %.2f€ (Apto venta: %s). Restan disponibles: %s%n",
                dev2.getCantidad(), dev2.getImporteReintegrado(), dev2.isAptoParaVenta(), lineaBebida.getCantidadDisponibleDevolucion());

        // Devolución 3: Intento de devolver 2 latas más (superaría la cantidad comprada de 3)
        System.out.println("Intentando devolver 2 latas adicionales cuando solo queda 1 disponible...");
        try {
            lineaBebida.registrarDevolucion(empleado, new BigDecimal("2"), "Exceso de devolución", LocalDateTime.now(), false);
            System.out.println("ERROR: No debió admitir la devolución.");
        } catch (IllegalArgumentException e) {
            System.out.println("RECHAZADA CORRECTAMENTE: " + e.getMessage());
        }

        System.out.println("\n======================================================================");
        System.out.println(" DEMOSTRACIÓN COMPLETADA CON ÉXITO");
        System.out.println("======================================================================");
    }

    private static void agregarLineaSegura(Venta ticket, Producto producto, BigDecimal cantidad) {
        try {
            ticket.agregarLinea(producto, cantidad);
        } catch (IllegalArgumentException e) {
            System.err.println("Error al agregar línea: " + e.getMessage());
        }
    }

    private static void probarValidacionRechazada(String caso, Producto producto, BigDecimal cantidad) {
        System.out.print("[Caso] " + caso + " -> ");
        try {
            producto.validarCantidad(cantidad);
            System.out.println("FALLO: La validación no detectó el error.");
        } catch (IllegalArgumentException e) {
            System.out.println("CORRECTAMENTE RECHAZADO: \"" + e.getMessage() + "\"");
        }
    }
}
