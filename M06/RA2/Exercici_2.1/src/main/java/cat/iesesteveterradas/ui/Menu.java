package cat.iesesteveterradas.ui;

import cat.iesesteveterradas.service.FaccioService;
import cat.iesesteveterradas.service.PersonatgeService;
import java.util.InputMismatchException;
import java.util.Scanner;

/**
 * Clase para gestionar la interfaz de usuario por consola
 */
public class Menu {
    
    private final Scanner scanner;
    private final FaccioService faccioService;
    private final PersonatgeService personatgeService;
    private boolean sortir;
    
    public Menu() {
        this.scanner = new Scanner(System.in);
        this.faccioService = new FaccioService();
        this.personatgeService = new PersonatgeService();
        this.sortir = false;
    }
    
    /**
     * Muestra el menú principal y gestiona las opciones
     */
    public void mostrarMenu() {
        while (!sortir) {
            mostrarOpcions();
            int opcio = llegirOpcio();
            processarOpcio(opcio);
        }
        
        scanner.close();
        System.out.println("\nGràcies per utilitzar For Honor Database Manager!");
    }
    
    /**
     * Muestra las opciones disponibles
     */
    private void mostrarOpcions() {
        System.out.println("\n" + "=".repeat(50));
        System.out.println("            MENÚ PRINCIPAL");
        System.out.println("=".repeat(50));
        System.out.println("1. Mostrar totes les faccions");
        System.out.println("2. Mostrar tots els personatges");
        System.out.println("3. Mostrar personatges per facció");
        System.out.println("4. Mostrar millor atacant per facció");
        System.out.println("5. Mostrar millor defensor per facció");
        System.out.println("0. Sortir");
        System.out.println("-".repeat(50));
        System.out.print("Selecciona una opció (0-5): ");
    }
    
    /**
     * Lee la opción seleccionada por el usuario
     * @return Opción seleccionada
     */
    private int llegirOpcio() {
        try {
            return scanner.nextInt();
        } catch (InputMismatchException e) {
            scanner.nextLine(); // Limpiar buffer
            return -1; // Opción inválida
        }
    }
    
    /**
     * Procesa la opción seleccionada
     * @param opcio Opción a procesar
     */
    private void processarOpcio(int opcio) {
        scanner.nextLine(); // Limpiar buffer después de nextInt()
        
        switch (opcio) {
            case 0:
                sortir = true;
                break;
                
            case 1:
                faccioService.mostrarTotesFaccions();
                break;
                
            case 2:
                personatgeService.mostrarTotsPersonatges();
                break;
                
            case 3:
                mostrarPersonatgesPerFaccio();
                break;
                
            case 4:
                mostrarMillorAtacantPerFaccio();
                break;
                
            case 5:
                mostrarMillorDefensorPerFaccio();
                break;
                
            default:
                System.out.println("\n⚠️  Opció invàlida. Si us plau, selecciona un número del 0 al 5.");
        }
    }
    
    /**
     * Solicita ID de facción y muestra sus personajes
     */
    private void mostrarPersonatgesPerFaccio() {
        System.out.println("\n=== MOSTRAR PERSONATGES PER FACCIÓ ===");
        mostrarLlistatFaccions();
        
        System.out.print("\nIntrodueix l'ID de la facció: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            
            if (faccioService.existeixFaccio(id)) {
                personatgeService.mostrarPersonatgesPerFaccio(id);
            } else {
                System.out.println("Error: No existeix cap facció amb ID: " + id);
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Error: Has d'introduir un número vàlid.");
            scanner.nextLine(); // Limpiar buffer
        }
    }
    
    /**
     * Solicita ID de facción y muestra su mejor atacante
     */
    private void mostrarMillorAtacantPerFaccio() {
        System.out.println("\n=== MILLOR ATACANT PER FACCIÓ ===");
        mostrarLlistatFaccions();
        
        System.out.print("\nIntrodueix l'ID de la facció: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            
            if (faccioService.existeixFaccio(id)) {
                personatgeService.mostrarMillorAtacantPerFaccio(id);
            } else {
                System.out.println("Error: No existeix cap facció amb ID: " + id);
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Error: Has d'introduir un número vàlid.");
            scanner.nextLine(); // Limpiar buffer
        }
    }
    
    /**
     * Solicita ID de facción y muestra su mejor defensor
     */
    private void mostrarMillorDefensorPerFaccio() {
        System.out.println("\n=== MILLOR DEFENSOR PER FACCIÓ ===");
        mostrarLlistatFaccions();
        
        System.out.print("\nIntrodueix l'ID de la facció: ");
        try {
            int id = scanner.nextInt();
            scanner.nextLine(); // Limpiar buffer
            
            if (faccioService.existeixFaccio(id)) {
                personatgeService.mostrarMillorDefensorPerFaccio(id);
            } else {
                System.out.println("Error: No existeix cap facció amb ID: " + id);
            }
            
        } catch (InputMismatchException e) {
            System.out.println("Error: Has d'introduir un número vàlid.");
            scanner.nextLine(); // Limpiar buffer
        }
    }
    
    /**
     * Muestra listado de facciones disponibles
     */
    private void mostrarLlistatFaccions() {
        System.out.println("Faccions disponibles:");
        var faccions = faccioService.obtenirTotesFaccions();
        for (var f : faccions) {
            System.out.println("  [" + f.getId() + "] " + f.getNom());
        }
    }
}