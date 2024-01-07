package specialcase;

public class Client {

    public static void main(String[] args) {
        Site site = new Site();
        Customer aCustomer = site.getCustomer();

        // 클라이언트 1
        String customerName = aCustomer.getName();
        System.out.println("customerName = " + customerName);

        // 클라이언트 2
        BillingPlans plan = aCustomer.getBillingPlans();
        System.out.println("plan = " + plan.name());

        // 클라이언트 3
        BillingPlans newPlan = BillingPlans.BASIC;
        aCustomer.setBillingPlans(newPlan);

        // 클라이언트4
        int weeksDelinquent = aCustomer.getPaymentHistory().getWeekDelinquentInLastYear();
        System.out.println("weeksDelinquent = " + weeksDelinquent);
    }

    public static boolean isUnknown(Customer customer){
        return customer.isUnKnown();
    }
}
