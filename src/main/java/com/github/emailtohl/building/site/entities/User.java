package com.github.emailtohl.building.site.entities;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Access;
import javax.persistence.AccessType;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.DiscriminatorColumn;
import javax.persistence.DiscriminatorType;
import javax.persistence.DiscriminatorValue;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.Lob;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Past;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.hibernate.search.annotations.Field;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.security.core.userdetails.UserDetails;

import com.github.emailtohl.building.common.Constant;

/**
 * Entity 用户
 * javax校验的注解在field上，JPA约束的注解写在JavaBean属性上
 * @author HeLei
 */
@Entity
@Table(name = "t_user")
@Access(AccessType.PROPERTY) // 实际上这就是默认的配置
//指定继承的映射策略，所有继承树上的实体共用一张表：SINGLE_TABLE，这是默认值
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
//定义辨别者列的列名为“user_type”，列类型是字符串
@DiscriminatorColumn(name = "user_type", discriminatorType = DiscriminatorType.STRING)
//指定User实体对应的记录在辨别者列的值是“user”
@DiscriminatorValue("user") // 若不注解，则默认使用实体名
public class User extends BaseEntity implements Authentication/* 实现Authentication接口可以被Spring security的安全管理器使用 */ {
	private static final long serialVersionUID = -2648409468140926726L;
	public enum Gender {
		MALE, FEMALE, UNSPECIFIED
	}
	protected String name;
	protected String username;
	@NotNull// 校验
	@Pattern(// 校验
		regexp = Constant.PATTERN_EMAIL,
		flags = {Pattern.Flag.CASE_INSENSITIVE}
	)
	protected String email;
	protected String address;
	protected String telephone;
	@Size(min = 6)
	@Pattern(regexp = "^[^\\s&\"<>]+$")
	protected transient String password;
	protected Boolean enabled;
	@Past// 校验，日期相对于当前较早
	protected Date birthday;
	@Min(value = 1)
	@Max(value = 120)
	protected Integer age;
	protected Gender gender;
	@Valid
	protected Subsidiary subsidiary;
	@Size(max = 1048576)
	protected transient byte[] icon;
	protected String iconSrc;
	@Size(max = 300)
	protected String description;
//	protected Set<Authority> authorities = new HashSet<Authority>();
	Set<Role> roles = new HashSet<Role>();
	
