# Spawncursion
### Currently in dev QAQ
## Intro
A Minecraft mod aims for a more controllable and custom-designable "spawner" to 
allow users to generate a series of multi-type enemies with initial effects,
trace and control the generated enemies by their amounts, lifecycles and active areas, and introduce the generating process in 
natural features , boss-like enemies or world events.

## TODO list
### Core Features
| ID | 名称      | 描述                                                          |
|----|---------|-------------------------------------------------------------|
| 1  | 复种类支持   | 应当能在单刷怪笼内定义多种类型的待召唤实体                                       |
| 2  | 数量控制    | 应当能控制每种实体在单刷怪笼生命周期内应当生成/被击杀的数量                              |
| 3  | 生成追踪    | 应当追踪被本刷怪笼生成的实体，主要用于记录其死亡信息以用于`#2`的控制，同时显然应当有机制响应/控制追踪失败时的行为 |
| 4  | 生物附加效果  | 应当能使通过本刷怪笼生成的实体获得属性加成，药水效果                                  |
| 5  | 生物行为附加  | 应当能修正实体的`ai_goal`，或附加兴趣点信息，修正其敌对目标                          |
| 6  | 生成控制    | 应当能控制本刷怪笼的某种实体的刷怪曲线，刷怪范围                                    |
| 7  | 刷怪笼模板   | 应当能通过`json`文件配置刷怪笼信息，详见`template features`                  |
| 8  | 刷怪笼附加功能 | 见`spawner extend features`                                   |

### Template Features
| ID | 名称   | 描述                     |
|----|------|------------------------|
| 1  | 模板继承 | 应当能使某刷怪笼的配置继承一个父亲配置    |
| 2  | 变量   | 应当能允许在模板配置中，用变量指代某一个字段 |

### Spawner Extend Features
| ID | 名称   | 描述                                                      |
|----|------|---------------------------------------------------------|
| 1  | 激活性  | 应当判断该刷怪笼是否应当生成实体（例如通过判断附近是否有玩家活动），如果为假，应当停止刷新并可能会回收追踪实体 |
| 2  | 子刷怪笼 | 当刷怪笼被激活时，应尝试在指定位置生成配置中的子刷怪笼；当激活失效时，可能应当摧毁子刷怪笼           |
| 3  | 邻域保护 | 应当阻止刷怪笼在刷怪完成前被摧毁，同时可以通过一些手段确保周围区域可以刷新实体，例如定期更新周围方块      |
| 4  | 奖励箱  | 刷怪完成后，`#3`应当失效,且在刷怪笼中心生成奖励箱                             |



