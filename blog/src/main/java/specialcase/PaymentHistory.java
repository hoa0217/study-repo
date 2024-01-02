package specialcase;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class PaymentHistory {

    private int weekDelinquentInLastYear;

    public int getWeekDelinquentInLastYear() {
        return weekDelinquentInLastYear;
    }
}
