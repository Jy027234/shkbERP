-- SHKB HR menu permission code alignment (business-confirmed).
-- Scope: align the V1.21 menu permission codes with the permission codes actually
-- used by the frontend views and backend controllers, without modifying the
-- registered V1.21 baseline file.
-- Fixes:
--   (c) authorization project menu kept the legacy code 'AuthorizationProject'
--       while both AuthorizationProjectController and the frontend use
--       'hr:authorization:query'.
--   (a) the employee "modify" action menu had a stray space:
--       'hr:employee: update' instead of 'hr:employee:update'.
--   (b) the training record menu and its three action menus used
--       'hr:employee:*' although the whole training-record flow belongs to the
--       'hr:training:*' namespace used by training courses and implementations.
-- Every UPDATE is guarded by the current value, so re-running this migration
-- is a no-op after the first successful run.

SET NAMES utf8mb4;
START TRANSACTION;

-- (c) 授权项目主菜单：AuthorizationProject -> hr:authorization:query
UPDATE `sys_menu`
SET `permission` = 'hr:authorization:query',
    `update_time` = NOW()
WHERE `id` = X'32303338323634333134363430363031303838'
  AND `permission` = 'AuthorizationProject';

-- (a) 员工档案-修改按钮：hr:employee: update -> hr:employee:update
UPDATE `sys_menu`
SET `permission` = 'hr:employee:update',
    `update_time` = NOW()
WHERE `id` = X'32303338323635323732373632323431303234'
  AND `permission` = 'hr:employee: update';

-- (b) 培训记录主菜单：hr:employee:query -> hr:training:query
UPDATE `sys_menu`
SET `permission` = 'hr:training:query',
    `update_time` = NOW()
WHERE `id` = X'32303338323633353336373737353634313630'
  AND `permission` = 'hr:employee:query';

-- (b) 培训记录-新增按钮：hr:employee:create -> hr:training:create
UPDATE `sys_menu`
SET `permission` = 'hr:training:create',
    `update_time` = NOW()
WHERE `id` = X'32303338323637373534373837373730333638'
  AND `permission` = 'hr:employee:create';

-- (b) 培训记录-修改按钮：hr:employee:update -> hr:training:update
UPDATE `sys_menu`
SET `permission` = 'hr:training:update',
    `update_time` = NOW()
WHERE `id` = X'32303338323638333332323732313238303030'
  AND `permission` = 'hr:employee:update';

-- (b) 培训记录-删除按钮：hr:employee:delete -> hr:training:delete
UPDATE `sys_menu`
SET `permission` = 'hr:training:delete',
    `update_time` = NOW()
WHERE `id` = X'32303338323638343937313636393935343536'
  AND `permission` = 'hr:employee:delete';

COMMIT;
