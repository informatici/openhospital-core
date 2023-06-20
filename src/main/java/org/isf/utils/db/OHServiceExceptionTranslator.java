/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2023 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
 *
 * Open Hospital is a free and open source software for healthcare data management.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * https://www.gnu.org/licenses/gpl-3.0-standalone.html
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program. If not, see <http://www.gnu.org/licenses/>.
 */
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

	private static final Logger LOGGER = LoggerFactory.getLogger(OHServiceExceptionTranslator.class);

	@Around("within(@org.isf.utils.db.TranslateOHServiceException *) || @annotation(org.isf.utils.db.TranslateOHServiceException)")
	public Object translateSqlExceptionToOHServiceException(ProceedingJoinPoint pjp) throws OHServiceException {
		try {
			return pjp.proceed();
		} catch (DataIntegrityViolationException e) {
			throw new OHDataIntegrityViolationException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.sql.theselecteditemisstillusedsomewhere.msg"),
					OHSeverityLevel.ERROR));
		} catch (InvalidDataAccessResourceUsageException e) {
			throw new OHInvalidSQLException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"),
					OHSeverityLevel.ERROR));
		} catch (CannotCreateTransactionException e) {
			throw new OHDBConnectionException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"),
					OHSeverityLevel.ERROR));
    	} catch (ObjectOptimisticLockingFailureException e) {
			throw new OHDataLockFailureException(e, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
					MessageBundle.getMessage("angal.sql.thedatahasbeenupdatedbysomeoneelse.msg"),
					OHSeverityLevel.ERROR));
    	} catch (OutOfMemoryError oome) {
    		LOGGER.error(oome.getMessage(), oome);
    		throw new OHServiceException(oome, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"), 
					MessageBundle.getMessage("angal.sql.pleaseconsiderenablingtheenhancedsearchsettingseeadminmanualformoreinfo.msg"),
					OHSeverityLevel.WARNING));
    	} catch (OHServiceException e) {
    		LOGGER.warn("Nested translation for {}", e.getMessage());
    		throw e; // for nested translators
    	} catch (Throwable throwable) {
    		LOGGER.error(throwable.getMessage(), throwable);
    		throw new OHServiceException(throwable, new OHExceptionMessage(MessageBundle.getMessage("angal.common.error.title"),
				    MessageBundle.getMessage("angal.sql.anunexpectederroroccurredpleasecheckthelogs.msg"),
				    OHSeverityLevel.ERROR));
		}
	}

}
