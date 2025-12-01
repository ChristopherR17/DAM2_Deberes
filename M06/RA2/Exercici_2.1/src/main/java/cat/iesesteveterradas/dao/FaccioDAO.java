package cat.iesesteveterradas.dao;

import cat.iesesteveterradas.model.Faccio;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla Faccio
 */
public class FaccioDAO {
    
    /**
     * Obtiene todas las facciones
     * @return Lista de facciones
     */
    public List<Faccio> getAll() {
        List<Faccio> faccions = new ArrayList<>();
        String sql = "SELECT * FROM Faccio ORDER BY id";
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Faccio faccio = new Faccio(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("resum")
                );
                faccions.add(faccio);
            }
            
            System.out.println("✓ Obtenidas " + faccions.size() + " facciones");
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener facciones: " + e.getMessage());
        }
        
        return faccions;
    }
    
    /**
     * Obtiene una facción por su ID
     * @param id ID de la facción
     * @return Facción o null si no existe
     */
    public Faccio getById(int id) {
        String sql = "SELECT * FROM Faccio WHERE id = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Faccio(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("resum")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener facción por ID: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene una facción por su nombre
     * @param nom Nombre de la facción
     * @return Facción o null si no existe
     */
    public Faccio getByNom(String nom) {
        String sql = "SELECT * FROM Faccio WHERE nom = ?";
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setString(1, nom);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return new Faccio(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getString("resum")
                );
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener facción por nombre: " + e.getMessage());
        }
        
        return null;
    }
}