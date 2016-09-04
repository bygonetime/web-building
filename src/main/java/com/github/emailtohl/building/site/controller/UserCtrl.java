package com.github.emailtohl.building.site.controller;

import static org.springframework.web.bind.annotation.RequestMethod.DELETE;
import static org.springframework.web.bind.annotation.RequestMethod.GET;
import static org.springframework.web.bind.annotation.RequestMethod.OPTIONS;
import static org.springframework.web.bind.annotation.RequestMethod.POST;
import static org.springframework.web.bind.annotation.RequestMethod.PUT;

import javax.inject.Inject;
import javax.validation.Valid;
import javax.validation.constraints.Min;

import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.github.emailtohl.building.common.repository.jpa.Pager;
import com.github.emailtohl.building.exception.ResourceNotFoundException;
import com.github.emailtohl.building.site.entities.User;
import com.github.emailtohl.building.site.service.UserService;

/**
 * 用户管理的控制器
 * @author Helei
 */
@Controller
@RequestMapping("user")
public class UserCtrl {
	@Inject
	UserService userService;
	
	public String getCurrentUsername() {
		String username = null;
		Authentication a = SecurityContextHolder.getContext().getAuthentication();
		if (a != null) {
			username = a.getName();
		}
		return username;
	}
	/**
	 * 查询user资源下提供哪些方法
	 * @return
	 */
	@RequestMapping(value = "", method = OPTIONS)
	public ResponseEntity<Void> discover() {
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,HEAD,GET");
		return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
	}
	/**
	 * 查询user/id下支持哪些方法
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "{id}", method = OPTIONS)
	public ResponseEntity<Void> discover(@PathVariable("id") long id) {
		if (userService.getUser(id) == null)
			throw new ResourceNotFoundException();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Allow", "OPTIONS,HEAD,GET,PUT,DELETE");
		return new ResponseEntity<>(null, headers, HttpStatus.NO_CONTENT);
	}
	/**
	 * 通过id获取User
	 * @param id
	 * @return
	 */
	@RequestMapping(value = "{id}", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public User getUserById(@PathVariable("id") Long id) {
		User u = userService.getUser(id);
		if (u == null) {
			throw new ResourceNotFoundException();
		}
		return u;
	}
	/**
	 * 通过email获取User
	 * @param email
	 * @return
	 */
	@RequestMapping(value = "{email}", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public User getUserByEmail(@PathVariable("email") String email) {
		User u = userService.getUserByEmail(email);
		if (u == null) {
			throw new ResourceNotFoundException();
		}
		return u;
	}
	/**
	 * 获取分页对象
	 * @param u
	 * @param pageable
	 * @return
	 */
	@RequestMapping(value = "pager", method = GET)
	@ResponseBody
	@ResponseStatus(HttpStatus.OK)
	public Pager<User> getUserPager(@ModelAttribute User u, Pageable pageable) {
		return userService.getUserPager(u, pageable);
	}
	/**
	 * 新增一个User
	 * @param u
	 * @return
	 */
	@RequestMapping(value = "", method = POST)
	public ResponseEntity<?> addUser(@RequestBody @Valid User u, Errors e) {
		if (e.hasErrors()) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
		Long id = userService.addUser(u);
		String uri = ServletUriComponentsBuilder.fromCurrentServletMapping().path("/user/{id}")
				.buildAndExpand(id).toString();
		HttpHeaders headers = new HttpHeaders();
		headers.add("Location", uri);
		return new ResponseEntity<>(u, headers, HttpStatus.CREATED);
	}
	/**
	 * 修改一个User
	 * @param id
	 * @param user
	 */
	@RequestMapping(value = "{id}", method = PUT)
	public ResponseEntity<?> update(@PathVariable("id") @Min(1L) long id, @Valid @RequestBody User user, Errors e) {
		if (e.hasErrors()) {
			return new ResponseEntity<>(e, HttpStatus.BAD_REQUEST);
		}
		userService.mergeUser(id, user);
		return new ResponseEntity<Void>(HttpStatus.NO_CONTENT);
	}
	/**
	 * 删除一个User
	 * @param id
	 */
	@RequestMapping(value = "{id}", method = DELETE)
	@ResponseStatus(HttpStatus.NO_CONTENT)
	public void delete(@PathVariable("id") long id) {
		userService.deleteUser(id);
	}
}
