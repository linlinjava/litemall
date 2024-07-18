module.exports = {
  root: true,
  env: {
    node: true,
    es6: true
  },
  // extends: ['plugin:vue/essential', '@vue/prettier'],
  // rules: {
  //   camelcase: 'off',
  //   quotes: ['error', 'single'],
  //   indent: ['error', 2, { SwitchCase: 1 }],
  //   'no-console': process.env.NODE_ENV === 'production' ? 'error' : 'off',
  //   'no-debugger': process.env.NODE_ENV === 'production' ? 'error' : 'off'
  // },
  parserOptions: {
    parser: 'babel-eslint',
    ecmaVersion:"latest",//最新版，或者你需要的 ECMAScript 版本
    sourceType:"module" // 允许使用 import/export 语句
  }
};
