package br.com.ufpb.nowpersonal.activities;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import java.sql.SQLException;

import br.com.ufpb.nowpersonal.NowPersonalApplication;
import br.com.ufpb.nowpersonal.R;
import br.com.ufpb.nowpersonal.model.Usuario;

// Tela de configurações
public class SettingsActivity extends AppCompatActivity {

    private Toolbar toolbar;

    private NowPersonalApplication application;

    private boolean login = false;
    private boolean inf = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings_count);

        toolbar = (Toolbar) findViewById(R.id.toolbar_settings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        application = (NowPersonalApplication) getApplicationContext();
        Usuario usuario = null;

        try {
            usuario = application.searchUserByStatus();
            ((TextView) findViewById(R.id.nameUser_Settings)).setText("Bem vindo(a), " + usuario.getNome() + " " + usuario.getSobrenome());
        } catch (SQLException e) {
            e.printStackTrace();
        }

        final View item1 = findViewById(R.id.item1);
        item1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SubSettingsActivity.class);
                intent.putExtra("TAG", "senha");
                startActivity(intent);
                finish();
            }
        });


        final View item2 = findViewById(R.id.item2);
        item2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SettingsActivity.this, SubSettingsActivity.class);
                intent.putExtra("TAG", "info");
                startActivity(intent);
                finish();
            }
        });


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if (id == android.R.id.home) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
