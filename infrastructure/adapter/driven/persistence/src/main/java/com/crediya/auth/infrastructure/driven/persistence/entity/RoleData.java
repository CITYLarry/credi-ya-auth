package com.crediya.auth.infrastructure.driven.persistence.entity;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

/**
 * Represents the 'roles' table in the database.
 */
@Data
@NoArgsConstructor
@Table("roles")
public class RoleData {

    @Id
    @Column("id_rol")
    private Long id;

    @Column("nombre")
    private String name;
}
