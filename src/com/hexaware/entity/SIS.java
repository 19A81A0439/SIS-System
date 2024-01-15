package com.hexaware.entity;

import java.util.ArrayList;
import java.util.List;

public class SIS {
    private List<Student> students;
    private List<Course> courses;
    private List<Enrollment> enrollments;
    private List<Teacher> teachers;
    private List<Payment> payments;

    public SIS() {
        this.students = new ArrayList<>();
        this.courses = new ArrayList<>();
        this.enrollments = new ArrayList<>();
        this.teachers = new ArrayList<>();
        this.payments = new ArrayList<>();
    }

    // Additional methods to manipulate and retrieve data in the SIS

    public List<Student> getStudents() {
        return students;
    }

    public List<Course> getCourses() {
        return courses;
    }

    public List<Enrollment> getEnrollments() {
        return enrollments;
    }

    public List<Teacher> getTeachers() {
        return teachers;
    }

    public List<Payment> getPayments() {
        return payments;
    }

    public void addStudent(Student student) {
        students.add(student);
    }

    public void addCourse(Course course) {
        courses.add(course);
    }

    public void addEnrollment(Enrollment enrollment) {
        enrollments.add(enrollment);
    }

    public void addTeacher(Teacher teacher) {
        teachers.add(teacher);
    }

    public void addPayment(Payment payment) {
        payments.add(payment);
    }

    // Additional methods for SIS functionality
}
