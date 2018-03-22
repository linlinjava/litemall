export default {
  ok: data => ({
    errno: 0,
    errmsg: 'success',
    data: data
  }),
  fail: () => ({
    errno: -1,
    errmsg: 'fail'
  })
}
