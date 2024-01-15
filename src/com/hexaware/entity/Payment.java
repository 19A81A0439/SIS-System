package com.hexaware.entity;

import java.util.Date;

public class Payment {
	private long paymentId;
    private Student student;
    private double amount;
    private Date paymentDate;
    

    // Constructor
    public Payment(Student student, double amount, Date paymentDate) {
        this.student = student;
        this.amount = amount;
        this.paymentDate = paymentDate;
    }

	public Payment(double amount2, Date paymentDate2) {
		// TODO Auto-generated constructor stub
		this.amount=amount2;
		this.paymentDate=paymentDate2;
		
	}

	public Payment(long l) {
		// TODO Auto-generated constructor stub
		this.paymentId=l;
	}

	public Payment(long paymentId2, double amount2, Date paymentDate2) {
		// TODO Auto-generated constructor stub
		this.paymentId=paymentId2;
		this.amount=amount2;
		this.paymentDate=paymentDate2;
	}

	public long getPaymentId() {
		return paymentId;
	}

	public void setPaymentId(long payment_Id) {
		this.paymentId = payment_Id;
	}

	public Student getStudent() {
		return student;
	}

	public void setStudent(Student student) {
		this.student = student;
	}

	public double getAmount() {
		return amount;
	}

	public void setAmount(double amount) {
		this.amount = amount;
	}

	public Date getPaymentDate() {
		return paymentDate;
	}

	public void setPaymentDate(Date paymentDate) {
		this.paymentDate = paymentDate;
	}

    
}