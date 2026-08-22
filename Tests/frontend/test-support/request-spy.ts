export type RequestConfig = {
  url: string;
  method: string;
  data?: unknown;
  params?: unknown;
};

export const requestCalls: RequestConfig[] = [];

export function resetRequestCalls() {
  requestCalls.splice(0, requestCalls.length);
}
export default function request(config: RequestConfig) {
  requestCalls.push(config);
  return Promise.resolve({
    data: {
      errno: 0,
      errmsg: '成功',
      data: {
        echo: config
      }
    }
  });
}
