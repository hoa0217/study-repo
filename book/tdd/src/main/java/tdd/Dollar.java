package tdd;

public class Dollar {
    int amount;

    public Dollar(int amount) {
        this.amount = amount;
    }

    void times(int multiplier) {
        // amount = 5 * 2;
        // amount = amount * 2;
        // amount = amount * multiplier;
        amount *= multiplier; // 중복제거완료.
    }
}
