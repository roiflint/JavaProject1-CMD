
import java.util.Scanner;

public class Main {

   public static void main(String[] args)
   {
      UI ui = new UI();
      EngineInter eng = new Engine();
      Scanner sc = new Scanner(System.in);
      while (!ui.getClose())
      {
         ui.Menu(eng,sc);
      }
      sc.close();
      System.out.println("Goodbye");
   }
}


