package org.teapot.backend.dao.impl;

import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.teapot.backend.dao.abstr.UserRoleDao;
import org.teapot.backend.model.UserRole;

import javax.persistence.NoResultException;


@Repository
@Transactional
public class UserRoleDaoImpl extends AbstractDaoImpl<UserRole> implements UserRoleDao {

    public UserRoleDaoImpl() {
        super(UserRole.class);
    }

    @Override
    public UserRole getByUserRoleName(String roleName) {
        UserRole role = null;
        try {
            role = entityManager
                    .createQuery("FROM UserRole WHERE name = :roleName", UserRole.class)
                    .setParameter("roleName", roleName)
                    .getSingleResult();
        } catch (NoResultException ignored) {
        }

        return role;
    }
}
