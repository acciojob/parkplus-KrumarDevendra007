package com.driver.services.impl;

import com.driver.entity.Payment;
import com.driver.entity.Reservation;
import com.driver.model.PaymentMode;
import com.driver.repository.PaymentRepository;
import com.driver.repository.ReservationRepository;
import com.driver.services.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PaymentServiceImpl implements PaymentService {
    @Autowired
    ReservationRepository reservationRepository2;
    @Autowired
    PaymentRepository paymentRepository2;

    @Override
    public Payment pay(Integer reservationId, int amountSent, String mode) throws Exception {

        Reservation reservation = reservationRepository2.findById(reservationId).get();
        Payment payment = new Payment();
        int bill = reservation.getNumberOfHour() * reservation.getSpot().getPricePerHour();
        if(amountSent < bill){
            throw new Exception("Insufficient Amount");
        }
        if(!mode.toUpperCase().equals("CASE") && !mode.toUpperCase().equals("CARD") && !mode.toUpperCase().equals("UPI")){
            throw new Exception("Payment mode not detected");
        }

        PaymentMode paymentMode = PaymentMode.valueOf(mode.toUpperCase());
        payment.setPaymentMode(paymentMode);
        payment.setPaymentCompleted(true);
        payment.setReservation(reservation);
        reservation.setPayment(payment);

        reservationRepository2.save(reservation);

        return payment;
    }
}
