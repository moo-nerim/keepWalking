package com.example.keepwalking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

//import com.example.KeepWalking.CalendarAdapter;
import com.bumptech.glide.Glide;
import com.example.keepwalking.R;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import static android.content.ContentValues.TAG;

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
}
