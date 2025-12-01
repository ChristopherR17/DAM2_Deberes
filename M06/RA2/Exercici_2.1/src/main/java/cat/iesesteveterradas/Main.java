package cat.iesesteveterradas;

import cat.iesesteveterradas.dao.DatabaseConnection;
import cat.iesesteveterradas.ui.Menu;

/**
 * Clase principal de la aplicación "For Honor Database Manager"
 */
public class Main {
    
    /**
     * Método principal que inicia la aplicación
     * @param args Argumentos de línea de comandos
     */
    public static void main(String[] args) {
        System.out.println("========================================");
        System.out.println("  FOR HONOR DATABASE MANAGER");
        System.out.println("========================================");
        
        try {
            // Inicializar base de datos
            System.out.println("Inicializando base de datos...");
            DatabaseConnection.initDatabase();
            
            // Ejecutar menú principal
            Menu menu = new Menu();
            menu.mostrarMenu();
            
        } catch (Exception e) {
            System.err.println("Error al iniciar la aplicación: " + e.getMessage());
            e.printStackTrace();
        }
    }
}