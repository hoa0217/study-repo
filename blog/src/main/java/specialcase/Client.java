package specialcase;

public class Client {

    public static void main(String[] args) {
        Site site = new Site();
        Customer aCustomer = site.getCustomer();

        // 클라이언트 1
        String customerName;
        if (isUnknown(aCustomer)) {
            customerName = "거주자";
        } else {
            customerName = aCustomer.getName();
        }

        // 클라이언트 2
        BillingPlans plan = isUnknown(aCustomer) ?
            BillingPlans.BASIC
            : aCustomer.getBillingPlans();

        // 클라이언트 3
        BillingPlans newPlan = BillingPlans.BASIC;
        if (isUnknown(aCustomer)) {
            aCustomer.setBillingPlans(newPlan);
        }

        // 클라이언트4
        int weeksDelinquent = isUnknown(aCustomer) ?
            0
            : aCustomer.getPaymentHistory().getWeekDelinquentInLastYear();
    }

    public static boolean isUnknown(Customer customer){
        return customer.isUnKnown();
    }
}
