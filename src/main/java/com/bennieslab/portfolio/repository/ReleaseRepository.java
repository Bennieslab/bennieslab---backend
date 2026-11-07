package com.bennieslab.portfolio.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Release;

@Repository
public interface ReleaseRepository extends JpaRepository<Release, Long>, JpaSpecificationExecutor<Release> {

    List<Release> findAllByOrderByPinnedDescSortOrderAscDatePostedDesc();

    Page<Release> findAll(Specification<Release> spec, Pageable pageable);

    List<Release> findAll(Specification<Release> spec);
}
