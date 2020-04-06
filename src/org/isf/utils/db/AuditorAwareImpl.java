/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.isf.utils.db;

import org.isf.menu.manager.UserBrowsingManager;
import org.springframework.data.domain.AuditorAware;

/**
 *
 * @author uni2grow
 */
public class AuditorAwareImpl implements AuditorAware<String>{

    @Override
    public String getCurrentAuditor() {
        return UserBrowsingManager.getCurrentUser();
    }
}
