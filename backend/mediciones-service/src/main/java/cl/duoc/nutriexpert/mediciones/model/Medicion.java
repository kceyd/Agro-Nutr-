package cl.duoc.nutriexpert.mediciones.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

/**
 * Medicion de suelo/follaje hecha en terreno. Tabla "mediciones" de la base nutri_mediciones.
 * predioId es solo un numero (el idLocal del predio): este servicio NO consulta al de predios,
 * asi cada microservicio queda completamente independiente.
 */
@Entity
@Table(name = "mediciones")
public class Medicion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /** Id de la medicion en Room (app Android). Evita duplicados al reenviar. */
    @Column(unique = true)
    private Integer idLocal;

    @Column(nullable = false)
    private Integer predioId;

    @Column(nullable = false, length = 40)
    private String cultivo;

    @Column(nullable = false, length = 40)
    private String etapa;

    @Column(nullable = false) private Double ph;
    @Column(nullable = false) private Double ce;
    @Column(nullable = false) private Double n;
    @Column(nullable = false) private Double p;
    @Column(nullable = false) private Double k;
    @Column(nullable = false) private Double ca;
    @Column(nullable = false) private Double mg;

    /** Fecha de la medicion en milisegundos (igual que fechaMillis en la app). */
    @Column(nullable = false)
    private Long fechaMillis;

    @Column(nullable = false)
    private boolean validada;

    @Column(nullable = false)
    private LocalDateTime recibidoEn = LocalDateTime.now();

    public Medicion() {}

    public Long getId() { return id; }
    public Integer getIdLocal() { return idLocal; }
    public void setIdLocal(Integer idLocal) { this.idLocal = idLocal; }
    public Integer getPredioId() { return predioId; }
    public void setPredioId(Integer predioId) { this.predioId = predioId; }
    public String getCultivo() { return cultivo; }
    public void setCultivo(String cultivo) { this.cultivo = cultivo; }
    public String getEtapa() { return etapa; }
    public void setEtapa(String etapa) { this.etapa = etapa; }
    public Double getPh() { return ph; }
    public void setPh(Double ph) { this.ph = ph; }
    public Double getCe() { return ce; }
    public void setCe(Double ce) { this.ce = ce; }
    public Double getN() { return n; }
    public void setN(Double n) { this.n = n; }
    public Double getP() { return p; }
    public void setP(Double p) { this.p = p; }
    public Double getK() { return k; }
    public void setK(Double k) { this.k = k; }
    public Double getCa() { return ca; }
    public void setCa(Double ca) { this.ca = ca; }
    public Double getMg() { return mg; }
    public void setMg(Double mg) { this.mg = mg; }
    public Long getFechaMillis() { return fechaMillis; }
    public void setFechaMillis(Long fechaMillis) { this.fechaMillis = fechaMillis; }
    public boolean isValidada() { return validada; }
    public void setValidada(boolean validada) { this.validada = validada; }
    public LocalDateTime getRecibidoEn() { return recibidoEn; }
}
