package com.example.keepwalking;

import android.content.Context;
import android.content.Intent;
import androidx.appcompat.app.AppCompatActivity;

import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity3 extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;

    public static Context mContext;

    public void test() {

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);
        buttonSignUp.setOnClickListener(v -> {
            // SignUpActivity 연결
            Intent intent = new Intent(LoginActivity3.this, SignupActivity.class);
            startActivity(intent);
        });

        buttonLogIn = (Button) findViewById(R.id.btn_login);
        buttonLogIn.setOnClickListener(v -> {
            if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            } else {
                Toast.makeText(LoginActivity3.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            }
        });

        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(LoginActivity3.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
            }
        };
        mContext = this;
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        Toast.makeText(LoginActivity3.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        ((GlobalApplication) getApplication()).setBasicEmail(email);
                    } else {
                        // 로그인 실패
                        Toast.makeText(LoginActivity3.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (firebaseAuthListener != null) {
            firebaseAuth.removeAuthStateListener(firebaseAuthListener);
        }
    }
}
