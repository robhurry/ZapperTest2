package com.example.rob.zappertest.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.TextView;

import com.example.rob.zappertest.MainActivity;
import com.example.rob.zappertest.R;
import com.example.rob.zappertest.models.PersonModel;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * A fragment representing a list of Items.
 * <p/>
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p/>
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class PersonList extends Fragment implements AbsListView.OnItemClickListener {

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;
    private ProgressDialog dialog;

    private List<PersonModel> peopleLoaded = new ArrayList<PersonModel>();

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
    private ListAdapter mAdapter;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public PersonList() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_item, container, false);

        mListView = (AbsListView) view.findViewById(android.R.id.list);
        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);
        peopleLoaded.clear();

        //Display a progress bar
        dialog = new ProgressDialog(MainActivity.activity);
        dialog.setMessage("Loading");
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        dialog.show();

        //Use the communication helper to call the url for person details
        CommunicationHelper commsHelper = new CommunicationHelper(mListView, peopleLoaded, dialog);
        commsHelper.execute();
        if (commsHelper.mException != null) {
            setEmptyText(commsHelper.mException.getMessage());
        }

        return view;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(peopleLoaded.get(position).id);


        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
    public void setEmptyText(CharSequence emptyText) {
        View emptyView = mListView.getEmptyView();

        if (emptyView instanceof TextView) {
            ((TextView) emptyView).setText(emptyText);
        }
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(String id);
    }

    public class CommunicationHelper extends AsyncTask<String, Void, JSONArray> {
        public Exception mException = null;
        private ListAdapter mAdapter;
        private List<PersonModel> personContentIn;
        private AbsListView mListView;
        private ProgressDialog dialog;

        public CommunicationHelper(AbsListView mListView, List<PersonModel> personContentIn, ProgressDialog dialog) {
            this.mListView = mListView;
            this.personContentIn = personContentIn;
            this.dialog = dialog;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            this.mException = null;
        }

        @Override
        protected JSONArray doInBackground(String... params) {
            StringBuilder urlString = new StringBuilder();
            urlString.append("http://demo3124542.mockable.io/candidatetest/person/list");

            HttpURLConnection urlConnection = null;
            URL url = null;
            JSONArray object = null;

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
                object = (JSONArray) new JSONTokener(response).nextValue();
            } catch (Exception e) {
                this.mException = e;
            }

            return (object);
        }

        @Override
        protected void onPostExecute(JSONArray result) {
            try {
                super.onPostExecute(result);
                for (int i = 0; i < result.length(); i++) {
                    JSONObject item = result.optJSONObject(i);
                    //Create a Person model from each list item and add it to the list to display
                    PersonModel newPerson = new PersonModel(item.getString("id"), item.getString("name"));
                    this.personContentIn.add(i, newPerson);
                }
                mAdapter = new ArrayAdapter<PersonModel>(getActivity(),
                        android.R.layout.simple_list_item_1, android.R.id.text1, personContentIn);
                // Set the adapter
                ((AdapterView<ListAdapter>) mListView).setAdapter(mAdapter);
                dialog.dismiss();
            } catch (Exception ex) {
                mException = ex;
                dialog.dismiss();
            }
        }

    }
}



