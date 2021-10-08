package com.example.keepwalking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    CompactCalendarView compactCalendarView;
    private SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MMMM-YYYY", Locale.getDefault());
    private SimpleDateFormat DateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
    SimpleDateFormat sdf;
    TextView tx_date, tx_today;
    LinearLayout ly_detail;
    LinearLayout ly_left, ly_right;
    Calendar myCalendar;
    ImageView im_back;
    Date c;
    SimpleDateFormat df;
    String formattedDate;
    String[] dates = new String[0];
    RecyclerView recyclerView;
    TextView tx_item;
//    CalendarAdapter adapter;

    Intent secondIntent = getIntent();
    String kakaoId = secondIntent.getStringExtra("KAKAOID");

    /*************************/
    TextView name, phone, city, date;
    private FirebaseStorage storage;

    /*************************/

    String[] day = {"10", "20", "21", "25", "27"};
    String[] month = {"10", "10", "11", "11", "12"};
    String[] year = {"2018", "2018", "2018", "2018", "2018"};
    private Object NullPointerException;

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        init();
        calendarlistener();
        Setdate();

        storage = FirebaseStorage.getInstance();


        tx_date.setText("" + formattedDate);


        ly_right.setOnClickListener(v -> {
            compactCalendarView.showCalendarWithAnimation();
            compactCalendarView.showNextMonth();
        });

        ly_left.setOnClickListener(v -> {
            compactCalendarView.showCalendarWithAnimation();
            compactCalendarView.showPreviousMonth();
        });

        tx_today.setOnClickListener(v -> {
            Intent intent = new Intent(CalendarActivity.this, CalendarActivity.class);
            startActivity(intent);
            finish();

        });

        im_back.setOnClickListener(v -> finish());
    }

    // variable initialization
    public void init() {
        compactCalendarView = (CompactCalendarView) findViewById(R.id.compactcalendar_view);
        tx_date = (TextView) findViewById(R.id.text);
        ly_left = (LinearLayout) findViewById(R.id.layout_left);
        ly_right = (LinearLayout) findViewById(R.id.layout_right);
        im_back = (ImageView) findViewById(R.id.image_back);
        tx_today = (TextView) findViewById(R.id.text_today);
        ly_detail = (LinearLayout) findViewById(R.id.layout_detail);
    }

    // calendar method
    public void calendarlistener() {
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            private Object NullPointerException;
            String fileName = "";

            @Override
            public void onDayClick(Date dateClicked) {
//                if (DateFormat.format(dateClicked).equals("2018-11-21")) {
//                    Toast.makeText(getApplicationContext(), DateFormat.format(dateClicked) + " This day your brother birth day ", Toast.LENGTH_LONG).show();
//                } else {
//                    Toast.makeText(getApplicationContext(), DateFormat.format(dateClicked) + " In This day no Events Available", Toast.LENGTH_LONG).show();
//                }

//                DateFormat.format(dateClicked)


                StorageReference storageReference = storage.getReference().child(kakaoId + "/"+ DateFormat.format(dateClicked));
                // 이거 비슷하게 응용할 예정
                if (storageReference == NullPointerException) {
                    tx_item.setVisibility(View.VISIBLE);
                    recyclerView.setVisibility(View.GONE);
                } else {
                    tx_item.setVisibility(View.GONE);
                    recyclerView.setVisibility(View.VISIBLE);
                }


            }

            @Override
            public void onMonthScroll(Date firstDayOfNewMonth) {
                compactCalendarView.removeAllEvents();
                Setdate();
                tx_date.setText(simpleDateFormat.format(firstDayOfNewMonth));
            }
        });
    }

    // get current date
    public void Setdate() {
        c = Calendar.getInstance().getTime();
        df = new SimpleDateFormat("yyyy-MM-dd");
        formattedDate = df.format(c);

        compactCalendarView.setUseThreeLetterAbbreviation(true);

        sdf = new SimpleDateFormat("MMMM yyyy");

        myCalendar = Calendar.getInstance();

        for (int j = 0; j < month.length; j++) {
            int mon = Integer.parseInt(month[j]);
            myCalendar.set(Calendar.YEAR, Integer.parseInt(year[j]));
            myCalendar.set(Calendar.MONTH, mon - 1);
            myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day[j]));

            Event event = new Event(Color.RED, myCalendar.getTimeInMillis(), "test");
            compactCalendarView.addEvent(event);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, phone, city, date;

        public MyViewHolder(View view) {
            super(view);
            name = (TextView) view.findViewById(R.id.text_name);
            phone = (TextView) view.findViewById(R.id.text_mobile);
            city = (TextView) view.findViewById(R.id.text_city);
//            date = (TextView) view.findViewById(R.id.text_date);
        }
    }

//    private void downLoadImageFromStorage2() {
//        // 이미지 폴더 경로 참조
//        StorageReference listRef = FirebaseStorage.getInstance().getReference().child("images/");
//
//        if(listRef != NullPointerException){
//            // listAll(): 폴더 내의 모든 이미지를 가져오는 함수
//            Log.e(TAG,"[TEST] NULL 아님");
//            listRef.listAll()
//                    .addOnSuccessListener(listResult -> {
//                        int i = 0;
//                        // 폴더 내의 item이 없어질 때까지 모두 가져온다.
//                        for (StorageReference item : listResult.getItems()) {
//                            // imageview와 textview를 생성할 레이아웃 id 받아오기
//                            LinearLayout layout = (LinearLayout) findViewById(R.id.layout_detail);
//                            // textview 동적생성
////                        TextView tv = new TextView(MainActivity2.this);
////                        tv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
////                        tv.setText(i +". new TextView");
////                        tv.setTextSize(30);
////                        tv.setTextColor(0xff004497);
////                        layout.addView(tv);
//
//                            //imageview 동적생성
////                            ImageView iv = new ImageView(CalendarActivity.this);
////                            iv.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
////                            layout.addView(iv);
//
////                            CardView cv = new CardView(findViewById(R.id.record_view));
////                        i++;
////                        Log.e(TAG, "몇개: " + i);
//
//                            // reference의 item(이미지) url 받아오기
//                            item.getDownloadUrl().addOnCompleteListener(task -> {
//                                if (task.isSuccessful()) {
//                                    // Glide 이용하여 이미지뷰에 로딩
//                                    Glide.with(CalendarActivity.this)
//                                            .load(task.getResult())
//                                            .override(1024, 980)
//                                            .into(iv);
//                                    Toast.makeText(CalendarActivity.this, "그래프가 정상적으로 로드되었습니다.", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    // URL을 가져오지 못하면 토스트 메세지
//                                    Toast.makeText(CalendarActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
//                                }
//                            }).addOnFailureListener(e -> {
//                                // Uh-oh, an error occurred!
//                            });
//                        }
//                    });
//        }
//    }
}
