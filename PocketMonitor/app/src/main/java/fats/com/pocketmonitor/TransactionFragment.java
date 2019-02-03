package fats.com.pocketmonitor;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import static fats.com.pocketmonitor.R.string.date;


public class TransactionFragment extends Fragment {

    Animation animShake;
    TextView pocketMoney;
    RadioGroup radioType;
    TextInputLayout calendarWrapper;
    TextView textCalendar;
    TextInputLayout detailWrapper;
    EditText detail;
    TextInputLayout categoryWrapper;
    AutoCompleteTextView category;
    TextInputLayout amountWrapper;
    EditText amount;
    Button buttonSubmit;
    DatabaseHelper databaseHelper;
    ArrayAdapter<String> categoryAdapter;
    String[] categoryList;
    int categoryType;
    Constant c;
    int yyyy;
    int mm;
    int dd;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_transaction,container,false);

        ((AppCompatActivity)getActivity()).getSupportActionBar().show();
        databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());

        // Set pocket money
        pocketMoney = (TextView) view.findViewById(R.id.tran_pocketmoney);
        setPocketMoney();
        setCategoryList(0);
        categoryType =0;

        animShake = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.shake);

        // Calendar stuff
        calendarWrapper = (TextInputLayout) view.findViewById(R.id.tran_dateWrapper);
        calendarWrapper.setHint(getString(date));
        final Calendar c = Calendar.getInstance();
        textCalendar = (TextView) view.findViewById(R.id.tran_calender);
        setCalendar(c.get(Calendar.YEAR),c.get(Calendar.MONTH),c.get(Calendar.DAY_OF_MONTH));


        // RadioButton stuff
        radioType = (RadioGroup) view.findViewById(R.id.tran_group_type);
        radioType.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {
                onRadioButtonClicked(i);
            }
        });


        //Setting the Category adapter
        categoryWrapper = (TextInputLayout) view.findViewById(R.id.tran_categoryWrapper);
        categoryWrapper.setHint(getString(R.string.category));
        category = (AutoCompleteTextView) view.findViewById(R.id.tran_category);

        // Setting the adapter for the autocomplete textview
        detailWrapper = (TextInputLayout) view.findViewById(R.id.tran_reasonWrapper);
        detailWrapper.setHint(getString(R.string.where_spent));
        detail = (EditText) view.findViewById(R.id.tran_reason);


        // Setting the textbox for the currency input
        amountWrapper = (TextInputLayout) view.findViewById(R.id.tran_amountWrapper);
        amountWrapper.setHint(getString(R.string.amount));
        amount = (EditText) view.findViewById(R.id.tran_amount);
        amount.addTextChangedListener(new NumberTextWatcher());


        // Updating the tables with the new transactions;
        buttonSubmit = (Button) view.findViewById(R.id.tran_submit);
        buttonSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(canSubmit()) {
                    submitTransaction(c.get(Calendar.YEAR), c.get(Calendar.MONTH), c.get(Calendar.DAY_OF_MONTH));
                }
            }
        });

        return view;
    }

    public void setPocketMoney(){
            User u = databaseHelper.getUser();
            double total = databaseHelper.getBalance(u);
        if(total!=-1)
            pocketMoney.setText(getString(R.string.money_in_pocket)+Double.toString(total));
        else
            pocketMoney.setText(getString(R.string.money_in_pocket)+getString(R.string.zero));
    }

/*----------------------------------------CalendarPicker stuff starts here---------------------------------------*/
    public void setCalendar(final int year, final int month, final int day){
        SharedPreferences sp  = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putInt("year",year);
        edit.putInt("month",month);
        edit.putInt("day",day);
        edit.apply();

        setCalendarText(day+"-"+(month+1)+"-"+year);


        textCalendar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //----------Initialising the datepickerfragment----------
                DatePickerFragment datePickerFragment = new DatePickerFragment().newInstance();
                datePickerFragment.setCallBack(onDate);
                //----------Passing values of calender textview to the datepicker----------
                //----------so that we can select that date and keep----------

                SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                int newYear = sp.getInt("year",year);
                int newMonth = sp.getInt("month",month);
                int newDay = sp.getInt("day",day);

                Bundle args = new Bundle();
                args.putInt("year", newYear);
                args.putInt("month", newMonth);
                args.putInt("day", newDay);



                datePickerFragment.setArguments(args);
                //----------Displaying the fragment----------
                datePickerFragment.show(getActivity().getSupportFragmentManager(),"DatePickerFragment");

            }
        });
    }

    DatePickerDialog.OnDateSetListener onDate = new DatePickerDialog.OnDateSetListener() {

        @Override
        public void onDateSet(DatePicker view, int newYear, int newMonth,
                              int newDay) {

            setCalendarText(newDay+"-"+(newMonth+1)+"-"+newYear);
            SharedPreferences sp  = getActivity().getPreferences(Context.MODE_PRIVATE);
            SharedPreferences.Editor edit = sp.edit();
            edit.putInt("year",newYear);
            edit.putInt("month",newMonth);
            edit.putInt("day",newDay);
            edit.apply();
        }


    };

    public void setCalendarText(String input){
        Date newDate;
        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
        try {
            newDate = format.parse(input);
            format = new SimpleDateFormat("EEE, MMM d",Locale.ENGLISH);
            textCalendar.setText( format.format(newDate));

        } catch (Exception e){
            e.printStackTrace();
        }
    }


