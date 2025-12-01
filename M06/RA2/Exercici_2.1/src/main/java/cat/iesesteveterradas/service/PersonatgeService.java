package cat.iesesteveterradas.service;

import cat.iesesteveterradas.dao.FaccioDAO;
import cat.iesesteveterradas.dao.PersonatgeDAO;
import cat.iesesteveterradas.model.Faccio;
import cat.iesesteveterradas.model.Personatge;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con Personaje
 */
public class PersonatgeService {
    
    private final PersonatgeDAO personatgeDAO;
    private final FaccioDAO faccioDAO;
    
    public PersonatgeService() {
        this.personatgeDAO = new PersonatgeDAO();
        this.faccioDAO = new FaccioDAO();
    }
    
    /**
     * Obtiene todos los personajes
     * @return Lista de personajes
     */
    public List<Personatge> obtenirTotsPersonatges() {
        return personatgeDAO.getAll();
    }
    
    /**
     * Muestra todos los personajes en consola
     */
    public void mostrarTotsPersonatges() {
        List<Personatge> personatges = obtenirTotsPersonatges();
        
        System.out.println("\n=== LLISTAT DE PERSONATGES ===");
        if (personatges.isEmpty()) {
            System.out.println("No hi ha personatges a la base de dades.");
        } else {
            for (Personatge p : personatges) {
                System.out.println(p);
            }
        }
        System.out.println("Total: " + personatges.size() + " personatges");
    }
    
    /**
     * Obtiene personajes por facción
     * @param idFaccio ID de la facción
     * @return Lista de personajes
     */
    public List<Personatge> obtenirPersonatgesPerFaccio(int idFaccio) {
        return personatgeDAO.getByFaccio(idFaccio);
    }
    
    /**
     * Muestra personajes de una facción específica
     * @param idFaccio ID de la facción
     */
    public void mostrarPersonatgesPerFaccio(int idFaccio) {
        Faccio faccio = faccioDAO.getById(idFaccio);
        if (faccio == null) {
            System.out.println("Error: No existe la facción con ID: " + idFaccio);
            return;
        }
        
        List<Personatge> personatges = obtenirPersonatgesPerFaccio(idFaccio);
        
        System.out.println("\n=== PERSONATGES DE LA FACCIÓ: " + faccio.getNom().toUpperCase() + " ===");
        System.out.println("Descripció: " + faccio.getResum() + "\n");
        
        if (personatges.isEmpty()) {
            System.out.println("Aquesta facció no té personatges.");
        } else {
            System.out.println("Personatges:");
            for (Personatge p : personatges) {
                System.out.println("  • " + p.getNom() + 
                    " (Atac: " + p.getAtac() + 
                    ", Defensa: " + p.getDefensa() + ")");
            }
        }
        System.out.println("Total: " + personatges.size() + " personatges");
    }
    
    /**
     * Obtiene y muestra el mejor atacante de una facción
     * @param idFaccio ID de la facción
     */
    public void mostrarMillorAtacantPerFaccio(int idFaccio) {
        Faccio faccio = faccioDAO.getById(idFaccio);
        if (faccio == null) {
            System.out.println("Error: No existe la facción con ID: " + idFaccio);
            return;
        }
        
        Personatge millorAtacant = personatgeDAO.getMillorAtacantPerFaccio(idFaccio);
        
        System.out.println("\n=== MILLOR ATACANT DE: " + faccio.getNom().toUpperCase() + " ===");
        
        if (millorAtacant == null) {
            System.out.println("Aquesta facció no té personatges.");
        } else {
            System.out.println("El millor atacant és:");
            System.out.println("  Nom: " + millorAtacant.getNom());
            System.out.println("  Atac: " + millorAtacant.getAtac());
            System.out.println("  Defensa: " + millorAtacant.getDefensa());
            System.out.println("  Facció: " + faccio.getNom());
        }
    }
    
    /**
     * Obtiene y muestra el mejor defensor de una facción
     * @param idFaccio ID de la facción
     */
    public void mostrarMillorDefensorPerFaccio(int idFaccio) {
        Faccio faccio = faccioDAO.getById(idFaccio);
        if (faccio == null) {
            System.out.println("Error: No existe la facción con ID: " + idFaccio);
            return;
        }
        
        Personatge millorDefensor = personatgeDAO.getMillorDefensorPerFaccio(idFaccio);
        
        System.out.println("\n=== MILLOR DEFENSOR DE: " + faccio.getNom().toUpperCase() + " ===");
        
        if (millorDefensor == null) {
            System.out.println("Aquesta facció no té personatges.");
        } else {
            System.out.println("El millor defensor és:");
            System.out.println("  Nom: " + millorDefensor.getNom());
            System.out.println("  Defensa: " + millorDefensor.getDefensa());
            System.out.println("  Atac: " + millorDefensor.getAtac());
            System.out.println("  Facció: " + faccio.getNom());
        }
    }
}