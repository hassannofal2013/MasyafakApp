package com.tirefiesama.masifk;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.os.Handler;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;

import android.widget.TextView;
import android.widget.Toast;


import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.savvi.rangedatepicker.CalendarPickerView;
import com.savvi.rangedatepicker.SubTitle;


import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.Month;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FreeDaysActivity extends AppCompatActivity {
    private DatabaseReference mDatabase;
    String citykey, adkey, currentmonth, chosenmonth, chosenyear,StChosenDays;


    TextView  choosen_days, choosendaysis, pay,choose;

    CalendarPickerView calendarPicker;
    String strdate,month1,year1,day1;
    ArrayList<Date> DeactevatedList = new ArrayList<>();
    Calendar calendar,calendar1,calendar2,calendar3;
    String userId;
    FirebaseAuth mAuth;
    private Toolbar mtoolbar;

    ValueEventListener valueEventListener,valueEventListener1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_free_days);


        SharedPreferences preferences = getSharedPreferences("sharedFile",MODE_PRIVATE);
        citykey = preferences.getString("citykey", "citykey");
        adkey = preferences.getString("adkey", "adkey");

        mAuth = FirebaseAuth.getInstance();
        userId = mAuth.getUid();

        mtoolbar = findViewById(R.id.toolbar_tool);
        setSupportActionBar(mtoolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.freedays));


        calendarPicker = findViewById(R.id.calendar_view);




        long date = System.currentTimeMillis();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MMMM");
        String dateString = dateFormat.format(date);


        choosen_days = findViewById(R.id.choosendays);
        choosendaysis = findViewById(R.id.textView33);
        pay = findViewById(R.id.textView32);


        if (dateString.equals("يناير")) {
            currentmonth = "January";
        }
        if (dateString.equals("فبرابر")) {
            currentmonth = "February";
        }
        if (dateString.equals("مارس")) {
            currentmonth = "March";
        }
        if (dateString.equals("أبريل")) {
            currentmonth = "April";
        }
        if (dateString.equals("مايو")) {
            currentmonth = "May";
        }
        if (dateString.equals("يونيو")) {
            currentmonth = "June";
        }
        if (dateString.equals("يوليو")) {
            currentmonth = "July";
        }
        if (dateString.equals("أغسطس")) {
            currentmonth = "August";
        }
        if (dateString.equals("سبتمبر")) {
            currentmonth = "September";
        }
        if (dateString.equals("أكتوبر")) {
            currentmonth = "October";
        }
        if (dateString.equals("نوفمبر")) {
            currentmonth = "November";
        }
        if (dateString.equals("ديسمبر")) {
            currentmonth = "December";
        } else {
            currentmonth = dateString;
        }


        pay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


               mDatabase.removeEventListener(valueEventListener);
                mDatabase.removeEventListener(valueEventListener1);

                if (choosen_days.getText().equals("") || choosen_days.getText().equals(null))
                {
                    Toast.makeText(FreeDaysActivity.this, getString(R.string.choosefirst), Toast.LENGTH_SHORT).show();
                } else {

                     StChosenDays = choosen_days.getText().toString();

                     AlertDialog alert=     new AlertDialog.Builder(FreeDaysActivity.this)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .setTitle(getString(R.string.sureBooking))
                    .setMessage(getString(R.string.suretoBook)+"\n"+StChosenDays)
                    .setPositiveButton(getString(R.string.yes), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which)
                        {


                            MakeDaysBusy(citykey,adkey,StChosenDays);
                            copytorented(citykey,adkey,StChosenDays);


                              sendConfermationEmail();

                            Toast.makeText(FreeDaysActivity.this, getString(R.string.bookingconfirmed), Toast.LENGTH_SHORT).show();

                            Intent intent = new Intent(FreeDaysActivity.this,RentedRoomDetailes.class);
                            startActivity(intent);
                            finish();
                            Toast.makeText(FreeDaysActivity.this, getString(R.string.checkEmail), Toast.LENGTH_SHORT).show();


                        }
                    })
                    .setNegativeButton(getString(R.string.no), null)
                    .show();

                }
            }
        });


    }




    @Override
    protected void onStart() {
        super.onStart();

        calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, +0);
        month1 = calendar.getTime().toString().substring(4, 7);
        year1 = calendar.getTime().toString().substring(30, 34);
        day1 = calendar.getTime().toString().substring(8, 10);




        readSchedualDataInCurrentMonth(day1, month1, year1);

        calendar1 = Calendar.getInstance();
        calendar1.add(Calendar.MONTH, +1);
        month1 = calendar1.getTime().toString().substring(4, 7);
        year1 = calendar1.getTime().toString().substring(30, 34);


        readSchedualData(month1, year1);

        calendar2 = Calendar.getInstance();
        calendar2.add(Calendar.MONTH, +2);
        month1 = calendar2.getTime().toString().substring(4, 7);
        year1 = calendar2.getTime().toString().substring(30, 34);

        readSchedualData(month1, year1);

        calendar3 = Calendar.getInstance();
        calendar3.add(Calendar.MONTH, +3);
        month1 = calendar3.getTime().toString().substring(4, 7);
        year1 = calendar3.getTime().toString().substring(30, 34);

        readSchedualData(month1, year1);

        createCalenderPicker();


        calendarPicker.setOnDateSelectedListener(new CalendarPickerView.OnDateSelectedListener() {
            @Override
            public void onDateSelected(Date date) {

                choosen_days.setVisibility(View.VISIBLE);
                choosendaysis.setVisibility(View.VISIBLE);

                chosenyear = String.valueOf(calendarPicker.getSelectedDate()).substring(30, 34);

                chosenmonth = String.valueOf(calendarPicker.getSelectedDate()).substring(0, 3);
                List<Date> selectedDays = calendarPicker.getSelectedDates();
                StringBuilder builder = new StringBuilder();
                for (Date date1 : selectedDays) {
                    String strdate1 = String.valueOf(date1).substring(4, 10);
                    String styear = String.valueOf(date1).substring(30, 34);

                    builder.append(strdate1 + " " + styear + "\n");

                }
                choosen_days.setText(builder);

            }

            @Override
            public void onDateUnselected(Date date)
            {
                StringBuilder builder = new StringBuilder();

                String strdate1 = String.valueOf(date).substring(4, 10);
                String styear = String.valueOf(date).substring(30, 34);
                builder.append(strdate1 + " " + styear + "\n");


                String oldSt = choosen_days.getText().toString();
                String newSt = oldSt.replace(builder.toString(),"");
                choosen_days.setText(newSt);

            }
        });



    }


    private void readSchedualDataInCurrentMonth(String day, String month, String year) {


        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey)
                .child("ads").child(adkey);


        int dayvalue = Integer.parseInt(day);

       valueEventListener = new ValueEventListener() {
           @Override
           public void onDataChange(@NonNull DataSnapshot snapshot)
           {


               if (dayvalue < 1) {
                   if (snapshot.child("calender").child(month + "_" + year).child("01").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("01").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("1", month, year);
                       }


                   }
               }
               if (dayvalue < 2) {
                   if (snapshot.child("calender").child(month + "_" + year).child("02").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("02").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("2", month, year);
                       }
                   }
               }
               if (dayvalue < 3) {
                   if (snapshot.child("calender").child(month + "_" + year).child("03").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("03").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("3", month, year);
                       }
                   }
               }

               if (dayvalue < 4) {
                   if (snapshot.child("calender").child(month + "_" + year).child("04").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("04").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("4", month, year);
                       }
                   }
               }

               if (dayvalue < 5) {
                   if (snapshot.child("calender").child(month + "_" + year).child("05").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("05").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("5", month, year);
                       }
                   }
               }
               if (dayvalue < 6) {
                   if (snapshot.child("calender").child(month + "_" + year).child("06").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("06").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("6", month, year);
                       }
                   }
               }

               if (dayvalue < 7) {
                   if (snapshot.child("calender").child(month + "_" + year).child("07").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("07").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("7", month, year);
                       }
                   }
               }

               if (dayvalue < 8) {
                   if (snapshot.child("calender").child(month + "_" + year).child("08").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("08").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("8", month, year);
                       }

                   }
               }

               if (dayvalue < 9) {
                   if (snapshot.child("calender").child(month + "_" + year).child("09").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("09").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("9", month, year);
                       }

                   }
               }


               if (dayvalue < 10) {
                   if (snapshot.child("calender").child(month + "_" + year).child("10").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("10").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("10", month, year);
                       }
                   }
               }

               if (dayvalue < 11) {
                   if (snapshot.child("calender").child(month + "_" + year).child("11").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("11").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("11", month, year);
                       }

                   }
               }

               if (dayvalue < 12) {
                   if (snapshot.child("calender").child(month + "_" + year).child("12").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("12").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("12", month, year);
                       }
                   }
               }

               if (dayvalue < 13) {
                   if (snapshot.child("calender").child(month + "_" + year).child("13").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("13").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("13", month, year);
                       }

                   }
               }

               if (dayvalue < 14) {
                   if (snapshot.child("calender").child(month + "_" + year).child("14").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("14").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("14", month, year);
                       }
                   }
               }


               if (dayvalue < 15) {
                   if (snapshot.child("calender").child(month + "_" + year).child("15").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("15").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("15", month, year);
                       }
                   }
               }

               if (dayvalue < 16) {
                   if (snapshot.child("calender").child(month + "_" + year).child("16").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("16").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("16", month, year);
                       }
                   }
               }


               if (dayvalue < 17) {
                   if (snapshot.child("calender").child(month + "_" + year).child("17").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("17").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("17", month, year);
                       }
                   }
               }

               if (dayvalue < 18) {
                   if (snapshot.child("calender").child(month + "_" + year).child("18").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("18").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("18", month, year);
                       }
                   }
               }

               if (dayvalue < 19) {
                   if (snapshot.child("calender").child(month + "_" + year).child("19").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("19").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("19", month, year);
                       }
                   }
               }

               if (dayvalue < 20) {
                   if (snapshot.child("calender").child(month + "_" + year).child("20").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("20").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("20", month, year);
                       }
                   }
               }

               if (dayvalue < 21) {
                   if (snapshot.child("calender").child(month + "_" + year).child("21").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("21").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("21", month, year);
                       }
                   }
               }

               if (dayvalue < 22) {
                   if (snapshot.child("calender").child(month + "_" + year).child("22").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("22").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("22", month, year);
                       }
                   }
               }

               if (dayvalue < 23) {
                   if (snapshot.child("calender").child(month + "_" + year).child("23").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("23").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("23", month, year);
                       }
                   }
               }

               if (dayvalue < 24) {
                   if (snapshot.child("calender").child(month + "_" + year).child("24").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("24").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("24", month, year);
                       }
                   }
               }

               if (dayvalue < 25) {
                   if (snapshot.child("calender").child(month + "_" + year).child("25").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("25").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("25", month, year);
                       }
                   }
               }

               if (dayvalue < 26) {
                   if (snapshot.child("calender").child(month + "_" + year).child("26").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("26").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("26", month, year);
                       }
                   }
               }

               if (dayvalue < 27) {
                   if (snapshot.child("calender").child(month + "_" + year).child("27").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("27").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("27", month, year);
                       }
                   }
               }

               if (dayvalue < 28) {
                   if (snapshot.child("calender").child(month + "_" + year).child("28").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("28").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("28", month, year);
                       }
                   }
               }

               if (dayvalue < 29) {
                   if (snapshot.child("calender").child(month + "_" + year).child("29").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("29").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("29", month, year);
                       }
                   }
               }

               if (dayvalue < 30) {
                   if (snapshot.child("calender").child(month + "_" + year).child("30").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("30").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("30", month, year);
                       }
                   }
               }

               if (dayvalue < 31) {
                   if (snapshot.child("calender").child(month + "_" + year).child("31").exists()) {
                       String value = snapshot.child("calender").child(month + "_" + year).child("31").getValue().toString();
                       if (value.equals("booked") || value.equals("confirmed")) {
                           MakeRedHighLight("31", month, year);
                       }
                   }
               }


               createCalenderPicker();

           }

           @Override
           public void onCancelled(@NonNull DatabaseError error) {

           }
       };

       mDatabase.addListenerForSingleValueEvent(valueEventListener);



    }


    private void createCalenderPicker()
    {
        final Calendar CurrentMonth = Calendar.getInstance();
        CurrentMonth.add(Calendar.DAY_OF_MONTH, 1);
        final Calendar Next3Month = Calendar.getInstance();
        Next3Month.add(Calendar.MONTH, +3);

        calendarPicker.init(CurrentMonth.getTime(), Next3Month.getTime())
                .inMode(CalendarPickerView.SelectionMode.MULTIPLE)
                //  .withDeactivateDates(list);  //day off every weak
                //  .withSubTitles(getSubTitles());
                .withHighlightedDates(DeactevatedList);

        SharedPreferences.Editor editor = getSharedPreferences("sharedFile", MODE_PRIVATE).edit();
        editor.putBoolean("fromcity", false);
        editor.apply();



    }










    private void readSchedualData(String month,String year) {


        mDatabase = FirebaseDatabase.getInstance().getReference().child("cities").child(citykey)
                .child("ads").child(adkey);

         valueEventListener1 = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot)
            {
                if(snapshot.child("calender").child(month+"_"+year).child("01").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("01").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")) { MakeRedHighLight("1",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("02").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("02").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("2",month,year);}
                }
                if(snapshot.child("calender").child(month+"_"+year).child("03").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("03").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("3",month,year);}
                }
                if(snapshot.child("calender").child(month+"_"+year).child("04").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("04").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("4",month,year);}
                }
                if(snapshot.child("calender").child(month+"_"+year).child("05").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("05").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("5",month,year);}
                }
                if(snapshot.child("calender").child(month+"_"+year).child("06").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("06").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("6",month,year);}
                }
                if(snapshot.child("calender").child(month+"_"+year).child("07").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("07").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("7",month,year);}
                }
                if(snapshot.child("calender").child(month+"_"+year).child("08").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("08").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("8",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("09").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("09").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("9",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("10").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("10").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("10",month,year);}
                }

                if(snapshot.child("calender").child(month+"_"+year).child("11").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("11").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("11",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("12").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("12").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("12",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("13").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("13").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("13",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("14").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("14").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("14",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("15").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("15").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("15",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("16").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("16").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("16",month,year);}
                }

                if(snapshot.child("calender").child(month+"_"+year).child("17").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("17").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("17",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("18").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("18").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("18",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("19").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("19").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("19",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("20").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("20").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("20",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("21").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("21").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("21",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("22").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("22").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("22",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("23").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("23").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("23",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("24").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("24").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("24",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("25").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("25").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("25",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("26").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("26").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("26",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("27").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("27").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("27",month,year);}

                }

                if(snapshot.child("calender").child(month+"_"+year).child("28").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("28").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("28",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("29").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("29").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("29",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("30").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("30").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("30",month,year);}

                }
                if(snapshot.child("calender").child(month+"_"+year).child("31").exists())
                {
                    String value = snapshot.child("calender").child(month+"_"+year).child("31").getValue().toString();
                    if(value.equals("booked") || value.equals("confirmed")){ MakeRedHighLight("31",month,year);}

                }

                createCalenderPicker();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        };

      mDatabase.addListenerForSingleValueEvent(valueEventListener1);

    }

    private void MakeRedHighLight(String day,String month,String year)
    {
        String MonNum = "null";
        if(month.equals("Jan")){MonNum="1";}
        if(month.equals("Fab")){MonNum="2";}
        if(month.equals("Mar")){MonNum="3";}
        if(month.equals("Apr")){MonNum="4";}
        if(month.equals("May")){MonNum="5";}
        if(month.equals("Jun")){MonNum="6";}
        if(month.equals("Jul")){MonNum="7";}
        if(month.equals("Aug")){MonNum="8";}
        if(month.equals("Sep")){MonNum="9";}
        if(month.equals("Oct")){MonNum="10";}
        if(month.equals("Nov")){MonNum="11";}
        if(month.equals("Dec")){MonNum="12";}

       // String strdate2 = "26-9-2021";
        strdate = day+"-"+MonNum+"-"+year;
        SimpleDateFormat dateformat = new SimpleDateFormat("dd-MM-yyyy");
        try {
            Date newdate = dateformat.parse(strdate);
            DeactevatedList.add(newdate);

        } catch (ParseException e) { e.printStackTrace(); }

    }



    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        Intent intent = new Intent(FreeDaysActivity.this,RoomDetailes.class);
        startActivity(intent);
        finish();
        return super.onOptionsItemSelected(item);
    }





    private void MakeDaysBusyOwnerPublishedAds(String ownerkey, String citykey, String roomkey, String stChosenDays)
    {


        int len = stChosenDays.length();
        int NoDayes;

        for( NoDayes = len/12;NoDayes>0;NoDayes--)
        {
            String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

            String month = month_day.substring(0,3);
            String day = month_day.substring(4,6);
            String year = month_day.substring(7,11);
            String monthkey = month+"_"+year;

            DatabaseReference adOwnertRef = FirebaseDatabase.getInstance().getReference().
                    child("users").child(ownerkey).child("published").child(roomkey).child("calender")
                    .child(monthkey);

            DatabaseReference userRented = FirebaseDatabase.getInstance().getReference().
                    child("users").child(userId).child("rented").child(roomkey).child("calender").child(userId)
                    .child(monthkey);

            Map<String, Object> updates = new HashMap<String,Object>();
            updates.put(day, "confirmed");

            //userRented.updateChildren(updates);
            adOwnertRef .updateChildren(updates).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) 
                {
                    Intent intent = new Intent(FreeDaysActivity.this,RentedRoomDetailes.class);
                    startActivity(intent);
                    finish();
                }
            });

        }
    }

    private void MakeDaysBusy(String citykey, String roomkey, String stChosenDays)
    {
        DatabaseReference adRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(roomkey).child("calender");
        int len = stChosenDays.length();
        int NoDayes;

        for( NoDayes = len/12;NoDayes>0;NoDayes--)
        {
            String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

            String month = month_day.substring(0,3);
            String day = month_day.substring(4,6);
            String year = month_day.substring(7,11);
            String monthkey = month+"_"+year;
            adRef.child(monthkey).child(day).setValue("confirmed");

        }


    }




    private void copytorented(String city, String adkey,String stChosenDays)
    {

     DatabaseReference   adRef = FirebaseDatabase.getInstance().getReference().
                child("cities").child(citykey).child("ads").child(adkey);

      DatabaseReference  rentedRef = FirebaseDatabase.getInstance().getReference()
                .child("users").child(userId).child("rented");
        Calendar calendar = Calendar.getInstance();

        calendar.add(Calendar.MONTH, +0);
        String month = calendar.getTime().toString().substring(4,7);
        String  year = calendar.getTime().toString().substring(30,34);
        String  day = calendar.getTime().toString().substring(8,10);
        String month_year = month+"_"+year;

        DatabaseReference  monitorRef = FirebaseDatabase.getInstance().getReference()
                .child("transactions").child("booking").child(month_year).child(day).child(adkey);


        rentedRef.addListenerForSingleValueEvent(new ValueEventListener() {
          @Override
          public void onDataChange(@NonNull DataSnapshot snapshot)
          {
              if(snapshot.child(adkey).exists())
              {
                  Toast.makeText(FreeDaysActivity.this, "exist", Toast.LENGTH_SHORT).show();

                  int len = stChosenDays.length();
                  int NoDayes;

                  for( NoDayes = len/12;NoDayes>0;NoDayes--)
                  {
                      String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

                      String month = month_day.substring(0,3);
                      String day = month_day.substring(4,6);
                      String year = month_day.substring(7,11);
                      String monthkey = month+"_"+year;
                      rentedRef.child(adkey).child("calender").child(userId).child(monthkey).child(day).setValue("confirmed");

                  }
              }
              else
                  {

                      Calendar calFordDate = Calendar.getInstance();
                      SimpleDateFormat Date = new SimpleDateFormat("dd-MMMM-yyyy");
                      SimpleDateFormat time = new SimpleDateFormat("hh-mm-ss");
                      String  saveCurrentDate = Date.format(calFordDate.getTime()) + time.format(calFordDate.getTime());
                      rentedRef.child(adkey).child("citykey").setValue(city);
                      rentedRef.child(adkey).child("rentingtime").setValue(saveCurrentDate);
                      monitorRef.child("citykey").setValue(city);
                       monitorRef.child("rentingtime").setValue(saveCurrentDate);

                      adRef.addListenerForSingleValueEvent(new ValueEventListener()
                      {
                          @Override
                          public void onDataChange(@NonNull DataSnapshot dataSnapshot)
                          {


                              for (DataSnapshot info : dataSnapshot.getChildren())
                              {
                                  String infoName = info.getKey();
                                  String infoValue = dataSnapshot.child(infoName).getValue().toString();
                                  HashMap hashMap = new HashMap();
                                  hashMap.put(infoName,infoValue);
                                  rentedRef.child(adkey).updateChildren(hashMap);
                                  monitorRef.updateChildren(hashMap);

                              }

                              for (DataSnapshot info : dataSnapshot.child("ownerkey").getChildren())
                              {
                                  String infoName = info.getKey();
                                  String infoValue = dataSnapshot.child("ownerkey").child(infoName).getValue().toString();

                                  HashMap hashMap = new HashMap();
                                  hashMap.put(infoName,infoValue);
                                  rentedRef.child(adkey).child("ownerkey").updateChildren(hashMap);


                              }
                              monitorRef.child("ownerkey").setValue(  dataSnapshot.child("ownerkey").child("key").getValue() );



                              int len = stChosenDays.length();
                              int NoDayes;

                              for( NoDayes = len/12;NoDayes>0;NoDayes--)
                              {
                                  String month_day = stChosenDays.substring((11*NoDayes+1+(NoDayes-1))-12,11*NoDayes+(NoDayes-1));

                                  String month = month_day.substring(0,3);
                                  String day = month_day.substring(4,6);
                                  String year = month_day.substring(7,11);
                                  String monthkey = month+"_"+year;
                                  rentedRef.child(adkey).child("calender").child(userId).child(monthkey).child(day).setValue("confirmed");
                                  monitorRef.child("calender").child(userId).child(monthkey).child(day).setValue("confirmed");

                              }
                              monitorRef.child("adminkey").setValue(userId);
                              monitorRef.child("ownerkey").setValue(userId);

                          }
                          @Override
                          public void onCancelled(@NonNull DatabaseError databaseError) {

                          }
                      });

                  }
          }
          @Override
          public void onCancelled(@NonNull DatabaseError error) {

          }
      });


    }



    private void sendConfermationEmail() {



        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference().child("users")
                .child(userId);
        DatabaseReference ownerRef = FirebaseDatabase.getInstance().getReference().child("cities")
                .child(citykey).child("ads").child(adkey).child("ownerkey");


        ownerRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                if (snapshot.exists()) {
                    if (snapshot.child("name").exists()) {
                        String ownerName = snapshot.child("name").getValue().toString();
                        if (snapshot.child("phone").exists()) {
                            String ownerPhone = snapshot.child("phone").getValue().toString();
                            if (snapshot.child("email").exists()) {
                                String ownerEmail = snapshot.child("email").getValue().toString();
                                if (snapshot.child("key").exists()) {
                                    String ownerKey = snapshot.child("key").getValue().toString();
                                    MakeDaysBusyOwnerPublishedAds(ownerKey, citykey, adkey, StChosenDays);

                                    userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            if (snapshot.exists()) {
                                                String useremail = snapshot.child("email").getValue().toString();
                                                String userphone = snapshot.child("phone").getValue().toString();
                                                String username = snapshot.child("name").getValue().toString();

                                                String subject = getResources().getString(R.string.confirmed);
                                                String message = getResources().getString(R.string.bookingconfirmed) + "\n" +
                                                        getResources().getString(R.string.fordays) + "\n" +
                                                        StChosenDays + "\n" +
                                                        getResources().getString(R.string.contactwithowner) + "\n" +
                                                        ownerName + "\n" +
                                                        ownerPhone + "\n" +
                                                        getResources().getString(R.string.adnuis) + "  " + adkey;

                                                JavaMailAPI javaMailAPI = new JavaMailAPI(FreeDaysActivity.this, useremail, subject, message);
                                                javaMailAPI.execute();


                                                String subject1 = getResources().getString(R.string.confirmed);
                                                String message1 = getResources().getString(R.string.bookingconfirmed) + "\n" +
                                                        getResources().getString(R.string.fordays) + "\n" +
                                                        StChosenDays + "\n" +
                                                        getResources().getString(R.string.contactwithuser) + "\n" +
                                                        username + "\n" +
                                                        userphone + "\n" +
                                                        getResources().getString(R.string.adnuis) + "  " + adkey;

                                                JavaMailAPI javaMailAPI1 = new JavaMailAPI(FreeDaysActivity.this, ownerEmail, subject1, message1);
                                                javaMailAPI1.execute();


                                                String subject2 = getResources().getString(R.string.confirmed);
                                                String message2 = getResources().getString(R.string.bookingconfirmed) + "\n" +
                                                        getResources().getString(R.string.fordays) + "\n" +
                                                        StChosenDays + "\n" +
                                                        getResources().getString(R.string.contactwithowner) + "\n" +
                                                        ownerName + "\n" +
                                                        ownerPhone + "\n" +

                                                        getResources().getString(R.string.contactwithuser) + "\n" +
                                                        username + "\n" +
                                                        userphone + "\n" +
                                                        getResources().getString(R.string.adnuis) + "  " + adkey;

                                                JavaMailAPI javaMailAPI2 = new JavaMailAPI(FreeDaysActivity.this, "masifkapp@gmail.com", subject2, message2);
                                                javaMailAPI2.execute();


                                            }
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                } else {
                                    Toast.makeText(FreeDaysActivity.this, "No owner key", Toast.LENGTH_SHORT).show();
                                }

                            } else {
                                Toast.makeText(FreeDaysActivity.this, "No owner email", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(FreeDaysActivity.this, "No owner phone", Toast.LENGTH_SHORT).show();
                        }

                    } else {
                        Toast.makeText(FreeDaysActivity.this, "No owner name", Toast.LENGTH_SHORT).show();
                    }


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }







}