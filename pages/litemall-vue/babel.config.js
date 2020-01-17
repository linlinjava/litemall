module.exports = {
  presets: ['@vue/app'],
  plugins: [
    'lodash',
    [
      'import',
      {
        libraryName: 'vant',
        libraryDirectory: 'es',
        style: true
      },
      'vant'
    ]
  ]
};
