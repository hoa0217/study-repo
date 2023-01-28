package tdd;

public class Dollar {
    private int amount;

    public Dollar(int amount) {
        this.amount = amount;
    }

    Dollar times(int multiplier){
        return new Dollar(amount * multiplier);
    }

    @Override
    public boolean equals(Object o) {
        if(o == null) return false;
        if(!(o instanceof Dollar)) return false;
        Dollar d = (Dollar) o;
        return amount == d.amount;
    }
}
