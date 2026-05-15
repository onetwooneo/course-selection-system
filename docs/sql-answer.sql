-- 高校选课管理系统 SQL 编程题答案

-- 题目1：
-- 统计每门课程的选课人数，返回课程ID、课程名称、选课人数，
-- 结果按选课人数降序排序。

SELECT
    c.course_id,
    c.course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
         LEFT JOIN enrollments e ON c.course_id = e.course_id
GROUP BY c.course_id, c.course_name
ORDER BY enroll_count DESC;


-- 题目2：
-- 统计选课人数超过50人的专业课，返回课程ID、课程名称、选课人数，
-- 结果按选课人数升序排序。

SELECT
    c.course_id,
    c.course_name,
    COUNT(e.student_id) AS enroll_count
FROM courses c
         JOIN enrollments e ON c.course_id = e.course_id
WHERE c.course_type = '专业课'
GROUP BY c.course_id, c.course_name
HAVING COUNT(e.student_id) > 50
ORDER BY enroll_count ASC;