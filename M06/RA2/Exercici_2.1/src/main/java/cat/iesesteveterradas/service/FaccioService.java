package cat.iesesteveterradas.service;

import cat.iesesteveterradas.dao.FaccioDAO;
import cat.iesesteveterradas.model.Faccio;
import java.util.List;

/**
 * Servicio para operaciones relacionadas con Facción
 */
public class FaccioService {
    
    private final FaccioDAO faccioDAO;
    
    public FaccioService() {
        this.faccioDAO = new FaccioDAO();
    }
    
    /**
     * Obtiene todas las facciones
     * @return Lista de facciones
     */
    public List<Faccio> obtenirTotesFaccions() {
        return faccioDAO.getAll();
    }
    
    /**
     * Muestra todas las facciones en consola
     */
    public void mostrarTotesFaccions() {
        List<Faccio> faccions = obtenirTotesFaccions();
        
        System.out.println("\n=== LLISTAT DE FACCIÓNS ===");
        if (faccions.isEmpty()) {
            System.out.println("No hi ha faccions a la base de dades.");
        } else {
            for (Faccio f : faccions) {
                System.out.println(f.mostrarDetall());
            }
        }
        System.out.println("Total: " + faccions.size() + " faccions");
    }
    
    /**
     * Obtiene una facción por ID
     * @param id ID de la facción
     * @return Facción o null
     */
    public Faccio obtenirFaccioPerId(int id) {
        return faccioDAO.getById(id);
    }
    
    /**
     * Verifica si existe una facción por ID
     * @param id ID a verificar
     * @return true si existe
     */
    public boolean existeixFaccio(int id) {
        return faccioDAO.getById(id) != null;
    }
}