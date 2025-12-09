package com.project.jpa;

import java.io.File;

public class MainJPA {
    public static void main(String[] args) {
        // Crear directorio data si no existe
        String basePath = System.getProperty("user.dir") + "/data/";
        File dir = new File(basePath);
        if (!dir.exists()) {
            if (!dir.mkdirs()) {
                System.out.println("Error creating 'data' folder");
            }
        }

        // Inicializar JPA
        ManagerJPA.init();
        
        System.out.println("=== EJERCICIO 1: Hibernate con JPA y anotaciones ===\n");

        // Punto 1: Creación inicial
        System.out.println("Punt 1: Després de la creació inicial d'elements");
        
        // Crear ciudades
        CiutatJPA ciutat1 = ManagerJPA.addCiutat("Vancouver", "Canada", 98661);
        CiutatJPA ciutat2 = ManagerJPA.addCiutat("Växjö", "Suècia", 35220);
        CiutatJPA ciutat3 = ManagerJPA.addCiutat("Kyoto", "Japó", 5200461);
        
        // Crear ciudadanos
        CiutadaJPA ciutada1 = ManagerJPA.addCiutada("Tony", "Happy", 20);
        CiutadaJPA ciutada2 = ManagerJPA.addCiutada("Monica", "Mouse", 22);
        CiutadaJPA ciutada3 = ManagerJPA.addCiutada("Eirika", "Erjo", 44);
        CiutadaJPA ciutada4 = ManagerJPA.addCiutada("Ven", "Enrison", 48);
        CiutadaJPA ciutada5 = ManagerJPA.addCiutada("Akira", "Akiko", 62);
        CiutadaJPA ciutada6 = ManagerJPA.addCiutada("Masako", "Kubo", 66);
        
        // Listar
        System.out.println(ManagerJPA.ciutatsToString(ManagerJPA.listAllCiutats()));
        System.out.println();
        System.out.println(ManagerJPA.ciutadansToString(ManagerJPA.listAllCiutadans()));
        System.out.println();

        // Punto 2: Asignar ciudadanos a ciudades
        System.out.println("Punt 2: Després d'actualitzar ciutats");
        
        // Asignar ciudadanos a Vancouver
        ManagerJPA.assignCiutadaToCiutat(ciutada1.getCiutadaId(), ciutat1.getCiutatId());
        ManagerJPA.assignCiutadaToCiutat(ciutada2.getCiutadaId(), ciutat1.getCiutatId());
        ManagerJPA.assignCiutadaToCiutat(ciutada3.getCiutadaId(), ciutat1.getCiutatId());
        
        // Asignar ciudadanos a Växjö
        ManagerJPA.assignCiutadaToCiutat(ciutada4.getCiutadaId(), ciutat2.getCiutatId());
        ManagerJPA.assignCiutadaToCiutat(ciutada5.getCiutadaId(), ciutat2.getCiutatId());
        
        // Kyoto se queda sin ciudadanos
        
        System.out.println(ManagerJPA.ciutatsToString(ManagerJPA.listAllCiutats()));
        System.out.println();
        System.out.println(ManagerJPA.ciutadansToString(ManagerJPA.listAllCiutadans()));
        System.out.println();

        // Punto 3: Actualizar nombres
        System.out.println("Punt 3: Després d'actualització de noms");
        
        // Actualizar nombres de ciudades
        ManagerJPA.updateCiutat(ciutat1.getCiutatId(), "Vancouver Updated", ciutat1.getPais(), ciutat1.getPoblacio());
        ManagerJPA.updateCiutat(ciutat2.getCiutatId(), "Växjö Updated", ciutat2.getPais(), ciutat2.getPoblacio());
        
        // Actualizar nombres de ciudadanos
        ManagerJPA.updateCiutada(ciutada1.getCiutadaId(), "Tony Updated", ciutada1.getCognom(), ciutada1.getEdat());
        ManagerJPA.updateCiutada(ciutada4.getCiutadaId(), "Ven Updated", ciutada4.getCognom(), ciutada4.getEdat());
        
        System.out.println(ManagerJPA.ciutatsToString(ManagerJPA.listAllCiutats()));
        System.out.println();
        System.out.println(ManagerJPA.ciutadansToString(ManagerJPA.listAllCiutadans()));
        System.out.println();

        // Punto 4: Eliminar elementos
        System.out.println("Punt 4: després d'esborrat");
        
        // Eliminar tercera ciudad y sexto ciudadano
        ManagerJPA.deleteCiutat(ciutat3.getCiutatId());
        ManagerJPA.deleteCiutada(ciutada6.getCiutadaId());
        
        System.out.println(ManagerJPA.ciutatsToString(ManagerJPA.listAllCiutats()));
        System.out.println();
        System.out.println(ManagerJPA.ciutadansToString(ManagerJPA.listAllCiutadans()));
        System.out.println();

        // Punto 5: Recuperar ciudadanos de una ciudad específica
        System.out.println("Punt 5: Recuperació de ciutadans d'una ciutat específica");
        
        // Refrescar la ciudad de la base de datos
        CiutatJPA ciutatActualizada = ManagerJPA.getCiutatById(ciutat1.getCiutatId());
        if (ciutatActualizada != null) {
            System.out.println("Ciutadans de la ciutat '" + ciutatActualizada.getNom() + "':");
            if (ciutatActualizada.getCiutadans() != null && !ciutatActualizada.getCiutadans().isEmpty()) {
                for (CiutadaJPA ciutada : ciutatActualizada.getCiutadans()) {
                    System.out.println("- " + ciutada.getNom() + " " + ciutada.getCognom());
                }
            } else {
                System.out.println("La ciutat no té ciutadans");
            }
        } else {
            System.out.println("No s'ha trobat la ciutat");
        }

        // Cerrar JPA
        ManagerJPA.close();
        System.out.println("\nEjercicio 1 (JPA) completado.");
    }
}