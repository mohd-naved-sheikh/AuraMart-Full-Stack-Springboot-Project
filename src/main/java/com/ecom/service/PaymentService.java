package com.ecom.service;

public interface PaymentService {

    String createTransaction(double amountInRupees) throws Exception;
}