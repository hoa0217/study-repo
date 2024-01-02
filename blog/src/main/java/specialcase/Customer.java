package specialcase;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Customer {

    private String name;
    private BillingPlans billingPlans;
    private PaymentHistory paymentHistory;

    public String getName() {
        return name;
    }

    public BillingPlans getBillingPlans() {
        return billingPlans;
    }

    public void setBillingPlans(BillingPlans billingPlans) {
        this.billingPlans = billingPlans;
    }

    public PaymentHistory getPaymentHistory() {
        return paymentHistory;
    }

    public boolean isUnKnown() {
        return false;
    }
}
