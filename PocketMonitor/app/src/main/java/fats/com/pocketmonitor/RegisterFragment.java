package fats.com.pocketmonitor;

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


public class RegisterFragment extends Fragment {

    Animation animShake;
    TextInputLayout nameWrapper;
    TextInputLayout emailWrapper;
    TextInputLayout passwordWrapper;
    TextInputLayout passwordConfirmWrapper;
    EditText textName;
    EditText textEmail;
    EditText textPassword;
    EditText textConfirmPassword;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register,container,false);

        nameWrapper = (TextInputLayout) view.findViewById(R.id.register_nameWrapper);
        textName = (EditText) view.findViewById(R.id.register_name);
        nameWrapper.setHint(getString(R.string.name));

        emailWrapper = (TextInputLayout) view.findViewById(R.id.register_emailWrapper);
        textEmail = (EditText) view.findViewById(R.id.register_email);
        emailWrapper.setHint(getString(R.string.email));

        passwordWrapper = (TextInputLayout) view.findViewById(R.id.register_passwordWrapper);
        textPassword = (EditText) view.findViewById(R.id.register_password);
        passwordWrapper.setHint(getString(R.string.password));

        passwordConfirmWrapper = (TextInputLayout) view.findViewById(R.id.register_confirmPasswordWrapper);
        textConfirmPassword = (EditText) view.findViewById(R.id.register_confirm_password);
        passwordConfirmWrapper.setHint(getString(R.string.confirm_password));

        final Button buttonRegister = (Button) view.findViewById(R.id.button_register);
        final Button buttonGoToLogin = (Button) view.findViewById(R.id.button_to_login);

        animShake = AnimationUtils.loadAnimation(getActivity().getApplicationContext(),R.anim.shake);


        buttonRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(submitForm()){

                    final Constant constant = new Constant();
                    final String name = textName.getText().toString().trim();
                    final String email = textEmail.getText().toString().trim();
                    final String password = textPassword.getText().toString().trim();

                    //----------Start of post request using volley, this will give string response----------
                    StringRequest stringRequest = new StringRequest(Request.Method.POST, constant.getRegisterUrl(),

                            //----------Response if there's no error----------
                            new Response.Listener<String>() {
                                @Override
                                public void onResponse(String response) {

                                    //----------If registration is successful----------
                                    if (response.equals("Yes")){

                                        // This opens up a pretty pretty snackbar
                                        final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "Registration was successful! Please Login to proceed.", Snackbar.LENGTH_LONG)
                                                .setAction("Action", null);
                                        snackbar.setAction("DISMISS", new View.OnClickListener(){
                                            @Override
                                            public void onClick(View view) {
                                                snackbar.dismiss();
                                            }
                                        });
                                        snackbar.show();
                                        getActivity().getSupportFragmentManager()
                                                .beginTransaction()
                                                .replace(R.id.fragment_container,new LoginFragment())
                                                .addToBackStack(null)
                                                .commit();
                                    }
                                    //----------If user exists----------
                                    else if(response.equals("User exists")){

                                        // This opens up a pretty pretty snackbar
                                        final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "This Email ID is already in use.", Snackbar.LENGTH_LONG)
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

                            //----------Response if there is error----------
                            new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    // This opens up a pretty pretty snackbar
                                    final Snackbar snackbar =  Snackbar.make(getActivity().findViewById(R.id.activity_main), "Please check internet connection and retry.", Snackbar.LENGTH_LONG)
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

                            //----------Set parameters to send in the post request----------
                    {
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> params = new HashMap<String, String>();
                            params.put("name", name);
                            params.put("password", password);
                            params.put("email", email);
                            params.put("APIkey",constant.getAPIkey());
                            return params;
                        }
                    };

                    //----------We add the request to request queue----------
                    MySingleton.getInstance(getActivity().getApplicationContext()).addToRequestQueue(stringRequest);

                }
            }
        });

        buttonGoToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getActivity().getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.fragment_container,new LoginFragment())
                        .commit();
            }
        });

        return view;
    }

    public boolean submitForm(){
        if(!checkName() || !checkEmail() || !checkPassword() || !checkPasswordConfirm()) {

            if(!checkPasswordConfirm()){
                textConfirmPassword.setAnimation(animShake);
                textConfirmPassword.startAnimation(animShake);
                textConfirmPassword.setText("");
                textConfirmPassword.requestFocus();
            }

            if (!checkPassword()) {
                textPassword.setAnimation(animShake);
                textPassword.startAnimation(animShake);
                textPassword.setText("");
                textConfirmPassword.setText("");
                textPassword.requestFocus();
            }

            if(!checkEmail()) {
                textEmail.setAnimation(animShake);
                textEmail.startAnimation(animShake);
                textEmail.requestFocus();
            }

            if(!checkName()){
                textName.setAnimation(animShake);
                textName.startAnimation(animShake);
                textName.requestFocus();
            }

            return false;
        }
        nameWrapper.setErrorEnabled(false);
        emailWrapper.setErrorEnabled(false);
        passwordWrapper.setErrorEnabled(false);
        passwordConfirmWrapper.setErrorEnabled(false);
        return true;
    }

    public boolean checkName(){
        if(textName.getText().toString().trim().isEmpty()){
            nameWrapper.setErrorEnabled(true);
            nameWrapper.setError(getString(R.string.name_required));
            textName.requestFocus();
            return false;
        }
        nameWrapper.setErrorEnabled(false);
        return true;
    }

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

    public boolean checkPassword(){
        String p1 = textPassword.getText().toString().trim();
        String p2 = textConfirmPassword.getText().toString().trim();

        if(p1.isEmpty()) {
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError(getString(R.string.password_required));
            textPassword.requestFocus();
            return false;
        } else if(!p1.equals(p2)){
            passwordConfirmWrapper.setErrorEnabled(true);
            passwordConfirmWrapper.setError(getString(R.string.passwords_do_not_match));
            passwordWrapper.setErrorEnabled(true);
            passwordWrapper.setError(getString(R.string.passwords_do_not_match));
            textPassword.requestFocus();
            return false;
        }
        passwordWrapper.setErrorEnabled(false);
        return true;
    }

    public boolean checkPasswordConfirm(){
        String p2 = textConfirmPassword.getText().toString().trim();
        if(p2.isEmpty()){
            passwordConfirmWrapper.setErrorEnabled(true);
            passwordConfirmWrapper.setError(getString(R.string.please_confrim_password));
            textConfirmPassword.requestFocus();
            return false;
        }
        passwordConfirmWrapper.setErrorEnabled(false);
        return true;
    }
}
