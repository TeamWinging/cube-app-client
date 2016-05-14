package com.example.rachele.helloworld;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class HelloActivity extends AppCompatActivity {

    TextView buttonAnswer;
    EditText x;
    EditText y;
    String stopID;
    String stopName;
    ArrayList<String> tramsInStop = new ArrayList<String>();
    String chosenTram;
    String[] directions;
    AutoCompleteTextView autocompleteStops;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hello);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        autocompleteStops = (AutoCompleteTextView) findViewById(R.id.autoCompleteStops);

        autocompleteStops.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View arg1, int pos,
                                    long id) {
                stopName = (String) parent.getItemAtPosition(pos);

                getStopID(stopName);

                String t = "Stop: " + stopName + ", id: " + stopID;
                buttonAnswer.setText(t);

            }
        });

        buttonAnswer = (TextView) findViewById(R.id.button_answer);

        Button sendButton = (Button) findViewById(R.id.send_button);

        x = (EditText) findViewById(R.id.x_pos);
        y = (EditText) findViewById(R.id.y_pos);



        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });




        // Get a reference to the AutoCompleteTextView in the layout
        AutoCompleteTextView textView = (AutoCompleteTextView) findViewById(R.id.autoCompleteStops);
        // Get the string array
        //ArrayList<String> allStops =
        String[] stops = getResources().getStringArray(R.array.stops_array);
//        ArrayList<String> stops = getStops("A");
        // Create the adapter and set it to the AutoCompleteTextView
        ArrayAdapter<String> adapter =
                new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, stops);
        textView.setAdapter(adapter);

    }

    private ArrayList<String> getStops(String input) {
        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://api.vasttrafik.se/bin/rest.exe/v1/location.name?format=json&authKey=6511154616&input=" + input;
        final ArrayList<String> stops = new ArrayList<String>();
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONObject locationL;
                        JSONArray stopL;
                        try {
//                            System.out.println("IN THE TRY");
                            locationL = response.getJSONObject("LocationList");
                            stopL =locationL.getJSONArray("StopLocation");
                            for (int i = 0; i < stopL.length(); i++) {
                                JSONObject stop = (JSONObject) stopL.get(i);
                                String s = (String) stop.get("name");
                                System.out.println(s);
                                stops.add(s);
                            }
//                            System.out.println("STOP " + stopL.toString());


                        } catch (Exception e) {
                            //do something
                            System.err.println("ERROR" + e.getMessage());
                            e.printStackTrace();
                        }
//                        Log.d("Response", response.toString());


                    }



                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                       //error
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);


        return stops;
    }




    private void post(final String xString, final String yString) {
        RequestQueue queue = Volley.newRequestQueue(this);
        String url = "http://192.168.1.102";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        // error

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams()
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("x", xString);
                params.put("y", yString);

                return params;
            }
        };
        queue.add(postRequest);
        System.out.println("REQUEST PUTTED");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_hello, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void clickSend(View view) {
        //String text = getStopID("Brunnsparken");
        //buttonAnswer.setText("Button clicked!\nValues: x = " + x.getText() + " y = " + y.getText());
        //buttonAnswer.setText(text);
        post(x.getText().toString(), y.getText().toString());

        ArrayList<String> stops = getStops("Brunn");
        System.out.println("SIZE = " + stops.size());
        for (String s : stops) {
            System.out.println(s);
        }

    }

    public void getStopID (String stop) {
        RequestQueue queue = Volley.newRequestQueue(this);
        //System.out.println(stop);
        String url = "";
        try {
            url = "https://api.vasttrafik.se/bin/rest.exe/v1/location.name?format=json&authKey=6511154616&input=" + URLEncoder.encode(stop, "utf-8");
        } catch (Exception e) {
            System.out.println("error in encoding url");
        }
        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONObject locationL;
                        JSONArray stopL;
                        try {
//                            System.out.println("IN THE TRY");
                            locationL = response.getJSONObject("LocationList");
                            stopL =locationL.getJSONArray("StopLocation");
                            JSONObject stop = (JSONObject) stopL.get(0);
                            stopID = (String) stop.get("id");
                            getTrams(stopID);


                           System.out.println("STOP ID " + stopID);


                        } catch (Exception e) {
                            //do something
                            System.err.println("ERROR" + e.getMessage());
                            e.printStackTrace();
                        }
//                        Log.d("Response", response.toString());


                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println("Error" +error.getMessage());
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);


    }

    public void getTrams(String stopID) {

        RequestQueue queue = Volley.newRequestQueue(this);
        final String url = "https://api.vasttrafik.se/bin/rest.exe/v1/departureBoard?format=json&authKey=6511154616&id=" + stopID;

        // prepare the Request
        JsonObjectRequest getRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                new Response.Listener<JSONObject>()
                {
                    @Override
                    public void onResponse(JSONObject response) {
                        // display response
                        JSONObject depB;
                        JSONArray dep;
                        try {
//                            System.out.println("IN THE TRY");
                            depB = response.getJSONObject("DepartureBoard");
                            dep =depB.getJSONArray("Departure");
                            for (int i = 0; i < dep.length(); i++) {
                                JSONObject stop = (JSONObject) dep.get(i);
                                String t = (String) stop.get("name");

                                if (!tramsInStop.contains(t)) {
                                    tramsInStop.add(t);
                                    System.out.println(t);
                                }
                            }
//                            System.out.println("STOP " + stopL.toString());


                        } catch (Exception e) {
                            //do something
                            System.err.println("ERROR" + e.getMessage());
                            e.printStackTrace();
                        }
//                        Log.d("Response", response.toString());


                    }



                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //error
                    }
                }
        );

        // add it to the RequestQueue
        queue.add(getRequest);


    }




}
