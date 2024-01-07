package specialcase;

public class UnknownCustomer extends Customer {

    @Override
    public boolean isUnKnown() {
        return true;
    }

    @Override
    public String getName() {
        return "거주자";
    }

    @Override
    public BillingPlans getBillingPlans() {
        return BillingPlans.BASIC;
    }

    @Override
    public void setBillingPlans(BillingPlans billingPlans) {
    }

    @Override
    public PaymentHistory getPaymentHistory() {
        return new NullPaymentHistory();
    }
}
