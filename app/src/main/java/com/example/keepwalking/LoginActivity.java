package com.example.keepwalking;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import static java.lang.Thread.sleep;

public class LoginActivity extends AppCompatActivity {

    private SessionCallback callback;

    // 날짜
    Date c;
    SimpleDateFormat df;
    String formattedDate;

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

        private Handler mHandler = new Handler();

        // 로그인에 성공한 상태
        @Override
        public void onSessionOpened() {
            requestMe();
            mHandler = new Handler() {
                @Override
                public void handleMessage(Message msg) {

                    if (msg.what == 0) {
                        redirectSignupActivity();
                    }
                }
            };
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
                            ((GlobalApplication) getApplication()).setKakaoID(id);
                            // Firebase
                            databaseReference.child("KAKAOID").child(id).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    Map<String, Object> map = (Map<String, Object>) snapshot.getValue();

                                    if (map != null) {
                                        // Toast.makeText(getApplicationContext(),"이미 존재하는 그룹명입니다.",Toast.LENGTH_SHORT).show();//토스메세지 출력
                                    } else {
                                        // addGroup(Gname_edit.getText().toString(),Gintro_edit.getText().toString(),Gcate_tv.getText().toString(), goaltime, gmp);
                                        databaseReference.child("KAKAOID").child(id).push().setValue(id);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError databaseError) {
                                    // 디비를 가져오던중 에러 발생 시
                                    //Log.e("MainActivity", String.valueOf(databaseError.toException())); // 에러문 출력
                                }
                            });

                            c = Calendar.getInstance().getTime();
                            df = new SimpleDateFormat("yyyy-MM-dd");
                            formattedDate = df.format(c);

                            // 걸음수 Firebase 저장
                            databaseReference.child("KAKAOID").child(id).child("STEPS").child(formattedDate).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.getValue() != null) {
                                        ((GlobalApplication) getApplication()).setSteps(snapshot.getValue(Integer.class));
                                    } else {
                                        ((GlobalApplication) getApplication()).setSteps(0);
                                    }
                                    Log.e("오늘의 걸음수:", "" + ((GlobalApplication) getApplication()).getSteps());
                                    mHandler.sendEmptyMessage(0);
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

                                    // 카카오 닉네임 저장
                                    ((GlobalApplication) getApplication()).setKakaoName(_profile.getNickname());

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
        startActivity(intent);
        finish();
    }
}