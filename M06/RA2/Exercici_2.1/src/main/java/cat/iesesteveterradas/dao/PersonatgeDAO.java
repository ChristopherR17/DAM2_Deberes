package cat.iesesteveterradas.dao;

import cat.iesesteveterradas.model.Personatge;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Data Access Object para la tabla Personatge
 */
public class PersonatgeDAO {
    
    /**
     * Obtiene todos los personajes
     * @return Lista de personajes
     */
    public List<Personatge> getAll() {
        List<Personatge> personatges = new ArrayList<>();
        String sql = """
            SELECT p.*, f.nom as nomFaccio 
            FROM Personatge p 
            JOIN Faccio f ON p.idFaccio = f.id 
            ORDER BY p.id
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            
            while (rs.next()) {
                Personatge personatge = new Personatge(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getFloat("atac"),
                    rs.getFloat("defensa"),
                    rs.getInt("idFaccio")
                );
                personatge.setNomFaccio(rs.getString("nomFaccio"));
                personatges.add(personatge);
            }
            
            System.out.println("✓ Obtenidos " + personatges.size() + " personajes");
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener personajes: " + e.getMessage());
        }
        
        return personatges;
    }
    
    /**
     * Obtiene personajes por facción
     * @param idFaccio ID de la facción
     * @return Lista de personajes de esa facción
     */
    public List<Personatge> getByFaccio(int idFaccio) {
        List<Personatge> personatges = new ArrayList<>();
        String sql = """
            SELECT p.*, f.nom as nomFaccio 
            FROM Personatge p 
            JOIN Faccio f ON p.idFaccio = f.id 
            WHERE p.idFaccio = ? 
            ORDER BY p.nom
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idFaccio);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                Personatge personatge = new Personatge(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getFloat("atac"),
                    rs.getFloat("defensa"),
                    rs.getInt("idFaccio")
                );
                personatge.setNomFaccio(rs.getString("nomFaccio"));
                personatges.add(personatge);
            }
            
            System.out.println("✓ Obtenidos " + personatges.size() + " personajes para facción ID: " + idFaccio);
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener personajes por facción: " + e.getMessage());
        }
        
        return personatges;
    }
    
    /**
     * Obtiene el mejor atacante de una facción
     * @param idFaccio ID de la facción
     * @return Personaje con mayor ataque
     */
    public Personatge getMillorAtacantPerFaccio(int idFaccio) {
        String sql = """
            SELECT p.*, f.nom as nomFaccio 
            FROM Personatge p 
            JOIN Faccio f ON p.idFaccio = f.id 
            WHERE p.idFaccio = ? 
            ORDER BY p.atac DESC 
            LIMIT 1
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idFaccio);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Personatge personatge = new Personatge(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getFloat("atac"),
                    rs.getFloat("defensa"),
                    rs.getInt("idFaccio")
                );
                personatge.setNomFaccio(rs.getString("nomFaccio"));
                System.out.println("✓ Mejor atacante obtenido para facción ID: " + idFaccio);
                return personatge;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener mejor atacante: " + e.getMessage());
        }
        
        return null;
    }
    
    /**
     * Obtiene el mejor defensor de una facción
     * @param idFaccio ID de la facción
     * @return Personaje con mayor defensa
     */
    public Personatge getMillorDefensorPerFaccio(int idFaccio) {
        String sql = """
            SELECT p.*, f.nom as nomFaccio 
            FROM Personatge p 
            JOIN Faccio f ON p.idFaccio = f.id 
            WHERE p.idFaccio = ? 
            ORDER BY p.defensa DESC 
            LIMIT 1
            """;
        
        try (Connection conn = DatabaseConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, idFaccio);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                Personatge personatge = new Personatge(
                    rs.getInt("id"),
                    rs.getString("nom"),
                    rs.getFloat("atac"),
                    rs.getFloat("defensa"),
                    rs.getInt("idFaccio")
                );
                personatge.setNomFaccio(rs.getString("nomFaccio"));
                System.out.println("✓ Mejor defensor obtenido para facción ID: " + idFaccio);
                return personatge;
            }
            
        } catch (SQLException e) {
            System.err.println("✗ Error al obtener mejor defensor: " + e.getMessage());
        }
        
        return null;
    }
}