	@Field
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Field
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	
	@Field
	@Column(nullable = false, unique = true)
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	
	public String getTelephone() {
		return telephone;
	}
	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}
	
    public Boolean getEnabled() {
		return enabled;
	}
	public void setEnabled(Boolean enabled) {
		this.enabled = enabled;
	}
	
	@Column(nullable = false)
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Temporal(TemporalType.DATE)
	public Date getBirthday() {
		return birthday;
	}
	public void setBirthday(Date birthday) {
		this.birthday = birthday;
	}
	
	public Integer getAge() {
		Integer age;
		if (this.birthday != null) {
			Instant now = Instant.now();
			Instant past = Instant.ofEpochMilli(this.birthday.getTime());
			long daysBetween = ChronoUnit.DAYS.between(past, now);
			age = (int) (daysBetween / 365);
		} else {
			age = this.age;
		}
		return age;
	}
	public void setAge(Integer age) {
		this.age = age;
	}
	
	// 枚举存入数据库默认为序号，这里指明将枚举名以字符串存入数据库
	@Enumerated(EnumType.STRING)
	public Gender getGender() {
		return gender;
	}
	public void setGender(Gender gender) {
		this.gender = gender;
	}
	
	@Embedded
	/*嵌入属性的映射可在嵌入实体中声明，不必要在此覆盖
	@AttributeOverrides({
		@AttributeOverride(name = "city", column = @Column(name = "subsidiary_city")),
		@AttributeOverride(name = "province", column = @Column(name = "subsidiary_province"))
	})*/
	public Subsidiary getSubsidiary() {
		return subsidiary;
	}
	public void setSubsidiary(Subsidiary subsidiary) {
		this.subsidiary = subsidiary;
	}
	
	@Lob
	@Basic(fetch = FetchType.LAZY)
	public byte[] getIcon() {
		return icon;
	}
	public void setIcon(byte[] icon) {
		this.icon = icon;
	}
	
	public String getIconSrc() {
		return iconSrc;
	}
	public void setIconSrc(String iconSrc) {
		this.iconSrc = iconSrc;
	}
	
	@Field
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
/*	
	// 此属性暂时为目前配置的spring security提供服务
	@ElementCollection(targetClass = Authority.class, fetch = FetchType.EAGER)
	// @CollectionTable是可选项，若不做注解，JPA提供者则会根据自动生成连接表的表名以及对应的列名
	@CollectionTable(name = "t_user_authority"
	, joinColumns = {
			// 注意，这里定义的是对主键的引用，非主键不能写在这里，否则查询会异常
			@JoinColumn(name = "user_id", referencedColumnName = "id")
		})
	@Enumerated(EnumType.STRING)// 若不指定此项，数据表中默认存储枚举的序号
	@Column(name = "authority")// 若不加此项，连接表中默认为authorities
	public Set<Authority> getAuthorities() {
		return authorities;
	}
	public void setAuthorities(Set<Authority> authorities) {
		this.authorities = authorities;
	}
	*/
	
	@ManyToMany(targetEntity = Role.class, fetch = FetchType.EAGER, cascade = CascadeType.ALL)
	@JoinTable(name = "t_user_role"
	, joinColumns = { @JoinColumn(name = "user_id", referencedColumnName = "id") }
	, inverseJoinColumns = { @JoinColumn(name = "role_id", referencedColumnName = "id") })
	public Set<Role> getRoles() {
		return roles;
	}
	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}
	
	/**
	 * 由于多对多关系，可通过本方法直接获取该用户的授权
	 * @return
	 */
	public Set<Authority> authoritySet() {
		Set<Authority> set = new HashSet<Authority>();
		for (Role r : roles) {
			set.addAll(r.getAuthorities());
		}
		return set;
	}
	
	/**
	 * 由于多对多关系，可通过本方法直接获取该用户的授权
	 * @return
	 */
	public Set<String> authorities() {
		Set<Authority> set = authoritySet();
		return set.stream().map(a -> a.getName()).collect(Collectors.toSet());
	}
	
	@Override
	public String toString() {
		return "User [name=" + name + ", username=" + username + ", email=" + email + ", address=" + address
				+ ", telephone=" + telephone + ", enabled=" + enabled + ", birthday=" + birthday + ", age=" + age
				+ ", gender=" + gender + ", subsidiary=" + subsidiary + ", iconSrc=" + iconSrc + ", description="
				+ description + ", roles=" + roles + "]";
	}
	
	/**
	 * 下面是实现Authentication的方法
	 */
	@Transient
	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<String> set = authorities();
		return AuthorityUtils.createAuthorityList(set.toArray(new String[set.size()]));
	}
	@Transient
	@Override
	public Object getCredentials() {
		// 认证的时候存储密码，用过之后会擦除，所以直接返回null
		return password;
	}
	
	@Transient
	private Object details;
	@Transient
	@Override
	public Object getDetails() {
		/*
		 * Stores additional details about the authentication request.
		 * These might be an IP address, certificate serial number etc.
		 */
		return details;
	}
	@Transient
	public void setDetails(Object details) {
		this.details = details;
	}
	
	@Transient
	@Override
	public Object getPrincipal() {
		/*
		 * The identity of the principal being authenticated. In the
		 * case of an authentication request with username and password,
		 * this would be the username. Callers are expected to populate
		 * the principal for an authentication request.
		 * 按照描述，getPrincipal()返回的应该是某种形式的用户名
		 * 但是spring security需要在这个返回中获取更多的用户信息，结构是
		 * org.springframework.security.core.userdetails.UserDetails
		 */
		return new UserDetails() {
			private static final long serialVersionUID = -6107779964176713375L;

			@Override
			public Collection<? extends GrantedAuthority> getAuthorities() {
				return getAuthorities();
			}

			@Override
			public String getPassword() {
				return password;
			}

			@Override
			public String getUsername() {
				return username;
			}

			@SuppressWarnings("unused")
			private boolean accountNonExpired = true;
			@Override
			public boolean isAccountNonExpired() {
				return true;
			}
			@SuppressWarnings("unused")
			public void setAccountNonExpired(boolean accountNonExpired) {
				this.accountNonExpired = accountNonExpired;
			}

			private boolean accountNonLocked = true;
			@Override
			public boolean isAccountNonLocked() {
				return accountNonLocked;
			}
			@SuppressWarnings("unused")
			public void setAccountNonLocked(boolean accountNonLocked) {
				this.accountNonLocked = accountNonLocked;
			}

			private boolean credentialsNonExpired = true;
			@Override
			public boolean isCredentialsNonExpired() {
				return credentialsNonExpired;
			}
			@SuppressWarnings("unused")
			public void setCredentialsNonExpired(boolean credentialsNonExpired) {
				this.credentialsNonExpired = credentialsNonExpired;
			}

			@Override
			public boolean isEnabled() {
				return enabled;
			}
			
		};
		
	}
	
	@Transient
	private boolean authenticated = false;
	@Transient
	@Override
	public boolean isAuthenticated() {
		return authenticated;
	}
	@Transient
	@Override
	public void setAuthenticated(boolean authenticated) throws IllegalArgumentException {
		this.authenticated = authenticated;
	}
	
}
