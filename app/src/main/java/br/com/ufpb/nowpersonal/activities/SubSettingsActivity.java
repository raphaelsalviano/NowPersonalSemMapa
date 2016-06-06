package br.com.ufpb.nowpersonal.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import br.com.ufpb.nowpersonal.R;
import br.com.ufpb.nowpersonal.fragments.LoginSegurancaFragment;
import br.com.ufpb.nowpersonal.fragments.RegisterFragment;

public class SubSettingsActivity extends AppCompatActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_settings);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_subsettings);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        String TAG = getIntent().getExtras().getString("TAG");

        if(TAG.equalsIgnoreCase("senha")){
            getSupportActionBar().setTitle("Login e Segurança");
            LoginSegurancaFragment fragment = new LoginSegurancaFragment();
            getSupportFragmentManager().beginTransaction().replace(R.id.container_subsettings, fragment).commit();
        }else{
            getSupportActionBar().setTitle("Informações pessoais e privacidade");
            RegisterFragment fragment = new RegisterFragment();
            Bundle bundle = new Bundle();
            bundle.putString("TAG","info");
            fragment.setArguments(bundle);
            getSupportFragmentManager().beginTransaction().replace(R.id.container_subsettings, fragment).commit();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        if(id == android.R.id.home){
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            finish();
        }

        return super.onOptionsItemSelected(item);
    }
}
