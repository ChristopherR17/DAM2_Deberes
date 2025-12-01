package cat.iesesteveterradas.model;

/**
 * Clase que representa una Facción en el juego For Honor
 */
public class Faccio {
    
    private int id;
    private String nom;
    private String resum;
    
    // Constructores
    public Faccio() {}
    
    public Faccio(int id, String nom, String resum) {
        this.id = id;
        this.nom = nom;
        this.resum = resum;
    }
    
    public Faccio(String nom, String resum) {
        this.nom = nom;
        this.resum = resum;
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
    
    public String getResum() {
        return resum;
    }
    
    public void setResum(String resum) {
        this.resum = resum;
    }
    
    @Override
    public String toString() {
        return String.format("Facció [ID: %d, Nom: %s]", id, nom);
    }
    
    public String mostrarDetall() {
        return String.format(
            "Facció: %s\nID: %d\nDescripció: %s\n",
            nom, id, resum
        );
    }
}