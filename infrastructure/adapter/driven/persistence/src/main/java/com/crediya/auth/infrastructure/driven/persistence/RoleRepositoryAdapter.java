package com.crediya.auth.infrastructure.driven.persistence;

import com.crediya.auth.domain.model.Role;
import com.crediya.auth.domain.ports.out.RoleRepository;
import com.crediya.auth.infrastructure.driven.persistence.mapper.RoleMapper;
import com.crediya.auth.infrastructure.driven.persistence.repository.RoleDataRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

/**
 * This is the driven adapter that implements the RoleRepository outbound port.
 */
@Repository
@RequiredArgsConstructor
public class RoleRepositoryAdapter implements RoleRepository {

    private final RoleDataRepository roleDataRepository;
    private final RoleMapper roleMapper;

    /**
     * Finds a role by its name by calling the Spring Data repository and then
     * mapping the resulting RoleData entity back to a Role domain model.
     *
     * @param name The name of the role to find.
     * @return A Mono emitting the found Role domain model.
     */
    @Override
    public Mono<Role> findByName(String name) {
        return roleDataRepository.findByName(name)
                .map(roleMapper::toDomain);
    }
}
