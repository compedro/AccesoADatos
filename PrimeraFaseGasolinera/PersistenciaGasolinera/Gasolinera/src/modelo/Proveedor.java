package modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

/**
 * Representa a un proveedor de mercancías del catálogo.
 */
public class Proveedor {
    private Integer idProveedor;
    private String codigoProveedor;
    private String cif;
    private String razonSocial;
    private String telefono;
    private String email;
    private String direccion;
    private final List<AcuerdoSuministro> acuerdos = new ArrayList<>();

    public Proveedor() {}

    public Proveedor(String codigoProveedor, String cif, String razonSocial, String telefono) {
        this(null, codigoProveedor, cif, razonSocial, telefono, null, null);
    }

    public Proveedor(Integer idProveedor, String codigoProveedor, String cif, String razonSocial, String telefono, String email, String direccion) {
        this.idProveedor = idProveedor;
        this.codigoProveedor = Objects.requireNonNull(codigoProveedor, "El código de proveedor no puede ser nulo");
        this.cif = Objects.requireNonNull(cif, "El CIF no puede ser nulo");
        this.razonSocial = Objects.requireNonNull(razonSocial, "La razón social no puede ser nula");
        this.telefono = Objects.requireNonNull(telefono, "El teléfono no puede ser nulo");
        this.email = email;
        this.direccion = direccion;
    }

    public void agregarAcuerdo(AcuerdoSuministro acuerdo) {
        Objects.requireNonNull(acuerdo, "El acuerdo no puede ser nulo");
        if (!acuerdos.contains(acuerdo)) {
            this.acuerdos.add(acuerdo);
        }
    }

    public Integer getIdProveedor() {
        return idProveedor;
    }

    public void setIdProveedor(Integer idProveedor) {
        this.idProveedor = idProveedor;
    }

    public String getCodigoProveedor() {
        return codigoProveedor;
    }

    public void setCodigoProveedor(String codigoProveedor) {
        this.codigoProveedor = codigoProveedor;
    }

    public String getCif() {
        return cif;
    }

    public void setCif(String cif) {
        this.cif = cif;
    }

    public String getRazonSocial() {
        return razonSocial;
    }

    public void setRazonSocial(String razonSocial) {
        this.razonSocial = razonSocial;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public List<AcuerdoSuministro> getAcuerdos() {
        return Collections.unmodifiableList(acuerdos);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Proveedor proveedor = (Proveedor) o;
        return Objects.equals(cif, proveedor.cif);
    }

    @Override
    public int hashCode() {
        return Objects.hash(cif);
    }

    @Override
    public String toString() {
        return "Proveedor{" +
                "cif='" + cif + '\'' +
                ", razonSocial='" + razonSocial + '\'' +
                ", acuerdos=" + acuerdos.size() +
                '}';
    }
}
