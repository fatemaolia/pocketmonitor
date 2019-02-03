package fats.com.pocketmonitor;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {


    DatabaseHelper databaseHelper;
    boolean doubleBackToExitPressedOnce = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle(getString(R.string.app_name));
        setSupportActionBar(toolbar);
        Objects.requireNonNull(getSupportActionBar()).hide();


        int count;

        databaseHelper = new DatabaseHelper(getApplicationContext());
        count = databaseHelper.checkUser();

        if(count==1){

            SharedPreferences sp  = getPreferences(Context.MODE_PRIVATE);
            String s = sp.getString("First Login?","yes");

            //if this is the first time the user has logged in, we want to fill the transaction table with their data
            if(s.equals("yes")){

                //we get user transactions
                getUserTransactionData();

            }

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container,new TransactionFragment())
                    .commit();
        } else if(count>1){
            databaseHelper.flushDatabase();
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container,new RegisterFragment())
                    .commit();
        } else {

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.fragment_container,new RegisterFragment())
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()){
            case R.id.logout:
                logout();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }


    public void getUserTransactionData(){
        final Constant c = new Constant();

        /*----------Start of post request using volley, this will give String response which we will convert to JsonArray----------*/
        StringRequest stringRequest = new StringRequest(Request.Method.POST, c.getGetTransactionsUrl(),

                /*---------Response if there's no error---------*/
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        Log.e("RESPONSE",response);
                        try {

                            //We convert the string response to JsonArray
                            JSONArray jsonArray = new JSONArray(response);
                            for (int i = 0; i < jsonArray.length(); i++)
                            {
                                //Now we take each row
                                JSONObject jsonObj = jsonArray.getJSONObject(i);

                                //create a transaction object to set all the transaction information
                                Transaction t = new Transaction();
                                t.setDate(jsonObj.getString("t_date"));
                                t.setType(Integer.parseInt(jsonObj.getString("type")));
                                t.setCategory(jsonObj.getString("category"));
                                t.setInfo(jsonObj.getString("info"));
                                t.setAmount(Double.parseDouble(jsonObj.getString("amount")));

                                //add this row to tran table
                                databaseHelper.populateTransactionTable(t);
                            }

                            SharedPreferences sp  = getPreferences(Context.MODE_PRIVATE);
                            SharedPreferences.Editor edit = sp.edit();
                            edit.putString("First Login?","no");
                            edit.apply();

                        } catch (Exception e){
                            Log.e("Error in creating JSON",e.toString());
                        }


                    }
                },

                /*----------Response if there is an error----------*/
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Toast.makeText(getApplicationContext(), "ERROR WHILE GETTING USER TRANSACTIONS: "+error.toString(), Toast.LENGTH_LONG).show();
                    }
                }
        )

                /*----------Set parameters to send in the post request----------*/
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();
                User u  = databaseHelper.getUser();
                params.put("email", u.getEmail());
                params.put("APIkey", c.getAPIkey());
                return params;
            }
        };

        /*----------We add the request to request queue----------*/
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(stringRequest);

    }

    @Override
    public void onBackPressed() {
        if (doubleBackToExitPressedOnce) {
            super.onBackPressed();
            return;
        }

        this.doubleBackToExitPressedOnce = true;
            // This opens up a pretty pretty snackbar
            final Snackbar snackbar =  Snackbar.make(findViewById(R.id.activity_main), "Press BACK again to Exit.", Snackbar.LENGTH_LONG)
                    .setAction("Dismiss", null);
            snackbar.setAction("DISMISS", new View.OnClickListener(){
                @Override
                public void onClick(View view) {
                    snackbar.dismiss();
                }
            });
            snackbar.setActionTextColor(ContextCompat.getColor(getApplicationContext(),R.color.colorAccent));
            snackbar.show();


        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                doubleBackToExitPressedOnce=false;
            }
        }, 2000);
    }

    public void logout(){
        DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
        databaseHelper.flushDatabase();

        getSupportActionBar().hide();

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container,new LoginFragment())
                .commit();

    }
}
