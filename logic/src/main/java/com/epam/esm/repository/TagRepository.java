package com.epam.esm.repository;

import com.epam.esm.entity.Tag;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface TagRepository extends PagingAndSortingRepository<Tag, Long> {
    boolean existsByName(String name);

    Page<Tag> findByNameLike(String name, Pageable pageable);

    @Query(nativeQuery = true,
    value = "SELECT tag.id, tag.name FROM tag " +
            "INNER JOIN gift_certificate_has_tag ON tag_id=tag.id " +
            "INNER JOIN orders ON gift_certificate_id=certificate_id " +
            "WHERE user_id= :user_id " +
            "GROUP BY tag.id ORDER BY COUNT(tag.id) DESC LIMIT 1")
    Optional<Tag> findSuperTag(@Param("user_id") long userId);
}
