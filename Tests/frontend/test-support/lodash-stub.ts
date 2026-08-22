export const isNumber = (value: unknown) =>
  typeof value === 'number' && !Number.isNaN(value);
