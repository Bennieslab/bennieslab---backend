package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long>{
    @Override
    @EntityGraph(attributePaths = "skills")
    List<Post> findAll();

    @Override
    @EntityGraph(attributePaths = "skills")
    Optional<Post> findById(Long id);

}
