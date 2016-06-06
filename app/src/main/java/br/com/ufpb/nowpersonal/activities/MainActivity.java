package br.com.ufpb.nowpersonal.activities;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.DividerDrawerItem;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileSettingDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import br.com.ufpb.nowpersonal.NowPersonalApplication;
import br.com.ufpb.nowpersonal.R;
import br.com.ufpb.nowpersonal.model.Usuario;
import br.com.ufpb.nowpersonal.util.PersonalAdapter;

public class MainActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView recyclerView;

    private Usuario usuario;
    private NowPersonalApplication application;
    private PersonalAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        application = (NowPersonalApplication) getApplicationContext();

        try {
            usuario = application.searchUserByStatus();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        toolbar = (Toolbar) findViewById(R.id.toolbar_main);
        setSupportActionBar(toolbar);

        createDrawer(savedInstanceState);

        try {
            List<Usuario> usuarios = application.searchAllPersonal();
            adapter = new PersonalAdapter(this, usuarios);
        } catch (SQLException e) {
            e.printStackTrace();
        }


        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

    }

    private void createDrawer(Bundle savedInstanceState) {
        AccountHeader accountHeader = new AccountHeaderBuilder().withActivity(this)
                .withCompactStyle(false)
                .withSavedInstance(savedInstanceState)
                .withHeaderBackground(R.color.md_amber_700)
                .addProfiles(
                        new ProfileDrawerItem().withIcon(R.drawable.ic_people)
                                .withName("" + usuario.getNome() + " " + usuario.getSobrenome())
                                .withEmail("" + usuario.getEmail())
                                .withTypeface(createTypeface()).withIdentifier(0),

                        new ProfileSettingDrawerItem().withIcon(R.drawable.ic_settings)
                                .withName(getString(R.string.header_settings)).withTypeface(createTypeface())
                                .withIdentifier(1)
                )
                .withOnAccountHeaderListener(new AccountHeaderListener())
                .build();

        new DrawerBuilder().withActivity(this)
                .withActionBarDrawerToggleAnimated(true)
                .withDrawerGravity(Gravity.START)
                .withToolbar(toolbar)
                .withAccountHeader(accountHeader)
                .withItemAnimator(new DefaultItemAnimator())
                .withSelectedItem(-1)
                .addDrawerItems(
                        new PrimaryDrawerItem().withName("Meu PIN: " + usuario.getPin())
                                .withIcon(R.drawable.ic_key_user)
                                .withTag("pin")
                )
                .addDrawerItems(new DividerDrawerItem()
                )
                .addDrawerItems(new PrimaryDrawerItem().withName("Ajuda")
                        .withIcon(R.drawable.ic_help_circle)
                        .withTag("help")
                )
                .addDrawerItems(new DividerDrawerItem()
                )
                .addDrawerItems(new PrimaryDrawerItem().withName("Sair")
                        .withIcon(R.drawable.ic_logout)
                        .withTag("logoff")
                )
                .withOnDrawerItemClickListener(new DrawerListener())
                .build();
    }

    private Typeface createTypeface() {
        return Typeface.createFromAsset(getAssets(), "fonts/roboto_medium.ttf");
    }

    private class AccountHeaderListener implements AccountHeader.OnAccountHeaderListener {

        @Override
        public boolean onProfileChanged(View view, IProfile profile, boolean current) {

            int id = profile.getIdentifier();

            if (id == 1) {
                Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                startActivity(intent);
                finish();
            }

            return false;
        }
    }

    private class DrawerListener implements Drawer.OnDrawerItemClickListener {

        @Override
        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {

            String TAG = drawerItem.getTag().toString();

            Intent intent = null;

            switch (TAG) {
                case "pin":
                    new AlertDialog.Builder(MainActivity.this)
                            .setTitle("O código PIN: " + usuario.getPin())
                            .setMessage("Este é seu código PIN, ele serve como chave de segurança para as configurações de conta e de aplicativos!" +
                                    "Ele é único e instranferível")
                            .setPositiveButton("OK", null)
                            .create().show();
                    return true;
                case "help":
                    intent = new Intent(MainActivity.this, AboutActivity.class);
                    startActivity(intent);
                    finish();
                    return true;
                case "logoff":
                    try {
                        usuario.setStatus(false);
                        application.addOrUpdateUsuario(usuario);
                        intent = new Intent(MainActivity.this, SlideActivity.class);
                        startActivity(intent);
                        finish();

                    } catch (SQLException e) {
                        e.printStackTrace();
                    }
                    return true;
            }

            return false;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);

        getMenuInflater().inflate(R.menu.menu_main, menu);
        SearchView searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        searchView.setOnQueryTextListener(new SearchList());

        return true;
    }

    private class SearchList implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            try {
                searchPersonalView(query);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            try {
                searchPersonalView(newText);
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return true;
        }


        private void searchPersonalView(String text) throws SQLException {
            List<Usuario> personal = new ArrayList<>();
            for (Usuario usuario : application.searchAllPersonal()) {
                if (usuario.getBairro().contentEquals(text)) {
                    personal.add(usuario);
                } else if ((usuario.getNome() + usuario.getSobrenome()).contentEquals(text)) {
                    personal.add(usuario);
                }
            }
            adapter.setPersonais(personal);
            adapter.notifyDataSetChanged();
        }
    }
}
