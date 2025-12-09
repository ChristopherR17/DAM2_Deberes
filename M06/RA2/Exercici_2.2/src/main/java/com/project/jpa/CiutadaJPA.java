package com.project.jpa;

import jakarta.persistence.*;

@Entity
@Table(name = "CIUTADA")
public class CiutadaJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CIUTADA_ID")
    private long ciutadaId;
    
    @Column(name = "NOM", nullable = false)
    private String nom;
    
    @Column(name = "COGNOM", nullable = false)
    private String cognom;
    
    @Column(name = "EDAT")
    private int edat;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "CIUTAT_ID")
    private CiutatJPA ciutat;
    
    public CiutadaJPA() {}
    
    public CiutadaJPA(String nom, String cognom, int edat) {
        this.nom = nom;
        this.cognom = cognom;
        this.edat = edat;
    }
    
    // Getters y Setters
    public long getCiutadaId() { return ciutadaId; }
    public void setCiutadaId(long ciutadaId) { this.ciutadaId = ciutadaId; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getCognom() { return cognom; }
    public void setCognom(String cognom) { this.cognom = cognom; }
    
    public int getEdat() { return edat; }
    public void setEdat(int edat) { this.edat = edat; }
    
    public CiutatJPA getCiutat() { return ciutat; }
    public void setCiutat(CiutatJPA ciutat) { this.ciutat = ciutat; }
    
    @Override
    public String toString() {
        return ciutadaId + ": " + nom + " " + cognom + " (" + edat + " anys)";
    }
}