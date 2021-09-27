package com.example.keepwalking;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class LoginActivity extends AppCompatActivity {

    private ImageView loginV;
    private SessionCallback sessionCallback = new SessionCallback();
    Session session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginV = findViewById(R.id.kakao_login_large_narrow);
//        logout = findViewById(R.id.logout);


        session = Session.getCurrentSession();
        session.addCallback(sessionCallback);

        loginV.setOnClickListener(v -> {
            if (Session.getCurrentSession().checkAndImplicitOpen()) {
                Log.d("카카오 로그인", "onClick: 로그인 세션살아있음");
                // 카카오 로그인 시도 (창이 안뜬다.)
                sessionCallback.requestMe();
            } else {
                Log.d("카카오 로그인", "onClick: 로그인 세션끝남");
                // 카카오 로그인 시도 (창이 뜬다.)
                session.open(AuthType.KAKAO_LOGIN_ALL, this);
            }
        });

//        logout.setOnClickListener(v -> {
//            Log.d(TAG, "onCreate:click ");
//            UserManagement.getInstance()
//                    .requestLogout(new LogoutResponseCallback() {
//                        @Override
//                        public void onSessionClosed(ErrorResult errorResult) {
//                            super.onSessionClosed(errorResult);
//                            Log.d(TAG, "onSessionClosed: "+errorResult.getErrorMessage());
//
//                        }
//                        @Override
//                        public void onCompleteLogout() {
//                            if (sessionCallback != null) {
//                                Session.getCurrentSession().removeCallback(sessionCallback);
//                            }
//                            Log.d(TAG, "onCompleteLogout:logout ");
//                        }
//                    });
//        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();

        // 세션 콜백 삭제
        Session.getCurrentSession().removeCallback(sessionCallback);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        // 카카오톡|스토리 간편로그인 실행 결과를 받아서 SDK로 전달
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }
}