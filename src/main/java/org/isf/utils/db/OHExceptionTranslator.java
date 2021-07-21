/*
 * Open Hospital (www.open-hospital.org)
 * Copyright © 2006-2021 Informatici Senza Frontiere (info@informaticisenzafrontiere.org)
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
import org.isf.utils.exception.OHException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.InvalidDataAccessResourceUsageException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.CannotCreateTransactionException;

@Aspect
@Order(value = Ordered.HIGHEST_PRECEDENCE)
@Component
public class OHExceptionTranslator {

	@Around("within(@org.isf.utils.db.TranslateOHException *)")
	public Object translateSqlExceptionToOhException(ProceedingJoinPoint pjp) throws OHException {
		try {
			return pjp.proceed();
		} catch (DataIntegrityViolationException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.constraintviolation.msg"), e);
		} catch (InvalidDataAccessResourceUsageException e) {
			throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwiththesqlinstruction.msg"), e);
		} catch (CannotCreateTransactionException e) {
    		throw new OHException(MessageBundle.getMessage("angal.sql.problemsoccurredwithserverconnection.msg"), e); 
    	} catch (Throwable e) {
			throw new OHException(e.getMessage(), e);
		}
	}
}
