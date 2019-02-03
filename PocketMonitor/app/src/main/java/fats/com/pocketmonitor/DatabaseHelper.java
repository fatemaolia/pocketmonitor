package fats.com.pocketmonitor;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class DatabaseHelper extends SQLiteOpenHelper {

    SQLiteDatabase sqLiteDatabase;

    // Logcat tag
    private static final String LOG = "DatabaseHelper";

    // Database Version
    private static final int DATABASE_VERSION = 3;

    // Database Name
    private static final String DATABASE_NAME = "pocketmonitor";

    // Table names
    private static final String TABLE_USER = "user";
    private static final String TABLE_TRAN = "tran";

    // Column names for user
    private static final String COL_EMAIL = "email";
    private static final String COL_BALANCE = "balance";
    private static final String COL_NAME = "name";

    // Column names for transcaction
    private static final String COL_ID = "id";
    private static final String COL_DATE = "date";
    private static final String COL_TYPE = "type";
    private static final String COL_CATEGORY = "category";
    private static final String COL_INFO = "info";
    private static final String COL_AMOUNT = "amount";

    // Table Create Statements
    // user table create statement

    private static final String CREATE_TABLE_USER = "CREATE TABLE " + TABLE_USER + "("
            + COL_EMAIL + " TEXT PRIMARY KEY, "
            + COL_NAME + " TEXT, "
            + COL_BALANCE + " REAL " + ")";

    // transaction table create statement
    private static final String CREATE_TABLE_TRANSACTION = "CREATE TABLE " + TABLE_TRAN
            + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + COL_DATE + " TEXT, "
            + COL_TYPE + " INTEGER, "
            + COL_CATEGORY + " TEXT, "
            + COL_INFO + " TEXT, "
            + COL_AMOUNT + " REAL " + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        sqLiteDatabase = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        sqLiteDatabase.execSQL(CREATE_TABLE_USER);
        sqLiteDatabase.execSQL(CREATE_TABLE_TRANSACTION);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_TRAN);

        // create new tables
        onCreate(sqLiteDatabase);
    }


    /*-------------------THIS IS WHERE WE START OUR CRUD OPERATIONS FOR THE TABLES------------------------*/


    // To add a new user on login
    public void createUser(User user){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values  = new ContentValues();
        values.put(COL_EMAIL,user.getEmail());
        values.put(COL_NAME, user.getName());
        values.put(COL_BALANCE,user.getBalance());

        long id = sqLiteDatabase.insert(TABLE_USER, null, values);

        Log.e(LOG, "User added successfully! "+id);
    }

    // To check if a user is present
    public int checkUser(){

        String query = "SELECT * FROM " + TABLE_USER;

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        Cursor c = sqLiteDatabase.rawQuery(query,null);

        int count = 0;
        if(c.moveToFirst()){
            do{
                count++;
            }while(c.moveToNext());
        }

        c.close();
        Log.e(LOG,"The row count for users table is "+count);

        return count;
    }

    // To get the user
    public User getUser(){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String selectQuery = " SELECT * FROM " + TABLE_USER;
        Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);
        if(c!=null)
            c.moveToFirst();
        User  u = new User(c.getString(c.getColumnIndex(COL_EMAIL)),c.getString(c.getColumnIndex(COL_NAME)),c.getDouble(c.getColumnIndex(COL_BALANCE)));
        Log.e(LOG,"User returned successfully"
                +" { Email = " +c.getString(c.getColumnIndex(COL_EMAIL))
                + " Name = "+ c.getString(c.getColumnIndex(COL_NAME))
                + " Balance = " + c.getDouble(c.getColumnIndex(COL_BALANCE)) + " }");
        c.close();


        return u;
    }


    // To add a row to transaction table
    public void populateTransactionTable(Transaction transaction){

        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COL_DATE, transaction.getDate());
        values.put(COL_TYPE, transaction.getType());
        values.put(COL_CATEGORY,transaction.getCategory());
        values.put(COL_INFO, transaction.getInfo());
        values.put(COL_AMOUNT, transaction.getAmount());

        long id = sqLiteDatabase.insert(TABLE_TRAN,null,values);

        Log.e(LOG,"Transaction added successfully! "+id);
    }

    // Adjust balance according to new value
    public void createTransaction(Transaction transaction, User user){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        double balance;
        populateTransactionTable(transaction);
        balance = user.getBalance() + transaction.getAmount();
        Log.e(LOG,"balance = "+user.getBalance() + " amount = " + transaction.getAmount());
        user.setBalance(balance);
        ContentValues values = new ContentValues();
        values.put(COL_BALANCE, balance);

        long id = sqLiteDatabase.update(TABLE_USER,values, null,null);
        Log.e(LOG,"Balance adjusted successfully! "+id);

    }

    public double getBalance(User u){
        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String selectQuery = "SELECT " + COL_BALANCE + " FROM " + TABLE_USER;
        Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);

        if(c!=null) {
            c.moveToFirst();

            return c.getDouble(c.getColumnIndex(COL_BALANCE));
        }

        return -1;
    }

    // To get list of reasons for autocomplete view
    public String[] getCategoryList(int type){

        SQLiteDatabase sqLiteDatabase = this.getReadableDatabase();
        String selectQuery = "SELECT DISTINCT " + COL_CATEGORY + " FROM " + TABLE_TRAN + " WHERE " + COL_TYPE + " = " + type;
        Cursor c = sqLiteDatabase.rawQuery(selectQuery,null);

        String category[] = new String[c.getCount()];

        int i = 0;
        if(c.moveToFirst()){
            do{
                category[i] = c.getString(c.getColumnIndex(COL_INFO));

                Log.e("DBhelper(get reason)",category[i]);
                i++;
            } while(c.moveToNext());
            return category;
        } else {
            return null;
        }
    }

    // To delete all rows from both tables
    public void flushDatabase(){
        SQLiteDatabase sqLiteDatabase = this.getWritableDatabase();
        sqLiteDatabase.execSQL("delete from " + TABLE_USER);
        sqLiteDatabase.execSQL("delete from " + TABLE_TRAN);
    }

}
