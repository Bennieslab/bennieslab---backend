package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>, JpaSpecificationExecutor<Post> {

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Post> findAll();

    @Override
    @EntityGraph(attributePaths = "skills")
    Optional<Post> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Post> findAll(Specification<Post> spec);

    @Override
    @EntityGraph(attributePaths = "skills")
    Page<Post> findAll(Specification<Post> spec, Pageable pageable);
}