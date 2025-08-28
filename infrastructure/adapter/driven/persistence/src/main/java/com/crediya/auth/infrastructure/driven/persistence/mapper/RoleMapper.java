package com.crediya.auth.infrastructure.driven.persistence.mapper;

import com.crediya.auth.domain.model.Role;
import com.crediya.auth.infrastructure.driven.persistence.entity.RoleData;
import org.mapstruct.Mapper;

/**
 * A MapStruct mapper for converting between the Role domain model and the RoleData persistence entity.
 *
 * @Mapper(componentModel = "spring") tells MapStruct to generate an implementation
 * that is a Spring component, which can then be injected into our repository adapter.
 */
@Mapper(componentModel = "spring")
public interface RoleMapper {

    /**
     * Maps a RoleData entity to a Role domain model.
     *
     * @param roleData The persistence entity.
     * @return The corresponding Role domain model.
     */
    Role toDomain(RoleData roleData);

    /**
     * Maps a Role domain model to a RoleData entity.
     *
     * @param role The domain model.
     * @return The corresponding RoleData persistence entity.
     */
    RoleData toData(Role role);
}
