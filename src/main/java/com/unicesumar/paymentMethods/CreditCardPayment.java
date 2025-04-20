package com.unicesumar.paymentMethods;

public class CreditCardPayment implements PaymentMethod {
    @Override
    public void pay() {
        System.out.println("Pagamento com cartão de crédito efetuado com sucesso!");
    }
}