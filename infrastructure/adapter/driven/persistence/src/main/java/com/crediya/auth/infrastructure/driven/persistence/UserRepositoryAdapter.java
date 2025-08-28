package com.crediya.auth.infrastructure.driven.persistence;

import com.crediya.auth.infrastructure.driven.persistence.entity.UserData;
import com.crediya.auth.infrastructure.driven.persistence.mapper.UserMapper;
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
    private final UserMapper userMapper;

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
                        user.getIdentityNumber(),
                        user.getPhoneNumber(),
                        user.getBirthDate(),
                        user.getAddress(),
                        user.getRole(),
                        user.getBaseSalary()
                ));
    }
}
