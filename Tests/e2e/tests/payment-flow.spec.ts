import { expect, test } from '@playwright/test';

test('MALL-PAY-AC08-E2E-001 browser payment flow reaches paid state exactly once', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByTestId('actual-price')).toHaveText('¥100.00');

  await page.getByRole('button', { name: '提交订单' }).click();
  await expect(page.getByTestId('order-sn')).toHaveText('20260711000001');
  await expect(page.getByTestId('order-status')).toHaveText('未付款');

  await page.getByRole('button', { name: '模拟支付回调' }).click();
  await expect(page.getByTestId('order-status')).toHaveText('已付款');
  await expect(page.getByTestId('callback-count')).toHaveText('1');

  await page.getByRole('button', { name: '重复回调' }).click();
  await expect(page.getByTestId('order-status')).toHaveText('已付款');
  await expect(page.getByTestId('callback-count')).toHaveText('1');
});

test('MALL-PAY-AC09-E2E-001 canceling unpaid order restores stock and coupon', async ({ page }) => {
  await page.goto('/');

  await expect(page.getByTestId('stock-count')).toHaveText('5');
  await expect(page.getByTestId('coupon-status')).toHaveText('可用');

  await page.getByRole('button', { name: '提交订单' }).click();
  await expect(page.getByTestId('order-status')).toHaveText('未付款');
  await expect(page.getByTestId('stock-count')).toHaveText('3');
  await expect(page.getByTestId('coupon-status')).toHaveText('已使用');

  await page.getByRole('button', { name: '取消订单' }).click();
  await expect(page.getByTestId('order-status')).toHaveText('已取消');
  await expect(page.getByTestId('stock-count')).toHaveText('5');
  await expect(page.getByTestId('coupon-status')).toHaveText('可用');
});
