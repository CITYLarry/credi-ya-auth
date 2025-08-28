package com.crediya.auth.infrastructure.driven.persistence;

import com.crediya.auth.domain.model.Role;
import com.crediya.auth.domain.model.User;
import com.crediya.auth.infrastructure.driven.persistence.entity.UserData;
import com.crediya.auth.infrastructure.driven.persistence.mapper.UserMapperImpl;
import com.crediya.auth.infrastructure.driven.persistence.mapper.RoleMapperImpl;
import com.crediya.auth.infrastructure.driven.persistence.repository.RoleDataRepository;
import com.crediya.auth.infrastructure.driven.persistence.repository.UserDataRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.test.autoconfigure.data.r2dbc.DataR2dbcTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.r2dbc.repository.config.EnableR2dbcRepositories;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Integration tests for the UserRepositoryAdapter.
 * @DataR2dbcTest loads the persistence context, including an in-memory H2 database.
 */
@DataR2dbcTest
@Import({
        UserRepositoryAdapter.class, UserMapperImpl.class,
        RoleRepositoryAdapter.class, RoleMapperImpl.class
})
public class UserRepositoryAdapterTest {

    @SpringBootApplication
    @EnableR2dbcRepositories
    static class TestConfiguration {}

    @Autowired
    private UserDataRepository userDataRepository;

    @Autowired
    private RoleDataRepository roleDataRepository;

    @Autowired
    private UserRepositoryAdapter userRepositoryAdapter;

    private Role clientRole;


    @BeforeEach
    void setUp() {
        userDataRepository.deleteAll().block();

        clientRole = roleDataRepository.findByName("ROLE_CLIENT")
                .map(roleData -> new Role(roleData.getId(), roleData.getName()))
                .block();
    }

    @Test
    void shouldSaveUserAndReturnDomainUserWithId() {

        User userToSave = User.newUser(
                "Larry",
                "Ramirez",
                "larry.ramirez11@outlook.com",
                "123456789",
                "3001234567",
                LocalDate.of(1995, 11, 11),
                "456 Oak Ave",
                clientRole,
                new BigDecimal("5000000")
        );

        Mono<User> savedUserMono = userRepositoryAdapter.save(userToSave);

        StepVerifier.create(savedUserMono)
                .expectNextMatches(savedUser ->
                        savedUser.getId() != null &&
                        savedUser.getEmail().equals(userToSave.getEmail()) &&
                        savedUser.getRole() != null &&
                        savedUser.getRole().getName().equals("ROLE_CLIENT")
                )
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldReturnTrueWhenEmailExists() {

        UserData userData = UserData.builder()
                .firstName("Larry")
                .lastName("Ramirez")
                .email("larry.ramirez11@outlook.com")
                .baseSalary(new BigDecimal("5000000"))
                .identityNumber("123456789")
                .phoneNumber("3001234567")
                .birthDate(LocalDate.of(1995, 11, 11))
                .address("456 Oak Ave")
                .idRole(clientRole.getId())
                .build();

        Mono<Void> setup = userDataRepository.save(userData).then();

        Mono<Boolean> existsMono = userRepositoryAdapter.existsByEmail("larry.ramirez11@outlook.com");

        StepVerifier.create(setup.then(existsMono))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    void existsByEmailShouldReturnFalseWhenEmailDoesNotExist() {

        Mono<Boolean> existsMono = userRepositoryAdapter.existsByEmail("nonexistent@example.com");

        StepVerifier.create(existsMono)
                .expectNext(false)
                .verifyComplete();
    }
}
