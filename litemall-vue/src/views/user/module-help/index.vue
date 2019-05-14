<template>
  <div>
 <van-collapse :accordion="true" v-model="activeNames">
  <van-collapse-item :title="issue.question" :name="index" v-for="(issue, index) in issueList" :key="index">
    {{issue.answer}}  
  </van-collapse-item>
</van-collapse>
  </div>
</template>

<script>
import { Collapse, CollapseItem } from 'vant';
import { issueList } from '@/api/api';

export default {
  data() {
    return {
      activeNames: [-1],
      issueList: []
    };
  },
  created() {
    this.getIssueList();
  },
  methods: {
    getIssueList() {
      issueList().then(res => {
        this.issueList = res.data.data.list
      })
    }
  },

  components: {
    [Collapse.name]: Collapse,
    [CollapseItem.name]: CollapseItem
  }
};
</script>