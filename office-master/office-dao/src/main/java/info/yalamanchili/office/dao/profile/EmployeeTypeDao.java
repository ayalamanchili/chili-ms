/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package info.yalamanchili.office.dao.profile;
import info.yalamanchili.office.dao.CRUDDao;
import info.yalamanchili.office.entity.profile.EmployeeType;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;
/**
 *
 * @author bala
 */

@Component
@Scope("request")
public class EmployeeTypeDao extends CRUDDao<EmployeeType> {
    
    public EmployeeTypeDao()
    {
      super(EmployeeType.class);
    }
    
   @PersistenceContext
	protected EntityManager em;

	@Override
	public EntityManager getEntityManager() {
		return em;
	}
    
}