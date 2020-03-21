# 2 litemall基础系统

目前litemall基础系统由以下部分组成：

* litemall-core模块
* litemall-db模块
* litemall-all模块
* litemall-all-war模块

litemall-db模块提供数据库访问服务。

litemall-core模块提供通用服务。

litemall-all模块则只是一个包裹模块，几乎没有任何代码。该模块的作用是融合两个spring boot模块
和litemall-admin模块静态文件到一个单独Spring Boot可执行jar包中。

litemall-all-war模块和litemall-all模块是一样的作用，只是采用war打包方式。

## 2.2 litemall-db

litemall-db模块是一个普通的Spring Boot应用，基于mybatis框架实现数据库访问操作，对外提供业务数据访问服务。

此外，litemall-db最终是作为一个类库被其他模块所依赖使用，因此并不对外
直接服务，没有使用Spring MVC技术。

技术：
* Spring Boot 2.x
* MySQL
* Druid
* Mybatis
* PageHelper
* Mybatis Generator
* Mybatis Generator非官方插件mybatis-generator-plugin

![](./pics/platform/db-main.png)

这里litemall-db模块可以分成以下几种代码：

* mybatis数据库访问代码
  * generator生成代码
  * 非generator手动代码
* 业务代码
* mybatis generator支持代码

### 2.2.1 mybatis数据库访问代码

mybatis数据库访问代码是指dao接口代码、dao数据库XML文件和domain代码:
* dao接口代码，声明了数据库访问接口
* dao数据库XML文件，实现了数据库访问操作
* domain代码，则是保存数据库返回数据。

此外，这里的数据库访问代码又进一步分成
* mybatis generator自动生成代码，即基于mybatis generator相关插件自动生成上述三种代码或文件；
* 非mybatis generator手动代码，则是需要开发者自己编写上述三种代码。

#### 2.2.1.1 自动生成代码

![](./pics/platform/mybatis-generator.png)

如上图所示，双击`mybatis-generator:generate`，则mybatis generator插件会：

1. 读取`mybatis-generator`文件夹下的`generatorConfig.xml`文件
2. 根据`jdbcConnection`访问数据库
3. 根据`table`, 自动生成三种代码:
   * src文件夹`org.linlinjava.litemall.db.dao` 包内的Java代码
   * src文件夹`org.linlinjava.litemall.db.domain` 包内的Java代码
   * resources文件夹`org.linlinjava.litemall.db.domain.dao` 内的XML文件

以上三种代码即可封装对数据库的操作，开发者无需直接操作sql代码，
而是直接操作Java代码来完成对数据库的访问处理。

关于如何基于mybatis的Example代码来访问数据库，请查阅相关资料，
或者参考本模块`org.linlinjava.litemall.db.service` 包内的Java代码。

当然，为了达到数据库访问效率，开发者也可以手动自定义mapper文件和对应的Java代码。
例如，当需要访问两个表的数据时，这里是在业务层通过Java代码遍历的形式来访问两个表，
也可以通过自定义的mapper文件来实现。

接下来，以`litemall_brand`表举例说明如何自动生成代码：

1. mybatis generator插件会读取`table`标签

    ```
    <generatorConfiguration>
         <table tableName="litemall_brand">
             <generatedKey column="id" sqlStatement="MySql" identity="true" />
         </table>
    </generatorConfiguration>
    ```
    
2. 自动生产src文件夹下domain包内的LitemallBrand.java类、LitemallBrandExample.java类、
    dao包内的LitemallBrandMapper.java接口和resources文件夹下dao包内的LitemallBrandMapper.xml文件。

3. 手动在service包内创建LitemallBrandService.java来对外提供具体的服务。
   例如，为了得到Brand列表，那么创建list方法，基于前面创建的三个Java来来实现。
   
   ```java
    @Service
    public class LitemallBrandService {
       @Resource
       private LitemallBrandMapper brandMapper;

        public List<LitemallBrand> query(int offset, int limit) {
           LitemallBrandExample example = new LitemallBrandExample();
           example.or().andDeletedEqualTo(false);
           PageHelper.startPage(offset, limit);
           return brandMapper.selectByExample(example);    
        }
    }
   ```

关于mybatis generator的用法，可以自行查阅官网或文档。

#### 2.2.1.2 手动代码

