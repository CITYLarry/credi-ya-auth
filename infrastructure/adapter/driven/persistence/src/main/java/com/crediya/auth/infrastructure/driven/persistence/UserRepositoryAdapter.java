package com.crediya.auth.infrastructure.driven.persistence;

import com.crediya.auth.domain.model.Role;
import com.crediya.auth.infrastructure.driven.persistence.entity.UserData;
import com.crediya.auth.infrastructure.driven.persistence.mapper.RoleMapper;
import com.crediya.auth.infrastructure.driven.persistence.mapper.UserMapper;
import com.crediya.auth.infrastructure.driven.persistence.repository.RoleDataRepository;
import com.crediya.auth.infrastructure.driven.persistence.repository.UserDataRepository;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.domain.ports.out.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;


/**
 * This is the driven adapter that implements the UserRepository outbound port.
 *
 * @Repository marks this as a Spring component for persistence.
 */
@Repository
@RequiredArgsConstructor
public class UserRepositoryAdapter implements UserRepository {

    private final UserDataRepository userDataRepository;
    private final RoleDataRepository roleDataRepository;
    private final UserMapper userMapper;
    private final RoleMapper roleMapper;

    /**
     * Checks if a user with the given email already exists.
     *
     * @param email The email to check.
     * @return A reactive stream emitting true if the email exists, false otherwise.
     */
    @Override
    public Mono<Boolean> existsByEmail(String email) {
        return this.userDataRepository.existsByEmail(email);
    }

    /**
     * Persists a new User object.
     *
     * @param user The domain model object to save.
     * @return A reactive stream emitting the saved User, potentially with updated state from the database (like an ID).
     */
    @Override
    public Mono<User> save(User user) {

        UserData userDataToSave = userMapper.toData(user);

        return userDataRepository
                .save(userDataToSave)
                .map(savedUserData -> new User(
                        savedUserData.getId(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getEmail(),
                        user.getPassword(),
                        user.getIdentityNumber(),
                        user.getPhoneNumber(),
                        user.getBirthDate(),
                        user.getAddress(),
                        user.getRole(),
                        user.getBaseSalary()
                ));
    }

    /**
     * Finds a user by email and composes the full User domain object, including its Role.
     *
     * @param email The email of the user to find.
     * @return A Mono emitting the complete User object, or empty if not found.
     */
    @Override
    public Mono<User> findByEmail(String email) {
        return userDataRepository.findByEmail(email)
                .flatMap(userData ->
                        // Once we have the user data, fetch the corresponding role data.
                        Mono.zip(
                                Mono.just(userData),
                                roleDataRepository.findById(userData.getIdRole())
                        )
                )
                .map(tuple -> {
                    // With both UserData and RoleData, we can build the complete User domain object.
                    UserData userData = tuple.getT1();
                    Role role = roleMapper.toDomain(tuple.getT2());

                    // The mapper gives us a partial User; we complete it with the Role.
                    return new User(
                            userData.getId(), userData.getFirstName(), userData.getLastName(),
                            userData.getEmail(), userData.getPassword(), userData.getIdentityNumber(),
                            userData.getPhoneNumber(), userData.getBirthDate(), userData.getAddress(),
                            role, userData.getBaseSalary()
                    );
                });
    }
}
