package modelo;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Asociación N:M entre un Proveedor y un Producto que recoge las condiciones de compra.
 */
public class AcuerdoSuministro {
    private Integer idAcuerdo;
    private Proveedor proveedor;
    private Producto producto;
    private String referenciaProveedor;
    private BigDecimal precioCompra;
    private int plazoEntregaDias;
    private boolean vigente;

    public AcuerdoSuministro() {
        this.vigente = true;
    }

    public AcuerdoSuministro(Proveedor proveedor, Producto producto, String referenciaProveedor, BigDecimal precioCompra, int plazoEntregaDias) {
        this(null, proveedor, producto, referenciaProveedor, precioCompra, plazoEntregaDias, true);
    }

    public AcuerdoSuministro(Integer idAcuerdo, Proveedor proveedor, Producto producto, String referenciaProveedor,
                            BigDecimal precioCompra, int plazoEntregaDias, boolean vigente) {
        this.idAcuerdo = idAcuerdo;
        this.proveedor = Objects.requireNonNull(proveedor, "El proveedor no puede ser nulo");
        this.producto = Objects.requireNonNull(producto, "El producto no puede ser nulo");
        this.referenciaProveedor = Objects.requireNonNull(referenciaProveedor, "La referencia del proveedor no puede ser nula");
        setPrecioCompra(precioCompra);
        setPlazoEntregaDias(plazoEntregaDias);
        this.vigente = vigente;

        proveedor.agregarAcuerdo(this);
    }

    public Integer getIdAcuerdo() {
        return idAcuerdo;
    }

    public void setIdAcuerdo(Integer idAcuerdo) {
        this.idAcuerdo = idAcuerdo;
    }

    public Proveedor getProveedor() {
        return proveedor;
    }

    public void setProveedor(Proveedor proveedor) {
        this.proveedor = proveedor;
    }

    public Producto getProducto() {
        return producto;
    }

    public void setProducto(Producto producto) {
        this.producto = producto;
    }

    public String getReferenciaProveedor() {
        return referenciaProveedor;
    }

    public void setReferenciaProveedor(String referenciaProveedor) {
        this.referenciaProveedor = referenciaProveedor;
    }

    public BigDecimal getPrecioCompra() {
        return precioCompra;
    }

    public void setPrecioCompra(BigDecimal precioCompra) {
        Objects.requireNonNull(precioCompra, "El precio de compra no puede ser nulo");
        if (precioCompra.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("El precio de compra debe ser estrictamente positivo: " + precioCompra);
        }
        this.precioCompra = precioCompra;
    }

    public int getPlazoEntregaDias() {
        return plazoEntregaDias;
    }

    public void setPlazoEntregaDias(int plazoEntregaDias) {
        if (plazoEntregaDias < 0) {
            throw new IllegalArgumentException("El plazo de entrega no puede ser negativo: " + plazoEntregaDias);
        }
        this.plazoEntregaDias = plazoEntregaDias;
    }

    public boolean isVigente() {
        return vigente;
    }

    public void setVigente(boolean vigente) {
        this.vigente = vigente;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AcuerdoSuministro that = (AcuerdoSuministro) o;
        return Objects.equals(proveedor, that.proveedor) && Objects.equals(producto, that.producto);
    }

    @Override
    public int hashCode() {
        return Objects.hash(proveedor, producto);
    }

    @Override
    public String toString() {
        return "AcuerdoSuministro{" +
                "proveedor=" + proveedor.getCodigoProveedor() +
                ", producto=" + producto.getReferenciaProducto() +
                ", refProv='" + referenciaProveedor + '\'' +
                ", precioCompra=" + precioCompra +
                "€, plazo=" + plazoEntregaDias + "d}";
    }
}
