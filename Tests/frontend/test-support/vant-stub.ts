import { vi } from 'vitest';

export const Dialog = {
  alert: vi.fn(() => Promise.resolve())
};

export const Toast = {
  fail: vi.fn(),
  success: vi.fn()
};

export const Radio = { name: 'van-radio' };
export const RadioGroup = { name: 'van-radio-group' };
export const Card = { name: 'van-card' };
export const Tag = { name: 'van-tag' };
export const Field = { name: 'van-field' };
export const SubmitBar = { name: 'van-submit-bar' };
export const CouponCell = { name: 'van-coupon-cell' };
export const CouponList = { name: 'van-coupon-list' };
export const Popup = { name: 'van-popup' };
