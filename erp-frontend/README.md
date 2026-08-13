### 项目介绍
基于SpringBoot框架的中小企业完全开源的ERP。

### 环境版本说明
* Node.js 24 LTS
* pnpm 9.15.9

无需修改全局 pnpm，使用项目锁定版本：

```powershell
npx --yes pnpm@9.15.9 install --frozen-lockfile
npx --yes pnpm@9.15.9 dev
```

生产构建与升级说明见 [UPGRADE.md](UPGRADE.md)。

日常验证：

```powershell
powershell -ExecutionPolicy Bypass -File .\scripts\verify.ps1
```

该门禁会依次执行工具链校验、TypeScript 检查、Vitest 回归测试和生产构建。

本地后端已启动时，可执行真实浏览器登录回归：

```powershell
npx --yes pnpm@9.15.9 exec playwright install chromium
powershell -ExecutionPolicy Bypass -File .\scripts\verify-e2e.ps1
```

### 主要技术框架
* Vue 3.4.38
* ant-design-vue 4.2.3
* vxe-table 4.7.68
* vue-vben-admin

### License
项目使用Apache 2.0许可证，请遵守此许可证的限制条件。

### 其他说明
* 作者是一个只有几年开发经验的后端开发人员，如有错误之处，望斧正。
* 后端项目Gitee地址：[点此进入][xingyunGitee]

### 注意事项
老版项目基于ElementUI，已转移到v1分支。
Vue2项目已转移至v3分支。

[xingyunGitee]: https://gitee.com/lframework/xingyun
