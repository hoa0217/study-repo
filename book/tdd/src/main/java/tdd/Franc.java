package tdd;

public class Franc {
    private int amount;

    public Franc(int amount) {
        this.amount = amount;
    }

    Franc times(int multiplier) {
        // amount *= multiplier;
        // return null;
        return new Franc(amount * multiplier);
    }

    @Override
    public boolean equals(Object object){
        //return true;
        Franc dollar = (Franc) object;
        return amount == dollar.amount;
    }
}
