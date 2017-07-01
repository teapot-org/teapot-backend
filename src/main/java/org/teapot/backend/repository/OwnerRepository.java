package org.teapot.backend.repository;

import org.teapot.backend.model.Owner;

import javax.transaction.Transactional;

@Transactional
public interface OwnerRepository extends AbstractOwnerRepository<Owner> {
}
