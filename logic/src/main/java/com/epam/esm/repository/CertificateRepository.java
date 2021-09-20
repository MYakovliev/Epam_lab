package com.epam.esm.repository;

import com.epam.esm.entity.Certificate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CertificateRepository extends PagingAndSortingRepository<Certificate, Long> {
    @Query(nativeQuery = true, value = "SELECT g.id, g.name," +
            " description, price, duration, create_date, last_update_date " +
            "FROM gift_certificate g INNER JOIN gift_certificate_has_tag ON id = gift_certificate_id " +
            "INNER JOIN tag ON gift_certificate_has_tag.tag_id = tag.id " +
            "WHERE (g.name LIKE CONCAT('%', :search, '%') OR description LIKE CONCAT('%', :search, '%')) " +
            "AND tag.name IN (:tag_names) GROUP BY g.id " +
            "HAVING COUNT(DISTINCT tag_id)=:tag_amount ")
    Page<Certificate> findAllByParametersWithTags(@Param("search") String search, @Param("tag_names") List<String> tags,
                                                  @Param("tag_amount") int amount, Pageable pageable);

    Page<Certificate> findAllByNameLikeOrDescriptionLike(String searchName, String searchDescription, Pageable pageable);

}