/*-----------------------------------------RadioButton stuff starts here------------------------------------------*/
    public void onRadioButtonClicked(int type) {
        // Is the button now checked?


        // Check which radio button was clicked
        switch(type) {
            case R.id.tran_radio_spent:
                detailWrapper.setHint(getString(R.string.where_spent));
                categoryType =0;
                setCategoryList(0);
                break;

            case R.id.tran_radio_received:
                detailWrapper.setHint(getString(R.string.where_received));
                categoryType =1;
                setCategoryList(1);
                break;

        }
    }



/*---------------------------------Populate the list of categories for autocomplete textview------------------------*/
    public void setCategoryList(int t){
        categoryList = databaseHelper.getCategoryList(t);
        if(categoryList!=null) {
            categoryAdapter = new ArrayAdapter<>(getActivity().getApplicationContext(), android.R.layout.simple_dropdown_item_1line, categoryList);
        }
    }


/*------------------------Submit the transaction to the online and internal databases----------------------------*/
    public void submitTransaction(final int year, final int day, final int month){

        c = new Constant();
        final Calendar cal = Calendar.getInstance();
        final double amt = Double.parseDouble(amount.getText().toString().trim());

 /*----------Start of post request using volley, this will give String response which we will convert to JsonArray----------*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, c.getAddTransactionUrl(),
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        if(response.equals("Success")){

                            // clear all the fields
                            radioType.clearCheck();
                            setCalendar(cal.get(Calendar.YEAR),cal.get(Calendar.MONTH),cal.get(Calendar.DAY_OF_MONTH));


                            // add transaction to local db
                            Transaction t = new Transaction();

                            // To get date value in sql format
                            SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                            yyyy = sp.getInt("year",year);
                            mm = sp.getInt("month",month);
                            dd = sp.getInt("day",day);


                            String date = yyyy+"-"+(mm+1)+dd;
                            t.setDate(date);
                            if(categoryType ==0) {
                                t.setAmount(-1.0*Double.parseDouble(amount.getText().toString().trim()));
                            } else{
                                t.setAmount(Double.parseDouble(amount.getText().toString().trim()));
                            }
                            t.setCategory(category.getText().toString().trim());
                            t.setInfo(detail.getText().toString().trim());
                            t.setType(categoryType);

                            User u = databaseHelper.getUser();
                            databaseHelper.createTransaction(t,u);
                            category.setText("");
                            detail.setText("");
                            amount.setText("");
                            radioType.check(R.id.tran_radio_spent);
                            category.requestFocus();
                            setPocketMoney();

                            // This opens up a pretty pretty snackbar
                            final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "Transaction added successfully!", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            snackbar.setAction("DISMISS", new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();
                        } else {
                            // This opens up a pretty pretty snackbar
                            final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "Uh oh, something went wrong! Please retry.", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null);
                            snackbar.setAction("DISMISS", new View.OnClickListener(){
                                @Override
                                public void onClick(View view) {
                                    snackbar.dismiss();
                                }
                            });
                            snackbar.show();

                        }

                    }
                },
                    /*----------Response if there is error----------*/
                new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    // This opens up a pretty pretty snackbar
                    final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "Please check your internet connection and try again.", Snackbar.LENGTH_LONG)
                            .setAction("Action", null);
                    snackbar.setAction("DISMISS", new View.OnClickListener(){
                        @Override
                        public void onClick(View view) {
                            snackbar.dismiss();
                        }
                    });
                    snackbar.show();
                }
            }
        )
            /*----------Set parameters to send in the post request----------*/
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String> params = new HashMap<>();

                // get the date
                SharedPreferences sp = getActivity().getPreferences(Context.MODE_PRIVATE);
                yyyy = sp.getInt("year",year);
                mm = sp.getInt("month",month);
                dd = sp.getInt("day",day);

                params.put("t_date",yyyy+"-"+(mm+1)+"-"+dd);
                params.put("type",Integer.toString(categoryType));
                params.put("category",category.getText().toString().trim());
                if(detail.getText().toString().trim().equals(""))
                    params.put("info","Unknown");
                else
                    params.put("info",detail.getText().toString().trim());
                params.put("APIkey", c.getAPIkey());
                if(categoryType ==0)
                    params.put("amount",Double.toString(-1.0*amt));
                else
                    params.put("amount",Double.toString(amt));
                params.put("email",databaseHelper.getUser().getEmail());
                return params;
            }
        };

        /*----------We add the request to request queue----------*/
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);

    }

    public boolean canSubmit(){

        if(!checkReason()||!checkAmt()) {

            if (!checkAmt()) {
                amount.setAnimation(animShake);
                amount.startAnimation(animShake);
                amount.requestFocus();
            }

            if(!checkReason()) {
                detail.setAnimation(animShake);
                detail.startAnimation(animShake);
                detail.requestFocus();
            }

            return false;
        }
        detailWrapper.setErrorEnabled(false);
        amountWrapper.setErrorEnabled(false);
        return true;
    }

    public boolean checkReason(){
        String r = detail.getText().toString();
        if(r.equals("")) {
            detailWrapper.setErrorEnabled(true);
            return false;
        }
        detailWrapper.setErrorEnabled(false);
        return true;
    }

    public boolean checkAmt(){
        String r = amount.getText().toString();
        if(r.equals("")) {
            amountWrapper.setErrorEnabled(true);
            return false;
        }
        amountWrapper.setErrorEnabled(false);
        return true;
    }
}
