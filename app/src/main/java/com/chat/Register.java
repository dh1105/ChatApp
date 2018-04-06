package com.chat;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class Register extends AppCompatActivity implements View.OnClickListener {

    EditText email, pass_1, pass_2;
    Button reg;
    FirebaseAuth mAuth;
    ProgressDialog p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email=(EditText) findViewById(R.id.email);
        pass_1=(EditText) findViewById(R.id.pass_1);
        pass_2=(EditText) findViewById(R.id.pass_2);
        reg=(Button) findViewById(R.id.create);
        reg.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create:
                createUser(email.getText().toString(), pass_1.getText().toString());
                break;
        }
    }

    private void createUser(String email, String pass_1) {
        if(!validateEntry()){
            return;
        }

        showProgressDia();
        mAuth=FirebaseAuth.getInstance();
        mAuth.createUserWithEmailAndPassword(email, pass_1)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if(!task.isSuccessful()){
                            hidProgressDia();
                            Toast.makeText(Register.this, "User already exists", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            hidProgressDia();
                            Intent i=new Intent(Register.this, LogIn.class);
                            setResult(RESULT_OK, i);
                            finish();
                        }
                    }
                });
    }

    private boolean validateEntry() {
        String pass_1, pass_2, email;
        pass_1=this.pass_1.getText().toString();
        pass_2=this.pass_2.getText().toString();
        email=this.email.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pass_1)){
            Toast.makeText(Register.this, "Enter email and password", Toast.LENGTH_LONG).show();
            return false;
        }

        if(!pass_1.equals(pass_2)){
            Toast.makeText(Register.this, "Please enter the same password twice", Toast.LENGTH_LONG).show();
            this.pass_1.setText(null);
            this.pass_2.setText(null);
            return false;
        }

        return true;
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