虽然mybatis generator可以自动生产代码，帮助开发者简化开发工作，但是在涉及到多表操作或特殊数据库操作时，
仍然需要开发者自己手动编写基于mybatis框架的相关代码。

具体如何基于mybatis框架编写代码，请开发者自己查找资料。

这里，以统计功能举例说明：

1. dao代码

   在src文件夹`org.linlinjava.litemall.db.domain` 包内的StatMapper.java代码定义了数据库访问的接口

2. domain代码
 
   如果希望数据库操作返回数据模型，可以在src文件夹`org.linlinjava.litemall.db.domain` 包内创建相应代码。
   而这里统计功能是采用简化的`List<Map>`保存数据，没有定义domain代码。

3. XML文件

   在resources文件夹`org.linlinjava.litemall.db.domain.dao` 内的StatMapper.xml文件则是实现真正的数据库访问操作。

4. service代码

   这里可以在`org.linlinjava.litemall.db.service` 内定义一个StatServie.java代码，调用底层mapper代码，对外服务。
    ```
    @Service
    public class StatService {
        @Resource
        private StatMapper statMapper;

        public List<Map> statUser() {
            return statMapper.statUser();
        }

        public List<Map> statOrder(){
            return statMapper.statOrder();
        }

        public List<Map> statGoods(){
            return statMapper.statGoods();
        }
    }
    ```
   
### 2.2.2 业务代码

虽然2.2.1节所述代码已经能够提供数据库访问操作，但是这里需要进一步地抽象出业务访问层代码，
即基于2.2.1所述代码和实际业务需求实现一些具体业务相关的操作，对其他模块提供便捷业务数据服务。

需要指出的是，这里的业务代码往往是单表相关的业务代码，而涉及到多表操作的java代码通常是在其他高层模块中实现。
这里的业务分层并不是绝对的。例如，开发者可以取消这里的业务代码，而在其他模块中直接调用2.2.1所述代码。

通常业务层代码在src文件夹`org.linlinjava.litemall.db.service` 包中。

### 2.2.3 mybatis generator支持代码

mybatis generator自动生成代码时，通过内置类型转换器自动把数据库类型转换成Java类。
例如数据库类型`varchar`自动转化成`java.lang.String`。

但是某些情况下，可以通过自定义TypeHandler的方式来采用自定义的类型转换器。
开发者可以自行阅读相关资料了解。

本项目中，为了简化数据表的设计，某些字段采用`varchar`来存储Json格式的数据。
例如商品的图片列表可以直接采用`[url0, url1, ...]`来存储，而不需要设计一个专门商品图片表。

这里通过自定义TypeHandler，可以实现Java的`String[]`和数据库类型`varchar`的自动转换。

1. 实现JsonStringArrayTypeHandler类；
2. 在mybatis generator配置文件中，配置需要的字段；
    ```
        <table tableName="litemall_goods">
            <columnOverride column="gallery" javaType="java.lang.String[]"
                            typeHandler="org.linlinjava.litemall.db.mybatis.JsonStringArrayTypeHandler"/>
        </table>
    ```
3. 使用mybatis generator自动生成代码，可以看到LitemallGoods的gallery是`String[]`类型。

目前只实现了两个自定义TypeHandler：
* JsonStringArrayTypeHandler类，实现`String[]`和`varchar`的转换，保存的JSON数据格式是`[string0, string1, ...]`
* JsonIntegerArrayTypeHandler类，实现`Integer[]`和`varchar`的转换，保存的JSON数据格式是`[integer0, integer1, ...]`

如果开发者需要保存其他格式的JSON数据或者自定义格式的数据，请自行开发。

### 2.2.4 新服务组件

本节介绍如果基于一个表创建新的服务组件。

1. 在数据库里面创建一个表，例如`litemall_demo`:

    ```sql
    CREATE TABLE `litemall`.`litemall_demo` (
      `id` INT NOT NULL AUTO_INCREMENT,
      `name` VARCHAR(45) NULL,
      `address` VARCHAR(45) NULL,
      PRIMARY KEY (`id`));
      
    INSERT INTO `litemall`.`litemall_demo` (`id`, `name`, `address`) 
    VALUES ('1', 'hello', 'world');
    ```

