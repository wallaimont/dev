-- V4: Seed — roles, permissions, admin user

-- Roles
INSERT INTO roles (id, name, description) VALUES
    ('00000000-0000-0000-0000-000000000001', 'ADMIN',      'System administrator with full access'),
    ('00000000-0000-0000-0000-000000000002', 'SUPERVISOR', 'Team supervisor with management access'),
    ('00000000-0000-0000-0000-000000000003', 'AGENT',      'Support agent handling tickets'),
    ('00000000-0000-0000-0000-000000000004', 'CUSTOMER',   'End customer who opens tickets');

-- Permissions
INSERT INTO permissions (id, name, description, module) VALUES
    ('10000000-0000-0000-0000-000000000001', 'TICKET_CREATE',      'Create new tickets',                   'TICKET'),
    ('10000000-0000-0000-0000-000000000002', 'TICKET_VIEW_OWN',    'View own tickets',                     'TICKET'),
    ('10000000-0000-0000-0000-000000000003', 'TICKET_VIEW_ALL',    'View all tickets',                     'TICKET'),
    ('10000000-0000-0000-0000-000000000004', 'TICKET_UPDATE',      'Update ticket details',                'TICKET'),
    ('10000000-0000-0000-0000-000000000005', 'TICKET_ASSIGN',      'Assign tickets to agents',             'TICKET'),
    ('10000000-0000-0000-0000-000000000006', 'TICKET_CLOSE',       'Close or cancel tickets',              'TICKET'),
    ('10000000-0000-0000-0000-000000000007', 'COMMENT_CREATE',     'Add comments to tickets',              'COMMENT'),
    ('10000000-0000-0000-0000-000000000008', 'COMMENT_VIEW_INTERNAL','View internal comments',             'COMMENT'),
    ('10000000-0000-0000-0000-000000000009', 'CATEGORY_MANAGE',    'Create, update, delete categories',    'CATEGORY'),
    ('10000000-0000-0000-0000-000000000010', 'USER_MANAGE',        'Create, update, delete users',         'USER'),
    ('10000000-0000-0000-0000-000000000011', 'ROLE_MANAGE',        'Manage roles and permissions',         'USER'),
    ('10000000-0000-0000-0000-000000000012', 'AUDIT_VIEW',         'View audit logs',                      'AUDIT'),
    ('10000000-0000-0000-0000-000000000013', 'REPORT_VIEW',        'View reports and analytics',           'REPORT');

-- ADMIN permissions (all)
INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000001', id FROM permissions;

-- SUPERVISOR permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000002', id FROM permissions
WHERE name IN ('TICKET_CREATE','TICKET_VIEW_ALL','TICKET_UPDATE','TICKET_ASSIGN','TICKET_CLOSE',
               'COMMENT_CREATE','COMMENT_VIEW_INTERNAL','CATEGORY_MANAGE','AUDIT_VIEW','REPORT_VIEW');

-- AGENT permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000003', id FROM permissions
WHERE name IN ('TICKET_VIEW_ALL','TICKET_UPDATE','TICKET_CLOSE','COMMENT_CREATE','COMMENT_VIEW_INTERNAL');

-- CUSTOMER permissions
INSERT INTO role_permissions (role_id, permission_id)
SELECT '00000000-0000-0000-0000-000000000004', id FROM permissions
WHERE name IN ('TICKET_CREATE','TICKET_VIEW_OWN','COMMENT_CREATE');

-- Default admin user (password: Admin@2024!)
-- BCrypt hash (strength 12) of "Admin@2024!"
INSERT INTO users (id, name, email, password_hash, enabled) VALUES
    ('20000000-0000-0000-0000-000000000001',
     'System Administrator',
     'admin@supportdesk.com',
     '$2a$12$fJ4YL3wNQxTl6Wk5UZKHzOv7mMKjcGq2Xd8oE1N9rSp0VbA3HuIe',
     TRUE);

INSERT INTO user_roles (user_id, role_id) VALUES
    ('20000000-0000-0000-0000-000000000001', '00000000-0000-0000-0000-000000000001');

-- Default categories
INSERT INTO categories (name, description, color, icon) VALUES
    ('Infraestrutura', 'Problemas de hardware, rede e servidores', '#EF4444', 'server'),
    ('Software',       'Bugs, instalação e configuração de software', '#3B82F6', 'code'),
    ('Acesso',         'Pedidos de acesso, senhas e permissões', '#F59E0B', 'lock'),
    ('Financeiro',     'Dúvidas e problemas financeiros', '#10B981', 'dollar-sign'),
    ('Outros',         'Demandas diversas não categorizadas', '#6B7280', 'help-circle');
