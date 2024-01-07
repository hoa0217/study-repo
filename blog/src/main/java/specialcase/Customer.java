package specialcase;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {

    private String name;
    private BillingPlans billingPlans;
    private PaymentHistory paymentHistory;

    public boolean isUnKnown() {
        return false;
    }
}
