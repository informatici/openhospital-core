-- Change Help button into a submenu
UPDATE `oh`.`oh_menuitem` SET `MNI_CLASS` = 'none', `MNI_IS_SUBMENU` = 'Y' WHERE (`MNI_ID_A` = 'help');

-- Add submenu items
INSERT INTO `oh`.`oh_menuitem` (`MNI_ID_A`, `MNI_BTN_LABEL`, `MNI_LABEL`, `MNI_TOOLTIP`, `MNI_SHORTCUT`, `MNI_SUBMENU`, `MNI_CLASS`, `MNI_IS_SUBMENU`, `MNI_POSITION`) VALUES ('doc', 'angal.menu.btn.doc', 'angal.menu.doc', 'x', 'D', 'help', 'org.isf.help.HelpViewer', 'N', '1');
INSERT INTO `oh`.`oh_menuitem` (`MNI_ID_A`, `MNI_BTN_LABEL`, `MNI_LABEL`, `MNI_TOOLTIP`, `MNI_SHORTCUT`, `MNI_SUBMENU`, `MNI_CLASS`, `MNI_IS_SUBMENU`, `MNI_POSITION`) VALUES ('logfile', 'angal.menu.btn.logfile', 'angal.menu.logfile', 'x', 'L', 'help', 'org.isf.utils.log.LogViewer', 'N', '2');

-- Add admin permissions
INSERT INTO `oh`.`oh_groupmenu` (`GM_UG_ID_A`, `GM_MNI_ID_A`, `GM_ACTIVE`) VALUES ('admin', 'doc', '1');
INSERT INTO `oh`.`oh_groupmenu` (`GM_UG_ID_A`, `GM_MNI_ID_A`, `GM_ACTIVE`) VALUES ('admin', 'logfile', '1');
