import { defineConfig } from '@playwright/test';

export default defineConfig({
  testDir: './tests',
  timeout: 30_000,
  use: {
    baseURL: 'http://127.0.0.1:4179',
    trace: 'retain-on-failure'
  },
  webServer: {
    command: 'node fixtures/payment-flow-server.mjs',
    url: 'http://127.0.0.1:4179',
    reuseExistingServer: false,
    timeout: 15_000
  }
});
