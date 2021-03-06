package com.github.emailtohl.building.site.dao.cms;

import org.springframework.data.jpa.repository.JpaRepository;

import com.github.emailtohl.building.site.entities.cms.Comment;

/**
 * 文章评论的数据访问接口
 * @author HeLei
 * @date 2017.02.17
 */
public interface CommentRepository extends JpaRepository<Comment, Long> {

}
