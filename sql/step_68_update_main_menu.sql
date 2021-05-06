# Remove "exit" and "file"
DELETE FROM MENUITEM WHERE MNI_ID_A = 'exit';
DELETE FROM MENUITEM WHERE MNI_ID_A = 'file';

# "General Data" is now "Settings" so change shortcut key
# Note: the label is set in the properties file
UPDATE MENUITEM SET MNI_SHORTCUT='S' WHERE MNI_ID_A='generaldata';

# Move the "Users" menu to the end of "Settings"
UPDATE MENUITEM SET MNI_SUBMENU="generaldata", MNI_POSITION=10 WHERE MNI_ID_A="users";

