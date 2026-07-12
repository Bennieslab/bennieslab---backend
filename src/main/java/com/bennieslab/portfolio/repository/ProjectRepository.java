package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

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

    @Query("SELECT p.id AS id, p.name AS name FROM Project p")
    List<ProjectMini> findAllProjectMini();
}
