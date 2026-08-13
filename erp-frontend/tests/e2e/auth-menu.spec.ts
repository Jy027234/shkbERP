import { env } from 'node:process';
import { expect, test } from '@playwright/test';

const tenantName = env.E2E_TENANT || '测试租户';
const username = env.E2E_USERNAME || 'admin';
const password = env.E2E_PASSWORD || 'admin';

test('logs in, loads the dashboard and authorized menu, and logs out', async ({ page }) => {
  const dashboardResponses: Array<{ url: string; status: number; body: unknown }> = [];
  page.on('response', async (response) => {
    if (!response.url().includes('/api/shkb/dashboard/')) return;
    dashboardResponses.push({
      url: response.url(),
      status: response.status(),
      body: await response.json(),
    });
  });

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
  await expect.poll(() => dashboardResponses.length, { timeout: 30_000 }).toBe(5);
  for (const response of dashboardResponses) {
    expect(response.status, response.url).toBe(200);
    expect(response.body, response.url).toMatchObject({ code: 200 });
  }
  await expect(page.getByText('加载数据失败，请稍后重试', { exact: true })).toHaveCount(0);
  await expect(page.getByText('系统管理员', { exact: true }).first()).toBeVisible();
  await expect(page.getByText('仪表板', { exact: true }).first()).toBeVisible();
  await expect(page.getByText('系统管理', { exact: true }).first()).toBeVisible();
  await expect(page.getByText('基础信息管理', { exact: true }).first()).toBeVisible();

  await page.getByText('系统管理员', { exact: true }).first().click();
  await page.getByRole('menuitem', { name: '退出系统' }).click();
  await expect(page.getByText('是否确认退出登录？', { exact: true })).toBeVisible();
  await page.getByRole('button', { name: /确\s*定/ }).click();

  await expect(page).toHaveURL(/#\/login$/);
  await expect(page.getByRole('heading', { name: '登录' })).toBeVisible();
});
