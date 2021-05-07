# Remove "exit" and "file"
DELETE FROM MENUITEM WHERE MNI_ID_A = 'exit';
DELETE FROM MENUITEM WHERE MNI_ID_A = 'file';

# "General Data" is now "Settings" so change shortcut key
# Note: the label is set in the properties file
UPDATE MENUITEM SET MNI_SHORTCUT='S' WHERE MNI_ID_A='generaldata';

# Move the "Users" menu to the end of "Settings"
UPDATE MENUITEM SET MNI_SUBMENU="generaldata", MNI_POSITION=10 WHERE MNI_ID_A="users";

# Rearrange main menu
UPDATE MENUITEM SET MNI_POSITION=1 WHERE MNI_ID_A="opd";
UPDATE MENUITEM SET MNI_POSITION=2 WHERE MNI_ID_A="pharmacy";
UPDATE MENUITEM SET MNI_POSITION=4 WHERE MNI_ID_A="admission";
UPDATE MENUITEM SET MNI_POSITION=5 WHERE MNI_ID_A="accounting";
UPDATE MENUITEM SET MNI_POSITION=11 WHERE MNI_ID_A="generaldata";
UPDATE MENUITEM SET MNI_POSITION=12 WHERE MNI_ID_A="help";
