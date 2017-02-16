package com.github.emailtohl.building.site.service.cms;

import static com.github.emailtohl.building.site.entities.role.Authority.FORUM_DELETE;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.constraints.NotNull;

import org.springframework.data.domain.Pageable;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;

import com.github.emailtohl.building.common.jpa.Pager;
import com.github.emailtohl.building.site.entities.cms.Article;

/**
 * cms的服务层接口
 * @author HeLei
 * @data 2017.02.15
 */
@Validated
@Transactional
public interface CmsService {
	/**
	 * 获取某文章
	 * @param id
	 * @return
	 */
	Article findOne(long id);
	
	/**
	 * 查询所有符合标准的对象
	 * @param query
	 * @return
	 */
	List<Article> findAll(String query);
	
	/**
	 * 全文搜索
	 * @param query
	 * @param pageable
	 * @return 只返回查找到的实体类E
	 */
	Pager<Article> find(String query, Pageable pageable);
	
	/**
	 * 保存文章，从安全上下文中查找用户名
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long save(@NotNull String title, String keywords, String body, String type);
	
	/**
	 * 保存文章
	 * @param email
	 * @param title
	 * @param keywords
	 * @param body
	 * @param type
	 * @return
	 */
	@PreAuthorize("isAuthenticated()")
	long save(@NotNull String email, @NotNull String title, String keywords, String body, String type);
	
	/**
	 * 修改某文章
	 * @param id
	 * @param article
	 */
	@PreAuthorize("isAuthenticated()")
	void update(long id, Article article);
	
	/**
	 * 特殊情况下用于管理员删除文章
	 * @param id
	 */
	@PreAuthorize("hasAuthority('" + FORUM_DELETE + "')")
	void delete(long id);
	
}