import java.util.Scanner;

public class Sum {


    static int add(int a, int b) {
        return a+b;
    }


    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);
        int a;
        a = in.nextInt();
        int b;
        b = in.nextInt();
        int sum;
        sum = add(a, b);
        System.out.println(sum);
    }
}
