package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Area;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
public interface AreaRepository extends AreaRepositoryCustom, PagingAndSortingRepository<Area, Long>, JpaSpecificationExecutor<Area> {

    Area findByName(String name);

    @Query(value = "select t.* from area t order by type ", nativeQuery = true)
    List<Area> findAll();

    @Query(value = "select a.* from area a  where id in(?1) and active=1 order by id ,type", nativeQuery = true)
    List<Area> findByIds(List ids);

    @Query(value = "select a.* from area a  where (type=1 or type=2  or type=3) and active=1 order by id ,type", nativeQuery = true)
    List<Area> findShortAreasList();

    Long countById(Long id);

    @Query(value = "select convert(id,char) id ,name,type,active,short_code, city_code,postal_code, reform from area  where convert(id,char)  like ?1%  and type=?2", nativeQuery = true)
    List<Area> findCityAreaListByProvinceId(String id, Long typeID);

    List<Area> findByActive(boolean active);

    @Query("select id from Area where active = ?1")
    List<Long> findAllIdByActive(boolean active);

    Area findById(Long id);

    Area findFirstByCityCode(Integer cityCode);

    @Query(value = "select * from area where type in(?1) order by type,id", nativeQuery = true)
    List<Area> findByType(List types);

    @Query(value = "select name from area where id in (?1);", nativeQuery = true)
    List<String> findPermissionNames(List<Long> ids);

    @Query(value = "select a.* from area a join agent_invite_code_area aica on a.id =aica.area where aica.cheche_agent_invite_code = ?1", nativeQuery = true)
    List<Area> findAllByChecheAgentInviteCode(Long ccAgentInviteCodeId);


    @Query(value = "SELECT a.* FROM area a,channel_agent ca,cheche_agent_invite_code caic,agent_invite_code_area aica" +
        "  WHERE ca.id = caic.channel_agent" +
        "  AND caic.id = aica.cheche_agent_invite_code" +
        "  AND aica.area = a.id" +
        "  AND ca.id = ?1" +
        "  GROUP BY a.name", nativeQuery = true)
    List<Area> findAreaByChannelAgent(Long id);

}


interface AreaRepositoryCustom extends BaseDao<Area> {
    public List<Map> findShortCodeAndProvinceId();
}

class AreaRepositoryImpl extends BaseDaoImpl<Area> implements AreaRepositoryCustom {

    @Override
    public List<Map> findShortCodeAndProvinceId() {
        String sql = "select t1.short_code,t2.id from (select min(id) mid , short_code from area where length(short_code)>0 and active=1 group by short_code order by mid) as t1 left outer join (select * from area where type=1 or type=2) as t2 on  substr(CONVERT(t1.mid,char),1,2)=substr(CONVERT(t2.id,char),1,2) ";
        List<Map> shortCodes = findBySql(sql, Map.class, new Object[0]);
        return shortCodes;

    }
}
