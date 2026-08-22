import { expect, test } from '@playwright/test';

test('MALL-BROWSE-AC03-E2E-001 browses homepage product into detail and related goods', async ({ page }) => {
  await page.goto('/browse');

  await expect(page.getByTestId('new-goods-count')).toHaveText('1');
  await expect(page.getByTestId('hot-goods-count')).toHaveText('1');

  await page.getByRole('button', { name: /青瓷杯/ }).first().click();

  await expect(page.getByTestId('product-name')).toHaveText('青瓷杯');
  await expect(page.getByTestId('product-price')).toHaveText('¥88.00');
  await expect(page.getByTestId('product-stock')).toHaveText('12');
  await expect(page.getByTestId('product-spec')).toHaveText('天青色');
  await expect(page.getByTestId('related-goods')).toContainText('手冲壶');
});

test('MALL-BROWSE-AC02-E2E-001 filters goods list by new item query without losing sort contract', async ({ page }) => {
  await page.goto('/browse');

  await page.getByRole('button', { name: '只看新品' }).click();

  await expect(page.getByTestId('goods-list')).toContainText('青瓷杯');
  await expect(page.getByTestId('goods-list')).not.toContainText('手冲壶');
  await expect(page.getByTestId('query-log')).toHaveText('isNew=true&page=1&limit=10&sort=retail_price&order=asc');
});
