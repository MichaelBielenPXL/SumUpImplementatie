package be.pxl.sumupimpementiebackend.api.Requests;

public class CreateCheckoutRequest {
    private String amount;    // e.g. "10.00"
    // + getters & setters
    public String getAmount() { return amount; }
    public void setAmount(String amount) { this.amount = amount; }
}