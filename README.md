# ITF_MR - MapReduce 倒排索引

基于 MapReduce 实现的倒排索引系统，大数据经典案例。

## 项目简介

倒排索引是搜索引擎核心技术，本项目使用 MapReduce 实现：
- 文本分词处理
- 词频统计
- 倒排索引构建
- 搜索查询支持

## 技术栈

- **语言**: Java
- **框架**: Hadoop MapReduce
- **数据**: 文本数据集

## 功能特性

- ✅ 分布式倒排索引构建
- ✅ 文档-单词索引映射
- ✅ 词频 TF 计算
- ✅ 支持 MapReduce 任务链

## 项目结构

```
ITF_MR/
├── src/              # 源代码
├── input/            # 输入数据
├── output/           # 输出结果
└── README.md
```

## 快速开始

### 环境要求

- Hadoop 2.7+
- Java 8+

### 运行

```bash
# 编译
javac -classpath hadoop-common.jar *.java

# 打包
jar cf itf_mr.jar *.class

# 运行
hadoop jar itf_mr.jar InvertedIndex input output
```

## 倒排索引说明

```
单词 -> [文档1:位置, 文档2:位置, ...]

示例:
hello -> [doc1:1,3, doc2:5]
world -> [doc1:2, doc3:1]
```

## License

MIT License
