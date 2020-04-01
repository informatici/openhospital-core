package org.isf.sms.service;

import java.util.Date;
import java.util.List;

import org.isf.sms.model.Sms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;

public interface SmsIoOperationRepository extends JpaRepository<Sms, Integer> {
    List<Sms> findBySmsDateSchedBetweenOrderBySmsDateSchedAsc(Date start, Date stop);
    List<Sms> findBySmsDateSchedBetweenAndSmsDateSentIsNullOrderBySmsDateSchedAsc(Date start, Date stop);
    List<Sms> findBySmsDateSentIsNullOrderBySmsDateSchedAsc();
    @Modifying
    void deleteByModuleAndModuleIDAndSmsDateSentIsNull(String mod, String id);
}