package modelo;

import java.math.BigDecimal;

/**
 * Producto comercializado al peso (a granel) por kilogramos.
 * Admite cantidades decimales respetando la precisión de pesada configurada.
 */
public class ProductoGranel extends Producto {
    private int precisionPesada;

    public ProductoGranel() {
        super();
        this.precisionPesada = 3;
    }

    public ProductoGranel(String referenciaProducto, String descripcion, BigDecimal precioVenta, int precisionPesada) {
        this(null, referenciaProducto, descripcion, precioVenta, EstadoComercializacion.ACTIVO, precisionPesada);
    }

    public ProductoGranel(Integer idProducto, String referenciaProducto, String descripcion, BigDecimal precioVenta,
                          EstadoComercializacion estadoComercializacion, int precisionPesada) {
        super(idProducto, referenciaProducto, descripcion, precioVenta, estadoComercializacion);
        setPrecisionPesada(precisionPesada);
    }

    @Override
    public ModalidadProducto getModalidad() {
        return ModalidadProducto.GRANEL;
    }

    @Override
    public void validarCantidad(BigDecimal cantidad) throws IllegalArgumentException {
        if (cantidad == null || cantidad.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser estrictamente positiva: " + cantidad);
        }
        // Verificar que el número de decimales significativos no supere la precisión permitida
        int escalaSignificativa = cantidad.stripTrailingZeros().scale();
        if (escalaSignificativa > this.precisionPesada) {
            throw new IllegalArgumentException(String.format(
                    "La cantidad indicada (%s) excede la precisión de pesada admitida para este producto (%d decimales)",
                    cantidad, this.precisionPesada
            ));
        }
    }

    public int getPrecisionPesada() {
        return precisionPesada;
    }

    public void setPrecisionPesada(int precisionPesada) {
        if (precisionPesada < 0) {
            throw new IllegalArgumentException("La precisión de pesada no puede ser negativa: " + precisionPesada);
        }
        this.precisionPesada = precisionPesada;
    }

    @Override
    public String toString() {
        return super.toString() + " [Precisión: " + precisionPesada + " dec]";
    }
}
