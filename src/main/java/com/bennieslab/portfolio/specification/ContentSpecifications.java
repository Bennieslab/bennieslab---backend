package com.bennieslab.portfolio.specification;

import org.springframework.data.jpa.domain.Specification;

/**
 * Shared Specification builders for content entities that support
 * category and skill filtering (Project, Post, Model).
 *
 * Each method returns a conjunction (match-all) when the corresponding
 * filter value is absent, so callers can chain them unconditionally.
 */
public final class ContentSpecifications {

    private ContentSpecifications() {
    }

    /**
     * Case-insensitive equality on a category field.
     * A null/blank/"all" value matches everything.
     */
    public static <T> Specification<T> categoryIs(String category, String fieldName) {
        return (root, query, cb) -> {
            if (category == null || category.isBlank() || "all".equalsIgnoreCase(category)) {
                return cb.conjunction();
            }
            return cb.equal(cb.lower(root.get(fieldName)), category.toLowerCase());
        };
    }

    /**
     * Matches entities that have a skill with the given id (via the
     * "skills" many-to-many collection). A null value matches everything.
     * Applies DISTINCT to avoid duplicate rows from the join when the
     * query is combined with pagination.
     */
    public static <T> Specification<T> skillIdIs(Long skillId) {
        return (root, query, cb) -> {
            if (skillId == null) {
                return cb.conjunction();
            }
            query.distinct(true);
            return cb.equal(root.join("skills").get("id"), skillId);
        };
    }
}