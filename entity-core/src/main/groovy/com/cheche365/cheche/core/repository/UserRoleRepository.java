package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Role;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserRole;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by liqiang on 3/20/15.
 */
@Repository
public interface UserRoleRepository extends PagingAndSortingRepository<UserRole, Long> {

    List<UserRole> findByUser(User user);

    UserRole findFirstByUserAndRole(User user, Role role);
}