2. 在generatorConfig.xml中增加一个新的table标签

    ```
    <generatorConfiguration>
         <table tableName="litemall_demo">
             <generatedKey column="id" sqlStatement="MySql" identity="true" />
         </table>
    </generatorConfiguration>
    ```
3. 双击mybatis generator插件，检查LitemallDemo.java类、LitemallDemoExample.java类、
   LitemallDemoMapper.java接口和LitemallDemoMapper.xml是否生产。
   
4. 在service里面新建LitemallDemoService.java类，

   ```java
    @Service
    public class LitemallDemoService {
       @Resource
       private LitemallDemoMapper demoMapper;

        public List<LitemallDemo> list() {
           LitemallDemoExample example = new LitemallDemoExample();
           return demoMapper.selectByExample(example);    
        }
    }
   ```

5. 可以在`src/test/java/org.linlinjava.litemall.db`包里面创建LitemallDemoTest.java类,
    使用Junit进行测试。

    ```java
    @WebAppConfiguration
    @RunWith(SpringJUnit4ClassRunner.class)
    @SpringBootTest
    public class LitemallDemoTest {    
       @Autowired
       private LitemallDemoService demoService;
    
       @Test
       public void test() {    
        List<LitemallDemo> litemallDemoList = demoService.list();
        Assert.assertTrue(litemallDemoList.size() != 0);
       }
    
    }
    ```

6. 同样地，可以在Controller中使用LitemallDemoService来对外提供服务。
    
    ```java
    @RestController
    @RequestMapping("/demo")
    public class DemoController {
       @Autowired
       private LitemallDemoService demoService;
    
       @RequestMapping("/list")
       public Object list(){    
           List<LitemallDemo> demoList = demoService.list();   
           return demoList;
       }
    }
    ```

### 2.2.5 逻辑删除

数据删除可以直接进行物理删除，也可以采用设置删除字段进行逻辑删除。
根据具体业务，也有可能部分数据可以物理删除，部分数据只能逻辑删除。

目前本项目所有删除操作都是逻辑删除。
开发者可以自行修改代码进行真正的物理删除，来避免数据库保存无用数据。

### 2.2.6 并发访问

由于服务是多线程并发的，因此这带来了多线程同时操作数据库中同一数据的问题。
由于数据极少删除或者是逻辑删除，因此操作数据，可以简化成更新数据。
也就是说，需要解决多线程更新数据库同一数据的并发问题。

* https://blog.csdn.net/qq315737546/article/details/76850173
* http://baijiahao.baidu.com/s?id=1571172971189129
* https://blog.csdn.net/speedme/article/details/48525119

例如，下单操作中，用户A购买商品G的数量是1个，而用户B同一时间也购买商品G的
数量也是1个，那么如果没有很好地并发控制，有可能商品G的数量仅仅是减1，而不是
设想的2。

通常采用悲观锁或者乐观锁来处理并发更新问题，

本项目目前采用基于`update_time`字段的乐观锁机制。
原理是：

1. 每个表都存在update_time字段
2. 更新前，先查询数据，得到表的业务数据和update_time字段
3. 更新时，通过where条件查询当前update_time字段和数据库中当前update_time字段是否相同。
   * 如果相同，说明数据没有改变则可以更新，数据更新同时update_time设置当前更新时间；
   * 如果不相同，则说明数据改变了则更新失败，不能修改数据。   
   
当然，由于采用乐观锁，这里也会带来另外一个问题：
数据库有可能更新失败，那么如何处理更新失败的情况？
目前只是简单地报错更新失败。

### 2.2.7 事务管理

litemall-db模块中不涉及到事务管理，而是在其他后台服务模块中涉及。
但是其他后台服务模块因为依赖litemall-db模块，因此这里列出。

事务管理的问题出现在多个表的修改操作中。

例如用户A修改表1，再修改表2，而如果修改表2的时候出现错误推出，
此时如果没有引入事务管理，那么这里会存在表1数据已更新，表2数据
未更新的问题。

解决的方案是采用spring自带的事务管理机制。
当事务管理中的任何SQL操作出现错误而抛出异常时，则回滚之前的操作。

注意：
> 并发访问是多个用户同时操作单个表时可能出现的问题；
> 而事务管理是单个用户操作多个表时可能出现的问题。

### 2.2.8 mybatis增强框架

