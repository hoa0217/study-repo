package specialcase;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
public class Site {

    private Customer customer;

    public Customer getCustomer() {
        return (this.customer == null) ? new UnknownCustomer() : this.customer;
    }
}
