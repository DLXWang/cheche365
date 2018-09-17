package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.User;
import org.springframework.data.repository.PagingAndSortingRepository;
import com.cheche365.cheche.core.model.UUIDMapping;

import java.util.List;

/**
 * Created by zhengwei on 6/10/15.
 */
public interface UuidMappingRepository extends PagingAndSortingRepository<UUIDMapping, Long> {

    UUIDMapping findFirstByUuid(String uuid);

    UUIDMapping findFirstByUuidAndClientType(String uuid, String clientType);

    List<UUIDMapping> findByUser(User user);
}
