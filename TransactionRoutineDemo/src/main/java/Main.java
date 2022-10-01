
import com.pismo.transactionroutine.exception.RequiredEntityMissingException;
import com.pismo.transactionroutine.model.Account;
import com.pismo.transactionroutine.model.Purchase;
import com.pismo.transactionroutine.model.Transaction;
import java.util.Calendar;

public class Main
{

    public static void main(String[] args) throws RequiredEntityMissingException
    {
        Transaction tx = new Purchase(new Account(232323232l), 4532.2, Calendar.getInstance());
        
        
        tx.getAccount();
    }
}
