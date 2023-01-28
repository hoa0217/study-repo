package blog.shortcir;

public class ShortCirOr {
    public static void main(String[] args) {
        // 첫 번째 피연산자가 true이므로 평가가 중지되고 true가 반환된다.
        // short-circuit O
        if (true || false || false) {
            System.out.println("This output "
                    + "got printed actually, "
                    + " due to short circuit");
        }
        else {
            System.out.println("This output "
                    + "will not "
                    + "be printed");
        }

        // 마지막 조건까지 true가 발생하지 않기 때문에, 전체 표현식이 평가된다.
        // short-circuit X
        if (false || false || true) {
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
