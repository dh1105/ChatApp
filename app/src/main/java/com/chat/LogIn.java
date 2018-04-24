package com.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;

public class LogIn extends AppCompatActivity implements View.OnClickListener{

    EditText user, pass;
    Button sign, reg;
    FirebaseAuth mAuth;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in);
        user = (EditText) findViewById(R.id.username);
        pass = (EditText) findViewById(R.id.pass);
        sign = (Button) findViewById(R.id.sign_in);
        sign.setOnClickListener(this);
        reg = (Button) findViewById(R.id.Reg);
        reg.setOnClickListener(this);
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        //FirebaseUser currentUser = mAuth.getCurrentUser();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==2){
            if(resultCode==RESULT_OK){
                Toast.makeText(LogIn.this, "Registeration successful!", Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.sign_in:
                signIn(user.getText().toString(), pass.getText().toString());
                break;

            case R.id.Reg:
                Intent i=new Intent(LogIn.this, Register.class);
                startActivityForResult(i, 2);
                break;
        }

    }

    private boolean validateEntry(){
        boolean valid=true;

        String email=user.getText().toString();
        if(TextUtils.isEmpty(email)){
            valid=false;
        }

        String pa=pass.getText().toString();
        if(TextUtils.isEmpty(pa)){
            valid=false;
        }

        return valid;
    }

    private void signIn(String user, String pass) {

        if(!validateEntry()){
            Toast.makeText(getApplicationContext(), "Username or password not entered", Toast.LENGTH_LONG).show();
            return;
        }

        showProgressDia();
        mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithEmailAndPassword(user, pass).addOnCompleteListener(this,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                            Intent i=new Intent(LogIn.this, Chat.class);
                            startActivity(i);
                            hidProgressDia();
                            finish();
                        }
                        else{
                            hidProgressDia();
                            try{
                                throw task.getException();
                            } catch(FirebaseAuthInvalidCredentialsException f){
                                f.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Invalid login details", Toast.LENGTH_LONG).show();
                            } catch(FirebaseAuthInvalidUserException c){
                                c.printStackTrace();
                                Toast.makeText(getApplicationContext(), "User does not exist", Toast.LENGTH_LONG).show();
                            } catch(FirebaseNetworkException d) {
                                d.printStackTrace();
                                Toast.makeText(getApplicationContext(), "Network error", Toast.LENGTH_LONG).show();
                            }catch (Exception e) {
                                e.printStackTrace();
                            }
                        }

//                        if(!task.isSuccessful()){
//                            Toast.makeText(LogIn.this, "Account does not exist", Toast.LENGTH_LONG).show();
//                        }
                    }
                });
    }

    private void showProgressDia(){
        p=new ProgressDialog(this);
        p.setMessage("Loading..");
        p.setIndeterminate(true);
        p.show();
    }

    private void hidProgressDia(){
        if(p.isShowing() && p!=null){
            p.dismiss();
        }
    }
}
