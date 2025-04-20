package com.unicesumar.entities;

import com.unicesumar.paymentMethods.PaymentMethod;
import com.unicesumar.paymentMethods.PaymentType;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Sale extends Entity {
    private User client;
    private List<Product> products;
    private double totalValue;
    private PaymentType paymentType;

    public Sale() {}

    public Sale(User client, List<Product> products, double totalValue, PaymentType paymentType) {
        this.client = client;
        this.products = products;
        this.totalValue = totalValue;
        this.paymentType = paymentType;
    }

    public Sale(UUID uuid, User client, List<Product> products, double totalValue, PaymentType paymentType) {
        super(uuid);
        this.client = client;
        this.products = products;
        this.totalValue = totalValue;
        this.paymentType = paymentType;
    }

    public User getClient() {
        return client;
    }

    public void setClient(User client) {
        this.client = client;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }

    public double getTotalValue() {
        return totalValue;
    }

    public void setTotalValue(double totalValue) {
        this.totalValue = totalValue;
    }

    public PaymentType getPaymentType() {
        return paymentType;
    }

    public void setPaymentType(PaymentType paymentType) {
        this.paymentType = paymentType;
    }

    @Override
    public String toString() {
        return "Sale{" +
                "client=" + client +
                ", products=" + products +
                ", totalValue=" + totalValue +
                ", paymentType=" + paymentType +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Sale sale = (Sale) o;
        return Double.compare(totalValue, sale.totalValue) == 0 && Objects.equals(client, sale.client) && Objects.equals(products, sale.products) && paymentType == sale.paymentType;
    }

    @Override
    public int hashCode() {
        return Objects.hash(client, products, totalValue, paymentType);
    }
}
