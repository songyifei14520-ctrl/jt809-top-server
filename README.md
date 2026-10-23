# jt809-top-server

JT/T 809《道路运输车辆卫星定位系统 平台数据交换》**上级平台模拟服务端** —— 用于下级平台（企业监控平台）开发联调与协议调试。

A JT/T 809 superior-platform (上级平台) simulator server for debugging subordinate vehicle-monitoring platforms. Spring Boot + Netty based TCP server implementing the JT/T 809-2011 / JT/T 809-2019 platform data-exchange protocol.

## 为什么做这个项目

开发 JT/T 809 下级平台时，联调必须有一个能真实响应主链路登录、动态信息交换、报警与平台间消息的**上级平台**可连。真实上级平台接入审批慢、环境不可控，调试效率很低。本项目把多年联调中沉淀的上级平台模拟服务开源出来，让开发者本地一键启动一个可控的上级平台，下级平台指过来就能调：登录鉴权、链路测试、定位上报、报警转发、下行指令全链路可复现。

## 功能概览

- **主/从链路管理**：主链路登录（0x1001/0x1002，含接入账号校验）、主链路注销、链路测试与保活、从链路连接/断开通知
- **动态信息交换（0x1200）**：车辆注册、实时定位、车辆行驶记录、电子运单等子业务
- **报警信息交换**：报警上报与转发处理
- **平台间消息**：平台查岗、下发平台间报文、平台通知
- **静态信息交换**：车辆/驾驶员/运输企业静态信息补报与查询应答
- **下行控制消息**：车辆应急接入监管平台、车辆拍照、文本下发等控制类指令构造与下发（定时任务示例）
- **管理类消息**：管理应答、SN 通知、接收回执统计
- **JT/T 1078 扩展**：实时视频、回放、文件列表、下载相关消息的 1078↔809 转换调度
- **协议层**：TCP 粘包/拆包处理、数据转义、CRC16-CCITT 校验、编解码全套实现
- **运维接口**：HTTP REST 接口下发主/从链路消息；Redis 缓存下行消息；可选 Kafka 输出

## 技术栈

- Java 17 / Spring Boot 3
- Netty 4（TCP 服务端 + 客户端从链路）
- Redis（下行消息缓存）、Kafka（可选输出）
- Maven 构建

## 快速开始

```bash
# 1. 克隆
git clone https://github.com/<your-account>/jt809-top-server.git
cd jt809-top-server

# 2. 按需修改配置（端口、Redis、下级平台接入校验账号）
vim src/main/resources/application.properties

# 3. 编译运行
mvn clean package -DskipTests
java -jar target/jt809-top-server.jar
```

启动后监听两个端口：

| 端口 | 用途 |
| --- | --- |
| 48000（默认） | JT/T 809 TCP 服务端口，下级平台连这个端口联调 |
| 9216（默认） | HTTP 端口（健康检查 / REST 下行接口） |

## 关键配置（application.properties）

| 配置项 | 说明 |
| --- | --- |
| `netty.server.tcp-port` | 809 TCP 监听端口，默认 48000 |
| `business.platform-user-id` | 下级平台接入校验用户 ID（**演示默认值，生产务必修改**） |
| `business.platform-password` | 下级平台接入校验密码（**演示默认值，生产务必修改**） |
| `business.isopen-client` | 是否启动从链路客户端 |
| `spring.data.redis.*` | Redis 地址与密码 |

仓库内的 `src/main/resources/testData.txt` 收录了若干真实协议报文十六进制样例，可用于对照解析结果做单元调试。

## 参考标准

- JT/T 809-2011《道路运输车辆卫星定位系统 平台数据交换》
- JT/T 809-2019（同上，2019 版）
- JT/T 808-2011 / 2013《道路运输车辆卫星定位系统 终端通讯协议及数据格式》

（标准全文请通过官方渠道获取，本仓库不附带标准 PDF。）

## 致谢与来源

本项目最初基于 [ch0ice/jt809-tcp-server](https://github.com/ch0ice/jt809-tcp-server)（MIT License, © 2019 Choice）的上级服务端实现演进而来，在其基础上补齐了 2019 版协议实体、JT/T 1078 扩展、链路管理、静态信息交换、下行控制等完整联调能力，并作为模拟上级平台在多年实际联调中持续维护。感谢原作者的开源工作。

## License

[MIT License](LICENSE) —— 保留原作者版权声明（Copyright (c) 2019 Choice）。
