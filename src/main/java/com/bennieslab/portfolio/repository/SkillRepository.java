package com.bennieslab.portfolio.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.bennieslab.portfolio.model.Skill;

@Repository
public interface SkillRepository extends JpaRepository<Skill, Long> {

    /**
     * Paginated, sorted fetch: pinned DESC → sortOrder ASC → name ASC.
     */
    @Query(value = "SELECT s FROM Skill s ORDER BY s.pinned DESC, s.sortOrder ASC, s.name ASC",
           countQuery = "SELECT COUNT(s) FROM Skill s")
    Page<Skill> findAllSorted(Pageable pageable);
}
