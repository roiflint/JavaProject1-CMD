import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;
import java.security.InvalidParameterException;
import java.util.Scanner;

public class UI {

    private boolean xml;
    private boolean close;

    public UI()
    {
        this.xml = false;
        this.close = false;
    }
    public boolean getClose() {return this.close;}

    public void Menu(EngineInter eng, Scanner sc) {
        int choice;
        String s;
        System.out.println("\nMAIN\n");
        System.out.println("1. Load XML File");
        System.out.println("2. Show all stocks information");
        System.out.println("3. Show single stock information");
        System.out.println("4. Transaction");
        System.out.println("5. Show all pending and done trades");
        System.out.println("6. Exit\n");
        System.out.println("Enter command number (1/2/3/4/5/6)");
        try {
            s = sc.nextLine();
            choice = Integer.parseInt(s);
            if (choice < 0 || choice > 6) {
                System.out.println("\nPlease enter a number between 1 and 6");
                return;
            }
            switch (choice) {
                case 1:
                    LoadXMlFile(eng, sc);
                    break;
                case 2:
                    if (!this.xml)
                    {
                        System.out.println("\nYou need to load a xml file first");
                        return;
                    }
                    else
                    {
                        System.out.println("\n"+eng.GetAllStocksInfo());
                    }
                    break;
                case 3:
                    if (!this.xml)
                    {
                        System.out.println("\nYou need to load a xml file first");
                        return;
                    }
                    else
                    {
                        StockInformation(eng,sc);
                    }
                    break;
                case 4:
                    if (!this.xml) {
                        System.out.println("\nYou need to load a xml file first");
                        return;
                    }
                    Action(eng,sc);
                    break;
                case 5:
                    if (!this.xml) {
                        System.out.println("\nYou need to load a xml file first");
                        return;
                    }
                    else
                    {
                        System.out.println("\n"+eng.GetAllDeals());
                    }
                    break;
                case 6:
                    this.close = true;
            }

        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a single digit number");
        }

    }

    private void MakeTransaction(EngineInter eng, Scanner sc, String action)
    {
        System.out.println("\n"+action);
        System.out.println("Enter desired command: BUY or SELL: ");
        String command = sc.nextLine();
        if (command.compareToIgnoreCase("buy")!=0 && command.compareToIgnoreCase("sell")!=0)
        {
            System.out.println("\nPlease enter BUY or SELL only");
            return;
        }
        System.out.println("Enter stock symbol: ");
        String symbol = sc.nextLine();
        if (!eng.IsStockExist(symbol.toUpperCase()))
        {
            System.out.println("\nStock not found, try a different stock");
            return;
        }
        System.out.println("Enter amount of stocks to trade: ");
        String amount = sc.nextLine();
        try
        {
            int cost;
            int stockAmount = Integer.parseInt(amount);
            if (stockAmount < 1 )
            {
                System.out.println("\nPlease enter a positive number");
                return;
            }
            if (action.compareToIgnoreCase("LMT")==0)
            {
                System.out.println("Enter desired price: ");
                String price = sc.nextLine();
                cost = Integer.parseInt(price);
                if (cost < 0)
                {
                    System.out.println("\nPlease enter a positive number");
                    return;
                }
            }
            else
            {
                cost = eng.getStockPrice(symbol);
            }
            Transaction newTransaction = new Transaction(stockAmount, cost, symbol.toUpperCase(),action);
            if (command.compareToIgnoreCase("buy")==0)
            {
                System.out.println(eng.BUY(newTransaction));
            }
            else
            {
                System.out.println(eng.SELL(newTransaction));
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a round number");
        } catch (InvalidParameterException e) {
            System.out.println("\n"+e.getMessage());
        }
    }

    private void Action(EngineInter eng, Scanner sc)
    {
        System.out.println("\nTransaction");
        System.out.println("1. LMT");
        System.out.println("2. MKT");
        System.out.println("3. Return to main menu");
        System.out.println("4. Load XML File");
        System.out.println("5. Exit");
        System.out.println("\nPlease enter (1/2/3/4/5)");
        String s;
        int choice;
        try
        {
            s = sc.nextLine();
            choice = Integer.parseInt(s);
            if (choice < 0 || choice > 5) {
                System.out.println("\nPlease enter a number between 1 and 5");
                return;
            }
            switch (choice)
            {
                case 1:
                    MakeTransaction(eng,sc,"LMT");
                    break;
                case 2:
                    MakeTransaction(eng,sc,"MKT");
                    break;
                case 3:
                    break;
                case 4:
                    LoadXMlFile(eng, sc);
                    break;
                case 5:
                    this.close = true;
                    break;
            }
        } catch (NumberFormatException e) {
            System.out.println("\nPlease enter a round number");
        }
    }

    private void StockInformation(EngineInter eng, Scanner sc) {
        System.out.println("\nPrint Single Stock Information");
        System.out.println("1. Print Single Stock Information");
        System.out.println("2. Return to main menu");
        System.out.println("3. Load XML File");
        System.out.println("4. Exit");
        System.out.println("\nPlease insert (1/2/3/4)");
        String s;
        int choice;
        try {
            s = sc.nextLine();
            choice = Integer.parseInt(s);
            if (choice < 0 || choice > 4) {
                System.out.println("\nPlease enter a number between 1 and 4");
                return;
            }
            switch (choice) {
                case 1:
                    System.out.println("Enter stock symbol:");
                    System.out.println("\n"+eng.GetSingleStockInfo(sc.nextLine().toUpperCase()));
                    break;
                case 2:
                    return;
                case 3:
                    LoadXMlFile(eng, sc);
                    break;
                case 4:
                    this.close = false;
                    break;
            }
        } catch (InvalidParameterException e) {
            System.out.println("\n"+e.getMessage());
        }
        catch (NumberFormatException e)
        {
            System.out.println("\nPlease enter a round number");
        }
    }


    private void LoadXMlFile(EngineInter eng, Scanner sc)
    {
        System.out.println("\nLoad XML File\n");
        System.out.println("1. Load XML File");
        System.out.println("2. Return to main menu");
        System.out.println("3. Exit");
        System.out.println("\nPlease Enter (1/2/3)");
        int choice;
        String s;
        try {
            s = sc.nextLine();
            choice = Integer.parseInt(s);
            if (choice < 0 || choice > 3) {
                System.out.println("\nPlease enter a number between 1 and 3");
                return;
            }
            switch (choice)
            {
                case 1:
                    System.out.println("Enter XML path:");
                    String file = sc.nextLine();
                    if (!file.endsWith(".xml"))
                    {
                        System.out.println("\nPath must end with .xml");
                        break;
                    }
                    else
                    {
                        if(eng.LoadXML(file))
                        {
                            System.out.println("\nXML File loaded successfully");
                            this.xml = true;
                        }
                        else
                        {
                            System.out.println("\nXML file was not loaded, try a different file path");
                        }
                    }
                    break;
                case 2:
                    break;
                case 3:
                    this.close = true;
                    break;
                }
            }
            catch (JAXBException | FileNotFoundException | InvalidParameterException e) {
                System.out.println("\n"+e.getMessage());
            }
        catch (NumberFormatException e)
        {
            System.out.println("\nPlease enter a round number");
        }
    }
}
