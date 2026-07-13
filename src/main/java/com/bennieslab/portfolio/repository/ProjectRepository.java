package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Project;
import com.bennieslab.portfolio.repository.mini.ProjectMini;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long> {

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Project> findAll();

    @Override
    @EntityGraph(attributePaths = "skills")
    Optional<Project> findById(Long id);

    /**
     * Paginated, sorted fetch: pinned DESC → sortOrder ASC → datePosted DESC.
     * The countQuery is necessary to avoid Hibernate joining the skills
     * collection in the COUNT query (which would inflate row counts).
     */
    @EntityGraph(attributePaths = "skills")
    @Query(value = "SELECT p FROM Project p ORDER BY p.pinned DESC, p.sortOrder ASC, p.datePosted DESC",
           countQuery = "SELECT COUNT(p) FROM Project p")
    Page<Project> findAllSorted(Pageable pageable);

    @Query("SELECT p.id AS id, p.name AS name FROM Project p")
    List<ProjectMini> findAllProjectMini();
}
