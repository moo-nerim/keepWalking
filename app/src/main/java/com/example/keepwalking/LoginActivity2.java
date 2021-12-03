package com.example.keepwalking;

import android.content.Intent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class LoginActivity2 extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener firebaseAuthListener;
    private EditText editTextEmail;
    private EditText editTextPassword;
    private Button buttonLogIn;
    private Button buttonSignUp;

    // 날짜
    Date c;
    SimpleDateFormat df;
    String formattedDate;


    private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
    private DatabaseReference databaseReference = firebaseDatabase.getReference();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);

        firebaseAuth = FirebaseAuth.getInstance();

        editTextEmail = (EditText) findViewById(R.id.edittext_email);
        editTextPassword = (EditText) findViewById(R.id.edittext_password);

        buttonSignUp = (Button) findViewById(R.id.btn_signup);
        buttonSignUp.setOnClickListener(v -> {
            // SignUpActivity 연결
            Intent intent = new Intent(LoginActivity2.this, SignupActivity.class);
            startActivity(intent);
        });

        buttonLogIn = (Button) findViewById(R.id.btn_login);
        buttonLogIn.setOnClickListener(v -> {
            if (!editTextEmail.getText().toString().equals("") && !editTextPassword.getText().toString().equals("")) {
                loginUser(editTextEmail.getText().toString(), editTextPassword.getText().toString());
            } else {
                Toast.makeText(LoginActivity2.this, "계정과 비밀번호를 입력하세요.", Toast.LENGTH_LONG).show();
            }
        });

        firebaseAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                Intent intent = new Intent(LoginActivity2.this, MainActivity.class);
                startActivity(intent);
                finish();
            } else {
            }
        };
    }

    public void loginUser(String email, String password) {
        firebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // 로그인 성공
                        Toast.makeText(LoginActivity2.this, "로그인 성공", Toast.LENGTH_SHORT).show();
                        firebaseAuth.addAuthStateListener(firebaseAuthListener);
                        ((GlobalApplication) getApplication()).setBasicEmail(email);

                        c = Calendar.getInstance().getTime();
                        df = new SimpleDateFormat("yyyy-MM-dd");
                        formattedDate = df.format(c);

                        // 걸음수 Firebase 저장
                        databaseReference.child("EMAIL").child(((GlobalApplication) getApplication()).getBasicName()).child(((GlobalApplication) getApplication()).getBasicEmail()).child(formattedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.getValue() != null) {
                                    ((GlobalApplication) getApplication()).setSteps(snapshot.getValue(Integer.class));
                                } else {
                                    ((GlobalApplication) getApplication()).setSteps(0);
                                }
                                Log.e("오늘의 걸음수:", "" + ((GlobalApplication) getApplication()).getSteps());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                                // 디비를 가져오던중 에러 발생 시
                                //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                            }
                        });

                        // email setter
                        String delEmail = email.substring(0, email.indexOf(".")) + "@" + email.substring(email.indexOf(".") + 1);
                        databaseReference.child("EMAIL").child(delEmail).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                ((GlobalApplication) getApplication()).setBasicEmail(snapshot.getValue(String.class));
                                Log.e("emaiiiiil : ",((GlobalApplication) getApplication()).getBasicEmail());
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError databaseError) {
                            }
                        });

                    } else {
                        // 로그인 실패
                        Toast.makeText(LoginActivity2.this, "아이디 또는 비밀번호가 일치하지 않습니다.", Toast.LENGTH_SHORT).show();
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
