package com.example.courseselectionsystem.controller;

import com.example.courseselectionsystem.entity.EnrollRecord;
import com.example.courseselectionsystem.service.EnrollmentService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 选课记录控制层
 *
 * Controller 只负责：
 * 1. 接收请求
 * 2. 调用 Service
 * 3. 返回响应
 *
 * 不写业务逻辑。
 */
@RestController
@RequestMapping("/api/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;

    public EnrollmentController(EnrollmentService enrollmentService) {
        this.enrollmentService = enrollmentService;
    }

    /**
     * 查询全部选课记录。
     */
    @GetMapping
    public List<EnrollRecord> findAll() {
        return enrollmentService.findAll();
    }

    /**
     * CSV 批量导入。
     */
    @PostMapping("/import")
    public Map<String, Object> importCsv(@RequestBody CsvImportRequest request) {
        List<EnrollRecord> records = enrollmentService.importCsv(request.getCsvText());

        Map<String, Object> result = new LinkedHashMap<>();
        result.put("message", "导入成功，共返回 " + records.size() + " 条去重排序后的选课记录");
        result.put("data", records);

        return result;
    }

    /**
     * 选课检索。
     */
    @GetMapping("/search")
    public Map<String, Object> search(@RequestParam String keyword) {
        List<EnrollRecord> records = enrollmentService.search(keyword);

        Map<String, Object> result = new LinkedHashMap<>();

        if (records.isEmpty()) {
            result.put("message", "无匹配选课记录");
        } else {
            result.put("message", "查询成功，共找到 " + records.size() + " 条记录");
        }

        result.put("data", records);

        return result;
    }

    /**
     * 按课程类型分组展示。
     */
    @GetMapping("/grouped")
    public Map<String, List<EnrollRecord>> groupByCourseType() {
        return enrollmentService.groupByCourseType();
    }

    /**
     * CSV 导入请求体。
     */
    public static class CsvImportRequest {
        private String csvText;

        public String getCsvText() {
            return csvText;
        }

        public void setCsvText(String csvText) {
            this.csvText = csvText;
        }
    }
}