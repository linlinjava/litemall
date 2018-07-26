开发者需要注意的是

litemall-os-api.jar、litemall-wx-api.jar和litemall-admin-api.jar三个模块内部
已经有默认的开发配置文件，但是这些配置文件可能仅仅适用于开发阶段。

为了应用部署阶段时期的配置文件，开发者可以在config文件夹里面的同名配置文件中
采用新的配置信息。

例如litemall.notify.mail.enable在开发litemall-core模块内部resources/application-core.yml
中设置的是false，而这里config/application-core.yml中设置的是true，项目部署启动时则会先
读取config/application-core.yml中的true值。