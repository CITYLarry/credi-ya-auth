package com.crediya.auth.domain.ports.out;

import com.crediya.auth.domain.model.Role;
import reactor.core.publisher.Mono;

/**
 * An outbound port that defines the contract for role persistence operations.
 */
public interface RoleRepository {

    /**
     * Finds a role by its unique name.
     *
     * @param name The name of the role to find (e.g., "ADMIN", "CLIENT").
     * @return A reactive stream emitting the found Role, or an empty Mono if not found.
     */
    Mono<Role> findByName(String name);
}
