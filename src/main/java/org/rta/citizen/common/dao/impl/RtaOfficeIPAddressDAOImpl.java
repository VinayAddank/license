/**
 * 
 */
package org.rta.citizen.common.dao.impl;

import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;
import org.rta.citizen.common.dao.BaseDAO;
import org.rta.citizen.common.dao.RtaOfficeIPAddressDAO;
import org.springframework.stereotype.Repository;
import org.rta.citizen.common.entity.RtaOfficeIPAddressEntity;

/**
 * @author neeraj.maletia
 *
 */
@Repository
public class RtaOfficeIPAddressDAOImpl extends BaseDAO<RtaOfficeIPAddressEntity> implements RtaOfficeIPAddressDAO {

	public RtaOfficeIPAddressDAOImpl() {
		super(RtaOfficeIPAddressEntity.class);
	}

	/**
	 * @author neeraj.maletia
	 * @description to get RtaOfficeIPAddress entity based on ip address
	 */
	@Override
	public RtaOfficeIPAddressEntity getRTAOfficeByIP(String ipAddress) {
		Criteria criteria = getSession().createCriteria(RtaOfficeIPAddressEntity.class);
		criteria.add(Restrictions.eq("ipAddress", ipAddress));
		criteria.setMaxResults(1);
		return (RtaOfficeIPAddressEntity) criteria.uniqueResult();
	}
	
	/**
	 * @author neeraj.maletia
	 * @description to get list of RTA office IP address based on office code
	 */
	@SuppressWarnings("unchecked")
	@Override
	public List<RtaOfficeIPAddressEntity> getRTAOfficeByOfficeCode(String officeCode){
		Criteria criteria = getSession().createCriteria(RtaOfficeIPAddressEntity.class);
		criteria.add(Restrictions.eq("officeCode", officeCode));
		return criteria.list();
	}

}
