package com.example.keepwalking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.kakao.auth.AuthType;
import com.kakao.auth.ISessionCallback;
import com.kakao.auth.KakaoSDK;
import com.kakao.auth.Session;
import com.kakao.network.ErrorResult;
import com.kakao.usermgmt.UserManagement;
import com.kakao.usermgmt.callback.MeV2ResponseCallback;
import com.kakao.usermgmt.response.MeV2Response;
import com.kakao.usermgmt.response.model.Profile;
import com.kakao.usermgmt.response.model.UserAccount;
import com.kakao.util.OptionalBoolean;
import com.kakao.util.exception.KakaoException;
import com.kakao.util.helper.log.Logger;

import java.io.Serializable;
import java.util.Map;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;
    private String KakaoId;
    String a = "test";

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
//        if (Session.getCurrentSession().checkAndImplicitOpen()) {
//            // 카카오 로그인 시도 (창이 안뜬다.)
////            callback.requestMe();
//            redirectSignupActivity();
//        } else {
//            Session.getCurrentSession().open(AuthType.KAKAO_LOGIN_ALL, LoginActivity.this);
//        }

        Session.getCurrentSession().checkAndImplicitOpen();
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

    public class SessionCallback implements ISessionCallback {

        private FirebaseDatabase firebaseDatabase = FirebaseDatabase.getInstance();
        private DatabaseReference databaseReference = firebaseDatabase.getReference();

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
            redirectSignupActivity();
        }

        // 로그인에 실패한 상태
        @Override
        public void onSessionOpenFailed(KakaoException exception) {
            Log.e("SessionCallback :: ", "onSessionOpenFailed : " + exception.getMessage());
        }

        // 사용자 정보 요청
        public void requestMe() {
            UserManagement.getInstance()
                    .me(new MeV2ResponseCallback() {
                        @Override
                        public void onSessionClosed(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "세션이 닫혀 있음: " + errorResult);
                        }

                        @Override
                        public void onFailure(ErrorResult errorResult) {
                            Log.e("KAKAO_API", "사용자 정보 요청 실패: " + errorResult);
                        }

                        @Override
                        public void onSuccess(MeV2Response result) {
                            Log.i("KAKAO_API", "사용자 아이디: " + result.getId());
                            String id = String.valueOf(result.getId());
                            UserAccount kakaoAccount = result.getKakaoAccount();

                            KakaoId = ((GlobalApplication) getApplication()).setKakaoID(id);

                            // Firebase
                            databaseReference.child("KAKAOID").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                                    if (map != null) {
                                        // Toast.makeText(getApplicationContext(),"이미 존재하는 그룹명입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                                    } else {
                                        //addGroup(Gname_edit.getText().toString(),Gintro_edit.getText().toString(),Gcate_tv.getText().toString(), goaltime, gmp);
                                        databaseReference.child("KAKAOID").child(id).push().setValue(id);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // 디비를 가져오던중 에러 발생 시
                                    //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                                }
                            });


                            if (kakaoAccount != null) {

                                // 이메일
                                String email = kakaoAccount.getEmail();
                                Profile profile = kakaoAccount.getProfile();
                                if (profile == null) {
                                    Log.d("KAKAO_API", "onSuccess:profile null ");
                                } else {
                                    Log.d("KAKAO_API", "onSuccess:getProfileImageUrl " + profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "onSuccess:getNickname " + profile.getNickname());
                                }
                                if (email != null) {

                                    Log.d("KAKAO_API", "onSuccess:email " + email);
                                }

                                // 프로필
                                Profile _profile = kakaoAccount.getProfile();

                                if (_profile != null) {

                                    Log.d("KAKAO_API", "nickname: " + _profile.getNickname());
                                    Log.d("KAKAO_API", "profile image: " + _profile.getProfileImageUrl());
                                    Log.d("KAKAO_API", "thumbnail image: " + _profile.getThumbnailImageUrl());

                                } else if (kakaoAccount.profileNeedsAgreement() == OptionalBoolean.TRUE) {
                                    // 동의 요청 후 프로필 정보 획득 가능

                                } else {
                                    // 프로필 획득 불가
                                }
                            } else {
                                Log.i("KAKAO_API", "onSuccess: kakaoAccount null");
                            }
                        }
                    });
        }
    }

    public void redirectSignupActivity() {
        //로그인이 완료된 후 이동하는 액티비티 지정
        final Intent intent = new Intent(this, MainActivity.class);
//        intent.putExtra("KAKAOID",KakaoId);
        startActivity(intent);
        finish();
    }
}