package blog.shortcir;

public class ShortCirAnd {
    public static void main(String[] args) {
        // 첫 번째 피연산자가 false이므로 평가가 중지되고 false가 반환된다.
        // short-circuit O
        if (false && true && true) {
            System.out.println("This output "
                    + "will not "
                    + "be printed");
        }
        else {
            System.out.println("This output "
                    + "got printed actually, "
                    + " due to short circuit");
        }

        // 마지막 조건까지 false가 발생하지 않기 때문에, 전체 표현식이 평가된다.
        // short-circuit X
        if (true && true && true) {
            System.out.println("This output "
                    + "gets print"
                    + " as there will be"
                    + " no Short circuit");
        }
        else {
            System.out.println("This output "
                    + "will not "
                    + "be printed");
        }
    }
}
