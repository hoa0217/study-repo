package blog.innerclass;

public class LocalInner {
    private int data = 30;//instance variable

    void outerMsg(int i){
        System.out.println(i);
    }

    void display() {
        class Local {
            public void innerMsg() {
                outerMsg(data);
            }
        }
        Local l = new Local();
        l.innerMsg();
    }

    public static void main(String args[]) {
        LocalInner obj = new LocalInner();
        obj.display();
    }
}
