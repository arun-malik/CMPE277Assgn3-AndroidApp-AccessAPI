package com.dubber.android;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.text.DecimalFormat;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class HelloAndroidActivity extends Activity {

	/**
	 * Called when the activity is first created.
	 * @param savedInstanceState If the activity is being re-initialized after 
	 * previously being shut down then this Bundle contains the data it most 
	 * recently supplied in onSaveInstanceState(Bundle). <b>Note: Otherwise it is null.</b>
	 */
	private Button btnBrowseSJSU,btnGetWeatherUpdates;
	private EditText edtCity;
	private TextView txtWeatherValue;
	private ToggleButton tglCelciusFaranheit;
	private static String URL = "http://api.openweathermap.org/data/2.5/weather?q=";
	String outputDataInCelcius, outputDataInFarahenheit;
	final String DEGREE  = "\u00b0";
	//private static String API_URL ="http://api.openweathermap.org/data/2.1/forecast/city?q=";

	@Override
	public void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		btnBrowseSJSU = (Button) findViewById(R.id.btnBrowseSJSU) ;
		btnGetWeatherUpdates = (Button) findViewById(R.id.btnGetWeatherUpdates) ;
		edtCity = (EditText) findViewById(R.id.edtCity) ;
		txtWeatherValue = (TextView) findViewById(R.id.txtWeatherValue);
		tglCelciusFaranheit = (ToggleButton) findViewById(R.id.tglCelciusFaranheit);

		btnBrowseSJSU.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (view == btnBrowseSJSU)
				{
					txtWeatherValue.setText("Browse SJSU button Clicked !!!");
					Uri uri = Uri.parse("http://www.sjsu.edu");
					Intent intent = new Intent(Intent.ACTION_VIEW, uri);
					startActivity(intent);
				}
				return;
			}
		});

		btnGetWeatherUpdates.setOnClickListener(new View.OnClickListener() {

			public void onClick(View view) {
				if (view == btnGetWeatherUpdates)
				{	
					String city = edtCity.getText().toString().trim().replace(" ", "%20");
					new getWeather().execute(city);
				}
			}
		});

		tglCelciusFaranheit.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
				if(null != outputDataInCelcius || !TextUtils.isEmpty(outputDataInCelcius))
				{
					if (isChecked) {
						txtWeatherValue.setText(outputDataInFarahenheit);
						
					} else {
						txtWeatherValue.setText(outputDataInCelcius);
					}
				}
			}
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(com.dubber.android.R.menu.main, menu);
		return true;
	}

	private class getWeather extends AsyncTask<String, Void, String> {


		protected String doInBackground(String... params) {

			String fullAPI_URL = URL + params[0];

			HttpClient httpClientObject = new DefaultHttpClient();
			HttpGet httpGetCall = new HttpGet(fullAPI_URL);

			String jsonResult = null;

			try {

				HttpEntity httpEntityObject = httpClientObject.execute(httpGetCall).getEntity();

				if (httpEntityObject != null) {

					InputStream inputStreamObject = httpEntityObject.getContent();
					Reader readerObject = new InputStreamReader(inputStreamObject);
					BufferedReader bufferedReaderObject = new BufferedReader(readerObject);

					StringBuilder jsonResultStringBuilder = new StringBuilder();
					String readLine = null;

					while ((readLine = bufferedReaderObject.readLine()) != null) {
						jsonResultStringBuilder.append(readLine + "\n");
					}

					jsonResult = jsonResultStringBuilder.toString();
				}

			} catch (ClientProtocolException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}

			return jsonResult;
		}

		protected void onPostExecute(String weatherData) {

			DecimalFormat df = new DecimalFormat("#.##"); 
			
			try {
				System.out.println("Weather Data:" + weatherData);
				if(null != weatherData)
				{
					JSONObject jObj = new JSONObject(weatherData);
					JSONObject mainJsonObject = jObj.getJSONObject("main");
					JSONObject windJsonObject = jObj.getJSONObject("wind");
					//JSONObject cloudJsonObject = jObj.getJSONObject("cloud");
					
					String cityName = jObj.getString("name");
					
					Double tempInKelvin =  Double.parseDouble(mainJsonObject.getString("temp"));
					String humidity = mainJsonObject.getString("humidity");
					String atmosphericPessure = mainJsonObject.getString("pressure");
					
					String windSpeed = windJsonObject.getString("speed");
					//String cloudiness = cloudJsonObject.getString("all");


					Double tempInCelcius = Double.valueOf(df.format( tempInKelvin - 273.15) );
					Double tempInFahrenheit = Double.valueOf(df.format( ((tempInKelvin - 273) * 1.8 ) + 32 ));

					outputDataInCelcius = "City: "+ cityName +"\nTemp: " + tempInCelcius + DEGREE +"Celcius \nHumidity: "+ humidity + "\nWind Speed: "+ windSpeed + "\nAtmospeheric Pressure: " + atmosphericPessure ;//+"\n Cloudiness: "+ cloudiness; 
					outputDataInFarahenheit = "City: "+ cityName +"\nTemp: " + tempInFahrenheit + DEGREE +"Fahrenheit \nHumidity: "+ humidity + "\nWind Speed: "+ windSpeed + "\nAtmospeheric Pressure: " + atmosphericPessure;// +"\n Cloudiness: "+ cloudiness; 


					txtWeatherValue.setText(outputDataInCelcius);
				}
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}



		}
	}

}

