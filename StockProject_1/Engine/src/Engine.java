import generated.*;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.security.InvalidParameterException;
import java.util.*;


public class Engine implements EngineInter{
    private final StockDB DB;
    private final Map<String,Stock> stocks;

    public Engine()
    {
        this.DB = new StockDB();
        this.stocks = new HashMap<>();
    }

    public String GetAllStocksInfo()
    {
        String buffer = "";
        for (Stock s: this.stocks.values())
        {
            buffer+=s.toString()+"\n";
        }
        return buffer;
    }

    public int getStockPrice(String symbol) {
        if (!IsStockExist(symbol.toUpperCase()))
        {
            throw new InvalidParameterException("Stock does not exist in the system, please enter a different stock");
        }
        return stocks.get(symbol.toUpperCase()).getPrice();
    }

    public String GetSingleStockInfo(String StockName) {
            String buffer = "";
            Stock s = this.stocks.get(StockName.toUpperCase());
            if (s == null)
                throw new InvalidParameterException("The stock name is not in the system, please enter a different stock");
        buffer+=s.toString()+"\n";
        buffer += DB.GetPreviousTransactions(StockName);
        return buffer;
    }

    public boolean LoadXML(String FileName) throws JAXBException, FileNotFoundException {
        try {
            InputStream inputStream = new FileInputStream((new File(FileName)));
            RseStocks stocks = deserializeFrom(inputStream);
            Map<String,Stock> tmp = new HashMap<>(this.stocks);
            this.stocks.clear();
            for (RseStock o:stocks.getRseStock())
            {
                Stock s = new Stock(o);
                for (Stock stock:this.stocks.values())
                {
                    if (stock.equals(s))
                    {
                        this.stocks.clear();
                        this.stocks.putAll(tmp);
                        throw new InvalidParameterException("Duplicate Stocks in file, load a new XML");
                    }
                }
                this.stocks.put(s.getSymbol().toUpperCase(),s);
            }
            this.DB.ClearDB();
            return true;
        } catch (JAXBException | FileNotFoundException | InvalidParameterException e) {
            throw e;
        }
    }

    private static RseStocks deserializeFrom(InputStream in) throws JAXBException
    {
        JAXBContext jc = JAXBContext.newInstance("generated");
        Unmarshaller u = jc.createUnmarshaller();
        return ((RizpaStockExchangeDescriptor) u.unmarshal(in)).getRseStocks();
    }

    public String GetAllDeals()
    {
        String buffer = "";
        for (Stock s:this.stocks.values())
        {
            buffer += "Stock Symbol - ";
            buffer += (s.getSymbol() + ":" + "\n");
            buffer += ("Buy Pending: \n");
            buffer += DB.GetBuyTransactions(s.getSymbol().toUpperCase());
            buffer += ("\nSell Pending: \n");
            buffer += DB.GetSellTransactions(s.getSymbol().toUpperCase());
            buffer += ("\nPrevious Transactions: \n");
            buffer += DB.GetPreviousTransactions(s.getSymbol().toUpperCase()) + "\n";
        }
        return buffer;
    }

