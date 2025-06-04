package com.homeproject.utils;

import com.homeproject.exception.InternalServerException;
import com.homeproject.student.Skill;
import com.homeproject.student.Student;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

import java.sql.PreparedStatement;
import java.sql.Statement;

@Slf4j
@Repository
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class StudentRepository {
    static final String INSERT_QUERY = "INSERT INTO students (first_name, second_name) VALUES (?, ?)";
    static final String GET_HARD_SKILL_QUERY = "SELECT skill_id FROM skills WHERE skill_name = ? AND hard_skill = ?";
    static final String GET_SOFT_SKILL_QUERY = "SELECT skill_id FROM skills WHERE skill_name = ? AND soft_skill = ?";
    static final String INSERT_HARD_SKILL_QUERY = "INSERT INTO skills (skill_name, hard_skill) VALUES (?, ?)";
    static final String INSERT_SOFT_SKILL_QUERY = "INSERT INTO skills (skill_name, soft_skill) VALUES (?, ?)";
    static final String INSERT_STUDENT_SKILL_QUERY = "INSERT INTO student_skills (student_id, skill_id) VALUES (?, ?)";

    final JdbcTemplate jdbcTemplate;

    public void saveStudent(Student student) {
        KeyHolder keyHolder = new GeneratedKeyHolder();
        try {
            jdbcTemplate.update(connection -> {
                PreparedStatement ps = connection.prepareStatement(INSERT_QUERY, Statement.RETURN_GENERATED_KEYS);
                ps.setString(1, student.getFirstName());
                ps.setString(2, student.getSecondName());
                return ps;
            }, keyHolder);
            Long studentId = keyHolder.getKey().longValue();

            for (Skill skill : student.getSkills()) {
                Long skillId;

                if (skill.getHard() == null) {
                    skillId = search(GET_SOFT_SKILL_QUERY, skill, skill.getSoft());

                    if (skillId == null) {
                        skillId = update(INSERT_SOFT_SKILL_QUERY, skill, skill.getSoft());
                    }
                } else {
                    skillId = search(GET_HARD_SKILL_QUERY, skill, skill.getHard());

                    if (skillId == null) {
                        skillId = update(INSERT_HARD_SKILL_QUERY, skill, skill.getHard());
                    }
                }

                jdbcTemplate.update(INSERT_STUDENT_SKILL_QUERY, studentId, skillId);
            }
        } catch (Exception e) {
            log.error("Ошибка сохранения студента", e);
            throw new InternalServerException("Произошла ошибка при сохранении студента");
        }
    }

    private Long update(String sql, Skill skill, Boolean skillType) {
        KeyHolder skillKeyHolder = new GeneratedKeyHolder();

        jdbcTemplate.update(connection -> {
            PreparedStatement ps = connection.prepareStatement(sql,
                    Statement.RETURN_GENERATED_KEYS);
            ps.setString(1, skill.getName());
            ps.setBoolean(2, skillType);
            return ps;
        }, skillKeyHolder);

        return skillKeyHolder.getKey().longValue();
    }

    private Long search(String sql, Skill skill, Boolean skillType) {
        Long skillId;
        try {
            skillId = jdbcTemplate.queryForObject(sql, Long.class, skill.getName(),
                    skillType);
        } catch (EmptyResultDataAccessException e) {
            skillId = null;
        }
        return skillId;
    }
}