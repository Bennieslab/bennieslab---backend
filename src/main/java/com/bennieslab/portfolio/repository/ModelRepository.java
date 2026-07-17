package com.bennieslab.portfolio.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.bennieslab.portfolio.model.Model;

public interface ModelRepository extends JpaRepository<Model, Long> {
}