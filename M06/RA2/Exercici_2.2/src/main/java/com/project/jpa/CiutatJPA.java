package com.project.jpa;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "CIUTAT")
public class CiutatJPA {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "CIUTAT_ID")
    private long ciutatId;
    
    @Column(name = "NOM", nullable = false)
    private String nom;
    
    @Column(name = "PAIS", nullable = false)
    private String pais;
    
    @Column(name = "POBLACIO")
    private int poblacio;
    
    @OneToMany(mappedBy = "ciutat", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private Set<CiutadaJPA> ciutadans = new HashSet<>();
    
    public CiutatJPA() {}
    
    public CiutatJPA(String nom, String pais, int poblacio) {
        this.nom = nom;
        this.pais = pais;
        this.poblacio = poblacio;
    }
    
    // Getters y Setters
    public long getCiutatId() { return ciutatId; }
    public void setCiutatId(long ciutatId) { this.ciutatId = ciutatId; }
    
    public String getNom() { return nom; }
    public void setNom(String nom) { this.nom = nom; }
    
    public String getPais() { return pais; }
    public void setPais(String pais) { this.pais = pais; }
    
    public int getPoblacio() { return poblacio; }
    public void setPoblacio(int poblacio) { this.poblacio = poblacio; }
    
    public Set<CiutadaJPA> getCiutadans() { return ciutadans; }
    public void setCiutadans(Set<CiutadaJPA> ciutadans) { this.ciutadans = ciutadans; }
    
    // Método helper para añadir ciudadano
    public void addCiutada(CiutadaJPA ciutada) {
        ciutadans.add(ciutada);
        ciutada.setCiutat(this);
    }
    
    // Método helper para eliminar ciudadano
    public void removeCiutada(CiutadaJPA ciutada) {
        ciutadans.remove(ciutada);
        ciutada.setCiutat(null);
    }
    
    @Override
    public String toString() {
        return ciutatId + ": " + nom + " (" + pais + "), Població: " + poblacio;
    }
}