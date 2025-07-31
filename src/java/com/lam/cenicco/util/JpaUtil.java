/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.lam.cenicco.util;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;

/**
 *
 * @author JoséAntonio
 */
public class JpaUtil {

    private JpaUtil() {
    }

    private static class JpaUtilHolder {

        private static final JpaUtil INSTANCE = new JpaUtil();
        private static final EntityManagerFactory EMF = Persistence.createEntityManagerFactory("CeniccoPU");
    }

    public static JpaUtil getInstance() {
        return JpaUtilHolder.INSTANCE;
    }

    public EntityManager createEntityManager() {
        return JpaUtilHolder.EMF.createEntityManager();
    }

    public void closeEntityManager(EntityManager em) {
        if (em != null) {
            em.close();
        }
    }

    public void rollbackEntityManager(EntityManager em) {
        if (em != null) {
            em.getTransaction().rollback();
        }
    }
}
