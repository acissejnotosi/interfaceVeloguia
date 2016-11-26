package com.example.jsampaio.interfaceveloguia;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.UUID;

public class MapsCorridaLivreActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener,
        LocationListener, OnMapReadyCallback{

    //************************************************************************************************
    /**
     * Inicialização de Variáveis e constantes
     */
    //***********************************************************************************************
    private GoogleMap mMap;

    /**
     * Retorna um objeto do tipo polyline
     */

    PolylineOptions rectOptions;

    protected static final String TAG = "location-updates-sample";

    /**
     * The desired interval for location updates. Inexact. Updates may be more or less frequent.
     */
    public static final long UPDATE_INTERVAL_IN_MILLISECONDS = 1000;

    /**
     * The fastest rate for active location updates. Exact. Updates will never be more frequent
     * than this value.
     */
    public static final long FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS =
            UPDATE_INTERVAL_IN_MILLISECONDS / 2;

    // Keys for storing activity state in the Bundle.
    protected final static String REQUESTING_LOCATION_UPDATES_KEY = "requesting-location-updates-key";
    protected final static String LOCATION_KEY = "location-key";
    protected final static String LAST_UPDATED_TIME_STRING_KEY = "last-updated-time-string-key";

    /**
     * Provides the entry point to Google Play services.
     */
    protected GoogleApiClient mGoogleApiClient;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */
    protected  int vetorPolyline = 0;

    /**
     * Stores parameters for requests to the FusedLocationProviderApi.
     */

    public LocationRequest mLocationRequest;

    /**
     * Represents a geographical location.
     */
    protected Location mCurrentLocation;

    // UI Widgets.
    protected Button mStartUpdatesButton;
    protected Button mStopUpdatesButton;
    protected TextView mLastUpdateTimeTextView;
    protected TextView mLatitudeTextView;
    protected TextView mLongitudeTextView;
    protected TextView mAltitudeTextView;
    protected TextView mSpeedTextView;

    /**
     * Strings
     */
    protected String mLatitudeLabel;
    protected String mLongitudeLabel;
    protected String mLastUpdateTimeLabel;
    protected String mAltitudeLabel;
    protected String mSpeedLabel;

    /**
     * floats
     */
    protected Float mLatitudeDouble;
    protected Float mLongitudeDouble;
    protected Float mAltitudeDouble;
    protected Float mSpeedFloat;

    /*
        graphico
     */
    private LineChart mHeartRateChart;

    private Queue<Entry> mReading;

    /**
     * Tracks the status of the location updates request. Value changes when the user presses the
     * Start Updates and Stop Updates buttons.
     */
    protected Boolean mRequestingLocationUpdates;

    /**
     * Time when the location was updated represented as a String.
     */
    protected String mLastUpdateTime;

    /**
     *Centraliza a camera
     */
    CameraPosition camera_position;

    /**
     *  Coordenada Curitiba
     */

    private final LatLng cordCuritiba = new LatLng(-25.431816, -49.279511);



    /**
     * Coordenada Casa Jessica
     */
    private final LatLng cordCasaJessica = new LatLng(-25.411959, -49.227481);

    /**
     * Uma polyline para ser usada para indicar a rota
     */
    Polyline polyline;

    /* Vetor contendo dados de latitude*/
    protected ArrayList<Float> latVector;

    /*Vetor contendo dados de longitude*/
    protected ArrayList<Float> lngVector;

    /*Vetor contendo dados de altitude*/
    protected ArrayList<Float> altVector;

    /* Vetor contendo dados de velocidade entre dois pontos*/
    protected ArrayList<Float> speedVector;

    /* Vetor contendo dados de pulsação*/
    protected ArrayList<Integer> pulseVector;

    /**
     *Formato Hora
     */
    SimpleDateFormat dateFormat_hora = new SimpleDateFormat("HH:mm:ss");

    /**
     * Strings
     */
    protected String mLastUpdateTimeString;


    /*
        Variáveis e constantes Bluetooth
     */

    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mbtnCalibrarSensor;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    public BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<String> mBTArrayAdapter;
    private ListView mDevicesListView;

    //private LineChart mHeartRateChart;
    //private Queue<Entry> mReading;


    protected String modoDeRecebimento;

    public Handler mHandler; // Our main handler that will receive callback notifications
    public ConnectedThread mConnectedThread; // bluetooth background worker thread to send and receive data
    public BluetoothSocket mBTSocket = null; // bi-directional client-to-client data path

    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier


    // #defines for identifying shared types between calling functions
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int MESSAGE_READ = 2; // used in bluetooth handler to identify message update
    private final static int CONNECTING_STATUS = 3; // used in bluetooth handler to identify message status

    //Nome do meu dispositivo
    private static final String HTC_MEDIA = "HC-05";

    /*
    Variável indicadora:
        0 recebe nenhum dado
        1 Recebe dados da média dos batimentos
        2 recebe dados de calibração

     */

    protected int indicadorDeTipoDeDadosRecebidos = 0;
    //************************************************************************************************
    /**
     * Inicio da Activity
     */
    //***********************************************************************************************
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps_corrida_livre);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        mStartUpdatesButton = (Button) findViewById(R.id.start_updates_button);
        mStopUpdatesButton = (Button) findViewById(R.id.stop_updates_button);
        /*mLatitudeTextView = (TextView) findViewById(R.id.latitude_text);
        mLongitudeTextView = (TextView) findViewById(R.id.longitude_text);
        mLastUpdateTimeTextView = (TextView) findViewById(R.id.last_update_time_text);
        mAltitudeTextView = (TextView) findViewById(R.id.altitude_text);
        mSpeedTextView = (TextView) findViewById(R.id.speed_text);*/
        mHeartRateChart = (LineChart)findViewById(R.id.heartRateChart);
        mbtnCalibrarSensor = (Button) findViewById(R.id.btnCalibrarSensor);
        mLatitudeLabel = getResources().getString(R.string.latitude_label);
        mLongitudeLabel = getResources().getString(R.string.longitude_label);
        mLastUpdateTimeLabel = getResources().getString(R.string.last_update_time_label);
        mAltitudeLabel = getResources().getString(R.string.altitude_label);
        mSpeedLabel = getResources().getString(R.string.speed_label);
        mRequestingLocationUpdates = false;
        mLastUpdateTime = "";
        mBluetoothStatus = (TextView) findViewById(R.id.bluetoothStatus);
        mReadBuffer = (TextView) findViewById(R.id.readBuffer);
        mScanBtn = (Button) findViewById(R.id.scan);
        mOffBtn = (Button) findViewById(R.id.off);
        mDiscoverBtn = (Button) findViewById(R.id.discover);
        mListPairedDevicesBtn = (Button) findViewById(R.id.PairedBtn);
        mBTArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio
        mDevicesListView = (ListView) findViewById(R.id.devicesListView);
        mDevicesListView.setAdapter(mBTArrayAdapter); // assign model to view
        mDevicesListView.setOnItemClickListener(mDeviceClickListener);

        modoDeRecebimento = new String("<mandnad>\0");
        // Update values using data stored in the Bundle.
        updateValuesFromBundle(savedInstanceState);

        // Kick off the process of building a GoogleApiClient and requesting the LocationServices
        // API.
        buildGoogleApiClient();

        constructMapDataVectors();
        //Atualização de dados recebidos pelo bluetooth
        dadosBluetooth();

    }


    /*
     * Inicializa Vetores de Dados
     */

    protected void constructMapDataVectors()
    {
        latVector = new ArrayList<>();
        lngVector = new ArrayList<>();
        altVector = new ArrayList<>();
        speedVector = new ArrayList<>();
        pulseVector = new ArrayList<>();

    }
    /**
     * Updates fields based on data stored in the bundle.
     *
     * @param savedInstanceState The activity state saved in the Bundle.
     */
    private void updateValuesFromBundle(Bundle savedInstanceState) {
        Log.i(TAG, "Updating values from bundle");
        if (savedInstanceState != null) {
            // Update the value of mRequestingLocationUpdates from the Bundle, and make sure that
            // the Start Updates and Stop Updates buttons are correctly enabled or disabled.
            if (savedInstanceState.keySet().contains(REQUESTING_LOCATION_UPDATES_KEY)) {
                mRequestingLocationUpdates = savedInstanceState.getBoolean(
                        REQUESTING_LOCATION_UPDATES_KEY);
                setButtonsEnabledState();
            }

            // Update the value of mCurrentLocation from the Bundle and update the UI to show the
            // correct latitude and longitude.
            if (savedInstanceState.keySet().contains(LOCATION_KEY)) {
                // Since LOCATION_KEY was found in the Bundle, we can be sure that mCurrentLocation
                // is not null.
                mCurrentLocation = savedInstanceState.getParcelable(LOCATION_KEY);
            }

            // Update the value of mLastUpdateTime from the Bundle and update the UI.
            if (savedInstanceState.keySet().contains(LAST_UPDATED_TIME_STRING_KEY)) {
                mLastUpdateTime = savedInstanceState.getString(LAST_UPDATED_TIME_STRING_KEY);
            }
            updateUI();
        }
    }

    /**
     * Builds a GoogleApiClient. Uses the {@code #addApi} method to request the
     * LocationServices API.
     */
    protected synchronized void buildGoogleApiClient() {
        Log.i(TAG, "Building GoogleApiClient");
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks((GoogleApiClient.ConnectionCallbacks) MapsCorridaLivreActivity.this)
                .addOnConnectionFailedListener((GoogleApiClient.OnConnectionFailedListener) this)
                .addApi(LocationServices.API)
                .build();
        createLocationRequest();
    }

    /**
     * Sets up the location request. Android has two location request settings:
     * {@code ACCESS_COARSE_LOCATION} and {@code ACCESS_FINE_LOCATION}. These settings control
     * the accuracy of the current location. This sample uses ACCESS_FINE_LOCATION, as defined in
     * the AndroidManifest.xml.
     * <p/>
     * When the ACCESS_FINE_LOCATION setting is specified, combined with a fast update
     * interval (5 seconds), the Fused Location Provider API returns location updates that are
     * accurate to within a few feet.
     * <p/>
     * These settings are appropriate for mapping applications that show real-time location
     * updates.
     */
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();

        // Sets the desired interval for active location updates. This interval is
        // inexact. You may not receive updates at all if no location sources are available, or
        // you may receive them slower than requested. You may also receive updates faster than
        // requested if other applications are requesting location at a faster interval.
        mLocationRequest.setInterval(UPDATE_INTERVAL_IN_MILLISECONDS);

        // Sets the fastest rate for active location updates. This interval is exact, and your
        // application will never receive updates faster than this value.
        mLocationRequest.setFastestInterval(FASTEST_UPDATE_INTERVAL_IN_MILLISECONDS);

        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    /**
     * Handles the Start Updates button and requests start of location updates. Does nothing if
     * updates have already been requested.
     */
    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public void startUpdatesButtonHandler(View view) {


        if (!mRequestingLocationUpdates) {
            if(mBTAdapter.getBluetoothLeScanner()!=null) {
                rectOptions = new PolylineOptions();
                mRequestingLocationUpdates = true;
                setButtonsEnabledState();
                startLocationUpdates();
                mDiscoverBtn.setEnabled(false);
                mListPairedDevicesBtn.setEnabled(false);
                mOffBtn.setEnabled(false);
                mScanBtn.setEnabled(false);
                modoDeRecebimento = "<batimed>\0";
                mConnectedThread.write(modoDeRecebimento);
                mHeartRateChart.setVisibility(View.INVISIBLE);
              //  mMap.clear();
            }else{
                    Toast.makeText(getApplicationContext(), "Bluetooth não está ligado", Toast.LENGTH_SHORT).show();
            }

        }
    }

    public void geraGraficosHandler(View view)
    {
        passarDadosParaOutraAtividade();


    }


    /**
     * Handles the Stop Updates button, and requests removal of location updates. Does nothing if
     * updates were not previously requested.
     */
    public void stopUpdatesButtonHandler(View view) {
        if (mRequestingLocationUpdates) {
            mRequestingLocationUpdates = false;
            setButtonsEnabledState();
          //  stopLocationUpdates();
            //passarDadosParaOutraAtividade();
            setLatLngFinal();
            mDiscoverBtn.setEnabled(true);
            mListPairedDevicesBtn.setEnabled(true);
            mOffBtn.setEnabled(true);
            mScanBtn.setEnabled(false);
            modoDeRecebimento = "<mandnad\0>";
            mConnectedThread.write(modoDeRecebimento);
            mHeartRateChart.setVisibility(View.INVISIBLE);



        }


    }

    public void setLatLngFinal()
    {
        addLastMarkerRoute((double)mLatitudeDouble, (double)mLongitudeDouble);
    }




    /**
     * Requests location updates from the FusedLocationApi.
     */
    protected void startLocationUpdates() {
        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, (LocationListener) this);

    }

    /**
     * Ensures that only one button is enabled at any time. The Start Updates button is enabled
     * if the user is not requesting location updates. The Stop Updates button is enabled if the
     * user is requesting location updates.
     */
    private void setButtonsEnabledState() {
        if (mRequestingLocationUpdates) {
            mStartUpdatesButton.setEnabled(false);
            mStopUpdatesButton.setEnabled(true);
        } else {
            mStartUpdatesButton.setEnabled(true);
            mStopUpdatesButton.setEnabled(false);
        }
    }

    /**
     * Updates the latitude, the longitude, and the last location time in the UI.
     */
    private void updateUI() {

        if(mLatitudeDouble != (float)mCurrentLocation.getLatitude() || mLongitudeDouble !=
                (float)mCurrentLocation.getLongitude()) {
            //Atribuicao das coordenadas Lat Lgn
            mLatitudeDouble = (float) mCurrentLocation.getLatitude();
            mLongitudeDouble = (float) mCurrentLocation.getLongitude();
            mAltitudeDouble = (float) mCurrentLocation.getAltitude();
            mSpeedFloat = mCurrentLocation.getSpeed();


            latVector.add(mLatitudeDouble);

            lngVector.add(mLongitudeDouble);

            altVector.add(mAltitudeDouble);

            speedVector.add(mSpeedFloat);


            rectOptions.add(new LatLng(mLatitudeDouble, mLongitudeDouble));

            //    polyline.setColor(Color.YELLOW); //EU AINDA NÃO SEI ARRUMAR ESSA PORRA DE COR
            polyline = mMap.addPolyline(rectOptions.geodesic(true).color(Color.BLUE).width(12));

            moveCamera((double) mLatitudeDouble, (double) mLongitudeDouble);

            if (latVector.size() == 1) {
                addFirstMarkerRoute((double) mLatitudeDouble, (double) mLongitudeDouble);


            }
        }
    }
