package org.rta.citizen.common.dao;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.entity.ServiceMasterEntity;
import org.rta.citizen.common.enums.ServiceCategory;
import org.rta.citizen.common.utils.ObjectsUtil;
import org.springframework.stereotype.Repository;

@Repository
public class ServiceMasterDAOImpl extends BaseDAO<ServiceMasterEntity> implements ServiceMasterDAO {

    public ServiceMasterDAOImpl() {
        super(ServiceMasterEntity.class);
    }

    @Override
    public ServiceMasterEntity getEntity(String code) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("code", code));
        return (ServiceMasterEntity) criteria.uniqueResult();
    }
    
    @SuppressWarnings("unchecked")
    @Override
    public List<ServiceMasterEntity> getServices(boolean havingSlot) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        criteria.add(Restrictions.eq("slotApplicable", havingSlot));
        return (List<ServiceMasterEntity>) criteria.list();
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<ServiceMasterEntity> getServices(ServiceCategory serviceCategory) {
        Criteria criteria = getSession().createCriteria(getPersistentClass());
        if(!ObjectsUtil.isNull(serviceCategory)){
            criteria.add(Restrictions.eq("serviceCategory", serviceCategory.getCode()).ignoreCase());
        }
        return (List<ServiceMasterEntity>) criteria.list();
    }
}
