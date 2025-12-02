package com.project;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.hibernate.query.Query;
import java.util.List;

public class Manager {
    private static SessionFactory sessionFactory;
    
    public static void createSessionFactory() {
        try {
            sessionFactory = new Configuration().configure("hibernate.cfg.xml").buildSessionFactory();
        } catch (Exception e) {
            System.err.println("Error creating SessionFactory: " + e);
            e.printStackTrace();
        }
    }
    
    public static void close() {
        if (sessionFactory != null && !sessionFactory.isClosed()) {
            sessionFactory.close();
        }
    }
    
    // Métodos CREATE
    public static Ciutat addCiutat(String nom, String pais, int poblacio) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Ciutat ciutat = null;
        
        try {
            tx = session.beginTransaction();
            ciutat = new Ciutat(nom, pais, poblacio);
            session.persist(ciutat);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutat;
    }
    
    public static Ciutada addCiutada(String nom, String cognom, int edat) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        Ciutada ciutada = null;
        
        try {
            tx = session.beginTransaction();
            ciutada = new Ciutada(nom, cognom, edat);
            session.persist(ciutada);
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutada;
    }
    
    // Método para listar elementos
    public static List<?> listCollection(Class<?> clazz, String where) {
        Session session = sessionFactory.openSession();
        List<?> result = null;
        
        try {
            String hql = "FROM " + clazz.getSimpleName();
            if (where != null && !where.isEmpty()) {
                hql += " WHERE " + where;
            }
            Query<?> query = session.createQuery(hql, clazz);
            result = query.list();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return result;
    }
    
    // Método para convertir colección a string (como lo usa tu Main)
    public static String collectionToString(Class<?> clazz, List<?> collection) {
        if (collection == null || collection.isEmpty()) {
            return "";
        }
        
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < collection.size(); i++) {
            sb.append(collection.get(i).toString());
            if (i < collection.size() - 1) {
                sb.append("\n");
            }
        }
        return sb.toString();
    }
    
    // Métodos UPDATE que tu Main necesita
    public static void updateCiutat(long id, String nom, String pais, int poblacio, java.util.Set<Ciutada> ciutadans) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            Ciutat ciutat = session.get(Ciutat.class, id);
            if (ciutat != null) {
                ciutat.setNom(nom);
                ciutat.setPais(pais);
                ciutat.setPoblacio(poblacio);
                // Nota: En el ejercicio 0 no hay relación física, así que no actualizamos ciutadans
                session.update(ciutat);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    public static void updateCiutada(long id, String nom, String cognom, int edat) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            Ciutada ciutada = session.get(Ciutada.class, id);
            if (ciutada != null) {
                ciutada.setNom(nom);
                ciutada.setCognom(cognom);
                ciutada.setEdat(edat);
                session.update(ciutada);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    // Método DELETE
    public static void delete(Class<?> clazz, long id) {
        Session session = sessionFactory.openSession();
        Transaction tx = null;
        
        try {
            tx = session.beginTransaction();
            Object obj = session.get(clazz, id);
            if (obj != null) {
                session.delete(obj);
            }
            tx.commit();
        } catch (Exception e) {
            if (tx != null) tx.rollback();
            e.printStackTrace();
        } finally {
            session.close();
        }
    }
    
    // Método para obtener ciudad con ciudadanos (simulado para ejercicio 0)
    public static Ciutat getCiutatWithCiutadans(long ciutatId) {
        Session session = sessionFactory.openSession();
        Ciutat ciutat = null;
        
        try {
            ciutat = session.get(Ciutat.class, ciutatId);
            // En ejercicio 0 no hay relación física, devolvemos la ciudad sin ciudadanos
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            session.close();
        }
        return ciutat;
    }
}