/*      }
        addPolyline();

        moveCamera((double)mLatitudeDouble, (double)mLongitudeDouble);
        mMap.animateCamera(CameraUpdateFactory.zoomTo(11));
        //stop location updates
        if (mGoogleApiClient != null) {
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);
        }
*/

       // dadosBluetooth();


    /**
     * Adicioanar uma polyline, em que o início é um marker
     */



    /**
     * Mover a câmera em um tempo, no zoom desejado e nas coordenas calculadas.
     * Parâmetros: lat -> latitude
     *             lgn -> longitude
     * Retorno:    NULL
     */
    protected  void moveCamera(Double lat, Double lgn)
    {

        //mMap.moveCamera(CameraUpdateFactory.newLatLng(cordUTFPR));
        LatLng coordenada = new LatLng(lat, lgn);
        camera_position = new CameraPosition(coordenada,20, 0,0);
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position), 1000, null);


    }

    /**
     * Adiciona um Marker na posição indicada com a cor desejada
     * Parâmetros: lat -> latitude
     *             lgn -> longitude
     * Retorno:    NULL
     */

    protected void addLastMarkerRoute(Double lat, Double lgn)
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lgn)).title("Posição Atual")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.end_green)));

    }


    /**
     * Adiciona um Marker na posição indicada com a cor desejada
     * Parâmetros: lat -> latitude
     *             lgn -> longitude
     * Retorno:    NULL
     */

    protected void addFirstMarkerRoute(Double lat, Double lgn)
    {
        mMap.addMarker(new MarkerOptions().position(new LatLng(lat, lgn)).title("Posição Atual")
                .icon(BitmapDescriptorFactory.fromResource(R.drawable.start_blue)));

    }

    /**
     * Removes location updates from the FusedLocationApi.
     */
    protected void stopLocationUpdates() {
        // It is a good practice to remove location requests when the activity is in a paused or
        // stopped state. Doing so helps battery performance and is especially
        // recommended in applications that request frequent location updates.

        // The final argument to {@code requestLocationUpdates()} is a LocationListener
        // (http://developer.android.com/reference/com/google/android/gms/location/LocationListener.html).
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, (LocationListener) this);


    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    public void onResume() {
        super.onResume();
        // Within {@code onPause()}, we pause location updates, but leave the
        // connection to GoogleApiClient intact.  Here, we resume receiving
        // location updates if the user has requested them.

        if (mGoogleApiClient.isConnected() && mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        // Stop location updates to save battery, but don't disconnect the GoogleApiClient object.
        if (mGoogleApiClient.isConnected()) {
            stopLocationUpdates();
        }
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();

        super.onStop();
    }

    /**
     * Runs when a GoogleApiClient object successfully connects.
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Log.i(TAG, "Connected to GoogleApiClient");

        // If the initial location was never previously requested, we use
        // FusedLocationApi.getLastLocation() to get it. If it was previously requested, we store
        // its value in the Bundle and check for it in onCreate(). We
        // do not request it again unless the user specifically requests location updates by pressing
        // the Start Updates button.
        //
        // Because we cache the value of the initial location in the Bundle, it means that if the
        // user launches the activity,
        // moves to a new location, and then changes the device orientation, the original location
        // is displayed as the activity is re-created.
        if (mCurrentLocation == null) {
            if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
            mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
            // updateUI();


        }

        // If the user presses the Start Updates button before GoogleApiClient connects, we set
        // mRequestingLocationUpdates to true (see startUpdatesButtonHandler()). Here, we check
        // the value of mRequestingLocationUpdates and if it is true, we start location updates.
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    /**
     * Callback that fires when the location changes.
     */
    @Override
    public void onLocationChanged(Location location) {
        mCurrentLocation = location;
        mLastUpdateTime = DateFormat.getTimeInstance().format(new Date());
        updateUI();
        Toast.makeText(this, "Localização Atualizada",
                Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionSuspended(int cause) {
        // The connection to Google Play services was lost for some reason. We call connect() to
        // attempt to re-establish the connection.
        Log.i(TAG, "Conexão suspendida");
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        // Refer to the javadoc for ConnectionResult to see what error codes might be returned in
        // onConnectionFailed.
        Log.i(TAG, "Conexão Falhou: ConnectionResult.getErrorCode() = " + result.getErrorCode());
    }


    /**
     * Stores activity data in the Bundle.
     */
    public void onSaveInstanceState(Bundle savedInstanceState) {
        savedInstanceState.putBoolean(REQUESTING_LOCATION_UPDATES_KEY, mRequestingLocationUpdates);
        savedInstanceState.putParcelable(LOCATION_KEY, mCurrentLocation);
        savedInstanceState.putString(LAST_UPDATED_TIME_STRING_KEY, mLastUpdateTime);
        super.onSaveInstanceState(savedInstanceState);
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        //Add a marker in Sydney and move the camera


        // mMap.addMarker(new MarkerOptions().position().title("Minha posição"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(cordUTFPR));
        //camera_position = new CameraPosition(cordCuritiba,10, 0,0);
        //mMap.animateCamera(CameraUpdateFactory.newCameraPosition(camera_position), 3000, null);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void handleNewLocation(Location location) {
        Log.d(TAG, location.toString());
        double currentLatitude = location.getLatitude();
        double currentLongitude = location.getLongitude();

        mLatitudeDouble =(float) currentLatitude;
        mLongitudeDouble = (float)currentLongitude;

        LatLng latLng = new LatLng(currentLatitude, currentLongitude);


        MarkerOptions options = new MarkerOptions()
                .position(latLng)
                .title("Eu estou Aqui!");
        mMap.addMarker(options);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(latLng));
    }




    protected void passarDadosParaOutraAtividade()
    {

        int i;

        float[] altArray = new float[altVector.size()];
        float[] spdArray = new float[speedVector.size()];
        float altMax = altVector.get(0);
        float altMin = altVector.get(0);
        float veloMax = speedVector.get(0);
        float veloMin = speedVector.get(0);

        int[] pulseArray = new int[pulseVector.size()];
        int pulseMax = pulseVector.get(0);
        int pulseMin = pulseVector.get(0);


        for(i = 0; i < altArray.length; i++) {
            altArray[i] = altVector.get(i);
            if(altMax < altArray[i]) altMax = altArray[i];
            if(altMin > altArray[i]) altMin = altArray[i];
        }

        for( i = 0; i < spdArray.length; i++) {
            spdArray[i] = speedVector.get(i);
            if(veloMax < spdArray[i]) veloMax = spdArray[i];
            if(veloMin > spdArray[i]) veloMin = spdArray[i];
        }


        for(i = 0; i < pulseArray.length; i++) {
            pulseArray[i] = pulseVector.get(i);
            if(pulseMax < pulseArray[i]) pulseMax = pulseArray[i];
            if(pulseMin > pulseArray[i]) pulseMin = pulseArray[i];
        }

        Intent nextActivity = new Intent(this, GeraGraficos.class);



        nextActivity.putExtra("pulseArray", pulseArray);
        nextActivity.putExtra("pulseMax", pulseMax);
        nextActivity.putExtra("pulseMin", pulseMin);
        nextActivity.putExtra("alt_dados", altArray);
        nextActivity.putExtra("alt_max", altMax);
        nextActivity.putExtra("alt_min", altMin);
        nextActivity.putExtra("velo_dados", spdArray);
        nextActivity.putExtra("velo_max", veloMax);
        nextActivity.putExtra("velo_min", veloMin);

        startActivity(nextActivity);

    }





    //************************************************************************************************
    /**
     * Liga Bluetooth, Dados dos Sensores
     */
    //***********************************************************************************************

    public void bluetoothOn(View view){
        if (!mBTAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
            mBluetoothStatus.setText("Bluetooth enabled");
            Toast.makeText(getApplicationContext(),"Bluetooth turned on",Toast.LENGTH_SHORT).show();

        }
        else{
            Toast.makeText(getApplicationContext(),"Bluetooth is already on", Toast.LENGTH_SHORT).show();
        }
    }

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data){
        // Check which request we're responding to
        if (requestCode == REQUEST_ENABLE_BT) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                // The user picked a contact.
                // The Intent's data Uri identifies which contact was selected.
                mBluetoothStatus.setText("Enabled");
            }
            else
                mBluetoothStatus.setText("Disabled");
        }
    }

    public void bluetoothOff(View view){
        mBTAdapter.disable(); // turn off
        mBluetoothStatus.setText("Bluetooth disabled");
        Toast.makeText(getApplicationContext(),"Bluetooth turned Off", Toast.LENGTH_SHORT).show();
    }

    public void conectarAoHC05(View view){

        BluetoothDevice device = findBondedDeviceByName(mBTAdapter, HTC_MEDIA);

        //If either is null, just return. The errors have already been logged
        if (device == null) {
            return;
        }

    }

    public void discover(View view){
        // Check if the device is already discovering
        if(mBTAdapter.isDiscovering()){
            mBTAdapter.cancelDiscovery();
            Toast.makeText(getApplicationContext(),"A descoberta parou",Toast.LENGTH_SHORT).show();
        }
        else{
            if(mBTAdapter.isEnabled()) {
                mBTArrayAdapter.clear(); // clear items
                mBTAdapter.startDiscovery();
                Toast.makeText(getApplicationContext(), "Descoberta Começou", Toast.LENGTH_SHORT).show();
                registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
            }
            else{
                Toast.makeText(getApplicationContext(), "Bluetooth não ligado", Toast.LENGTH_SHORT).show();
            }
        }
    }

    final BroadcastReceiver blReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if(BluetoothDevice.ACTION_FOUND.equals(action)){
                BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());
                mBTArrayAdapter.notifyDataSetChanged();
            }
        }
    };

    public void listPairedDevices(View view){
        mPairedDevices = mBTAdapter.getBondedDevices();
        if(mBTAdapter.isEnabled()) {
            // put it's one to the adapter
            for (BluetoothDevice device : mPairedDevices)
                mBTArrayAdapter.add(device.getName() + "\n" + device.getAddress());

            Toast.makeText(getApplicationContext(), "Mostrando dispositivos pairados", Toast.LENGTH_SHORT).show();
        }
        else
            Toast.makeText(getApplicationContext(), "Bluetooth não está ligado", Toast.LENGTH_SHORT).show();
    }

    public AdapterView.OnItemClickListener mDeviceClickListener = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int arg2, long arg3) {

            if(!mBTAdapter.isEnabled()) {
                Toast.makeText(getBaseContext(), "Bluetooth não está ligado", Toast.LENGTH_SHORT).show();
                return;
            }

            mBluetoothStatus.setText("Connecting...");
            // Get the device MAC address, which is the last 17 chars in the View
            String info = ((TextView) v).getText().toString();
            final String address = info.substring(info.length() - 17);
            final String name = info.substring(0,info.length() - 17);

            // Spawn a new thread to avoid blocking the GUI one
            new Thread()
            {
                public void run() {
                    boolean fail = false;

                    BluetoothDevice device = mBTAdapter.getRemoteDevice(address);

                    try {
                        mBTSocket = createBluetoothSocket(device);
                    } catch (IOException e) {
                        fail = true;
                        Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                    }
                    // Establish the Bluetooth socket connection.
                    try {
                        mBTSocket.connect();
                    } catch (IOException e) {
                        try {
                            fail = true;
                            mBTSocket.close();
                            mHandler.obtainMessage(CONNECTING_STATUS, -1, -1)
                                    .sendToTarget();
                        } catch (IOException e2) {
                            //insert code to deal with this
                            Toast.makeText(getBaseContext(), "Socket creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                    if(fail == false) {
                        mConnectedThread = new ConnectedThread(mBTSocket);
                        mConnectedThread.start();
                        mConnectedThread.write("<mandnad>\0");
                        mHandler.obtainMessage(CONNECTING_STATUS, 1, -1, name)
                                    .sendToTarget();


                        }
                }
            }.start();
        }
    };

    public BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        return  device.createRfcommSocketToServiceRecord(BTMODULEUUID);
        //creates secure outgoing connection with BT device using UUID
    }



    /**
     * Search the set of bonded devices in the BluetoothAdapter for one that matches
     * the given name
     * @param adapter the BluetoothAdapter whose bonded devices should be queried
     * @param name the name of the device to search for
     * @return the BluetoothDevice by the given name (if found); null if it was not found
     */

    public static BluetoothDevice findBondedDeviceByName (BluetoothAdapter adapter, String name) {
        for (BluetoothDevice device : getBondedDevices(adapter)) {
            if (name.matches(device.getName())) {
                Log.v(TAG, String.format("Found device with name %s and address %s.", device.getName(), device.getAddress()));
                return device;
            }
        }
        Log.w(TAG, String.format("Unable to find device with name %s.", name));
        return null;
    }

    /**
     * Safety wrapper around BluetoothAdapter#getBondedDevices() that is guaranteed
     * to return a non-null result
     * @param adapter the BluetoothAdapter whose bonded devices should be obtained
     * @return the set of all bonded devices to the adapter; an empty set if there was an error
     */

    public static Set<BluetoothDevice> getBondedDevices (BluetoothAdapter adapter) {
        Set<BluetoothDevice> results = adapter.getBondedDevices();
        if (results == null) {
            results = new HashSet<BluetoothDevice>();
        }
        return results;
    }


    public void dadosBluetooth()
    {

        mHandler = new Handler() {
            public void handleMessage(android.os.Message msg){
                if(msg.what == MESSAGE_READ){
                    String readMessage = null;
                    try {
                        readMessage = new String((byte[]) msg.obj, "UTF-8");
                    } catch (UnsupportedEncodingException e) {
                        e.printStackTrace();
                    }
                    mReadBuffer.setText(readMessage);
                    float value = 0;
                    try {
                        value = ((float) Integer.parseInt(readMessage)) / 1023F;
                    }catch (NumberFormatException e){
                        value = 0.5f;
                    }

                        LineDataSet dataSet = mHeartRateChart.getLineData().getDataSetByIndex(0);
                        LineData data = mHeartRateChart.getData();

                        dataSet.removeEntry(mReading.poll());

                        for (Entry read : mReading)
                            read.setXIndex(read.getXIndex() - 1);

                        Entry e = new Entry(value, dataSet.getEntryCount());
                        mReading.add(e);
                        dataSet.addEntry(e);

                        mHeartRateChart.notifyDataSetChanged();
                        mHeartRateChart.invalidate();
                    if(modoDeRecebimento=="<batimed>\0"){
                        pulseVector.add(Integer.parseInt(readMessage));
                        Log.d("Valor", pulseVector.get(0) + "neste indexe");
                    }
                }

                if(msg.what == CONNECTING_STATUS){
                    if(msg.arg1 == 1)
                        mBluetoothStatus.setText("Conectado com o dispositivo: " + (String)(msg.obj));
                    else
                        mBluetoothStatus.setText("Falha na Conexão");
                }
            }
        };

        if (mBTArrayAdapter == null) {
            // Device does not support Bluetooth
            mBluetoothStatus.setText("Status: não encontrado");
            Toast.makeText(getApplicationContext(),"Bluetooth device not found!",Toast.LENGTH_SHORT).show();
        }
        else {

            mScanBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    bluetoothOn(v);
                }
            });

            mOffBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    bluetoothOff(v);
                }
            });

            mListPairedDevicesBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v){
                    listPairedDevices(v);
                }
            });

            mDiscoverBtn.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    discover(v);
                }
            });

            mbtnCalibrarSensor.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v)
                {
                    if(mBTAdapter.isEnabled()) {
                        modoDeRecebimento = "<instant>\0";
                        mConnectedThread.write(modoDeRecebimento);
                        mHeartRateChart.setVisibility(View.VISIBLE);
                        Toast.makeText(getApplicationContext(),"Use o gráfico para calibrar",Toast.LENGTH_SHORT).show();
                    }else
                        Toast.makeText(getApplicationContext(), "Bluetooth não está ligado", Toast.LENGTH_SHORT).show();

                }
            });


                mReading = new LinkedList<>();

                for (int i = 0; i < 256; i++) {
                    mReading.add(new Entry(0f, i));
                }

                List<Entry> entries = new ArrayList<>();
                int i = 0;
                for (Entry mread : mReading) {
                    entries.add(mread);
                }


                LineDataSet dataset = new LineDataSet(entries, "");
                dataset.setDrawCubic(false);
                dataset.setDrawCircles(false);
                dataset.setDrawCircleHole(false);
                dataset.setDrawValues(false);
                dataset.setColor(Color.RED);


                List<String> labels = new ArrayList<>();

                for (i = 0; i < 256; i++) {
                    labels.add("");
                }


                LineData data = new LineData(labels, dataset);
                data.setHighlightEnabled(false);
                mHeartRateChart.setClickable(false);
                mHeartRateChart.setDrawGridBackground(false);
                mHeartRateChart.setData(data);
                mHeartRateChart.setTouchEnabled(false);
                mHeartRateChart.setDescription("Calibragem da pulsação");
                mHeartRateChart.getAxisLeft().setDrawLabels(false);
                mHeartRateChart.getAxisLeft().setDrawGridLines(false);
                mHeartRateChart.getAxisRight().setDrawLabels(false);
                mHeartRateChart.getAxisRight().setDrawGridLines(false);
                mHeartRateChart.getAxisLeft().setAxisMaxValue(1.2f);
                mHeartRateChart.getAxisLeft().setAxisMinValue(-0.2f);
                mHeartRateChart.getXAxis().setDrawGridLines(false);
                mHeartRateChart.getXAxis().setDrawAxisLine(true);
                mHeartRateChart.getLegend().setEnabled(false);
                mHeartRateChart.getLineData().getDataSets().get(0).setColor(Color.rgb(255, 128, 64));

            }


    }


    public class ConnectedThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;

        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;

            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            byte[] buffer = new byte[4];  // buffer store for the stream
            List<Integer> bufferArray = new ArrayList<>();
            boolean reading = false;
            // Keep listening to the InputStream until an exception occurs
            int next = 0;
            int bytes = 0; // bytes returned from read()
            while (true) {
                try {
                    next = mmInStream.read();
                    if(next == -1) {
                        SystemClock.sleep(100);
                        continue;
                    }
                    if(next == (int)'<') {
                        reading = true;
                        continue;
                    }
                    if(next == (int)'>') {
                        reading = false;
                        if(bytes > 0){
                            //buffer[bytes] = '\0';
                            while(bytes++ < 4) bufferArray.add(0, (int)'0');
                            int i = 0;
                            for(Integer in: bufferArray)
                                buffer[i++] = (byte) in.intValue();
                            mHandler.obtainMessage(MapsCorridaLivreActivity.MESSAGE_READ, bytes-1, -1, buffer)
                                    .sendToTarget();
                        }
                        bufferArray.clear();
                        bytes = 0;
                        continue;
                    }
                    if(reading && next >= '0' && next <= '9'){
                        if(bytes < buffer.length) {
                            //buffer[bytes++] = (byte) next;
                            bufferArray.add(next);
                            bytes++;
                        }
                        else{
                            bytes = 0;
                            reading = false;
                        }
                        continue;
                    }
                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(String input) {
            byte[] bytes = input.getBytes();           //converts entered String into bytes
            try {
                mmOutStream.write(bytes);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

    public void changeChart(View view) {
        LineDataSet dataSet = mHeartRateChart.getLineData().getDataSetByIndex(0);
        LineData data = mHeartRateChart.getData();

        dataSet.removeEntry(mReading.poll());

        for(Entry read: mReading)
            read.setXIndex(read.getXIndex()-1);

        Entry e = new Entry((float)Math.random(), dataSet.getEntryCount());
        mReading.add(e);
        dataSet.addEntry(e);

        mHeartRateChart.notifyDataSetChanged();
        mHeartRateChart.invalidate();
    }

}
