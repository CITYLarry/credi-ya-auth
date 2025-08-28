package com.crediya.auth.infrastructure.driven.persistence.repository;

import com.crediya.auth.infrastructure.driven.persistence.entity.RoleData;
import org.springframework.data.r2dbc.repository.R2dbcRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * Spring Data R2DBC repository for the RoleData entity.
 */
@Repository
public interface RoleDataRepository extends R2dbcRepository<RoleData, Long> {

    /**
     * Finds a role by its unique name.
     *
     * @param name The name of the role to find.
     * @return A Mono emitting the found RoleData, or empty if not found.
     */
    Mono<RoleData> findByName(String name);
}
