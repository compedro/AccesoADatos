package modelo;

import java.util.Objects;

/**
 * Catálogo maestro de responsabilidades y tipos de funciones del personal.
 */
public class Funcion {
    private Integer idFuncion;
    private TipoFuncion codigoFuncion;
    private String nombre;
    private String descripcion;

    public Funcion() {}

    public Funcion(TipoFuncion codigoFuncion, String nombre, String descripcion) {
        this(null, codigoFuncion, nombre, descripcion);
    }

    public Funcion(Integer idFuncion, TipoFuncion codigoFuncion, String nombre, String descripcion) {
        this.idFuncion = idFuncion;
        this.codigoFuncion = Objects.requireNonNull(codigoFuncion, "El tipo de función no puede ser nulo");
        this.nombre = Objects.requireNonNull(nombre, "El nombre no puede ser nulo");
        this.descripcion = descripcion;
    }

    public Integer getIdFuncion() {
        return idFuncion;
    }

    public void setIdFuncion(Integer idFuncion) {
        this.idFuncion = idFuncion;
    }

    public TipoFuncion getCodigoFuncion() {
        return codigoFuncion;
    }

    public void setCodigoFuncion(TipoFuncion codigoFuncion) {
        this.codigoFuncion = codigoFuncion;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public String toString() {
        return "Funcion{" + codigoFuncion + " - " + nombre + '}';
    }
}
