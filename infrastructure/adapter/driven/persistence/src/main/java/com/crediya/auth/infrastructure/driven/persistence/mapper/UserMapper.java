package com.citylarry.mapper;

import com.crediya.auth.domain.model.User;
import com.citylarry.entity.UserData;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * A MapStruct mapper interface for converting between the User domain model and the UserData persistence entity.
 *
 * @Mapper(componentModel = "spring") tells MapStruct to generate an implementation that is a Spring component, which can be injected into other beans.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

    /**
     * Maps a UserData entity to a User domain model.
     * MapStruct automatically maps fields with the same name.
     *
     * @param userData The persistence entity.
     * @return The corresponding User domain model.
     */
    User toDomain(UserData userData);

    /**
     * Maps a User domain model to a UserData entity.
     *
     * @param user The domain model.
     * @return The corresponding UserData persistence entity.
     */
    @Mapping(target = "id", ignore = true)
    UserData toData(User user);
}

