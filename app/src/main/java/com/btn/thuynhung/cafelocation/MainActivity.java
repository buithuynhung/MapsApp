package com.btn.thuynhung.cafelocation;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends AppCompatActivity {

    private Button showMapsButton;
    private Button akaButton, shopButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        shopButton = findViewById(R.id.button_shop);
        akaButton = findViewById(R.id.button_aka);
        placesButton();

        showMapsButton = findViewById(R.id.button_show_maps);
        showMapsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("PLACE", 3);
                startActivity(intent);
                finish();
            }
        });
    }
    private void placesButton() {


        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("PLACE", 2);
                startActivity(intent);
                finish();

            }
        });

        akaButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("PLACE", 1);
                startActivity(intent);
                finish();

            }
        });
    }


}
