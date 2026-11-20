package modelo;

import java.time.LocalDate;
import java.util.Objects;

/**
 * Representa a un cliente registrado en la tienda y estación de servicio.
 */
public class Cliente {
    private Integer idCliente;
    private String codigoCliente;
    private String nombre;
    private String telefono;
    private String email;
    private String matricula;
    private LocalDate fechaRegistro;
    private TarjetaFidelizacion tarjetaFidelizacion;

    public Cliente() {}

    public Cliente(String codigoCliente, String nombre, LocalDate fechaRegistro) {
        this(null, codigoCliente, nombre, null, null, null, fechaRegistro);
    }

    public Cliente(Integer idCliente, String codigoCliente, String nombre, LocalDate fechaRegistro) {
        this(idCliente, codigoCliente, nombre, null, null, null, fechaRegistro);
    }

    public Cliente(Integer idCliente, String codigoCliente, String nombre, String telefono, String email, String matricula, LocalDate fechaRegistro) {
        this.idCliente = idCliente;
        this.codigoCliente = Objects.requireNonNull(codigoCliente, "El código de cliente no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.telefono = telefono;
        this.email = email;
        this.matricula = matricula;
        this.fechaRegistro = Objects.requireNonNull(fechaRegistro, "La fecha de registro no puede ser nula");
    }

    public void asignarTarjeta(TarjetaFidelizacion tarjeta) {
        if (this.tarjetaFidelizacion != null && !this.tarjetaFidelizacion.equals(tarjeta)) {
            throw new IllegalStateException("El cliente ya tiene asignada una tarjeta de fidelización activa");
        }
        this.tarjetaFidelizacion = tarjeta;
    }

    public Integer getIdCliente() {
        return idCliente;
    }

    public void setIdCliente(Integer idCliente) {
        this.idCliente = idCliente;
    }

    public String getCodigoCliente() {
        return codigoCliente;
    }

    public void setCodigoCliente(String codigoCliente) {
        this.codigoCliente = codigoCliente;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
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

    public String getMatricula() {
        return matricula;
    }

    public void setMatricula(String matricula) {
        this.matricula = matricula;
    }

    public LocalDate getFechaRegistro() {
        return fechaRegistro;
    }

    public void setFechaRegistro(LocalDate fechaRegistro) {
        this.fechaRegistro = fechaRegistro;
    }

    public TarjetaFidelizacion getTarjetaFidelizacion() {
        return tarjetaFidelizacion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Cliente cliente = (Cliente) o;
        return Objects.equals(codigoCliente, cliente.codigoCliente);
    }

    @Override
    public int hashCode() {
        return Objects.hash(codigoCliente);
    }

    @Override
    public String toString() {
        return "Cliente{" +
                "codigo='" + codigoCliente + '\'' +
                ", nombre='" + nombre + '\'' +
                ", tarjeta=" + (tarjetaFidelizacion != null ? tarjetaFidelizacion.getNumeroTarjeta() : "ninguna") +
                '}';
    }
}
