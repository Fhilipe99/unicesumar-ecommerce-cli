package com.unicesumar.paymentMethods;

public class BoletoPayment implements PaymentMethod {
    @Override
    public void pay() {
        System.out.println("Pagamento com boleto efetuado com sucesso!");
    }
}