package com.example.keepwalking;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //카카오톡 로그인 init
        if (KakaoSDK.getAdapter() == null) {
            KakaoSDK.init(new GlobalApplication.KakaoSDKAdapter());
        }

        /**
         * 로그인 버튼을 클릭 했을시 access token을 요청하도록 설정한다.
         *
         * @param savedInstanceState 기존 session 정보가 저장된 객체
         */
        callback = new SessionCallback();
        Session.getCurrentSession().addCallback(callback);
        if (Session.getCurrentSession().checkAndImplicitOpen()) {
            // 카카오 로그인 시도 (창이 안뜬다.)
//            callback.requestMe();
        } else {
            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
        }
//        redirectSignupActivity();
//        Session.getCurrentSession().checkAndImplicitOpen();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (Session.getCurrentSession().handleActivityResult(requestCode, resultCode, data)) {
            return;
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Session.getCurrentSession().removeCallback(callback);
    }

//    private class SessionCallback implements ISessionCallback {
//
//        @Override
//        public void onSessionOpened() {
//            redirectSignupActivity();
//        }
//
//        @Override
//        public void onSessionOpenFailed(KakaoException exception) {
//            if (exception != null) {
//                Logger.e(exception);
//            }
//        }
//    }

    public void redirectSignupActivity() {
        //로그인이 완료된 후 이동하는 액티비티 지정
        final Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}