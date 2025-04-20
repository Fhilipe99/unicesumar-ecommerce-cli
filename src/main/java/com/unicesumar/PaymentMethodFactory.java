package com.unicesumar;

import com.unicesumar.paymentMethods.BoletoPayment;
import com.unicesumar.paymentMethods.CreditCardPayment;
import com.unicesumar.paymentMethods.PaymentMethod;
import com.unicesumar.paymentMethods.PaymentType;
import com.unicesumar.paymentMethods.PixPayment;

class PaymentMethodFactory {
    public static PaymentMethod create(PaymentType type) {
        if (type == null) {
            throw new IllegalArgumentException("Tipo de pagamento não pode ser nulo.");
        }

        return switch (type) {
            case PIX -> new PixPayment();
            case CARTAO -> new CreditCardPayment();
            case BOLETO -> new BoletoPayment();
        };
    }
}