import { env } from 'node:process';
import { defineConfig, devices } from '@playwright/test';

const baseURL = env.E2E_BASE_URL || 'http://127.0.0.1:5173';

export default defineConfig({
  testDir: './tests/e2e',
  fullyParallel: false,
  workers: 1,
  timeout: 60_000,
  expect: {
    timeout: 15_000,
  },
  reporter: [['list']],
  use: {
    ...devices['Desktop Chrome'],
    baseURL,
    screenshot: 'only-on-failure',
    trace: 'retain-on-failure',
    video: 'retain-on-failure',
  },
  webServer: {
    command: 'npx --yes pnpm@9.15.9 dev --host 127.0.0.1',
    url: baseURL,
    reuseExistingServer: true,
    timeout: 180_000,
  },
});
