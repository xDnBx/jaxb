package com.homeproject.utils;

import com.homeproject.student.Students;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE)
public class MainRunner implements ApplicationRunner {
    final StudentRepository studentRepository;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Students students = XmlUtil.unmarshal();
        students.getStudents().forEach(studentRepository::saveStudent);
        XmlUtil.marshal(students);
    }
}