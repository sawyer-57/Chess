package exception;

public class DataAccessException
        extends Exception {

    public DataAccessException(){
        super("Data Access Error");
    }
}