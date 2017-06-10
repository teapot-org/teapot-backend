package org.teapot.backend.service.abstr;

import org.teapot.backend.model.UserRole;

public interface UserRoleService extends AbstractService<UserRole> {

    UserRole getByName(String name);
}
