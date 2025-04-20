package com.unicesumar;
import com.unicesumar.paymentMethods.PaymentMethod;

public class PaymentManager {
    private PaymentMethod paymentMethod;

    public void setPaymentMethod(PaymentMethod paymentMethod) {
        this.paymentMethod = paymentMethod;
    }

    public void pay() {
        this.paymentMethod.pay();
    }
}