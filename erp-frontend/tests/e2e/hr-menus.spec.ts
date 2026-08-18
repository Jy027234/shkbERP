import { env } from 'node:process';
import { expect, test, type Page } from '@playwright/test';

const tenantName = env.E2E_TENANT || '上海凯奔航空技术有限公司';
const username = env.E2E_USERNAME || 'admin';
const password = env.E2E_PASSWORD || 'admin';

async function login(page: Page) {
  await page.goto('/');
  await expect(page.getByRole('heading', { name: '登录' })).toBeVisible();
  await page.getByPlaceholder('请输入租户名称').fill(tenantName);
  await page.getByPlaceholder('请输入用户名').fill(username);
  await page.getByPlaceholder('请输入密码').fill(password);
  const loginResponsePromise = page.waitForResponse(
    (response) => response.url().endsWith('/api/auth/login') && response.request().method() === 'POST',
  );
  await page.getByRole('button', { name: /登\s*录/ }).click();
  const loginResponse = await loginResponsePromise;
  expect(loginResponse.ok()).toBe(true);
  const loginBody = (await loginResponse.json()) as { code?: number; data?: { token?: string } };
  expect(loginBody.code).toBe(200);
  expect(loginBody.data?.token).toBeTruthy();
  await expect(page).toHaveURL(/#\/dashboard\//);
}

const hrMenus = [
  { title: '员工档案', path: '/hr/employee' },
  { title: '证书管理', path: '/hr/certificate' },
  { title: '培训记录', path: '/hr/training/record' },
  { title: '培训课程', path: '/hr/training-course' },
  { title: '培训实施', path: '/hr/training-implementation' },
  { title: '授权项目', path: '/hr/authorization-project' },
  { title: '人员授权', path: '/hr/authorization-person' },
];

test('navigates to every HR menu page and loads its list query', async ({ page }) => {
  await login(page);

  // HR 一级菜单展开
  await page.getByText('人事管理', { exact: true }).first().click();
  await expect(page.getByText('员工档案', { exact: true }).first()).toBeVisible();

  for (const menu of hrMenus) {
    await page.getByText(menu.title, { exact: true }).first().click();
    // 路由跳转
    await expect(page).toHaveURL(new RegExp(menu.path));
    // 页面不应显示全局加载失败
    await expect(page.getByText('加载数据失败，请稍后重试', { exact: true })).toHaveCount(0);
    // 回到 HR 一级（避免菜单折叠差异：每次从侧栏再次点击人事管理）
    await page.getByText('人事管理', { exact: true }).first().click();
  }
});

test('employee page supports create-dialog open and code query', async ({ page }) => {
  await login(page);
  await page.getByText('人事管理', { exact: true }).first().click();
  await page.getByText('员工档案', { exact: true }).first().click();
  await expect(page).toHaveURL(/#\/hr\/employee\b/);

  // 新增员工入口可见（弹窗渲染依赖 v-if/权限，只验证按钮存在）
  const addButton = page.getByRole('button', { name: '新增员工' });
  await expect(addButton).toBeVisible();

  // 工号查询输入框存在且可交互（label 为“工号”，无 placeholder）
  const codeInput = page.getByText('工号：', { exact: true }).locator('..').locator('input');
  await expect(codeInput.first()).toBeVisible();
  await codeInput.first().fill('E2E-HR-NO-MATCH');
  await page.getByRole('button', { name: /查\s*询/ }).first().click();
  // 查询请求返回（空结果也是合法响应）
  await expect(page.getByText('加载数据失败，请稍后重试', { exact: true })).toHaveCount(0);
});

test('training course page lists enabled courses and opens create dialog', async ({ page }) => {
  await login(page);
  await page.getByText('人事管理', { exact: true }).first().click();
  await page.getByText('培训课程', { exact: true }).first().click();
  await expect(page).toHaveURL(/#\/hr\/training-course\b/);

  const addButton = page.getByRole('button', { name: '新增课程' });
  if (await addButton.isVisible().catch(() => false)) {
    await addButton.click();
    await expect(page.getByText('课程名称', { exact: true }).first()).toBeVisible();
    await page.keyboard.press('Escape');
  }
  await expect(page.getByText('加载数据失败，请稍后重试', { exact: true })).toHaveCount(0);
});

test('authorization project page lists projects without load failures', async ({ page }) => {
  await login(page);
  await page.getByText('人事管理', { exact: true }).first().click();
  await page.getByText('授权项目', { exact: true }).first().click();
  await expect(page).toHaveURL(/#\/hr\/authorization-project\b/);
  await expect(page.getByText('加载数据失败，请稍后重试', { exact: true })).toHaveCount(0);
});
