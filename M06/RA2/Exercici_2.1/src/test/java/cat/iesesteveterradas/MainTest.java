package cat.iesesteveterradas;

import cat.iesesteveterradas.dao.DatabaseConnection;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class MainTest {
    
    @Test
    public void testBaseDatosInicializacion() {
        // Inicializar base de datos
        DatabaseConnection.initDatabase();
        
        // Verificar que se creó el archivo
        java.io.File dbFile = new java.io.File("for_honor.db");
        assertTrue(dbFile.exists(), "El archivo de base de datos debería existir");
        
        System.out.println("✓ Base de datos inicializada correctamente");
    }
}