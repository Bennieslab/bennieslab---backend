package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Post;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Post> findAll();

    @Override
    @EntityGraph(attributePaths = "skills")
    Optional<Post> findById(Long id);

    /**
     * Paginated, sorted fetch: pinned DESC → sortOrder ASC → datePosted DESC.
     */
    @EntityGraph(attributePaths = "skills")
    @Query(value = "SELECT p FROM Post p ORDER BY p.pinned DESC, p.sortOrder ASC, p.datePosted DESC",
           countQuery = "SELECT COUNT(p) FROM Post p")
    Page<Post> findAllSorted(Pageable pageable);
}
