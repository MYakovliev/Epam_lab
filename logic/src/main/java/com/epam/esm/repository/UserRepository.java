package com.epam.esm.repository;

import com.epam.esm.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface UserRepository extends PagingAndSortingRepository<User, Long> {
    Page<User> findAllByNameLike(String name, Pageable pageable);

    Optional<User> findByLogin(String login);

    boolean existsByLogin(String login);

    @Query(nativeQuery = true,
            value = "SELECT user.id, user.name, user.login, user.password FROM user " +
                    "    INNER JOIN orders ON orders.user_id=user.id GROUP BY user.id " +
                    "ORDER BY SUM(price) DESC LIMIT 1")
    Optional<User> findSuperUser();
}
