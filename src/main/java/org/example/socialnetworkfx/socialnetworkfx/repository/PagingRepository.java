package org.example.socialnetworkfx.socialnetworkfx.repository;

import org.example.socialnetworkfx.socialnetworkfx.domain.Entity;
import org.example.socialnetworkfx.socialnetworkfx.domain.User;
import org.example.socialnetworkfx.socialnetworkfx.utils.Paging.Page;
import org.example.socialnetworkfx.socialnetworkfx.utils.Paging.Pageable;

public interface PagingRepository<ID, E extends Entity<ID>> extends NewRepository<ID, E> {
    Page<E> findAllOnPage(User user, Pageable pageable);
}
