package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Partner;
import com.cheche365.cheche.core.model.PartnerAttachment;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by sunhuazhong on 2015/8/25.
 */
@Repository
public interface PartnerAttachmentRepository extends PagingAndSortingRepository<PartnerAttachment, Long> {
    PartnerAttachment findFirstByPartner(Partner partner);
}
