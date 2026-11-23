package cl.duoc.nutriexpert.predios.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/** Un predio (campo) de un agricultor. Tabla "predios" de la base nutri_predios. */
@Entity
@Table(name = "predios")
public class Predio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id que el predio tiene en la base SQLite (Room) de la app. Sirve para no duplicar al sincronizar. */
    @Column(unique = true)
    private Integer idLocal;

    @Column(nullable = false, length = 120)
    private String nombre;

    @Column(nullable = false)
    private Double superficieHa;

    @Column(nullable = false, updatable = false)
    private LocalDateTime creadoEn = LocalDateTime.now();

    public Predio() {}

    public Predio(Integer idLocal, String nombre, Double superficieHa) {
        this.idLocal = idLocal;
        this.nombre = nombre;
        this.superficieHa = superficieHa;
    }

    public Long getId() { return id; }
    public Integer getIdLocal() { return idLocal; }
    public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public Double getSuperficieHa() { return superficieHa; }
    public void setSuperficieHa(Double superficieHa) { this.superficieHa = superficieHa; }
    public LocalDateTime getCreadoEn() { return creadoEn; }
}
