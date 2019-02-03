package fats.com.pocketmonitor;



public class Constant {
//    final private String base = "http://pocketmonitor.pe.hu/pocketmonitor/";
    final private String base = "http://192.168.0.158:8888/pocketmonitor/";
    final private String registerUrl = "register.php";
    final private String loginUrl = "login.php";
    final private String APIkey = "a199d4a58f507e9f22f5494ad3964bc1ec723d52";
    final private String getTransactionsUrl = "gettransactions.php";
    final private String addTransactionUrl = "addtransaction.php";

    public String getRegisterUrl(){
        return base+registerUrl;
    }

    public String getLoginUrl() {
        return base+loginUrl;
    }

    public String getAPIkey() {
        return APIkey;
    }

    public String getGetTransactionsUrl() {
        return base+getTransactionsUrl;
    }

    public String getAddTransactionUrl() {
        return base+addTransactionUrl;
    }
}
