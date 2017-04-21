package com.example.dimbler.first;

import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Toast;
import android.widget.ToggleButton;
import com.example.dimbler.first.AlarmJobService;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

import static com.example.dimbler.first.MainActivity.PREFS_NAME;
import static com.example.dimbler.first.MainActivity.StartChecked;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AlarmSetup.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AlarmSetup#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AlarmSetup extends Fragment{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "isSheduled";
    private static final String ARG_PARAM2 = "StartDays";
    public static final String PREFS_NAME = "FirstAttempt";
    private static final String TAG = MainActivity.class.getSimpleName();

    public static boolean StartChecked = false;
    private String StartDays = "";
    private ToggleButton ToggleMonday;
    private ToggleButton ToggleTuesday;
    private ToggleButton ToggleWensday;
    private ToggleButton ToggleThursday;
    private ToggleButton ToggleFriday;
    private ToggleButton ToggleSaturday;
    private ToggleButton ToggleSunday;

    private OnFragmentInteractionListener mListener;

    public AlarmSetup() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     *               * @param param2 Parameter 1.
     * @return A new instance of fragment AlarmSetup.
     */
    // TODO: Rename and change types and number of parameters
    public static AlarmSetup newInstance(Boolean param1, String param2) {
        AlarmSetup fragment = new AlarmSetup();
        Bundle args = new Bundle();
        args.putBoolean(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            StartChecked = getArguments().getBoolean(ARG_PARAM1);
            StartDays = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onStop() {
        super.onStop();
    }

    final CompoundButton.OnCheckedChangeListener toggleButtonChangeListener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(isChecked) {
                StartChecked = true;
                StartDays = "";
                if (ToggleMonday.isChecked()) { StartDays += ":Mon"; }
                if (ToggleTuesday.isChecked()) { StartDays += ":Tue"; }
                if (ToggleWensday.isChecked()) { StartDays += ":Wen"; }
                if (ToggleThursday.isChecked()) { StartDays += ":Thu"; }
                if (ToggleFriday.isChecked()) { StartDays += ":Fri"; }
                if (ToggleSaturday.isChecked()) { StartDays += ":Sat"; }
                if (ToggleSunday.isChecked()) { StartDays += ":Sun"; }
            }else{
                StartChecked = false;
            }
            if (mListener != null) {
                mListener.onFragmentInteraction(StartChecked, StartDays);
            }
        }
    };


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_alarm_setup, container, false);

        //StartDays
        ToggleMonday = (ToggleButton) view.findViewById(R.id.checkmonday);
        ToggleTuesday = (ToggleButton) view.findViewById(R.id.checktuesday);
        ToggleWensday = (ToggleButton) view.findViewById(R.id.checkwensday);
        ToggleThursday = (ToggleButton) view.findViewById(R.id.checkthursday);
        ToggleFriday = (ToggleButton) view.findViewById(R.id.checkfriday);
        ToggleSaturday = (ToggleButton) view.findViewById(R.id.checksaturday);
        ToggleSunday = (ToggleButton) view.findViewById(R.id.checksunday);

        if (StartDays != null) {
            String[] separated = StartDays.split(":");
            for (String sDay : separated) {
                switch (sDay) {
                    case "Mon":
                        ToggleMonday.setChecked(true);
                        break;
                    case "Tue":
                        ToggleTuesday.setChecked(true);
                        break;
                    case "Wen":
                        ToggleWensday.setChecked(true);
                        break;
                    case "Thu":
                        ToggleThursday.setChecked(true);
                        break;
                    case "Fri":
                        ToggleFriday.setChecked(true);
                        break;
                    case "Sat":
                        ToggleSaturday.setChecked(true);
                        break;
                    case "Sun":
                        ToggleSunday.setChecked(true);
                        break;
                }
            }
        }

        //StartChecked
        ToggleButton start_stop_Button = (ToggleButton) view.findViewById(R.id.start_stop_button);

        if (StartChecked){
            start_stop_Button.setChecked(true);
        }
        start_stop_Button.setOnCheckedChangeListener(toggleButtonChangeListener);
        return  view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Boolean StartChecked, String uri);
    }
}
