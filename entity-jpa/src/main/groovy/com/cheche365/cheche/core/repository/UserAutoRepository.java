package com.cheche365.cheche.core.repository;

import com.cheche365.cheche.core.model.Auto;
import com.cheche365.cheche.core.model.User;
import com.cheche365.cheche.core.model.UserAuto;
import org.apache.commons.lang3.StringUtils;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by sunhuazhong on 2015/3/19.
 */
@Repository
public interface UserAutoRepository extends UserAutoRepositoryCustom, PagingAndSortingRepository<UserAuto, Long> {
    public UserAuto findFirstByAuto(Auto auto);

    public UserAuto findFirstByUser(User user);

    public UserAuto findFirstByUserAndAuto(User user, Auto auto);

    @Query(value = "select ua.* from user_auto ua left join auto a on ua.auto=a.id where ua.user=?1 and a.license_plate_no=?2 and a.disable=0 order by a.vin_no desc, a.update_time desc limit 1  ", nativeQuery = true)
    public UserAuto searchByUserAndPlate(Long userId, String licensePlateNo);

    @Query(value = "select ua.* from user_auto ua where ua.user=?1 and ua.auto=?2 order by ua.id desc limit 1  ", nativeQuery = true)
    public UserAuto searchByIds(Long userId, Long autoId);

    public List<UserAuto> findByAuto(Auto auto);

    public List<UserAuto> findByUser(User user);
}
interface UserAutoRepositoryCustom extends BaseDao<UserAuto> {
    List<Auto> findAutosByConditions(User user, Auto auto);
}

class UserAutoRepositoryImpl extends BaseDaoImpl<UserAuto> implements UserAutoRepositoryCustom {

    @Override
    public List<Auto> findAutosByConditions(User user, Auto auto) {
        List<Auto> autos = new ArrayList<>();
        DetachedCriteria detachedCriteria = createDetachedCriteria();
        detachedCriteria.createAlias("auto", "auto");
        detachedCriteria.createAlias("user", "user");
        detachedCriteria.add(Restrictions.eq("auto.licensePlateNo", auto.getLicensePlateNo()));
        detachedCriteria.add(Restrictions.eq("auto.owner", auto.getOwner()));
        if (StringUtils.isNotBlank(auto.getIdentity())) {
            detachedCriteria.add(Restrictions.eq("auto.identity", auto.getIdentity()));
        }
        if (StringUtils.isNotBlank(auto.getEngineNo())) {
            detachedCriteria.add(Restrictions.eq("auto.engineNo", auto.getEngineNo()));
        }
        if (StringUtils.isNotBlank(auto.getVinNo())) {
            detachedCriteria.add(Restrictions.eq("auto.vinNo", auto.getVinNo()));

        }
        detachedCriteria.add(Restrictions.eq("auto.disable", false));
        detachedCriteria.add(Restrictions.eq("user", user));
        List<UserAuto> userAutos = find(detachedCriteria);
        userAutos.stream().forEach(it -> autos.add(it.getAuto()));
        return autos;
    }
}