    public String BUY(Transaction transaction)  {
        String buffer = "\n";
        List<Transaction> remove = new ArrayList<>();
        List<Transaction> sell = DB.getSell(transaction.getSymbol().toUpperCase());
        if (!IsStockExist(transaction.getSymbol().toUpperCase()))
            throw new InvalidParameterException("The stock name is not in the system, please enter a different stock");
        if (transaction.getCost()<1)
            throw new InvalidParameterException("Enter a positive price only");
        if (transaction.getNumberOfStocks()<1)
            throw new InvalidParameterException("Number of stocks must be positive");
        if (sell==null)
        {
            DB.insertBuyTransaction(transaction);
            DB.getBuy(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
            return "\nTransaction added to the pending buy actions";
        }
        for (Object o:sell)
        {
            Transaction t = (Transaction) o;
            if (t.getCost() <= transaction.getCost())
            {
                if (t.getNumberOfStocks() >= transaction.getNumberOfStocks())
                {
                    t.setNumberOfStocks(t.getNumberOfStocks()- transaction.getNumberOfStocks());
                    Transaction newTrans = new Transaction(transaction.getNumberOfStocks(),t.getCost(),transaction.getSymbol(),transaction.getAction());
                    buffer += newTrans.toString();
                    this.DB.insertDoneTransaction(newTrans);
                    this.stocks.get(t.getSymbol().toUpperCase()).setPrice(t.getCost());
                    this.stocks.get(t.getSymbol().toUpperCase()).setNumberOfTransactions();
                    this.stocks.get(t.getSymbol().toUpperCase()).setCycle(transaction.getNumberOfStocks()*t.getCost());
                    if (t.getNumberOfStocks() == 0)
                    {
                        sell.remove(o);
                    }
                    this.DB.getSell(transaction.getSymbol().toUpperCase()).removeAll(remove);
                    remove.clear();
                    return buffer;
                }
                else
                {
                    Transaction newTrans = new Transaction(t.getNumberOfStocks(),t.getCost(), transaction.getSymbol(), transaction.getAction());
                    buffer += newTrans.toString();
                    this.DB.insertDoneTransaction(newTrans);
                    remove.add(t);
                    transaction.setNumberOfStocks(transaction.getNumberOfStocks()-t.getNumberOfStocks());
                    this.stocks.get(t.getSymbol().toUpperCase()).setPrice(t.getCost());
                    this.stocks.get(t.getSymbol().toUpperCase()).setNumberOfTransactions();
                    this.stocks.get(t.getSymbol().toUpperCase()).setCycle(t.getNumberOfStocks()*t.getCost());
                    if (transaction.getNumberOfStocks() == 0)
                    {
                        this.DB.getSell(transaction.getSymbol().toUpperCase()).removeAll(remove);
                        remove.clear();
                        return buffer;
                    }
                }
            }
        }
        this.DB.getSell(transaction.getSymbol().toUpperCase()).removeAll(remove);
        remove.clear();
        if (transaction.getNumberOfStocks() != 0)
        {
            DB.insertBuyTransaction(transaction);
            DB.getBuy(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
            buffer += transaction.getNumberOfStocks() + " Stocks were not bought and added to the pending buy actions";
            return buffer;
        }
        return buffer;
    }

    public String SELL(Transaction transaction) {
        String buffer = "\n";
        List<Transaction> remove = new ArrayList<>();
        List<Transaction> buy = this.DB.getBuy(transaction.getSymbol().toUpperCase());
        if (!IsStockExist(transaction.getSymbol().toUpperCase()))
            throw new InvalidParameterException("The stock name is not in the system, please enter a different stock");
        if (transaction.getCost()<1)
            throw new InvalidParameterException("Enter a positive price only");
        if (transaction.getNumberOfStocks()<1)
            throw new InvalidParameterException("Number of stocks must be positive");
        if (buy==null)
        {
            this.DB.insertSellTransaction(transaction);
            DB.getSell(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
            return "\nTransaction added to the pending sell actions";
        }
        for (Object o:buy)
        {
            Transaction t = (Transaction) o;
            if (t.getCost() >= transaction.getCost())
            {
                if (t.getNumberOfStocks() >= transaction.getNumberOfStocks())
                {
                    t.setNumberOfStocks(t.getNumberOfStocks()- transaction.getNumberOfStocks());
                    Transaction newTrans = new Transaction(transaction.getNumberOfStocks(),transaction.getCost(),transaction.getSymbol(),transaction.getAction());
                    buffer += newTrans.toString();
                    this.DB.insertDoneTransaction(newTrans);
                    this.stocks.get(t.getSymbol().toUpperCase()).setPrice(t.getCost());
                    this.stocks.get(t.getSymbol().toUpperCase()).setNumberOfTransactions();
                    this.stocks.get(t.getSymbol().toUpperCase()).setCycle(transaction.getNumberOfStocks()*t.getCost());
                    if (t.getNumberOfStocks() == 0)
                    {
                        buy.remove(o);
                    }
                    this.DB.getBuy(transaction.getSymbol().toUpperCase()).removeAll(remove);
                    remove.clear();
                    return buffer;
                }
                else
                {
                    Transaction newTrans = new Transaction(t.getNumberOfStocks(),transaction.getCost(), transaction.getSymbol(), transaction.getAction());
                    buffer += newTrans.toString();
                    this.DB.insertDoneTransaction(newTrans);
                    remove.add(t);
                    transaction.setNumberOfStocks(transaction.getNumberOfStocks()-t.getNumberOfStocks());
                    this.stocks.get(t.getSymbol().toUpperCase()).setPrice(t.getCost());
                    this.stocks.get(t.getSymbol().toUpperCase()).setNumberOfTransactions();
                    this.stocks.get(t.getSymbol().toUpperCase()).setCycle(t.getNumberOfStocks()*t.getCost());
                    if (transaction.getNumberOfStocks() == 0)
                    {
                        this.DB.getBuy(transaction.getSymbol().toUpperCase()).removeAll(remove);
                        remove.clear();
                        return buffer;
                    }
                }
            }
        }
        this.DB.getBuy(transaction.getSymbol().toUpperCase()).removeAll(remove);
        remove.clear();
        if (transaction.getNumberOfStocks() != 0)
        {
            DB.insertSellTransaction(transaction);
            DB.getSell(transaction.getSymbol().toUpperCase()).sort(new TransactionComperator());
            buffer += transaction.getNumberOfStocks() + " Stocks were not sold and added to pending sell actions";
            return buffer;
        }
        return buffer;
    }

    public boolean IsStockExist(String symbol)
    {
        return this.stocks.containsKey(symbol);
    }
}



