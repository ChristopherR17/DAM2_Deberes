package cat.iesesteveterradas.model;

/**
 * Clase que representa un Personaje en el juego For Honor
 */
public class Personatge {
    
    private int id;
    private String nom;
    private float atac;
    private float defensa;
    private int idFaccio;
    private String nomFaccio; // Para mostrar en consultas JOIN
    
    // Constructores
    public Personatge() {}
    
    public Personatge(int id, String nom, float atac, float defensa, int idFaccio) {
        this.id = id;
        this.nom = nom;
        this.atac = atac;
        this.defensa = defensa;
        this.idFaccio = idFaccio;
    }
    
    public Personatge(String nom, float atac, float defensa, int idFaccio) {
        this.nom = nom;
        this.atac = atac;
        this.defensa = defensa;
        this.idFaccio = idFaccio;
    }
    
    // Getters y Setters
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public String getNom() {
        return nom;
    }
    
    public void setNom(String nom) {
        this.nom = nom;
    }
    
    public float getAtac() {
        return atac;
    }
    
    public void setAtac(float atac) {
        this.atac = atac;
    }
    
    public float getDefensa() {
        return defensa;
    }
    
    public void setDefensa(float defensa) {
        this.defensa = defensa;
    }
    
    public int getIdFaccio() {
        return idFaccio;
    }
    
    public void setIdFaccio(int idFaccio) {
        this.idFaccio = idFaccio;
    }
    
    public String getNomFaccio() {
        return nomFaccio;
    }
    
    public void setNomFaccio(String nomFaccio) {
        this.nomFaccio = nomFaccio;
    }
    
    @Override
    public String toString() {
        if (nomFaccio != null) {
            return String.format(
                "Personatge [ID: %d, Nom: %s, Atac: %.1f, Defensa: %.1f, Facció: %s]",
                id, nom, atac, defensa, nomFaccio
            );
        }
        return String.format(
            "Personatge [ID: %d, Nom: %s, Atac: %.1f, Defensa: %.1f, ID Facció: %d]",
            id, nom, atac, defensa, idFaccio
        );
    }
    
    public String mostrarDetall() {
        return String.format(
            "Personatge: %s\nID: %d\nAtac: %.1f\nDefensa: %.1f\nFacció ID: %d\n",
            nom, id, atac, defensa, idFaccio
        );
    }
}