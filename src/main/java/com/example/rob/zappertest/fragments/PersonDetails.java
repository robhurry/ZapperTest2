package com.example.rob.zappertest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.rob.zappertest.MainActivity;
import com.example.rob.zappertest.R;

import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * A simple {@link Fragment} subclass.
 */
public class PersonDetails extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_Id = "param1";

    //The person id selected
    private String paramid;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramid The Id of the person to load.
     * @return A new instance of fragment PersonDetails.
     */
    // TODO: Rename and change types and number of parameters
    public static PersonDetails newInstance(String paramid) {
        PersonDetails fragment = new PersonDetails();
        Bundle args = new Bundle();
        args.putString(ARG_Id, paramid);
        fragment.setArguments(args);
        return fragment;
    }

    public PersonDetails() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            paramid = getArguments().getString(ARG_Id);
        }
    }

    ProgressDialog dialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_person_details, container, false);
        //Set the text views with the content of the JSON
        TextView txtAge = (TextView) view.findViewById(R.id.txtAge);
        TextView txtCountry = (TextView) view.findViewById(R.id.txtCountry);
        TextView txtJob = (TextView) view.findViewById(R.id.txtJob);
        TextView txtDescription = (TextView) view.findViewById(R.id.txtDescription);
        TextView txtAddress = (TextView) view.findViewById(R.id.txtAddress);
        //Display a progress bar
        dialog = new ProgressDialog(MainActivity.activity);
        dialog.setMessage("Loading");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();
        //Use the communication helper to call the url for person details
        CommunicationHelper commsHelper = new CommunicationHelper(paramid, txtAge, txtJob, txtDescription, txtAddress, txtCountry, dialog);
        commsHelper.execute();
        if (commsHelper.mException != null) {
            TextView txtEmpty = (TextView) view.findViewById(android.R.id.empty);
            txtEmpty.setText(commsHelper.mException.getMessage());
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public class CommunicationHelper extends AsyncTask<String, Void, JSONObject> {
        public Exception mException = null;
        private String id;
        private TextView txtAge;
        TextView txtJob;
        TextView txtDescription;
        TextView txtCountry;
        TextView txtAddress;
        private ProgressDialog dialog;

        public CommunicationHelper(String id, TextView txtAge, TextView txtJob, TextView txtDescription, TextView txtCountry, TextView txtAddress, ProgressDialog dialog) {
            this.txtAddress = txtAddress;
            this.txtAge = txtAge;
            this.txtCountry = txtCountry;
            this.id = id;
            this.txtJob = txtJob;
            this.txtDescription = txtDescription;
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {

            this.mException = null;
            super.onPreExecute();
        }

        @Override
        protected JSONObject doInBackground(String... params) {
            StringBuilder urlString = new StringBuilder();
            urlString.append("http://demo3124542.mockable.io/candidatetest/person/" + id);

            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONObject object = null;

            try {
                url = new URL(urlString.toString());
                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();
                InputStream inStream = null;
                inStream = urlConnection.getInputStream();
                BufferedReader bReader = new BufferedReader(new InputStreamReader(inStream));
                String temp, response = "";
                while ((temp = bReader.readLine()) != null)
                    response += temp;
                bReader.close();
                inStream.close();
                urlConnection.disconnect();
                object = (JSONObject) new JSONTokener(response).nextValue();
            } catch (Exception e) {
                this.mException = e;
            }

            return (object);
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            try {
                super.onPostExecute(result);
                //Set the relevant text views with the JSON asynchronously retrieved
                txtAge.setText(result.getString("age"));
                txtJob.setText(result.getString("job"));
                txtDescription.setText(result.getString("description"));
                txtCountry.setText(result.getString("country"));
                txtAddress.setText(result.getString("address"));
                //Hide the progress dialog
                dialog.dismiss();

            } catch (Exception ex) {
                this.mException = ex;
                dialog.dismiss();
            }
        }

    }
}



