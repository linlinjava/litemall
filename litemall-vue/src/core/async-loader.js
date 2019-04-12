// 使用这个会导致组件内部的 router 导航守卫无法使用, 慎用
/**
 * @param  { string } chunkPath views 文件夹下的页面路径
 * @return { function } 返回 promise<component> 的匿名函数
 */
import spinner from '@/vue/components/spinner';

export default chunkPath => {
  const AsyncHandler = () => ({
    component: new Promise(resolve => {
      setTimeout(() => {
        resolve(
          import(/* webpackChunkName: "[request]" */ `@/views/${chunkPath}`)
        );
      }, 1000);
    }),
    loading: spinner,
    error: {
      render(h) {
        return h('div', {}, ['异步组件加载失败']);
      }
    },
    timeout: 10000
  });
  return () =>
    Promise.resolve({
      functional: true,
      render(h, { data, children }) {
        return h(AsyncHandler, data, children);
      }
    });
};
