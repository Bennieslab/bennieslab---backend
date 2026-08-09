package com.bennieslab.portfolio.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import com.bennieslab.portfolio.model.Model;

public interface ModelRepository extends JpaRepository<Model, Long>, JpaSpecificationExecutor<Model> {

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Model> findAll();

    @Override
    @EntityGraph(attributePaths = "skills")
    Optional<Model> findById(Long id);

    @Override
    @EntityGraph(attributePaths = "skills")
    List<Model> findAll(Specification<Model> spec);

    @Override
    @EntityGraph(attributePaths = "skills")
    Page<Model> findAll(Specification<Model> spec, Pageable pageable);
}