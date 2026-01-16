package com.project;

import com.project.dao.Manager;
import com.project.domain.*;
import org.junit.jupiter.api.*;
import java.time.LocalDate;
import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class ManagerTest {

    @BeforeAll
    void init() {
        Manager.createSessionFactory();
    }

    @AfterAll
    void close() {
        Manager.close();
    }

    @Test
    void testAddAutor() {
        Autor autor = Manager.addAutor("Joan Miró");
        assertNotNull(autor);
        assertNotNull(autor.getAutorId());
        assertEquals("Joan Miró", autor.getNom());
    }

    @Test
    void testAddLlibre() {
        Llibre llibre = Manager.addLlibre("ISBN001", "Art Contemporani", "Editorial X", 2020);
        assertNotNull(llibre);
        assertEquals("ISBN001", llibre.getIsbn());
        assertEquals("Art Contemporani", llibre.getTitol());
    }

    @Test
    void testAddPrestecIRegistrarRetorn() {
        Biblioteca biblio = Manager.addBiblioteca("Central", "Barcelona", "C/ Ex.", "123456789", "biblio@ex.com");
        Llibre llibre = Manager.addLlibre("ISBN002", "Llibre Test", "Editorial Y", 2021);
        Exemplar exemplar = Manager.addExemplar("CB001", llibre, biblio);
        Persona persona = Manager.addPersona("12345678A", "Anna", "987654321", "anna@ex.com");

        // Crear préstec
        Prestec prestec = Manager.addPrestec(exemplar, persona, LocalDate.now(), LocalDate.now().plusDays(7));
        assertNotNull(prestec);
        assertFalse(exemplar.isDisponible());

        // Retorn del préstec
        Manager.registrarRetornPrestec(prestec.getPrestecId(), LocalDate.now().plusDays(7));
        assertTrue(exemplar.isDisponible());
        assertFalse(prestec.isActiu());
    }

    @Test
    void testAddPrestecNoDisponible() {
        Biblioteca biblio = Manager.addBiblioteca("Central 2", "Barcelona", "C/ Ex. 2", "123456789", "biblio2@ex.com");
        Llibre llibre = Manager.addLlibre("ISBN003", "Llibre Ocupat", "Editorial Z", 2022);
        Exemplar exemplar = Manager.addExemplar("CB002", llibre, biblio);
        Persona persona1 = Manager.addPersona("11111111B", "Marc", "987654321", "marc@ex.com");
        Persona persona2 = Manager.addPersona("22222222C", "Laia", "987654322", "laia@ex.com");

        // Primer préstec
        Prestec p1 = Manager.addPrestec(exemplar, persona1, LocalDate.now(), LocalDate.now().plusDays(5));
        assertNotNull(p1);

        // Intent de segon préstec sobre el mateix exemplar
        Prestec p2 = Manager.addPrestec(exemplar, persona2, LocalDate.now(), LocalDate.now().plusDays(5));
        assertNull(p2);
    }
}
