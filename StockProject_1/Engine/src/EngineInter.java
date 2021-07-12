import javax.xml.bind.JAXBException;
import java.io.FileNotFoundException;

public interface EngineInter {
    //
    //    //Return a string containing information n the fields of every stock in the system ready to be printed
    public String GetAllStocksInfo();

    //Receives a stock symbol, returns a string containing information on the stock fields and every
    //transaction made on that stock ready to be printed
    public String GetSingleStockInfo(String StockName);

    //Receives a path for a xml file, if the file is applicable the file is loaded to the system and
    //override any information stored before the file was loaded
    public boolean LoadXML(String FileName) throws JAXBException, FileNotFoundException;

    //Return a string containing all buy pending actions, sell pending actions and
    //every previous transaction made ready to be printed
    public String GetAllDeals();

    //Receives an object from class transaction, making any transaction that can be made,
    //updating the buy, sell and previous transactions lists
    public String BUY(Transaction transaction);

    //Receives an object from class transaction, making any transaction that can be made,
    //updating the buy, sell and previous transactions lists
    public String SELL(Transaction transaction);

    //Receives a stock symbol and checking if the stock exists in the system
    public boolean IsStockExist(String symbol);

    //Receives a stock symbol and return the stock current price
    public int getStockPrice(String symbol);


}
