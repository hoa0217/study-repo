package specialcase;

public class NullPaymentHistory extends PaymentHistory{

    @Override
    public int getWeekDelinquentInLastYear() {
        return 0;
    }
}
