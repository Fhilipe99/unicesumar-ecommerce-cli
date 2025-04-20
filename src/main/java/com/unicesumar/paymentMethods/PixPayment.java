package com.unicesumar.paymentMethods;

import java.util.UUID;

public class PixPayment implements PaymentMethod {
    public void pay() {
        System.out.println("Pagamento com PIX efetuado com sucesso! Chave de autenticação: " + UUID.randomUUID());
    }
}