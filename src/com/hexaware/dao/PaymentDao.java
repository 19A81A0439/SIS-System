package com.hexaware.dao;

import java.util.Date;

import com.hexaware.entity.Payment;
import com.hexaware.entity.Student;

public interface PaymentDao {
	public Student getStudent(Payment payment);
	 public double getPaymentAmount(Payment payment);
	 public Date getPaymentDate(Payment payment);
}
