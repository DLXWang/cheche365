package com.cheche365.cheche.core.repository.agent

import com.cheche365.cheche.core.model.Channel
import com.cheche365.cheche.core.model.User
import com.cheche365.cheche.core.model.agent.QuoteHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository

@Repository
interface QuoteHistoryRepository extends JpaRepository<QuoteHistory, Long> {

    @Query(value = '''
    select q.* from (
        select a.license_plate_no, max(qh.id) id from quote_history qh,channel n,auto a
        where qh.channel = n.id and qh.auto = a.id and qh.user = ?1 and n.parent = ?2 and qh.create_time >= ?4
        and (
             instr(a.license_plate_no,(if((?3 is null or ?3 = ''),a.license_plate_no,?3))) > 0 
             or 
             instr(a.owner,(if((?3 is null or ?3 = ''),a.owner,?3))) > 0 
        )
        group by a.license_plate_no
    ) t, quote_history q
    where t.id = q.id and not exists (
            select 1 from purchase_order po,auto au,channel n
            where po.auto = au.id and po.source_channel = n.id
            and po.applicant = ?1 and n.parent = ?2
            and au.license_plate_no = t.license_plate_no
            and (po.status = 1 or po.status = 3 or po.status = 5)
    ) order by q.id desc #pageable
    ''',
     countQuery = '''
     select count(q.id) from (
        select a.license_plate_no, max(qh.id) id from quote_history qh,channel n,auto a
        where qh.channel = n.id and qh.auto = a.id and qh.user = ?1 and n.parent = ?2 and qh.create_time >= ?4
        and (
             instr(a.license_plate_no,(if((?3 is null or ?3 = ''),a.license_plate_no,?3))) > 0 
             or 
             instr(a.owner,(if((?3 is null or ?3 = ''),a.owner,?3))) > 0 
        )
        group by a.license_plate_no
    ) t, quote_history q
    where t.id = q.id and not exists (
            select 1 from purchase_order po,auto au,channel n
            where po.auto = au.id and po.source_channel = n.id
            and po.applicant = ?1 and n.parent = ?2
            and au.license_plate_no = t.license_plate_no
            and (po.status = 1 or po.status = 3 or po.status = 5)
    ) order by q.id desc 
     ''',nativeQuery = true)
    Page<QuoteHistory> findByUserAndChannel(User user, Channel channel, String keyWords, Date createTime, Pageable pageable)
}
