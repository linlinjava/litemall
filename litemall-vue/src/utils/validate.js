export const idCard = /^[1-9]{1}[0-9]{14}$|^[1-9]{1}[0-9]{16}([0-9]|[xX])$/;

export const mobileReg = /^1[0-9]{10}$/;

export const address = val => {
  const value = val.trim();
  return value.length >= 5 && value.length <= 100;
};

export const userName = /^[a-zA-Z0-9_\u4e00-\u9fa5]{3,20}$/;

export const emailReg = /^(\w)+(\.\w+)*@(\w)+((\.\w+)+)$/;
