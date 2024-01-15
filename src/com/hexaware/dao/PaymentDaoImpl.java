package com.hexaware.dao;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Date;

import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;

public class PaymentDaoImpl implements PaymentDao {

    private static final String DB_URL = "jdbc:mysql://localhost:3306/sisdb";
    private static final String DB_USER = "root";
    private static final String DB_PASSWORD = "Subbu@999";

    @Override
    public Student getStudent(Payment payment) {
        Student student = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT * FROM students s JOIN payments p ON s.student_id = p.student_id WHERE p.payment_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, payment.getPaymentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        student = new Student(
                                resultSet.getLong("student_id"),
                                resultSet.getString("first_name"),
                                resultSet.getString("last_name"),
                                resultSet.getDate("date_of_birth"),
                                resultSet.getString("email"),
                                resultSet.getLong("phone_number")
                        );
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }
    @Override
    public double getPaymentAmount(Payment payment) {
        double paymentAmount = 0.0;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT amount FROM payments WHERE payment_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, payment.getPaymentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        paymentAmount = resultSet.getDouble("amount");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentAmount;
    }
    @Override
    public Date getPaymentDate(Payment payment) {
        Date paymentDate = null;
        try (Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASSWORD)) {
            String selectQuery = "SELECT payment_date FROM payments WHERE payment_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, payment.getPaymentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        paymentDate = resultSet.getDate("payment_date");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentDate;
    }

    public static void main(String[] args) {
        // Example usage
        PaymentDao paymentDao = new PaymentDaoImpl();

        // Assuming you have a Payment object with a valid paymentId
        Payment payment = new Payment(100002L);

        // Get the student associated with the payment
        Student student = paymentDao.getStudent(payment);

        if (student != null) {
            System.out.println("Student Information:");
            System.out.println("Student ID: " + student.getStudentId());
            System.out.println("First Name: " + student.getFirstName());
            System.out.println("Last Name: " + student.getLastName());
            System.out.println("Date of Birth: " + student.getDateOfBirth());
            System.out.println("Email: " + student.getEmail());
            System.out.println("Phone Number: " + student.getPhoneNumber());
            // Get the payment amount associated with the payment
            double paymentAmount = paymentDao.getPaymentAmount(payment);

            System.out.println("Payment Amount: " + paymentAmount);
         // Get the payment date associated with the payment
            Date paymentDate = paymentDao.getPaymentDate(payment);

            System.out.println("Payment Date: " + paymentDate);
        }
        
    		else {
            System.out.println("Student not found for the given payment ID.");
        }
       
        
    }
}
