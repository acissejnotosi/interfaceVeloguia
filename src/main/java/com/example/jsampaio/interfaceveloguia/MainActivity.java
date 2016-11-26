package com.example.jsampaio.interfaceveloguia;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.appindexing.Thing;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {
    //private Button mbtnLocalAtual;
    protected Button mbtnComecar;
    protected Spinner mSpinnerToogle;

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        starAtribuicoes();


        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();


        mbtnComecar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick (View v){

                // aqui eu recupero as posições selecionadas
                switch (mSpinnerToogle.getSelectedItemPosition()) {

                    case 0:
                        mapsCorrida();
                        break;


                    case 1:
                        mapsRotaGPS();
                        break;


                    case 2:
                        finish();
                }

            }
        });
    }

    public void starAtribuicoes() {


        mbtnComecar = (Button) findViewById(R.id.btnComecar);
        mSpinnerToogle = (Spinner) findViewById(R.id.spinnerToogle);
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.tipos_nevagecao_mapa, android.R.layout.simple_spinner_item);
        // Specify the layout to use when the list of choices appears
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Apply the adapter to the spinner
        mSpinnerToogle.setAdapter(adapter);

    }




    // método que me direciona a tela2 do exemplo
    public void mapsCorrida() {

        Intent chamarTela2 = new Intent(this, MapsCorridaLivreActivity.class);
        startActivity(chamarTela2);
    }
    // método que me direciona a tela2 do exemplo

    public void mapsRotaGPS() {

        Intent chamarTela2 = new Intent(this, MapsRotaSolicitadaActivity.class);
        startActivity(chamarTela2);
    }

    // METODO DE MENSAGEM NA TELA DO USUARIO
    public void showMessage(String Caption, Activity activity) {

        // GERANDO UMA INSTANCIA DA CLASSE DIALOG
        AlertDialog.Builder dialogo = new AlertDialog.Builder(activity);

        // GERANDO O BOX DE MENSAGEM
        dialogo.setTitle("Atenção");
        dialogo.setMessage(Caption);
        dialogo.setNeutralButton("OK", null);
        dialogo.show();
    }


    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    public Action getIndexApiAction() {
        Thing object = new Thing.Builder()
                .setName("Main Page") // TODO: Define a title for the content shown.
                // TODO: Make sure this auto-generated URL is correct.
                .setUrl(Uri.parse("http://[ENTER-YOUR-URL-HERE]"))
                .build();
        return new Action.Builder(Action.TYPE_VIEW)
                .setObject(object)
                .setActionStatus(Action.STATUS_TYPE_COMPLETED)
                .build();
    }

    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        AppIndex.AppIndexApi.start(client, getIndexApiAction());
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        AppIndex.AppIndexApi.end(client, getIndexApiAction());
        client.disconnect();
    }
}
