import docCookie from '@/core/utils/cookies'
import { setLocalStorage, getLocalStorage } from '@/core/utils/local-storage';

export function procJumpToken() {
  if (docCookie.getItem('litetoken')) {
    let litetoken = docCookie.getItem('litetoken')
    if (!PSMU.isEmtVal(litetoken)) {
      let jsonStr = PSMU.DU(litetoken)
      console.log("jsonStr is : ", jsonStr);
      if (!PSMU.isEmt(jsonStr) && PSMU.isString(jsonStr)) {
        let tokenInfo = JSON.parse(jsonStr)
        console.log("tokenInfo is : ", tokenInfo);
        if (tokenInfo && tokenInfo.token) {
          setLocalStorage({
            Authorization: tokenInfo.token
          });
        }
        const infoData = getLocalStorage(
          'Authorization'
        )
        console.log("procJumpToken getLocalStorage ", infoData);

        if (tokenInfo && tokenInfo.userInfo) {
          // debugger
          setLocalStorage({
            avatar: tokenInfo.userInfo.avatarUrl,
            // user_id: data.user_id,
            // background_image: data.background_image,
            nickName: tokenInfo.userInfo.nickName
          });
        }

        const infoData2 = getLocalStorage(
          'avatarUrl',
          'nickName'
        )
        console.log("procJumpToken getLocalStorage 2 ", infoData2);
      }
    }
    docCookie.removeItem('litetoken')
  }
}
