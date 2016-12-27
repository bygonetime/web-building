package com.github.emailtohl.building.site.controller;

import javax.inject.Inject;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.common.jpa.envers.Tuple;
import com.github.emailtohl.building.site.entities.BaseEntity;
import com.github.emailtohl.building.site.entities.Role;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.AuditedService;

/**
 * 查阅Hibernate Envers产生的审计记录
 * 
 * @author HeLei
 */
@RestController
@RequestMapping("audited")
public class AuditCtrl {
	private AuditedService auditedService;
	
	@Inject
	public void setAuditedService(AuditedService auditedService) {
		this.auditedService = auditedService;
	}

	/**
	 * 根据User的email查询某实体所有历史记录
	 * 
	 * @param email
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "userRevision", method = RequestMethod.GET)
	public Pager<Tuple<User>> getUserRevision(@RequestParam String email,
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<Tuple<User>> page = auditedService.getUserRevision(email, pageable);
		return new Pager<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
	}

	/**
	 * 查询User某个修订版下所有的历史记录
	 * 
	 * @param revision
	 * @param email
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "usersAtRevision", method = RequestMethod.GET)
	public Pager<User> getUsersAtRevision(@RequestParam Number revision, @RequestParam String email,
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<User> page = auditedService.getUsersAtRevision(revision, email, pageable);
		return new Pager<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
	}

	/**
	 * 查询User在某个修订版时的历史记录
	 * 
	 * @param userId
	 * @param revision
	 * @return
	 */
	@RequestMapping(value = "userAtRevision", method = RequestMethod.GET)
	public User getUserAtRevision(@RequestParam Long userId, @RequestParam Number revision) {
		return auditedService.getUserAtRevision(userId, revision);
	}

	/**
	 * 根据Role的名字查询某实体所有历史记录
	 * 
	 * @param name 实体属性名和属性值
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "roleRevision", method = RequestMethod.GET)
	public Pager<Tuple<Role>> getRoleRevision(@RequestParam String name,
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<Tuple<Role>> page = auditedService.getRoleRevision(name, pageable);
		return new Pager<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
	}

	/**
	 * 查询Role修订版下所有的历史记录
	 * 
	 * @param revision
	 * @param name
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "rolesAtRevision", method = RequestMethod.GET)
	public Pager<Role> getRolesAtRevision(@RequestParam Number revision, @RequestParam String name,
			@PageableDefault(page = 0, size = 20, sort = BaseEntity.CREATE_DATE_PROPERTY_NAME, direction = Direction.DESC) Pageable pageable) {
		Page<Role> page = auditedService.getRolesAtRevision(revision, name, pageable);
		return new Pager<>(page.getContent(), page.getTotalElements(), page.getNumber(), page.getSize());
	}

	/**
	 * 查询Role在某个修订版时的历史记录
	 * 
	 * @param id
	 * @param revision
	 * @return
	 */
	@RequestMapping(value = "roleAtRevision", method = RequestMethod.GET)
	public Role getRoleAtRevision(@RequestParam Long roleId, @RequestParam Number revision) {
		return auditedService.getRoleAtRevision(roleId, revision);
	}
	
}
