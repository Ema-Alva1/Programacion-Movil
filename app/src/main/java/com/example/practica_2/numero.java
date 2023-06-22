package com.example.practica_2;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import android.os.Bundle;

public class numero extends AppCompatActivity {
    Button whats, dos;
    EditText numero;
    TextView mostrar32;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_numero);

        whats = (Button) findViewById(R.id.btnGuardar);

        numero = (EditText) findViewById(R.id.editText);
        mostrar32 = (TextView) findViewById(R.id.textWiew);
        SharedPreferences sharedPreferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
        String valorGuardado = sharedPreferences.getString("numero", "no hay numero registrado");
        //Toast.makeText(numero.this, "numero actual: "+valorGuardado, Toast.LENGTH_SHORT).show();
        mostrar32.setText("Número actual: " + valorGuardado);

        dos = (Button) findViewById(R.id.btnVolver);

        dos.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent dos = new Intent(numero.this, MainActivity.class);
                startActivity(dos);
            }
        });


        whats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String valor = numero.getText().toString();
                SharedPreferences sharedPreferences = getSharedPreferences("preferencias", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("numero", valor);
                editor.apply();
                Toast.makeText(numero.this, "registrado, numero: " + valor, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "archivo: "+sharedPreferences, Toast.LENGTH_SHORT).show();
                String valorGuardado = sharedPreferences.getString("numero", "no hay numero registrado");
                //Toast.makeText(numero.this, "numero actual: "+valorGuardado, Toast.LENGTH_SHORT).show();
                mostrar32.setText("Número actual: " + valorGuardado);
            }
        });

    }
}