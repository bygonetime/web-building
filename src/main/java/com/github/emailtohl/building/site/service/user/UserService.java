package com.github.emailtohl.building.site.service.user;

import static com.github.emailtohl.building.site.entities.role.Authority.USER_CREATE_SPECIAL;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_DELETE;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_DISABLE;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_GRANT_ROLES;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_READ_ALL;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_READ_SELF;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_UPDATE_ALL;
import static com.github.emailtohl.building.site.entities.role.Authority.USER_UPDATE_SELF;

import java.util.List;
import java.util.Set;

import javax.transaction.Transactional;
import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.access.method.P;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.Constant;
import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.role.Role;
import com.github.emailtohl.building.site.entities.user.Customer;
import com.github.emailtohl.building.site.entities.user.Employee;
import com.github.emailtohl.building.site.entities.user.User;
/**
 * 用户管理的服务
 * @author HeLei
 * @date 2017.02.04
 */
@Transactional
@Validated
public interface UserService extends AuthenticationProvider, UserDetailsService {

	/**
	 * 创建雇员账号
	 * 注意：对于Spring Security来说，新增用户时，必须同时为其添加相应的用户授权，否则即便激活了该用户，也不会让其登录
	 * @param u
	 * @return
	 */
	@PreAuthorize("hasAuthority('" + USER_CREATE_SPECIAL + "')")
	Long addEmployee(@Valid Employee u);
	
	/**
	 * 注册普通账号，无需权限即可
	 * 注意：对于Spring Security来说，新增用户时，必须同时为其添加相应的用户授权，否则即便激活了该用户，也不会让其登录
	 * @param u
	 * @return
	 */
	Long addCustomer(@Valid Customer u);
	
	/**
	 * 启用用户，无需权限即可
	 * @param id
	 */
	void enableUser(@Min(value = 1L) Long id);
	
	/**
	 * 禁用用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + USER_DISABLE + "')")
	void disableUser(@Min(value = 1L) Long id);
	
	/**
	 * 授予用户角色
	 * @param roleNames
	 */
	@PreAuthorize("hasAuthority('" + USER_GRANT_ROLES + "')")
	void grantRoles(long id, String... roleNames);
	
	/**
	 * 新建用户时，授予普通用户角色
	 * @param id
	 */
	void grantUserRole(long id);
	
	/**
	 * 修改密码，限制只能本人才能修改
	 * 登录页面中通过邮箱方式修改密码在AuthenticationService接口中
	 * authentication是直接从SecurityContextHolder中获取的对象
	 * @param email
	 * @param newPassword
	 */
	@PreAuthorize("#email == authentication.principal.username")
	void changePassword(@P("email") String email, @NotNull @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 修改密码，用于用户忘记密码的场景，没有权限控制
	 * 由方法内部逻辑判断进行修改
	 * @param email
	 * @param newPassword
	 */
	void changePasswordByEmail(String email, @NotNull @Pattern(regexp = "^[^\\s&\"<>]+$") String newPassword);
	
	/**
	 * 删除用户
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + USER_DELETE + "')")
	void deleteUser(@Min(value = 1L) Long id);
	
	/**
	 * 查询用户，通过认证的均可调用
	 * returnObject和principal是spring security内置对象
	 * @param id
	 * @return
	 */
	@PostAuthorize("hasAuthority('" + USER_READ_ALL + "') || (hasAuthority('" + USER_READ_SELF + "') && returnObject.username == principal.username)")
	User getUser(@Min(value = 1L) Long id);
	
	/**
	 * 通过邮箱名查询用户，通过认证的均可调用
	 * 
	 * @param email
	 * @return
	 */
	@PostAuthorize("hasAuthority('" + USER_READ_ALL + "') || (hasAuthority('" + USER_READ_SELF + "') && #email == principal.username)")
	User getUserByEmail(@NotNull @P("email") String email);
	
	/**
	 * 修改用户的头像地址
	 * @param iconSrc 修改用户头像的地址
	 */
	@PreAuthorize("isAuthenticated()")
	void updateIconSrc(long id, String iconSrc);
	
	/**
	 * 将用户的头像二进制文件存入数据库
	 * @param icon 二进制图片文件
	 */
	@PreAuthorize("isAuthenticated()")
	void updateIcon(long id, byte[] icon);
	
	/**
	 * 修改用户
	 * 这里的方法名使用的是merge，传入的User参数只存储需要更新的属性，不更新的属性值为null
	 * 
	 * 修改密码，启用/禁用账户，授权功能，不走此接口
	 * 
	 * @param u中的id不能为null， u中属性不为null的值为修改项
	 */
	@PreAuthorize("hasAuthority('" + USER_UPDATE_ALL + "') || (hasAuthority('" + USER_UPDATE_SELF + "') && #email == principal.username)")
	void mergeEmployee(@NotNull @P("email") String email, Employee emp);
	
	/**
	 * 修改用户
	 * 这里的方法名使用的是merge，传入的User参数只存储需要更新的属性，不更新的属性值为null
	 * 
	 * 修改密码，启用/禁用账户，授权功能，不走此接口
	 * 
	 * @param u中的id不能为null， u中属性不为null的值为修改项
	 */
	@PreAuthorize("hasAuthority('" + USER_UPDATE_ALL + "') || (hasAuthority('" + USER_UPDATE_SELF + "') && #email == principal.username)")
	void mergeCustomer(@NotNull @P("email") String email, Customer cus);
	
	/**
	 * 获取用户Pager
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	@PreAuthorize("isAuthenticated()")
	Pager<User> getUserPager(User u, Pageable pageable);
	
	/**
	 * 获取用户Page,这里的Page是Spring Data提供的数据结构
	 * 
	 * 实现类中要对Pager中返回的List中敏感信息进行过滤
	 * 
	 * @param u
	 * @param pageable
	 * @return
	 */
	@NotNull
	@PreAuthorize("isAuthenticated()")
	Page<User> getUserPage(User u, Pageable pageable);
	
	/**
	 * 检查该邮箱是否注册
	 * @param email
	 * @return
	 */
	boolean isExist(@Pattern(regexp = Constant.PATTERN_EMAIL, flags = { Pattern.Flag.CASE_INSENSITIVE }) String email);
	
	/**
	 * 通过用户邮箱名和角色名组合查询Pager
	 * @param email
	 * @param roles
	 * @param pageable
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	Pager<User> getPageByRoles(String email, Set<String> roleNames, Pageable pageable);
	
	/**
	 * 获取用户角色
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	List<Role> getRoles();
	
	/**
	 * 认证（登录）
	 * @param email
	 * @param password
	 * @return
	 * @throws AuthenticationException 认证失败抛出异常
	 */
	Authentication authenticate(String email, String password) throws AuthenticationException;
	
	/**
	 * 上传用户的公钥
	 * @param publicKey
	 * @param module
	 */
	@PreAuthorize("isAuthenticated()")
	void setPublicKey(@NotNull String publicKey);
	
	/**
	 * 删除用户的公钥
	 * @param publicKey
	 * @param module
	 */
	@PreAuthorize("isAuthenticated()")
	void clearPublicKey();
	
}
