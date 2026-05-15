# 高校选课管理系统

## 一、项目简介

本项目是一个基于 Spring Boot 3.x 开发的高校选课管理系统基础处理工具，主要用于完成学生选课数据的批量导入、去重、排序、分类、检索和页面展示。

系统适用于高校选课数据的基础管理场景，支持用户通过页面输入多行 CSV 格式选课数据，后端接收数据后进行处理，并将处理结果返回前端页面展示。

---

## 二、技术栈

| 技术 | 说明 |
|---|---|
| Java 17 | 后端开发语言 |
| Spring Boot 3.x | 后端开发框架 |
| Spring Web | 提供 REST API 接口 |
| HTML | 前端页面结构 |
| CSS | 页面基础样式 |
| JavaScript | 前后端交互 |
| Maven | 项目构建工具 |
| Git / GitHub | 版本管理与代码托管 |

---

## 三、核心功能

### 1. CSV 批量导入

支持用户在页面文本框中输入多行 CSV 格式选课数据。

CSV 格式示例：

```text
S000001,C000001,Java程序设计,专业课
S000002,C000003,计算机网络,公共课
S000003,C000002,大学英语,公共课
S000004,C000004,人工智能导论,选修课
```

每一行表示一条选课记录，字段含义如下：

```text
学生ID,课程ID,课程名称,课程类型
```

---

### 2. 选课记录去重

去重规则：

```text
学生ID + 课程ID 完全一致时，视为重复选课记录
```

例如：

```text
S000001,C000001,Java程序设计,专业课
S000001,C000001,Java程序设计,专业课
```

这两条记录会被视为重复记录，只保留一条。

去重时只判断：

```text
studentId + courseId
```

与课程名称无关。

---

### 3. 选课记录排序

排序规则：

```text
先按学生ID升序排序
学生ID相同时，再按课程ID升序排序
```

示例排序结果：

```text
S000001,C000001,Java程序设计,专业课
S000002,C000003,计算机网络,公共课
S000003,C000002,大学英语,公共课
```

---

### 4. 选课分类

系统支持按照课程类型对选课记录进行分类展示。

支持的课程类型包括：

```text
公共课
专业课
选修课
```

如果 CSV 数据中已经手动填写课程类型，则优先使用用户填写的课程类型。

如果没有填写课程类型，系统会根据课程名称进行简单自动识别。

---

### 5. 选课检索

系统支持按照以下关键词进行检索：

1. 学生ID；
2. 课程ID；
3. 课程名称；
4. 课程类型。

如果没有找到匹配记录，页面会显示：

```text
无匹配选课记录
```

---

### 6. 页面展示

系统提供一个简单的前端页面，支持：

1. 输入 CSV 数据；
2. 点击按钮批量导入；
3. 展示处理后的选课记录；
4. 按课程类型分组展示；
5. 根据关键词搜索选课记录。

---

## 四、项目结构

```text
course-selection-system
├── docs
│   ├── analysis-design.md
│   └── sql-answer.sql
├── src
│   ├── main
│   │   ├── java
│   │   │   └── com.example.courseselectionsystem
│   │   │       ├── controller
│   │   │       │   └── EnrollmentController.java
│   │   │       ├── entity
│   │   │       │   └── EnrollRecord.java
│   │   │       ├── service
│   │   │       │   └── EnrollmentService.java
│   │   │       └── CourseSelectionSystemApplication.java
│   │   └── resources
│   │       ├── static
│   │       │   └── index.html
│   │       └── application.properties
│   └── test
├── pom.xml
├── README.md
└── .gitignore
```

---

## 五、后端接口说明

### 1. 查询全部选课记录

请求方式：

```http
GET /api/enrollments
```

功能说明：

返回系统中所有选课记录，结果已经完成去重和排序。

---

### 2. CSV 批量导入

请求方式：

```http
POST /api/enrollments/import
```

请求体示例：

```json
{
  "csvText": "S000001,C000001,Java程序设计,专业课\nS000002,C000003,计算机网络,公共课"
}
```

功能说明：

后端接收 CSV 文本后，对选课记录进行：

1. 解析；
2. 去重；
3. 排序；
4. 分类；
5. 返回处理后的结果。

---

### 3. 选课检索

请求方式：

```http
GET /api/enrollments/search?keyword=Java
```

功能说明：

支持按照学生ID、课程ID、课程名称、课程类型进行关键词检索。

---

### 4. 按课程类型分组展示

请求方式：

