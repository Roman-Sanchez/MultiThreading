package com.example.roman.multithreading;

import android.content.Context;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ProgressBar;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;
import static android.R.layout.simple_list_item_1;

public class MainActivity extends ActionBarActivity {
    private final String fileName = "numbers.txt";
    private final int numItems = 10;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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

    public void loadFile(View view){

        ProgressBar myBar = (ProgressBar) findViewById(R.id.progressBar);
        myBar.setProgress(0);
        new LoadItemsFromFileTask().execute();
    }

    private class LoadItemsFromFileTask extends AsyncTask<Void, Integer, ArrayList<Integer>>{

        @Override
        protected ArrayList<Integer> doInBackground(Void... params) {

            ArrayList<Integer> listOfNums = new ArrayList<>();
            try{
                InputStream reader = openFileInput(fileName);
                if(reader != null){
                    InputStreamReader inputReader = new InputStreamReader(reader);
                    BufferedReader buffReader = new BufferedReader(inputReader);
                    String currentLine = null;
                    double counter = 0;

                    while((currentLine = buffReader.readLine()) != null){
                        listOfNums.add(Integer.valueOf(currentLine));
                        Thread.sleep(250);
                        counter++;

                        publishProgress((int)((counter / numItems) * 100));
                        if (isCancelled()){
                            break;
                        }
                        reader.close();
                    }
                }
            } catch (FileNotFoundException error){
                Log.i("FILENOTFOUND", error.toString());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
            return listOfNums;
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }

        protected void onPostExecute(ArrayList<Integer> results) {
            setResults(results);
        }
    }

    /**
     * clear out the listView and resets the progress bar
     * @param view
     */
    public void listClear(View view){
        ProgressBar myBar = (ProgressBar) findViewById(R.id.progressBar);
        myBar.setProgress(0);

        ListView listView = (ListView) findViewById(R.id.listView);
        ArrayAdapter<Integer> myAdapter = (ArrayAdapter<Integer>) listView.getAdapter();
        if(myAdapter != null){
            myAdapter.clear();
        }
    }

    public void makeFile(View view){
        ProgressBar myBar = (ProgressBar) findViewById(R.id.progressBar);
        myBar.setProgress(0);

        new CreateFile().execute();
    }

    private class CreateFile extends AsyncTask<Void, Integer, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            OutputStreamWriter outputWriter;
            try {
                outputWriter = new OutputStreamWriter(openFileOutput(fileName, Context.MODE_PRIVATE));
            for (Integer i = 1; i <= numItems; i++){
                outputWriter.write(i + "\n");
                Thread.sleep(250);
                publishProgress((int) (((double) i / numItems) * 100));

                if (isCancelled()){
                    break;
                }
            }
            outputWriter.close();
            } catch (InterruptedException | IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onProgressUpdate(Integer... progress) {
            setProgressPercent(progress[0]);
        }
    }

    private void setResults(ArrayList<Integer> results) {
        ListView listView = (ListView) findViewById(R.id.listView);
        listView.setAdapter(new ArrayAdapter<>(this,simple_list_item_1, results));
    }

    private void setProgressPercent(Integer numOfProgress) {
        ProgressBar myBar = (ProgressBar) findViewById(R.id.progressBar);
        myBar.setProgress(numOfProgress);
    }
}
