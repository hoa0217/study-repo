package tdd;

public class Dollar {
    int amount;

    public Dollar(int amount) {
        this.amount = amount;
    }

    Dollar times(int multiplier) {
        // amount *= multiplier;
        // return null;
        return new Dollar(amount * multiplier);
    }

    @Override
    public boolean equals(Object object){
        //return true;
        Dollar dollar = (Dollar) object;
        return amount == dollar.amount;
    }
}