通过mybatis-generator已经自动生成了很多代码，而且具有一定的功能，
但是开发者仍然需要基于生成的代码写一些固定的CRUD代码。

目前发现已经有两个mybatis增强的框架可以进一步简化代码和功能增强：
* [mybatis-plus](https://github.com/baomidou/mybatis-plus)
* [Mapper](https://github.com/abel533/Mapper)

目前没有采用，以后可能会基于其中之一重构数据库访问代码。
开发者感兴趣的可以自行研究使用。

## 2.3 litemall-core

litemall-core模块是本项目通用的代码：

* config

  通用配置，例如开启Spring Boot异步功能。

* util

  工具代码。

* qcode

  本项目定制的分享二维码图片。
  
* storage

  存储功能，支持本地存储、腾讯云存储、阿里云存储和七牛云存储。
      
* notify

  通知提醒功能，支持邮件通知、短信通知和微信通知。

* express

  物流服务，查询订单物流信息。
  
* system

  通过litemall-db模块的数据库访问，读取本项目系统配置信息。

* validator

  提供两个校验注解，帮助后端验证请求参数。
  
### 2.3.1 config

#### 2.3.1.1 CorsConfig

目前开发过程中，CORS配置是允许所有请求。

真正部署时，开发者需要做一些调整，来保证当前的服务只接受来自所设置域名的请求。

#### 2.3.1.2 GlobalExceptionHandler

如果系统内部产生了异常而开发者没有catch，那么异常的内容会发送到前端。
这里通过提供全局异常处理器，来处理所有开发者没有处理的异常，返回
“系统内部错误”之类的信息给前端从而达到保护系统的效果。

#### 2.3.1.3 JacksonConfig

Jackson做一些设置。

### 2.3.2 util

注意
> 这里的util代码不会涉及具体业务，例如litemall-db模块中存在一个
> OrderUtil类处理数据库中litemall_order表的一些转换工作。

#### 2.3.2.1 ResponseUtil

这里是用于设置response中body的内容格式。

如果是成功则是 ：

```json
{
  errno: 0,
  errmsg: '成功',
  data: XXX
}
```

如果失败则是：

```json
{
  errno: 非0的XXX,
  errmsg: XXX
}
```

#### 2.3.2.2 JacksonUtil

当请求时POST时，请求的json内容在body。
通常存在存在两种方式取出数据：
* 如果json内容正好对应一个POJO，那么在方法中使用POJO时，spring会自动解析填充数据；
* 或者开发者自己采用jackson或者其他json处理库手动解析数据。

这里JacksonUtil简化解析工作。这里代码有局限性，开发者请谨慎使用，或者熟悉Jackson
使用的开发者欢迎优化代码。

#### 2.3.2.3 CharUtil

生成固定长度的随机字母字符串或者随机数字字符串。

#### 2.3.2.4 bcypt

这里是用于对用户密码或者管理员密码加密存储。

bcypt代码本质上是spring里面的代码。

### 2.3.3 二维码

见QCodeService类。

### 2.3.4 对象存储

对象存储服务目前的目标是支持图片的上传下载。

对象存储服务会自动读取配置配置，然后实例化服务。

对象存储接口：
```
public interface Storage {
    void store(InputStream inputStream, long contentLength, String contentType, String keyName);
    Stream<Path> loadAll();
    Path load(String keyName);
    Resource loadAsResource(String keyName);
    void delete(String keyName);
    String generateUrl(String keyName);
}
```

#### 2.3.4.1 本地存储服务

见LocalStorage类。

#### 2.3.4.2 腾讯云存储服务

见TencentStorage类。

#### 2.3.4.3 阿里云存储服务

见AliyunStorage类。

#### 2.3.4.4 七牛云存储服务

见QiniuStorage类。

### 2.3.5 消息通知

消息通知用于通知用户或者管理员。

注意：
> 目前这里实现比较粗糙，以后会完善细节。

#### 2.3.5.1 邮件通知

见NotifyService类的`notifyMail`方法。

#### 2.3.5.2 短信通知

见NotifyService类的`notifySms`和`notifySmsTemplate`方法。

而短信通知实现类见`TencentSmsSender`类。
也就是目前仅支持腾讯云短信服务，其他短信服务不支持。
此外，开发者必须先在腾讯云短信平台申请模板才能使用。

#### 2.3.5.3 微信通知

见NotifyService类的`notifySms`和`notifyWxTemplate`方法。
而微信通知实现类见`WxTemplateSender`类。
开发者必须在微信平台申请模板才能使用。

### 2.3.6 物流跟踪

物流跟踪是基于第三方服务快鸟物流查询服务。
开发者需要申请才能使用。

见`ExpressService`类。

### 2.3.7 系统设置

### 2.3.8 校验注解

自定了两个校验注解，帮助开发者校验HTTP参数。

#### 2.3.8.1 Order

校验用户请求参数值只能是`desc`或者`asc`。

注意，这里的Order不是订单的意思，而是排序的意思。

1. 定义注解Order
    ```
    @Target({METHOD, FIELD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = OrderValidator.class)
    public @interface Order {
        String message() default "排序类型不支持";
        String[] accepts() default {"desc", "asc"};
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
    ```
2. 实现OrderValidator
    ```
    public class OrderValidator implements ConstraintValidator<Order, String> {
        private List<String> valueList;
        @Override
        public void initialize(Order order) {
            valueList = new ArrayList<String>();
            for (String val : order.accepts()) {
                valueList.add(val.toUpperCase());
            }
        }
        @Override
        public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
            if (!valueList.contains(s.toUpperCase())) {
                return false;
            }
            return true;
        }
    }
    ```
3. 使用注解
    ```
    @RestController
    @RequestMapping("/wx/topic")
    @Validated
    public class WxTopicController {
        @GetMapping("list")
        public Object list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
         ...
         }
    ```
    
#### 2.3.8.2 Sort

校验用户请求参数值只能是`add_time`或者`id`。

1. 定义注解Sort
    ```
    @Target({METHOD, FIELD, PARAMETER})
    @Retention(RUNTIME)
    @Documented
    @Constraint(validatedBy = SortValidator.class)
    public @interface Sort {
        String message() default "排序字段不支持";
        String[] accepts() default {"add_time", "id"};
        Class<?>[] groups() default {};
        Class<? extends Payload>[] payload() default {};
    }
    ```
2. 实现SortValidator
    ```
    public class SortValidator implements ConstraintValidator<Sort, String> {
        private List<String> valueList;
    
        @Override
        public void initialize(Sort sort) {
            valueList = new ArrayList<String>();
            for (String val : sort.accepts()) {
                valueList.add(val.toUpperCase());
            }
        }
    
        @Override
        public boolean isValid(String s, ConstraintValidatorContext constraintValidatorContext) {
            if (!valueList.contains(s.toUpperCase())) {
                return false;
            }
            return true;
        }
    }
    ```
3. 使用注解
    ```
    @RestController
    @RequestMapping("/wx/topic")
    @Validated
    public class WxTopicController {
        @GetMapping("list")
        public Object list(@RequestParam(defaultValue = "1") Integer page,
                       @RequestParam(defaultValue = "10") Integer limit,
                       @Sort @RequestParam(defaultValue = "add_time") String sort,
                       @Order @RequestParam(defaultValue = "desc") String order) {
         ...
         }
    ```

## 2.4 litemall-all

在章节1.5中讨论的部署方案中设计了一种单服务器单服务方案，
也就是说两个后台服务和静态文件都部署在一个Spring Boot可执行jar包中。

查看litemall-all模块，代码仅仅只有一个Application类。

实际的原理是litemall-all模块内的pom.xml文件：

1. 打包方式是`jar`，因此最后会打包可执行jar格式；
2. 对litemall-wx-api模块和litemall-admin-api模块依赖，
   因此打包时会作为依赖库而打包到litemall-all模块的输出中；
3. 使用copy-resources插件，在打包时把litemall-admin模块的dist
   文件夹拷贝到litemall-all模块的static文件夹中；而这个文件夹
   正是Spring Boot应用的默认静态文件路径。
   
   注意：
   > 这个插件只是简单的拷贝操作；因此开发者应该在打包litemall-all
   > 之前确保先编译litemall-admin模块得到最终静态文件。


## 2.5 litemall-all-war

litemall-all-war模块就是对litemall-all模块进行少量调整，
最后打包时会在target目录下面生成litemall.war，用于tomcat部署。