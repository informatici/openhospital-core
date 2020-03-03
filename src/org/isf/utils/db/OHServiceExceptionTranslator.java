package org.isf.utils.db;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.isf.generaldata.MessageBundle;
import org.isf.utils.exception.OHDBConnectionException;
import org.isf.utils.exception.OHDataIntegrityViolationException;
import org.isf.utils.exception.OHDataLockFailureException;
import org.isf.utils.exception.OHInvalidSQLException;
import org.isf.utils.exception.OHServiceException;
import org.isf.utils.exception.model.OHExceptionMessage;
import org.isf.utils.exception.model.OHSeverityLevel;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class OHServiceExceptionTranslator {

	@Around("within(@org.isf.utils.db.TranslateOHServiceException *)")
	public Object translateSqlExceptionToOHServiceException(ProceedingJoinPoint pjp) throws OHServiceException {
		try {
			return pjp.proceed();
		} catch (DataIntegrityViolationException e) {
			throw new OHDataIntegrityViolationException(e, new OHExceptionMessage(null, MessageBundle.getMessage("angal.sql.theselecteditemisstillusedsomewhere"), OHSeverityLevel.ERROR));
		} catch (InvalidDataAccessResourceUsageException e) {
			throw new OHInvalidSQLException(e, new OHExceptionMessage(null, MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlistruction"), OHSeverityLevel.ERROR));
		} catch (CannotCreateTransactionException e) {
			throw new OHDBConnectionException(e, new OHExceptionMessage(null, MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection"), OHSeverityLevel.ERROR));
    	} catch (ObjectOptimisticLockingFailureException e) {
			throw new OHDataLockFailureException(e, new OHExceptionMessage(null, MessageBundle.getMessage("angal.sql.thedatahasbeenupdatedbysomeoneelse"), OHSeverityLevel.ERROR));
    	} catch (Throwable e) {
    		throw new OHServiceException(e, new OHExceptionMessage(null, e.getMessage(), OHSeverityLevel.ERROR));
		}
	}
}
