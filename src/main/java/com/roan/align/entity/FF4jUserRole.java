package com.roan.align.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.IdClass;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Entity representing user role assignments in FF4J.
 * Maps to FF4J_USER_ROLES table.
 *
 * @author Roan
 * @date 2026/4/7
 */
@Entity
@Table(name = "FF4J_USER_ROLES")
@IdClass(FF4jUserRoleId.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FF4jUserRole {

    @Id
    private String username;

    @Id
    private String roleName;
}