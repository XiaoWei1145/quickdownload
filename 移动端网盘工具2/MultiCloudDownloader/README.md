# MultiCloud Downloader (多云下载器)

一个支持多网盘平台链接解析和下载的Android应用。

## 功能特性

- 支持多个网盘平台链接解析（百度网盘、阿里云盘、夸克网盘、天翼云、迅雷网盘等）
- 多任务并发下载（最多3个并发）
- 断点续传功能
- 自定义下载路径
- 后台下载支持
- 使用 Jetpack Compose 构建现代化界面

## 项目结构

```
MultiCloudDownloader/
├── app/                 # 主应用模块
├── core/                # 核心逻辑模块
├── parser/              # 链接解析模块
├── download/            # 下载管理模块
└── ui/                  # 界面组件模块
```

## 支持的网盘平台

| 网盘 | 域名特征 | 解析方式 |
|------|----------|----------|
| 百度网盘 | pan.baidu.com | HTML解析 + 模拟登录（可选） |
| 阿里云盘 | aliyundrive.com | API调用（公开接口） |
| 夸克网盘 | quark.cn | 模拟请求获取直链 |
| 天翼云 | cloud.189.cn | HTML解析提取下载地址 |
| 迅雷网盘 | share.weiyun.com | 微云接口解析 |

## 技术栈

- Kotlin 编程语言
- Jetpack Compose 现代界面工具包
- OkHttp 网络请求库
- MVVM 架构模式
- Coroutines 异步处理

## 构建项目

```bash
# 构建项目
./gradlew build

# 构建APK
./gradlew assembleDebug

# 构建发布版APK
./gradlew assembleRelease
```

## GitHub Actions 自动打包

本项目配置了 GitHub Actions 工作流，可以自动构建和打包 APK 文件：

1. 每当有代码推送到 main/master 分支时，会自动触发构建
2. 每当有 Pull Request 提交到 main/master 分支时，也会触发构建
3. 构建成功后，Debug 版本的 APK 会作为构建产物上传，可以在 Actions 页面下载

### 发布版本自动打包

当推送一个以 `v` 开头的标签（如 `v1.0.0`）时，会自动触发发布工作流：

1. 自动构建 Debug 和 Release 版本 APK
2. 创建 GitHub Release
3. 将 APK 文件作为附件上传到 Release 中

### 配置签名密钥（可选）

如果要构建签名的 Release APK，需要在仓库的 Secrets 中配置以下环境变量：

- `SIGNING_KEY`: Base64 编码的签名密钥文件
- `KEY_ALIAS`: 密钥别名
- `KEY_PASSWORD`: 密钥密码
- `STORE_PASSWORD`: 密钥库密码

可以通过以下命令生成 Base64 编码的密钥文件：

```bash
base64 keystore.jks > keystore.base64
```

然后将生成的 base64 内容添加到仓库的 Secrets 中。

## 使用说明

1. 在输入框中粘贴网盘分享链接
2. 点击"解析并下载"按钮
3. 应用将自动解析链接并开始下载文件
4. 可以在下载列表中查看下载进度和管理任务
5. 通过设置可以自定义下载目录

## 注意事项

- 本应用仅解析公开分享链接，不涉及任何账号登录操作
- 不会存储用户密码或提取码，所有信息仅在内存中处理
- 请遵守各网盘平台的使用条款和robots.txt协议