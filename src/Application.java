import main.Args;
import main.ArgsException;

public class Application {

    public static void main(String[] args) throws ArgsException {
        var arg = new Args("l,p#,d*", new String[]{"-lpd", "true", "8080", "teste"});

        System.out.println(arg.getBoolean('l'));
        System.out.println(arg.getInt('p'));
        System.out.println(arg.getString('d'));
    }
}