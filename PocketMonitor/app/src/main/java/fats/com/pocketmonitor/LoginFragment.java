package fats.com.pocketmonitor;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import java.util.HashMap;
import java.util.Map;

public class LoginFragment extends Fragment {

    Animation animShake;
    TextInputLayout emailWrapper;
    TextInputLayout passwordWrapper;
    EditText textEmail;
    EditText textPassword;
    DatabaseHelper databaseHelper;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_login,container,false);

        emailWrapper = (TextInputLayout) view.findViewById(R.id.login_emailWrapper);
        textEmail = (EditText) view.findViewById(R.id.login_email);

        passwordWrapper = (TextInputLayout) view.findViewById(R.id.login_passwordWrapper);
        textPassword = (EditText) view.findViewById(R.id.login_password);


        final Button buttonLogin = (Button) view.findViewById(R.id.button_login);
        final Button buttonGoToRegister = (Button) view.findViewById(R.id.button_to_register);

        animShake = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.shake);

        databaseHelper = new DatabaseHelper(getActivity().getApplicationContext());



        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(submitForm()) {
                    performLogin();
                }

            }
        });

        buttonGoToRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new RegisterFragment())
                        .commit();
            }
        });

        return view;
    }


/*----------Check details of all inputs----------------*/
    private boolean submitForm(){
        if(!checkEmail()||!checkPassword()) {

            if (!checkPassword()) {
                textPassword.setAnimation(animShake);
                textPassword.startAnimation(animShake);
                textPassword.requestFocus();
            }

            if(!checkEmail()) {
                textEmail.setAnimation(animShake);
                textEmail.startAnimation(animShake);
                textEmail.requestFocus();
            }

            return false;
        }
        emailWrapper.setErrorEnabled(false);
        passwordWrapper.setErrorEnabled(false);
        return true;
    }


/*----------Check if email is correct-------------------*/
    public boolean checkEmail(){
        String email = textEmail.getText().toString().trim();
        if(email.isEmpty() || !isValidEmail(email)){
            emailWrapper.setErrorEnabled(true);
            emailWrapper.setError(getString(R.string.invalid_email));
            textEmail.requestFocus();
            return  false;
        }
        emailWrapper.setErrorEnabled(false);
        return true;
    }


    public boolean isValidEmail(String email){
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }


/*-----------Check if password is correct---------------*/
    public boolean checkPassword(){
        if(textPassword.getText().toString().trim().isEmpty()){
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError(getString(R.string.invalid_password));
            textPassword.requestFocus();
            return false;
        }
        passwordWrapper.setErrorEnabled(false);
        return true;
    }


/*---------------Start login procedure-------------------*/
    public void performLogin(){
        final Constant c = new Constant();
        final String email = textEmail.getText().toString().trim();
        final String password = textPassword.getText().toString().trim();

        /*----------Start of post request using volley, this will give string response----------*/

        StringRequest stringRequest = new StringRequest(Request.Method.POST, c.getLoginUrl(),

                // Response if there's no error
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {

                        String[] ans = response.split(",");
                        // If login is successful
                        if (ans[2].equals("Yes")) {

                            userLogin(email,ans[0],ans[1]);

                        } else if (ans[2].equals("No")) {

                            // This opens up a pretty pretty snackbar
                            final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "Email ID or Password is incorrect!", Snackbar.LENGTH_LONG)
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
                        final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), error.toString(), Snackbar.LENGTH_LONG)
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
                params.put("email", email);
                params.put("password", password);
                params.put("APIkey", c.getAPIkey());
                return params;
            }
        };

        /*----------We add the request to request queue----------*/
        MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);
    }

    public void userLogin(String email, String balance, String name){
        // Add the user to the user table
        User user = new User(email,name,Double.parseDouble(balance));
        databaseHelper.createUser(user);

        // Mention it's the first login (needed to load tran table with user transactions)
        SharedPreferences sp  = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor edit = sp.edit();
        edit.putString("First Login?","yes");
        edit.apply();

        Intent i = new Intent(getActivity(),MainActivity.class);
        startActivity(i);
    }
}
