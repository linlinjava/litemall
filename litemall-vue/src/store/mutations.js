import * as types from './mutation-types'

export default {
  [types.CHANGE_HEADER] (state,payload) {
    state.showHeader=payload.showHeader;
    state.title=payload.title;
  }
}