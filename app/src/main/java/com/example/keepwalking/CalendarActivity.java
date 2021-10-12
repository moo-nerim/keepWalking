package com.example.keepwalking;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
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
    RecyclerView recyclerView;
    TextView tx_item;

    private FirebaseStorage storage;

    ArrayList<String> day = new ArrayList<>();
    ArrayList<String> month = new ArrayList<>();
    ArrayList<String> year = new ArrayList<>();

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        RecyclerView recyclerView = findViewById(R.id.recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getApplicationContext());
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);

        storage = FirebaseStorage.getInstance();

        init();
        calendarlistener();
        Setdate();

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

        /************* 하단바 *************/
        BottomNavigationView bottomNav = findViewById(R.id.bottom_menu3);

        // item selection part
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home:
                        final Intent intent = new Intent(CalendarActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                        overridePendingTransition(R.anim.slide_left_enter, R.anim.slide_left_exit);
                        return true;
                }
                return false;
            }

        });
        /************* 하단바 *************/
    }

    // variable initialization
    public void init() {
        compactCalendarView = findViewById(R.id.compactcalendar_view);
        tx_date = findViewById(R.id.text);
        ly_left = findViewById(R.id.layout_left);
        ly_right = findViewById(R.id.layout_right);
        im_back = findViewById(R.id.image_back);
        tx_today = findViewById(R.id.text_today);
        ly_detail = findViewById(R.id.layout_detail);
        tx_item = findViewById(R.id.text_item);
        recyclerView = findViewById(R.id.recyclerview);
    }

    // calendar method
    public void calendarlistener() {
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {

            @Override
            public void onDayClick(Date dateClicked) {
                downLoadImageFromStorage(dateClicked);
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

        getEventDateFromStorage();
        myCalendar = Calendar.getInstance();

        for (int j = 0; j < month.size(); j++) {
            int mon = Integer.parseInt(month.get(j));
            myCalendar.set(Calendar.YEAR, Integer.parseInt(year.get(j)));
            myCalendar.set(Calendar.MONTH, mon - 1);
            myCalendar.set(Calendar.DAY_OF_MONTH, Integer.parseInt(day.get(j)));

            Event event = new Event(Color.RED, myCalendar.getTimeInMillis(), "test");
            compactCalendarView.addEvent(event);
        }
    }


    private void downLoadImageFromStorage(Date dateClicked) {
        String kakaoid = ((GlobalApplication) getApplication()).getKakaoID();
        StorageReference storageReference = storage.getReference().child(kakaoid + "/" + DateFormat.format(dateClicked));

        List<RecyclerItem> items = new ArrayList<>();
        storageReference.listAll()
                .addOnSuccessListener(listResult -> {
                    if (listResult.getItems().isEmpty()) {
                        tx_item.setVisibility(View.VISIBLE);
                        recyclerView.setVisibility(View.GONE);
                    } else {
                        tx_item.setVisibility(View.GONE);
                        recyclerView.setVisibility(View.VISIBLE);

                        for (StorageReference item : listResult.getItems()) {
//                                Log.e("파일이름",item.getName());
                            String time = item.getName().split("_")[0];
                            String result = item.getName().split("_")[1];

                            RecyclerItem result_item = new RecyclerItem(DateFormat.format(dateClicked), time, result);
                            items.add(result_item);

                            recyclerView.setAdapter(new CalendarAdapter(getApplicationContext(), items, R.layout.activity_calendar, kakaoid));
                        }
                    }
                });
    }

    private void getEventDateFromStorage() {
        String kakaoid = ((GlobalApplication) getApplication()).getKakaoID();
        StorageReference storageReference = storage.getReference().child(kakaoid + "/");

        storageReference.listAll()
                .addOnSuccessListener(listResult -> {
                    ArrayList folders = (ArrayList) listResult.getPrefixes();

                    for (Object folder : folders) {
                        int len = folder.toString().split("/").length;
                        String measureDate = folder.toString().split("/")[len - 1];
                        String measureYear = measureDate.split("-")[0];
                        String measureMonth = measureDate.split("-")[1];
                        String measureDay = measureDate.split("-")[2];

                        year.add(measureYear);
                        month.add(measureMonth);
                        day.add(measureDay);
                    }
                });
    }
}
