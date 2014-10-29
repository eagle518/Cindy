package co.mindie.cindy.dao.utils.builtin;

import co.mindie.cindy.automapping.Load;
import co.mindie.cindy.dao.utils.CriteriaBuilder;
import co.mindie.cindy.dao.utils.CriteriaBuilderFactory;
import me.corsin.javatools.misc.SynchronizedPool;
import org.hibernate.Session;

/**
 * Pooled implementation of the criteria builder factory
 */
@Load(creationPriority = -1)
public class PooledCriteriaBuilderFactory extends SynchronizedPool<CriteriaBuilder> implements CriteriaBuilderFactory {
	@Override
	public CriteriaBuilder createCriteria(Session session, Class<?> managedClass) {
		return this.obtain().configure(
				session,
				managedClass
		);
	}

	@Override
	protected CriteriaBuilder instantiate() {
		return new CriteriaBuilder(this);
	}
}
