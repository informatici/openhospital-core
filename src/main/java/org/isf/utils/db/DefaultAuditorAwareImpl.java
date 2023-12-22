package org.isf.utils.db;

import java.util.Optional;

import org.isf.menu.manager.UserBrowsingManager;

public class DefaultAuditorAwareImpl implements AuditorAwareInterface {

	@Override
	public Optional<String> getCurrentAuditor() {
		return Optional.ofNullable(UserBrowsingManager.getCurrentUser());
	}
}