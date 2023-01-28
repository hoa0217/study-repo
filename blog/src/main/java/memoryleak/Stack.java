package blog.memoryleak;

public class Stack {
    private int maxSize;
    private int[] stackArray;
    private int pointer;

    public Stack(int s) {
        maxSize = s;
        stackArray = new int[maxSize];
        pointer = -1;
    }

    public void push(int j) {
        stackArray[++pointer] = j;
    }

    public int pop() {
        int size = pointer--;
        int element= stackArray[size];
        stackArray[size] = 0;
        return element;
    }

    public int peek() {
        return stackArray[pointer];
    }

    public boolean isEmpty() {
        return (pointer == -1);
    }

    public boolean isFull() {
        return (pointer == maxSize - 1);
    }

    public static void main(String[] args) {
        Stack stack = new Stack(1000);
        for (int i = 0; i < 1000; i++) {
            stack.push(i);
        }
        for (int i = 0; i < 1000; i++) {
            int element = stack.pop();
            System.out.println("Poped element is " + element);
        }
    }
}
