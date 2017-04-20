package com.example.dimbler.first;

import java.lang.ref.WeakReference;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.ColorRes;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.util.Log;
import java.sql.Time;
import java.util.GregorianCalendar;
import java.util.Locale;
import java.util.concurrent.TimeUnit;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.widget.ToggleButton;

import layout.alarm_show;


public class MainActivity extends AppCompatActivity implements
        AlarmSetup.OnFragmentInteractionListener, alarm_show.ShowFragmentInteractionListener{
    private ComponentName mServiceComponent;
    public TimePicker mTimePicker;
    public static long pickerTime;
    private static final int JOB_ID = 1;
    private static final String TAG = MainActivity.class.getSimpleName();
    public static final String PREFS_NAME = "FirstAttempt";
    public static boolean StartChecked = false;


    public static boolean isActive = false;

    AlarmSetup SetupFragment;

    // Handler for incoming messages from the service.
    private IncomingMessageHandler mHandler;
    public static final String MESSENGER_INTENT_KEY
            = BuildConfig.APPLICATION_ID + ".MESSENGER_INTENT_KEY";
    public static final int MSG_ALARM = 1;
    private String StartDays = "";

    public void ShowInteraction(){
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.FrameFragment, SetupFragment);
        ft.commit();
    }

    // Now we can define the action to take in the activity when the fragment event fires
    public void onFragmentInteraction(Boolean StartCh, String Start) {
        StartChecked = StartCh;
        StartDays = Start;
        Log.d(TAG, Start);
        if (StartChecked ){
            scheduleJob();
        }else{
            cancelAllJobs();
        }
    }

    private static class IncomingMessageHandler extends Handler {

        // Prevent possible leaks with a weak reference.
        private WeakReference<MainActivity> mActivity;

        IncomingMessageHandler(MainActivity activity) {
            super(/* default looper */);
            this.mActivity = new WeakReference<>(activity);
        }

        @Override
        public void handleMessage(Message msg) {
            MainActivity mainActivity = mActivity.get();
            if (mainActivity == null) {
                // Activity is no longer available, exit.
                return;
            }
            Message m;
            switch (msg.what) {
                /*
                 * Receives callback from the service when a job has landed
                 * on the app. Turns on indicator and sends a message to turn it off after
                 * a second.
                 */
                case MSG_ALARM:
                    Fragment ShowFragment = new alarm_show();
                    FragmentActivity activity = (FragmentActivity)mActivity.get();
                    FragmentTransaction ft = activity.getSupportFragmentManager().beginTransaction();
                    ft.replace(R.id.FrameFragment, ShowFragment);
                    //ft.addToBackStack(null);
                    ft.commit();
                    Log.d(TAG, "Message Handle");
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mServiceComponent = new ComponentName(this, AlarmJobService.class);
        mTimePicker = (TimePicker) findViewById(R.id.mTimePicker);
        mTimePicker.setIs24HourView(true);




        mTimePicker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                Calendar calendar = Calendar.getInstance();
                calendar.set(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), 0);
                pickerTime = calendar.getTimeInMillis();

                SimpleDateFormat format = new SimpleDateFormat("EEEE, MMMM d, yyyy 'at' h:mm a");
                Log.d("Picker", format.format(calendar.getTime()));

            }
        });

        isActive = true;

        mHandler = new IncomingMessageHandler(this);

        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        StartChecked = settings.getBoolean("isSheduled", false);
        StartDays = settings.getString("StartDays", "");
        Bundle bundle=new Bundle();
        bundle.putBoolean("isSheduled", StartChecked);
        bundle.putString("StartDays", StartDays);
                //set Fragmentclass Arguments());

        //Add fragment
        SetupFragment = new AlarmSetup();
        SetupFragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();

        ft.add(R.id.FrameFragment, SetupFragment);
        ft.addToBackStack(null);
        ft.commit();

    }

    @Override
    protected void onStart() {
        super.onStart();
        // Start service and provide it a way to communicate with this class.
        Intent startServiceIntent = new Intent(this, AlarmJobService.class);
        Messenger messengerIncoming = new Messenger(mHandler);
        startServiceIntent.putExtra(MESSENGER_INTENT_KEY, messengerIncoming);
        startService(startServiceIntent);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first

    }

        @Override
    protected void onStop() {
        super.onStop();
        isActive = false;
    }

    public void scheduleJob() {

        // We need an Editor object to make preference changes.
        // All objects are from android.context.Context
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, 0);
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean("isSheduled", StartChecked);
        editor.putString("StartDays", StartDays);
        // Commit the edits!
        editor.commit();

        if (StartChecked == true) {

            Calendar now = Calendar.getInstance();

            Calendar schedule = Calendar.getInstance();
            schedule.set(schedule.get(Calendar.YEAR), schedule.get(Calendar.MONTH), schedule.get(Calendar.DAY_OF_MONTH), mTimePicker.getCurrentHour(), mTimePicker.getCurrentMinute(), 0);
            Log.d(TAG, "Schedule day " + String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", schedule));

            if (now.compareTo(schedule) > 0){
                schedule.add(Calendar.DATE, 1);
            }

            for (int i=0; i < 7; i++){
                Calendar testCal = (Calendar)schedule.clone();
                testCal.add(Calendar.DATE, i);
                Log.d(TAG, "testCal day " + String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", testCal));
                String[] separated = StartDays.split(":");
                switch (testCal.get(Calendar.DAY_OF_WEEK)){
                    case 2:
                        if (Arrays.asList(separated).contains("Mon")) { schedule = testCal; i = 9; break; }
                    case 3:
                        if (Arrays.asList(separated).contains("Tue")) { schedule = testCal; i = 9; break; }
                    case 4:
                        if (Arrays.asList(separated).contains("Wen")) { schedule = testCal; i = 9; break; }
                    case 5:
                        if (Arrays.asList(separated).contains("Thu")) { schedule = testCal; i = 9; break; }
                    case 6:
                        if (Arrays.asList(separated).contains("Fri")) { schedule = testCal; i = 9; break; }
                    case 7:
                        if (Arrays.asList(separated).contains("Sat")) { schedule = testCal; i = 9; break; }
                    case 1:
                        if (Arrays.asList(separated).contains("Sun")) { schedule = testCal; i = 9; break; }
                }

            }

            Log.d(TAG, "Schedule day " + String.format("%1$tA %1$tb %1$td %1$tY at %1$tI:%1$tM %1$Tp", schedule));

            // Get date from timepicker
            //Get currentdate
            Calendar currentCalendar = Calendar.getInstance();
            long cTime = currentCalendar.getTimeInMillis();
            //Choise age gap
            long pickerTime;
            pickerTime = schedule.getTimeInMillis();
            if (Long.valueOf(cTime).compareTo(Long.valueOf(pickerTime)) > 0) {
                pickerTime = pickerTime + TimeUnit.DAYS.toMillis(1);
            }
            long timeGap = pickerTime - cTime;

            JobInfo jobInfo = new JobInfo.Builder(JOB_ID, mServiceComponent)
                    .setRequiredNetworkType(JobInfo.NETWORK_TYPE_UNMETERED)
                    .setRequiresDeviceIdle(false)
                    .setPersisted(true)
                    .setRequiresCharging(false)
                    .setMinimumLatency(timeGap)
                    .setOverrideDeadline(timeGap + 60)
                    .build();

            // Schedule job
            Log.d(TAG, "Scheduling job");


            JobScheduler scheduler = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
            int result = scheduler.schedule(jobInfo);
            if (result == JobScheduler.RESULT_SUCCESS) Log.d(TAG, "Job scheduled successfully!");

            if (scheduler.schedule(jobInfo) > 0) {
                Toast.makeText(this,
                        "Successfully scheduled job: " + result,
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this,
                        "RESULT_FAILURE: " + result,
                        Toast.LENGTH_SHORT).show();
            }
            Log.d("GAP", "Time GAP is " + timeGap);

        }

    }

    /**
     * Executed when user clicks on CANCEL ALL.
     */
    public void cancelAllJobs() {
        JobScheduler tm = (JobScheduler) this.getSystemService(Context.JOB_SCHEDULER_SERVICE);
        tm.cancelAll();
        Toast.makeText(this, R.string.all_jobs_cancelled, Toast.LENGTH_SHORT).show();
    }


}
