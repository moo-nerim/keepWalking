package com.example.gait_health_prediction_androidphone;

import com.kakao.kakaolink.v2.KakaoLinkResponse;
import com.kakao.kakaolink.v2.KakaoLinkService;
import com.kakao.message.template.ButtonObject;
import com.kakao.message.template.ContentObject;
import com.kakao.message.template.FeedTemplate;
import com.kakao.message.template.LinkObject;
import com.kakao.message.template.SocialObject;
import com.kakao.network.ErrorResult;
import com.kakao.network.callback.ResponseCallback;
import com.kakao.util.helper.log.Logger;

import java.util.HashMap;
import java.util.Map;

public class ShareKakao {
    public void click() {
        ((MainActivity) MainActivity.context_main1).kakaoLinkBtn.setOnClickListener(v -> {
            FeedTemplate params = FeedTemplate
                    .newBuilder(ContentObject.newBuilder("딥러닝을 통한 보행 건강 예측",
                            "https://res.cloudinary.com/im2015/image/upload/w_1200,h_1200,c_fill,g_center//blog/running_cover_1.jpg",
                            LinkObject.newBuilder().setWebUrl("https://developers.kakao.com")
                                    .setMobileWebUrl("https://developers.kakao.com").build())
                            .setDescrption("측정 결과 확인하기")
                            .build())
                    .setSocial(SocialObject.newBuilder().setLikeCount(10).setCommentCount(20)
                            .setSharedCount(30).setViewCount(40).build())
                    .addButton(new ButtonObject("웹에서 보기", LinkObject.newBuilder().setWebUrl("'https://developers.kakao.com").setMobileWebUrl("'https://developers.kakao.com").build()))
                    .addButton(new ButtonObject("앱에서 보기", LinkObject.newBuilder()
                            .setWebUrl("'https://developers.kakao.com")
                            .setMobileWebUrl("'https://developers.kakao.com")
                            .setAndroidExecutionParams("key1=value1")
                            .setIosExecutionParams("key1=value1")
                            .build()))
                    .build();

            Map<String, String> serverCallbackArgs = new HashMap<String, String>();
            serverCallbackArgs.put("user_id", "${current_user_id}");
            serverCallbackArgs.put("product_id", "${shared_product_id}");

            KakaoLinkService.getInstance().sendDefault(v.getContext(), params, serverCallbackArgs, new ResponseCallback<KakaoLinkResponse>() {
                @Override
                public void onFailure(ErrorResult errorResult) {
                    Logger.e(errorResult.toString());
                }

                @Override
                public void onSuccess(KakaoLinkResponse result) {
                    // 템플릿 밸리데이션과 쿼터 체크가 성공적으로 끝남. 톡에서 정상적으로 보내졌는지 보장은 할 수 없다. 전송 성공 유무는 서버콜백 기능을 이용하여야 한다.
                }
            });
        });
    }
}
