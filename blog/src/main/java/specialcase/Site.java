package specialcase;

import lombok.Setter;

@Setter
public class Site {

    private Customer customer;

    public Customer getCustomer() {
        return (this.customer == null) ? new UnknownCustomer() : this.customer;
    }
}
