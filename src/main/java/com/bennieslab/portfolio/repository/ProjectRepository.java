package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Project;
import com.bennieslab.portfolio.repository.mini.ProjectMini;

@Repository
public interface ProjectRepository extends JpaRepository<Project, Long>, JpaSpecificationExecutor<Project> {

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Project> findAll();

    @Override
    @EntityGraph(attributePaths = "skills")
    Optional<Project> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Project> findAll(Specification<Project> spec);

    @Override
    @EntityGraph(attributePaths = "skills")
    Page<Project> findAll(Specification<Project> spec, Pageable pageable);

    @Query("SELECT p.id AS id, p.name AS name FROM Project p")
    List<ProjectMini> findAllProjectMini();
}