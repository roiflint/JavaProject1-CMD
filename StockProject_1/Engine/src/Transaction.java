import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;

public class Transaction {
    private String Date;
    private int NumberOfStocks;
    private int Cost;
    private String Symbol;
    private String Action;

    public Transaction(String date, int numberofstocks, int cost, String symbol, String action) {
        this.Date = date;
        this.NumberOfStocks = numberofstocks;
        this.Cost = cost;
        this.Symbol = symbol.toUpperCase();
        this.Action = action;
    }
    public Transaction(int numberofstocks, int cost, String symbol, String action) {
        this.Date  = DateTimeFormatter.ofPattern("HH:mm:ss:SSS").format(LocalDateTime.now());
        this.NumberOfStocks = numberofstocks;
        this.Cost = cost;
        this.Symbol = symbol.toUpperCase();
        this.Action = action;
    }

    public String getDate() {
        return this.Date;
    }

    public int getNumberOfStocks() {
        return this.NumberOfStocks;
    }

    public int getCost() {
        return this.Cost;
    }

    public String getSymbol() {return this.Symbol;}

    public void setNumberOfStocks(int newNumberOfStocks){this.NumberOfStocks = newNumberOfStocks;}

    public String getAction(){return this.Action;}

    @Override
    public String toString() {
        String buffer = "Transaction Date: " + this.Date + "\n";
        buffer += "Action: " + this.Action+"\n";
        buffer += "Number of Stocks: " + this.NumberOfStocks + "\n";
        buffer += "Price per Stock: " + this.Cost + "\n";
        buffer += "Transaction Value: " + (this.getNumberOfStocks() * this.Cost) + "\n\n";
        return buffer;
    }
}

class TransactionComperator implements Comparator<Transaction>
{
    @Override
    public int compare(Transaction o1, Transaction o2) {
        if (o1.getAction().compareToIgnoreCase("sell")==0) {
            if (o1.getCost() > o2.getCost()) {
                return 1;
            } else if (o1.getCost() == o2.getCost()) {
                if (o1.getDate().compareTo(o2.getDate()) < 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        } else {
            if (o1.getCost() < o2.getCost()) {
                return 1;
            } else if (o1.getCost() == o2.getCost()) {
                int comp = o1.getDate().compareTo(o2.getDate());
                if (comp < 0) {
                    return -1;
                } else {
                    return 1;
                }
            } else {
                return -1;
            }
        }
    }
}