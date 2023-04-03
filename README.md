# Advanced Fish - 高级钓鱼

## 此插件原作者已停止维护! 此仓库为xiaoyueyoqwq和CBer_SuXuan代理更新！</br>
</br>
## 高级钓鱼，高度自定义，不止钓鱼!</br>
</br>
> Mcbbs帖
- https://www.mcbbs.net/thread-1393202-1-1.html
---
> bStats
- https://bstats.org/plugin/bukkit/AdvancedFish/16770
---
> 原Gitee仓库
- https://gitee.com/A2000000/advanced-fish
---
> 此插件的配置文件较多，您需要自己慢慢阅读，只要您读通配置文件，那么您可以轻而易举地创建出属于您的独一无二的鱼类，您可以通过查看 Wiki 来快速了解插件内容 -> https://gitee.com/A2000000/advanced-fish/wikis#
---
## 指令
> 主要指令
- af getFishList - 获取所有的已加载鱼类.
- af getFishItem <鱼类文件名> <自定义的钓手名> - 获取指定鱼类的物品.
- af getFurnaceFishItem <鱼类文件名> <自定义的钓手名> - 获取指定鱼类烹饪后的物品.
- af getBaitList - 获取所有的已加载鱼饵.
- af getBaitItem <鱼饵文件名> <自定义的制作者名> - 获取指定鱼饵的物品.
- af reload - 重新注册所有内容 (自动重载内容请详细查看鱼类配置文件)
---
> 辅助指令
- getEnchantment - 辅助指令, 获取手上物品的附魔以供配置文件参考.
- getPotionEffect - 辅助指令, 获取手上物品的药水以供配置文件参考.
- getBiome - 辅助指令，获取您现在所在的生物群系英文名称。
- getMaterial - 辅助指令，获取您现在手上物品的英文名称。
---
> 鱼域指令
- area getAreaList - 查看所有已加载鱼域.
- area create <鱼域文件名> - 创建一个新的鱼域.
- area delete <鱼域文件名> - 删除一个鱼域.
- area setName <鱼域文件名> <鱼域名> - 设置鱼域的名字.
- area setMax <鱼类文件名> - 设置鱼域的最高点.
- area setMin <鱼类文件名> - 设置鱼域的最低点.
---
> 比赛指令
- fishMatch start - 举办一个钓鱼比赛.
- fishMatch join - 加入钓鱼比赛.
- fishMatch leave - 退出钓鱼比赛
- fishMatch set <玩家> <积分> - 设置玩家钓鱼比赛积分.
- census - 查看钓鱼比赛统计 Gui.
---
> 控制台指令
- afc getFishList - 获取所有的已加载鱼类.
- afc getFishItem <鱼类文件名> <自定义的钓手名> <给予的玩家> - 获取指定鱼类的物品.
- afc getFurnaceFishItem <鱼类文件名> <自定义的钓手名> <给予的玩家> - 获取指定鱼类烹饪后的物品.
- afc getBaitList - 获取所有的已加载鱼饵.
- afc getBaitItem <鱼饵文件名> <自定义的制作者名> <给予的玩家> - 获取指定鱼饵的物品.
- afc reload - 重新注册所有内容 (自动重载内容请详细查看鱼类配置文件)
---
## 注意事项
> 基础配置
- 您至少需要一条没有任何限制便可以钓上来的鱼来防止没有内容而引起的报错。
- 如果您的版本低于 1.16 那么您需要重写默认的配置文件，默认的配置文件内含有高版本附魔与物品，您需要改写它们。
- 此插件会使用自定义鱼类物品的名字以获取对应数据，这将意味着您每一条鱼、鱼饵、食用自定义物品都只能够有一个名字，无论是文件名还是自定义物品的物品名。
> 作者的话
- 我在编写此插件时较为急促，这导致此插件的代码很多地方很 Gay
- 此插件也许不会再存在任何大更新，我仅仅只是保证正常运行不出错，重写可能在计划中，但是依然待考虑。
---
