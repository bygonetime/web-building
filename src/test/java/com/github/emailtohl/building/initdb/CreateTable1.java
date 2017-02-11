package com.github.emailtohl.building.initdb;

import static com.github.emailtohl.building.initdb.PersistenceData.*;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
/**
 * 创建数据库表和测试数据
 * @author HeLei
 * @date 2017.02.04
 */
public class CreateTable1 {

	public void init() {
		EntityManagerFactory entityManagerFactory = Persistence.createEntityManagerFactory("building-unit");
		EntityManager entityManager = entityManagerFactory.createEntityManager();
		entityManager.getTransaction().begin();

		entityManager.persist(user_role_authority_allocation);
		entityManager.persist(user_create_ordinary);
		entityManager.persist(user_create_special);
		entityManager.persist(user_enable);
		entityManager.persist(user_disable);
		entityManager.persist(user_grant_roles);
		entityManager.persist(user_read_all);
		entityManager.persist(user_read_self);
		entityManager.persist(user_update_all);
		entityManager.persist(user_update_self);
		entityManager.persist(user_delete);
		entityManager.persist(user_customer);
		entityManager.persist(application_form_transit);
		entityManager.persist(application_form_read_history);
		entityManager.persist(application_form_delete);
		entityManager.persist(forum_delete);
		entityManager.persist(audit_user);
		entityManager.persist(audit_role);
		entityManager.persist(resource_manager);
		
		entityManager.persist(admin);
		entityManager.persist(manager);
		entityManager.persist(employee);
		entityManager.persist(user);
		
		entityManager.persist(company);
		entityManager.persist(product);
		entityManager.persist(qa);
		entityManager.persist(emailtohl);
		entityManager.persist(foo);
		entityManager.persist(bar);
		entityManager.persist(baz);
		entityManager.persist(qux);
	
		entityManager.persist(article);

		entityManager.getTransaction().commit();
		entityManager.close();
		entityManagerFactory.close();
	}

	public static void main(String[] args) throws Exception {
		CreateTable1 ct = new CreateTable1();
		ct.init();
	}

}
