package com.example.courseselectionsystem.service;

import com.example.courseselectionsystem.entity.EnrollRecord;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 选课记录业务处理层
 *
 * 负责：
 * 1. CSV 批量导入
 * 2. 去重
 * 3. 排序
 * 4. 课程分类
 * 5. 选课检索
 *
 * 注意：
 * 业务逻辑全部写在 Service 中，Controller 只负责接收请求和返回结果。
 */
@Service
public class EnrollmentService {

    /**
     * 使用内存集合模拟数据库存储。
     * 作业要求是基础处理工具，这里不强制连接数据库。
     */
    private final List<EnrollRecord> recordStore = new ArrayList<>();

    /**
     * 项目启动时初始化一些样例数据。
     */
    @PostConstruct
    public void initSampleData() {
        List<EnrollRecord> sampleRecords = List.of(
                new EnrollRecord("S000002", "C000003", "计算机网络", "公共课"),
                new EnrollRecord("S000001", "C000001", "Java程序设计", "专业课"),
                new EnrollRecord("S000003", "C000002", "大学英语", "公共课"),
                new EnrollRecord("S000001", "C000001", "Java程序设计", "专业课"),
                new EnrollRecord("S000004", "C000004", "人工智能导论", "选修课")
        );

        recordStore.addAll(deduplicateAndSort(sampleRecords));
    }

    /**
     * 批量导入 CSV 文本。
     *
     * CSV 格式：
     * S000001,C000001,Java程序设计,专业课
     * S000002,C000003,计算机网络,公共课
     */
    public synchronized List<EnrollRecord> importCsv(String csvText) {
        List<EnrollRecord> importedRecords = parseCsv(csvText);

        recordStore.addAll(importedRecords);

        List<EnrollRecord> processedRecords = deduplicateAndSort(recordStore);

        recordStore.clear();
        recordStore.addAll(processedRecords);

        printRecords(processedRecords);

        return processedRecords;
    }

    /**
     * 获取全部选课记录。
     */
    public synchronized List<EnrollRecord> findAll() {
        return new ArrayList<>(deduplicateAndSort(recordStore));
    }

    /**
     * 按学生ID、课程ID、课程名称、课程类型检索。
     */
    public synchronized List<EnrollRecord> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return findAll();
        }

        String lowerKeyword = keyword.trim().toLowerCase();

        List<EnrollRecord> result = recordStore.stream()
                .filter(record ->
                        containsIgnoreCase(record.getStudentId(), lowerKeyword)
                                || containsIgnoreCase(record.getCourseId(), lowerKeyword)
                                || containsIgnoreCase(record.getCourseName(), lowerKeyword)
                                || containsIgnoreCase(record.getCourseType(), lowerKeyword)
                )
                .sorted(Comparator
                        .comparing(EnrollRecord::getStudentId)
                        .thenComparing(EnrollRecord::getCourseId))
                .toList();

        if (result.isEmpty()) {
            System.out.println("无匹配选课记录");
        }

        return result;
    }

    /**
     * 按课程类型分组。
     */
    public synchronized Map<String, List<EnrollRecord>> groupByCourseType() {
        Map<String, List<EnrollRecord>> groupedMap = new LinkedHashMap<>();
        groupedMap.put("公共课", new ArrayList<>());
        groupedMap.put("专业课", new ArrayList<>());
        groupedMap.put("选修课", new ArrayList<>());

        for (EnrollRecord record : findAll()) {
            String courseType = normalizeCourseType(record.getCourseType(), record.getCourseName());
            groupedMap.computeIfAbsent(courseType, key -> new ArrayList<>()).add(record);
        }

        return groupedMap;
    }

    /**
     * CSV 解析。
     */
    private List<EnrollRecord> parseCsv(String csvText) {
        List<EnrollRecord> records = new ArrayList<>();

        if (csvText == null || csvText.trim().isEmpty()) {
            return records;
        }

        String[] lines = csvText.split("\\R");

        for (String line : lines) {
            if (line == null || line.trim().isEmpty()) {
                continue;
            }

            String[] parts = line.split(",", -1);

            if (parts.length < 3) {
                continue;
            }

            String studentId = parts[0].trim();
            String courseId = parts[1].trim();
            String courseName = parts[2].trim();
            String courseType = parts.length >= 4 ? parts[3].trim() : "";

            courseType = normalizeCourseType(courseType, courseName);

            records.add(new EnrollRecord(studentId, courseId, courseName, courseType));
        }

        return records;
    }

    /**
     * 去重 + 排序。
     *
     * 去重规则：
     * 学生ID + 课程ID 完全一致，视为重复。
     *
     * 排序规则：
     * 先按学生ID升序，再按课程ID升序。
     */
    private List<EnrollRecord> deduplicateAndSort(List<EnrollRecord> records) {
        Map<String, EnrollRecord> uniqueMap = new LinkedHashMap<>();

        for (EnrollRecord record : records) {
            String key = buildUniqueKey(record.getStudentId(), record.getCourseId());

            record.setCourseType(normalizeCourseType(record.getCourseType(), record.getCourseName()));

            uniqueMap.putIfAbsent(key, record);
        }

        return uniqueMap.values()
                .stream()
                .sorted(Comparator
                        .comparing(EnrollRecord::getStudentId)
                        .thenComparing(EnrollRecord::getCourseId))
                .toList();
    }

    /**
     * 构造去重 key。
     */
    private String buildUniqueKey(String studentId, String courseId) {
        return studentId + "#" + courseId;
    }

    /**
     * 课程类型规范化。
     *
     * 如果用户手动传入了合法课程类型，则直接使用。
     * 如果没有传入，则根据课程名称简单自动识别。
     */
    private String normalizeCourseType(String courseType, String courseName) {
        if ("公共课".equals(courseType) || "专业课".equals(courseType) || "选修课".equals(courseType)) {
            return courseType;
        }

        if (courseName == null) {
            return "选修课";
        }

        if (courseName.contains("Java")
                || courseName.contains("数据结构")
                || courseName.contains("数据库")
                || courseName.contains("操作系统")
                || courseName.contains("人工智能")) {
            return "专业课";
        }

        if (courseName.contains("英语")
                || courseName.contains("体育")
                || courseName.contains("数学")
                || courseName.contains("思政")
                || courseName.contains("计算机网络")) {
            return "公共课";
        }

        return "选修课";
    }

    private boolean containsIgnoreCase(String source, String keyword) {
        return source != null && source.toLowerCase().contains(keyword);
    }

    /**
     * 按题目要求逐行打印格式化信息。
     */
    private void printRecords(List<EnrollRecord> records) {
        for (EnrollRecord record : records) {
            System.out.println(record);
        }
    }
}