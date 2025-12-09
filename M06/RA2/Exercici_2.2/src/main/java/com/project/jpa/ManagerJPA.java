package com.project.jpa;

import jakarta.persistence.*;
import java.util.List;

public class ManagerJPA {
    private static EntityManagerFactory emf;
    private static EntityManager em;
    
    public static void init() {
        try {
            emf = Persistence.createEntityManagerFactory("hibernate-pu");
            em = emf.createEntityManager();
        } catch (Exception e) {
            System.err.println("Error initializing JPA: " + e);
            e.printStackTrace();
        }
    }
    
    public static void close() {
        if (em != null && em.isOpen()) {
            em.close();
        }
        if (emf != null && emf.isOpen()) {
            emf.close();
        }
    }
    
    // CREATE - Ciutat
    public static CiutatJPA addCiutat(String nom, String pais, int poblacio) {
        EntityTransaction tx = em.getTransaction();
        CiutatJPA ciutat = null;
        
        try {
            tx.begin();
            ciutat = new CiutatJPA(nom, pais, poblacio);
            em.persist(ciutat);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
        return ciutat;
    }
    
    // CREATE - Ciutada
    public static CiutadaJPA addCiutada(String nom, String cognom, int edat) {
        EntityTransaction tx = em.getTransaction();
        CiutadaJPA ciutada = null;
        
        try {
            tx.begin();
            ciutada = new CiutadaJPA(nom, cognom, edat);
            em.persist(ciutada);
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
        return ciutada;
    }
    
    // READ - Listar todos
    public static List<CiutatJPA> listAllCiutats() {
        TypedQuery<CiutatJPA> query = em.createQuery("SELECT c FROM CiutatJPA c", CiutatJPA.class);
        return query.getResultList();
    }
    
    public static List<CiutadaJPA> listAllCiutadans() {
        TypedQuery<CiutadaJPA> query = em.createQuery("SELECT c FROM CiutadaJPA c", CiutadaJPA.class);
        return query.getResultList();
    }
    
    // READ - Obtener por ID
    public static CiutatJPA getCiutatById(long id) {
        return em.find(CiutatJPA.class, id);
    }
    
    public static CiutadaJPA getCiutadaById(long id) {
        return em.find(CiutadaJPA.class, id);
    }
    
    // UPDATE - Ciutat
    public static void updateCiutat(long id, String nom, String pais, int poblacio) {
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            CiutatJPA ciutat = em.find(CiutatJPA.class, id);
            if (ciutat != null) {
                ciutat.setNom(nom);
                ciutat.setPais(pais);
                ciutat.setPoblacio(poblacio);
                em.merge(ciutat);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    
    // UPDATE - Ciutada
    public static void updateCiutada(long id, String nom, String cognom, int edat) {
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            CiutadaJPA ciutada = em.find(CiutadaJPA.class, id);
            if (ciutada != null) {
                ciutada.setNom(nom);
                ciutada.setCognom(cognom);
                ciutada.setEdat(edat);
                em.merge(ciutada);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    
    // DELETE
    public static void deleteCiutat(long id) {
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            CiutatJPA ciutat = em.find(CiutatJPA.class, id);
            if (ciutat != null) {
                em.remove(ciutat);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    
    public static void deleteCiutada(long id) {
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            CiutadaJPA ciutada = em.find(CiutadaJPA.class, id);
            if (ciutada != null) {
                em.remove(ciutada);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    
    // Asignar ciudadano a ciudad
    public static void assignCiutadaToCiutat(long ciutadaId, long ciutatId) {
        EntityTransaction tx = em.getTransaction();
        
        try {
            tx.begin();
            CiutadaJPA ciutada = em.find(CiutadaJPA.class, ciutadaId);
            CiutatJPA ciutat = em.find(CiutatJPA.class, ciutatId);
            
            if (ciutada != null && ciutat != null) {
                ciutada.setCiutat(ciutat);
                ciutat.getCiutadans().add(ciutada);
                em.merge(ciutada);
                em.merge(ciutat);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx.isActive()) tx.rollback();
            e.printStackTrace();
        }
    }
    
    // Método para convertir lista a string
    public static String ciutatsToString(List<CiutatJPA> ciutats) {
        if (ciutats == null || ciutats.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciutats.size(); i++) {
            CiutatJPA c = ciutats.get(i);
            sb.append((i + 1) + ": " + c.getNom() + " (" + c.getPais() + 
                     "), Població: " + c.getPoblacio() + 
                     ", Ciutadans: [");
            
            if (c.getCiutadans() != null && !c.getCiutadans().isEmpty()) {
                boolean first = true;
                for (CiutadaJPA ciutada : c.getCiutadans()) {
                    if (!first) sb.append(" | ");
                    sb.append(ciutada.getNom() + " " + ciutada.getCognom());
                    first = false;
                }
            }
            sb.append("]");
            
            if (i < ciutats.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    public static String ciutadansToString(List<CiutadaJPA> ciutadans) {
        if (ciutadans == null || ciutadans.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ciutadans.size(); i++) {
            CiutadaJPA c = ciutadans.get(i);
            sb.append((i + 1) + ": " + c.getNom() + " " + c.getCognom() + 
                     " (" + c.getEdat() + " anys)");
            
            if (i < ciutadans.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
}