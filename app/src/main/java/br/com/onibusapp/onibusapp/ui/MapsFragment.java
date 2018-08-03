package br.com.onibusapp.onibusapp.ui;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import br.com.onibusapp.onibusapp.R;
import br.com.onibusapp.onibusapp.domain.Onibus;
import br.com.onibusapp.onibusapp.ui.mapa.MapsContract;
import br.com.onibusapp.onibusapp.ui.mapa.MapsPresenter;

public class MapsFragment extends Fragment implements MapsContract.View, OnMapReadyCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private static final int PERMISSION_ACCESS_COARSE_LOCATION = 1;

    private GoogleMap mMap;
    private Button btnAtualizar;

    private MapsContract.Presenter mMapsPresenter;

    private Set<String> linhas = new HashSet<>();

   /* @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }*/

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_maps, container, false);
        MapView mapView = (MapView) view.findViewById(R.id.mapaFragment);
        btnAtualizar = (Button) view.findViewById(R.id.btnAtualizar);

        mapView.onCreate(savedInstanceState);
        mapView.onResume();
        mapView.getMapAsync(this);
        this.mMapsPresenter = new MapsPresenter(getActivity(), this);

        btnAtualizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLocalizacaoOnibus();
            }
        });
        getParametros();
        return view;
    }

    private void getParametros() {
        Bundle bundle = this.getArguments();
        if (bundle != null) {
            String linha = bundle.getString("linha");
            Integer sentido = bundle.getInt("sentido");
            Log.d("PARAMENTROS", linha + " " + sentido);
        }
    }

    /*private void inicializaLinhas() {
        linhas.add("0.006");
        linhas.add("512.1");
        linhas.add("0.527");
        linhas.add("0.513");
        linhas.add("0.512");

    }*/

    private void getLocalizacaoOnibus() {
        RequestQueue queue = Volley.newRequestQueue(getActivity());
        String url = "http://00224.transdatasmart.com.br:22401/ITS-infoexport/api/Data/VeiculosGTFS";
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        // Display the first 500 characters of the response string.
                       Log.d("SUCESSO", "OK");
                        Map<String, Object> retMap = new Gson().fromJson(
                                response, new TypeToken<HashMap<String, Object>>() {}.getType()
                        );
                        List<List<String>> resposta = (List<List<String>>) retMap.get("Dados");
                        mMap.clear();
                        mMapsPresenter.getMyLocation(false);
                        for (List<String> dados : resposta) {

                            if (dados.get(5) != "") {
                                Onibus onibus = new Onibus();
                                onibus.setPrefixo(dados.get(0));
                                onibus.setDataHora(dados.get(1));
                                onibus.setLatitude(dados.get(2));
                                onibus.setLongitude(dados.get(3));
                               // onibus.setDirecao(Long.valueOf(dados.get(4)));
                                onibus.setLinha(dados.get(5));
                                onibus.setGtfsLinha(dados.get(6));
                                onibus.setSentido(dados.get(7));
                                if (linhas.contains(onibus.getLinha())) {
                                    Log.d("MARCANDO", onibus.getLinha());
                                    LatLng localizacao = new LatLng(onibus.getLatitude(), onibus.getLongitude());
                                    mMap.addMarker(new MarkerOptions().position(localizacao).title(onibus.getLinha() + " - " + onibus.getPrefixo()));
                                }
                            }

                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e("ERRO", error.getMessage());
            }
        });

// Add the request to the RequestQueue.
        queue.add(stringRequest);
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
        mMapsPresenter.onMapReady();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public static boolean haveLocationPermission(Context context) {
        return ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED;
    }

    @Override
    public void onMapReady() {
        if (haveLocationPermission(getActivity())) {
            try {
                mMap.getUiSettings().setMyLocationButtonEnabled(true);
                mMap.getUiSettings().setMapToolbarEnabled(true);
                mMap.setMyLocationEnabled(true);
                this.mMapsPresenter.getMyLocation(true);
            } catch (SecurityException e) {
                Toast.makeText(getActivity(), "Erro", Toast.LENGTH_LONG);
            }
        }
    }

    @Override
    public void setLocalizacao(Double lat, Double lnt, Boolean focarLocalizacao) {
        if (mMap != null) {
            LatLng minhaLocalizacao = new LatLng(lat, lnt);
            if (focarLocalizacao) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(minhaLocalizacao, 14));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(minhaLocalizacao));
            }
        }
    }

    @Override
    public Activity getCurrentActivity() {
        return this.getActivity();
    }
}

/* EXEMPLO DE PERMISSOES RUNTIME
final private int REQUEST_CODE_ASK_PERMISSIONS = 123;

    private void insertDummyContactWrapper() {
        int hasWriteContactsPermission = checkSelfPermission(Manifest.permission.WRITE_CONTACTS);
        if (hasWriteContactsPermission != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[] {Manifest.permission.WRITE_CONTACTS},
                    REQUEST_CODE_ASK_PERMISSIONS);
            return;
        }
        insertDummyContact();
    }

*/
