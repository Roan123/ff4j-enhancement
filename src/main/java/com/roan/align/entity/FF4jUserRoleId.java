package com.roan.align.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * Composite key class for FF4jUserRole entity.
 *
 * @author Roan
 * @date 2026/4/7
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class FF4jUserRoleId implements Serializable {

    private String username;
    private String roleName;
}