import { defineConfig } from 'vitest/config';
import { resolve } from 'node:path';

export default defineConfig({
  resolve: {
    alias: [
      {
        find: /^dayjs$/,
        replacement: resolve(__dirname, 'node_modules/dayjs/dayjs.min.js')
      },
      {
        find: /^lodash$/,
        replacement: resolve(__dirname, 'test-support/lodash-stub.ts')
      },
      {
        find: /^@\/utils\/request$/,
        replacement: resolve(__dirname, 'test-support/request-spy.ts')
      },
      {
        find: 'vant',
        replacement: resolve(__dirname, 'test-support/vant-stub.ts')
      },
      {
        find: '@',
        replacement: resolve(__dirname, '../../litemall-vue/src')
      }
    ]
  },
  test: {
    environment: 'jsdom',
    environmentOptions: {
      jsdom: {
        url: 'http://localhost/'
      }
    },
    setupFiles: ['test-support/setup-browser.ts'],
    globals: true,
    include: ['tests/**/*.test.ts'],
    coverage: {
      provider: 'v8',
      reportsDirectory: 'coverage',
      reporter: ['text', 'html', 'lcov', 'json-summary'],
      allowExternal: true,
      include: [
        resolve(__dirname, 'src/**/*.ts'),
        resolve(__dirname, '../../litemall-vue/src/api/api.js')
      ],
      exclude: [
        'test-support/**',
        'tests/**'
      ]
    }
  }
});
