package com.homeproject.utils;

import com.homeproject.student.Skill;
import com.homeproject.student.Student;
import com.homeproject.student.Students;
import org.springframework.stereotype.Component;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

@Component
public class XmlUtil {
    public static void marshal(Students students) throws JAXBException {
        for (Student student : students.getStudents()) {
            Skill skill = Skill.builder().hard(true).soft(null).name("SpringFramework").build();
            student.getSkills().add(skill);
        }

        JAXBContext context = JAXBContext.newInstance(Students.class);
        Marshaller mar = context.createMarshaller();
        mar.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        mar.marshal(students, new File("./students_updated.xml"));
    }

    public static Students unmarshal() throws JAXBException, IOException {
        JAXBContext context = JAXBContext.newInstance(Students.class);
        return (Students) context.createUnmarshaller()
                .unmarshal(new FileReader("./students.xml"));
    }
}