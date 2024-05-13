# AI Driven e-Commerce

this project is based on [litemall](https://github.com/linlinjava/litemall),We have made some modifications to it so
that it can run correctly and efficiently on AWS Lambda.

What changes have we made：

1. Treat litemall as a regular Java web project and
   use [aws-lambda-web-adapter](https://github.com/awslabs/aws-lambda-web-adapter) to package it, so that we can migrate
   traditional Java web projects to AWS lambda with minimal changes
2. The [AWS lambda snapstart](https://docs.aws.amazon.com/lambda/latest/dg/snapstart.html) feature has been enabled,
   which makes the cold start speed of Java projects running on AWS lambda faster.
3. Added Amazon ElastiCache Redis as a distributed cache，modified Litemall's Shiro to use Redis for distributed session
   synchronization. This allows multiple lambda instances to synchronize user login status.
4. Added a POC(Proof Of Concept)：use [Amazon Q](https://aws.amazon.com/q/) to update litemall from java 8 to java 17
5. Add AI functionality to Litemall by using [AWS Bedlock](https://aws.amazon.com/bedrock)

# How To deploy

## Compile or Deploy Admin backend

1. change to project's root directory [litemall](./)
2. maven package project
    ```shell
    # maven build project
    mvn clean
    mvn install
    mvn clean package
    ```
3. use [aws sam](https://aws.amazon.com/serverless/sam/) deploy project to [aws lambda](https://aws.amazon.com/lambda/)
    ```
    # sam build project
    sam build
    # deploy to aws lambda
    sam deploy
    ```

## Compile frontend

### Admin console frontend

1. change to project's litemall-admin directory [litemall-admin](./litemall-admin)
2. use npm build frontend project
    ```shell
    cd litemall-admin
    npm install --registry=https://registry.npmmirror.com
    # for developing and debugging locally
    npm run dev
    # optional: for test environment
    # npm run build:dep
    # optional: for product environment
    # npm run build:prod
    ```

### mall website frontend

1. change to project's litemall-vue directory [litemall-vue](./litemall-vue)
2. use npm build frontend project
   ```shell
   cd litemall-vue
   npm install --registry=https://registry.npmmirror.com
   # for developing and debugging locally
   npm run dev
   # optional: for test environment
   # npm run build:dep
   # optional: for product environment
   # npm run build:prod
   ```