# 关于本文档

# 待办和计划

- [x] Amazon Q & Codewhisperer 进行Java8升级到Java17，并进行AI代码提示
- [x] 改造后的代码需要在AWS Lambda中运行
  - [x] 使用AWS Lambda SnapStart加速
  - [x] 使用AWS Lambda WebAdapter
- [ ] 前后端部署联调，workshop文档初步整理
- [ ] 完成最后的测试，workshop文档整理
  - [ ] MySQL资源创建和初始化 ?
  - [ ] Mistral Model ?
  - [ ] VUE 前端的部署S3+Cloudfront
- 复盘
- 先写文档

# 操作指南

```shell
mvn clean
mvn clean package

sam build
sam deploy
```

```shell
cd litemall/litemall-admin
npm install --registry=https://registry.npmmirror.com
npm run dev

npm run build:dep
npm run build:prod
```

```shell
cd litemall/litemall-vue
npm install --registry=https://registry.npmmirror.com
npm run dev

npm run build:dep
npm run build:prod
```

```shell
cd dist
aws s3 sync . s3://litemall-think-1
```