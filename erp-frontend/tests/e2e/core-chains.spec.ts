import { test, expect, Page } from '@playwright/test';

// 核心浏览器 E2E：合同、维修任务、工卡、采购、库存/盘点/调整关键链路。
// 菜单导航交互已由 auth-menu / hr-menus 覆盖；本用例按路由直达各关键页面，
// 断言路由正确且页面列表查询无“加载数据失败”。
// 路由来自 V1.21 菜单基线的本地冒烟库。

const E2E_TENANT = process.env.E2E_TENANT || '上海凯奔航空技术有限公司';
const E2E_USERNAME = process.env.E2E_USERNAME || 'admin';
const E2E_PASSWORD = process.env.E2E_PASSWORD || 'admin';

async function login(page: Page) {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: '登录' })).toBeVisible();

  const tenantInput = page.locator('input').first();
  await tenantInput.fill(E2E_TENANT);
  await page.getByPlaceholder(/用户名|账号/).fill(E2E_USERNAME);
  await page.getByPlaceholder(/密码/).fill(E2E_PASSWORD);
  await page.getByRole('button', { name: /登\s*录/ }).click();
  await expect(page).toHaveURL(/#\/dashboard\//);
}

async function assertChainPageLoads(page: Page, path: string) {
  await page.goto('/#' + path);
  await expect(page).toHaveURL(new RegExp('#' + path));
  await expect(
    page.getByText('加载数据失败，请稍后重试', { exact: true }),
  ).toHaveCount(0);
}

test('core chains: contract, work-card, maintenance, purchase, stock/take/adjust list pages load without failures', async ({
  page,
}) => {
  await login(page);

  // 合同管理
  await assertChainPageLoads(page, '/contract/aviation');
  await assertChainPageLoads(page, '/contract/contract-task');

  // 维修任务管理
  await assertChainPageLoads(page, '/maintenance/aviation');

  // 工卡管理
  await assertChainPageLoads(page, '/work-card/list');

  // 工具设备
  await assertChainPageLoads(page, '/equipment/tool');

  // 采购管理
  await assertChainPageLoads(page, '/purchase/order');
  await assertChainPageLoads(page, '/purchase/receive');

  // 发料管理
  await assertChainPageLoads(page, '/material/apply');

  // 库存管理：仓库调拨（批次/序列号追溯链路入口）
  await assertChainPageLoads(page, '/stock/transfer');

  // 库存盘点
  await assertChainPageLoads(page, '/take/plan');
  await assertChainPageLoads(page, '/take/sheet');

  // 库存调整
  await assertChainPageLoads(page, '/take-adjust/stock');
});
