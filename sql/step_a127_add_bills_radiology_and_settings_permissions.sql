INSERT INTO oh_permissions(P_NAME, P_ACTIVE)
VALUES
    ('bills.create', 1),('bills.read', 1),('bills.update', 1),('bills.delete', 1),('radiology.read', 1),('settings.read', 1),('settings.update', 1);

INSERT INTO oh_grouppermission (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE)
VALUES
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'bills.create'), '1'),
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'bills.read'), '1'),
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'bills.update'), '1'),
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'bills.delete'), '1'),
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'radiology.read'), '1'),
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'settings.read'), '1'),
('admin', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'settings.update'), '1');


INSERT INTO oh_grouppermission (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE)
VALUES
('doctor', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'bills.read'), '1'),
('doctor', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'radiology.read'), '1');


INSERT INTO oh_grouppermission (GP_UG_ID_A, GP_P_ID_A, GP_ACTIVE)
VALUES
('laboratorist', (SELECT P_ID_A FROM oh_permissions WHERE P_NAME = 'radiology.read'), '1');


