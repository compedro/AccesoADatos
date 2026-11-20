package modelo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Producto comercializado por unidades enteras completas (formato de envase específico).
 */
public class ProductoEnvasado extends Producto {
    private String formatoEnvase;

    public ProductoEnvasado() {
        super();
    }

    public ProductoEnvasado(String referenciaProducto, String descripcion, BigDecimal precioVenta, String formatoEnvase) {
        this(null, referenciaProducto, descripcion, precioVenta, EstadoComercializacion.ACTIVO, formatoEnvase);
    }

    public ProductoEnvasado(Integer idProducto, String referenciaProducto, String descripcion, BigDecimal precioVenta,
                            EstadoComercializacion estadoComercializacion, String formatoEnvase) {
        super(idProducto, referenciaProducto, descripcion, precioVenta, estadoComercializacion);
        this.formatoEnvase = Objects.requireNonNull(formatoEnvase, "El formato de envase no puede ser nulo");
    }

    @Override
    public ModalidadProducto getModalidad() {
        return ModalidadProducto.ENVASADO;
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) throws IllegalArgumentException {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser estrictamente positiva: " + cantidad);
        }
        // Verificar que no posea parte fraccionaria (debe ser una unidad entera)
        if (cantidad.stripTrailingZeros().scale() > 0) {
            throw new IllegalArgumentException("Los productos envasados se venden exclusivamente por unidades completas (sin decimales). Cantidad no admitida: " + cantidad);
        }
    }

    public String getFormatoEnvase() {
        return formatoEnvase;
    }

    public void setFormatoEnvase(String formatoEnvase) {
        this.formatoEnvase = Objects.requireNonNull(formatoEnvase, "El formato de envase no puede ser nulo");
    }

    @Override
    public String toString() {
        return super.toString() + " [Envase: " + formatoEnvase + "]";
    }
}
