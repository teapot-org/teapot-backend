package org.teapot.backend.dao.abstr;

import org.teapot.backend.model.UserRole;


public interface UserRoleDao extends AbstractDao<UserRole> {

    UserRole getByUserRoleName(String roleName);
}