```http
GET /api/enrollments/grouped
```

功能说明：

按照课程类型返回分组后的选课记录。

---

## 六、运行方式

### 1. 克隆项目

```bash
git clone https://github.com/onetwooneo/course-selection-system.git
```

进入项目目录：

```bash
cd course-selection-system
```

---

### 2. 使用 IDEA 打开项目

使用 IntelliJ IDEA 打开项目根目录：

```text
course-selection-system
```

等待 Maven 自动加载依赖。

---

### 3. 启动 Spring Boot 项目

运行主启动类：

```text
CourseSelectionSystemApplication
```

启动成功后，控制台会显示 Spring Boot 启动日志。

---

### 4. 访问页面

浏览器访问：

```text
http://localhost:8080/index.html
```

进入页面后，可以输入 CSV 选课数据并点击按钮进行导入和展示。

---

## 七、SQL 编程题答案

SQL 题目答案见：

```text
docs/sql-answer.sql
```

其中包含：

1. 统计每门课程的选课人数；
2. 统计选课人数超过 50 人的专业课。

---

## 八、分析及设计文档

系统分析及设计文档见：

```text
docs/analysis-design.md
```

文档内容包括：

1. 核心数据模型设计；
2. 学生表、教师表、课程表、选课记录表设计；
3. 表间关系说明；
4. ER 图；
5. SQL 编程题答案；
6. 并发风险分析；
7. 索引设计；
8. AI 编程工具使用说明；
9. AI 生成部分与本人修改优化部分说明。

---

## 九、分层设计说明

本项目遵循基础的三层架构设计：

```text
Controller → Service → Entity
```

### 1. Controller 层

位置：

```text
src/main/java/com/example/courseselectionsystem/controller
```

主要职责：

1. 接收前端请求；
2. 调用 Service 层；
3. 返回 JSON 响应数据。

Controller 中不编写具体业务逻辑。

---

### 2. Service 层

位置：

```text
src/main/java/com/example/courseselectionsystem/service
```

主要职责：

1. CSV 数据解析；
2. 选课记录去重；
3. 选课记录排序；
4. 选课分类；
5. 选课检索；
6. 控制台格式化输出。

---

### 3. Entity 层

位置：

```text
src/main/java/com/example/courseselectionsystem/entity
```

主要职责：

定义选课记录实体类 `EnrollRecord`，包含：

1. 学生ID；
2. 课程ID；
3. 课程名称；
4. 课程类型。

---

## 十、AI 编程工具使用说明

本项目开发过程中使用的 AI 编程工具为：

```text
ChatGPT GPT-5.5 Thinking
```

AI 主要辅助完成：

1. Spring Boot 后端代码生成；
2. Controller、Service、Entity 分层结构设计；
3. 前端 HTML 页面生成；
4. CSV 导入、去重、排序、分类、检索逻辑设计；
5. SQL 题目编写；
6. 数据库模型、并发风险和索引设计文档整理。

本人在 AI 生成内容基础上进行了修改和优化，包括：

1. 适配高校选课业务场景；
2. 增加课程类型字段；
3. 明确学生ID + 课程ID的去重规则；
4. 完善前后端接口衔接；
5. 调整页面交互按钮；
6. 整理项目文档和说明文件。

---

## 十一、性能说明

本项目使用内存集合保存和处理选课记录，适合基础作业和小规模演示场景。

在 1000 条左右选课数据规模下，系统可以较快完成：

1. CSV 数据解析；
2. 选课记录去重；
3. 选课记录排序；
4. 选课记录检索。

为了提升基础处理性能，Service 层中使用 `LinkedHashMap` 进行去重，并使用集合排序完成数据整理。

如果后续扩展为真实业务系统，可以进一步接入 MySQL、Redis、数据库索引和分页查询。

---

## 十二、项目说明

本项目是一个面向课程作业和基础实训场景的简化版高校选课管理系统。

项目重点不在于实现完整教务系统，而是围绕学生选课记录处理，完成以下核心能力：

1. 掌握 Spring Boot 3.x 基础项目结构；
2. 掌握 Controller、Service、Entity 分层开发；
3. 掌握前端页面和后端接口的基础交互；
4. 掌握 CSV 数据批量导入处理；
5. 掌握基础 SQL 查询和统计；
6. 掌握简单数据库设计、并发分析和索引设计。

---

## 十三、提交说明

常用 Git 提交流程如下：

```bash
git add .
git commit -m "Update course selection system"
git push
```