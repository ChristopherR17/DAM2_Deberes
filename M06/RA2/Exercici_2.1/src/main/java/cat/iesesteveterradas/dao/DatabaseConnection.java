package cat.iesesteveterradas.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.ResultSet;

/**
 * Clase para gestionar la conexión con la base de datos SQLite
 * Implementa patrón Singleton para una única conexión
 */
public class DatabaseConnection {
    
    private static final String DB_URL = "jdbc:sqlite:for_honor.db";
    private static Connection connection = null;
    private static boolean initialized = false;
    
    /**
     * Obtiene la conexión a la base de datos
     * @return Connection objeto de conexión
     * @throws SQLException si hay error de conexión
     */
    public static Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            try {
                connection = DriverManager.getConnection(DB_URL);
                System.out.println("✓ Conexión establecida con la base de datos");
            } catch (SQLException e) {
                System.err.println("✗ Error al conectar con la base de datos: " + e.getMessage());
                throw e;
            }
        }
        return connection;
    }
    
    /**
     * Cierra la conexión a la base de datos
     */
    public static void closeConnection() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("✓ Conexión cerrada");
            } catch (SQLException e) {
                System.err.println("✗ Error al cerrar la conexión: " + e.getMessage());
            }
        }
    }
    
    /**
     * Inicializa la base de datos: crea tablas e inserta datos solo si no existen
     */
    public static void initDatabase() {
        createTables();
        if (!tieneDatos()) {
            insertarDatosIniciales();
        }
        initialized = true;
    }
    
    /**
     * Crea las tablas si no existen
     */
    private static void createTables() {
        String createFaccioTable = """
            CREATE TABLE IF NOT EXISTS Faccio (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom VARCHAR(15) NOT NULL,
                resum VARCHAR(500)
            );
            """;
        
        String createPersonatgeTable = """
            CREATE TABLE IF NOT EXISTS Personatge (
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                nom VARCHAR(15) NOT NULL,
                atac REAL NOT NULL,
                defensa REAL NOT NULL,
                idFaccio INTEGER NOT NULL,
                FOREIGN KEY (idFaccio) REFERENCES Faccio(id)
            );
            """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(createFaccioTable);
            stmt.execute(createPersonatgeTable);
            System.out.println("✓ Tablas creadas o ya existentes");
            
        } catch (SQLException e) {
            System.err.println("✗ Error al crear tablas: " + e.getMessage());
        }
    }
    
    /**
     * Verifica si la base de datos ya tiene datos
     */
    private static boolean tieneDatos() {
        String sql = "SELECT COUNT(*) as count FROM Faccio";
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                return rs.getInt("count") > 0;
            }
        } catch (SQLException e) {
            System.err.println("✗ Error al verificar datos: " + e.getMessage());
        }
        return false;
    }
    
    /**
     * Inserta los datos iniciales en la base de datos
     */
    private static void insertarDatosIniciales() {
        String insertFaccions = """
            INSERT INTO Faccio (id, nom, resum) VALUES 
            (1, 'Cavallers', 'Though seen as a single group, the Knights are hardly unified. There are many Legions in Ashfeld, the most prominent being The Iron Legion.'),
            (2, 'Vikings', 'The Vikings are a loose coalition of hundreds of clans and tribes, the most powerful being The Warborn.'),
            (3, 'Samurais', 'The Samurai are the most unified of the three factions, though this does not say much as the Daimyos were often battling each other for dominance.');
            """;
            
        String insertPersonatges = """
            INSERT INTO Personatge (nom, atac, defensa, idFaccio) VALUES
            ('Warden', 1, 3, 1),
            ('Conqueror', 2, 2, 1),
            ('Peacekeep', 2, 3, 1),
            ('Raider', 3, 3, 2),
            ('Warlord', 2, 2, 2),
            ('Berserker', 1, 1, 2),
            ('Kensei', 3, 2, 3),
            ('Shugoki', 2, 1, 3),
            ('Orochi', 3, 2, 3);
            """;
        
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            stmt.execute(insertFaccions);
            stmt.execute(insertPersonatges);
            System.out.println("✓ Datos iniciales insertados");
            
        } catch (SQLException e) {
            System.err.println("✗ Error al insertar datos: " + e.getMessage());
        }
    }
    
    /**
     * Reinicia la base de datos (solo para tests)
     */
    public static void resetForTests() {
        try (Connection conn = getConnection();
             Statement stmt = conn.createStatement()) {
            
            // Eliminar datos existentes
            stmt.execute("DELETE FROM Personatge");
            stmt.execute("DELETE FROM Faccio");
            
            // Resetear los autoincrementos
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='Faccio'");
            stmt.execute("DELETE FROM sqlite_sequence WHERE name='Personatge'");
            
            // Insertar datos iniciales
            insertarDatosIniciales();
            
            System.out.println("✓ Base de datos reiniciada para tests");
            
        } catch (SQLException e) {
            System.err.println("✗ Error al reiniciar base de datos: " + e.getMessage());
        }
    }
    
    /**
     * Método para tests - inicializa base de datos limpia
     */
    public static void initForTests() {
        // Eliminar archivo de base de datos si existe
        java.io.File dbFile = new java.io.File("for_honor.db");
        if (dbFile.exists()) {
            dbFile.delete();
        }
        
        // Reinicializar
        initialized = false;
        connection = null;
        initDatabase();
    }
}