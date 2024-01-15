package com.hexaware.dao;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.Connection;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;
import com.hexaware.exception.PaymentValidationException;
import com.hexaware.exception.StudentNotFoundException;
import com.hexaware.util.DBConnUtil;
import com.hexaware.util.DBPropertyUtil;

public class PaymentClassException {

	private static final String fileName="src/com/hexaware/util/db.properties"; 
    private static final String URL = DBPropertyUtil.getConnectionString(fileName);

    public Student getStudent(Payment payment) throws StudentNotFoundException, FileNotFoundException, ClassNotFoundException, IOException {
        Student student = null;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
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
                    } else {
                        throw new StudentNotFoundException("Student not found for the given payment ID.");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return student;
    }

    public double getPaymentAmount(Payment payment) throws PaymentValidationException, FileNotFoundException, ClassNotFoundException, IOException {
        double paymentAmount = 0.0;
        try (Connection connection =  DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT amount FROM payments WHERE payment_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, payment.getPaymentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        paymentAmount = resultSet.getDouble("amount");
                        validatePaymentAmount(paymentAmount);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentAmount;
    }

    public Date getPaymentDate(Payment payment) throws PaymentValidationException, FileNotFoundException, ClassNotFoundException, IOException {
        Date paymentDate = null;
        try (Connection connection = DBConnUtil.getConnection(URL)) {
            String selectQuery = "SELECT payment_date FROM payments WHERE payment_id = ?";
            try (PreparedStatement selectStatement = connection.prepareStatement(selectQuery)) {
                selectStatement.setLong(1, payment.getPaymentId());
                try (ResultSet resultSet = selectStatement.executeQuery()) {
                    if (resultSet.next()) {
                        String dateStr = resultSet.getString("payment_date");
                        if (isValidDateFormat(dateStr)) {
                            paymentDate = resultSet.getDate("payment_date");
                        } else {
                            throw new PaymentValidationException("Invalid Payment Date Format: " + dateStr);
                        }
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return paymentDate;
    }

    public static boolean isValidDateFormat(String dateStr) throws PaymentValidationException {
        if (dateStr == null || dateStr.trim().isEmpty()) {
            throw new PaymentValidationException("Empty or null Payment Date");
        }

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
        sdf.setLenient(false);

        try {
            sdf.parse(dateStr);
            return true;
        } catch (Exception e) {
            throw new PaymentValidationException("Invalid Payment Date Format: " + dateStr);
        }
    }

    private void validatePaymentAmount(double paymentAmount) throws PaymentValidationException {
        if (paymentAmount <= 0) {
            throw new PaymentValidationException("Invalid payment amount. Amount should be greater than 0.");
        }
    }

    

    public static void main(String[] args) throws FileNotFoundException, ClassNotFoundException, IOException {
        // Example usage
        PaymentClassException paymentDao = new PaymentClassException();

        // Assuming you have a Payment object with a valid paymentId
        Payment payment = new Payment(100002L);

        try {
            // Test 1: Get the student associated with the payment
            Student student = paymentDao.getStudent(payment);

            if (student != null) {
                System.out.println("Student Information:");
                System.out.println("Student ID: " + student.getStudentId());
                System.out.println("First Name: " + student.getFirstName());
                System.out.println("Last Name: " + student.getLastName());
                System.out.println("Date of Birth: " + student.getDateOfBirth());
                System.out.println("Email: " + student.getEmail());
                System.out.println("Phone Number: " + student.getPhoneNumber());

                // Test 2: Get the payment amount associated with the payment
                double validPaymentAmount = paymentDao.getPaymentAmount(payment);
                System.out.println("Valid Payment Amount: " + validPaymentAmount);

                // Test 3: Get the payment date associated with the payment
                Date validPaymentDate = paymentDao.getPaymentDate(payment);
                System.out.println("Valid Payment Date: " + validPaymentDate);
            }

            // Test 4: Get the payment amount with invalid value (0.0)
            Payment invalidAmountPayment = new Payment(100003L);
            try {
                double invalidPaymentAmount = paymentDao.getPaymentAmount(invalidAmountPayment);
                System.out.println("Invalid Payment Amount: " + invalidPaymentAmount);
            } catch (PaymentValidationException e) {
                System.out.println("Test 4: " + e.getMessage());
            }

            // Test 5: Get the payment amount with less than 0
            Payment negativeAmountPayment = new Payment(100006L);
            try {
                double negativePaymentAmount = paymentDao.getPaymentAmount(negativeAmountPayment);
                System.out.println("Negative Payment Amount: " + negativePaymentAmount);
            } catch (PaymentValidationException e) {
                System.out.println("Test 5: " + e.getMessage());
            }

            // Test 6: Get the payment amount with null value
            Payment nullAmountPayment = new Payment(100007L);
            try {
                double nullPaymentAmount = paymentDao.getPaymentAmount(nullAmountPayment);
                System.out.println("Null Payment Amount: " + nullPaymentAmount);
            } catch (PaymentValidationException e) {
                System.out.println("Test 6: " + e.getMessage());
            }

            // Test 7: Get the payment amount with empty value
            Payment emptyAmountPayment = new Payment(100008L);
            try {
                double emptyPaymentAmount = paymentDao.getPaymentAmount(emptyAmountPayment);
                System.out.println("Empty Payment Amount: " + emptyPaymentAmount);
            } catch (PaymentValidationException e) {
                System.out.println("Test 7: " + e.getMessage());
            }

            // Test 8: Get the payment date with invalid format
            Payment invalidDatePayment = new Payment(100004L);
            
            
            try {
                Date invalidPaymentDate = paymentDao.getPaymentDate(invalidDatePayment);
                
                System.out.println("Invalid Payment Date: " + invalidPaymentDate);
            } catch (PaymentValidationException e) {
                System.out.println("Test 8: " + e.getMessage());
            }

            // Test 9: Get the payment date with empty value
            Payment emptyDatePayment = new Payment(100005L);
           
            try {
                Date emptyPaymentDate = paymentDao.getPaymentDate(emptyDatePayment);
                System.out.println("Empty Payment Date: " + emptyPaymentDate);
            } catch (PaymentValidationException e) {
                System.out.println("Test 9: " + e.getMessage());
            }

        } catch (StudentNotFoundException | PaymentValidationException e) {
            System.out.println(e.getMessage());
        }
    }

}
