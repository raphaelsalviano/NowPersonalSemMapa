package br.com.ufpb.nowpersonal;

import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.multidex.MultiDexApplication;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.j256.ormlite.support.ConnectionSource;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import br.com.ufpb.nowpersonal.database.DBCoreORM;
import br.com.ufpb.nowpersonal.database.UsuarioDao;
import br.com.ufpb.nowpersonal.model.Usuario;

public class NowPersonalApplication extends MultiDexApplication implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks, LocationListener {

    private Usuario usuario;
    private DBCoreORM dbCoreORM;
    private UsuarioDao usuarioDao;

    private GoogleApiClient apiClient;
    private LocationRequest locationRequest;

    private Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        new AsyncApplication().execute((Void) null);
        dbCoreORM = new DBCoreORM(getBaseContext());
        instanciarUsuarioDao(dbCoreORM.getConnectionSource());

    }

    @Override
    public void onTerminate() {
        super.onTerminate();
        dbCoreORM.close();
        apiClient.disconnect();
    }

    private void connectGooglePlayServices() {
        apiClient = new GoogleApiClient.Builder(getBaseContext())
                .addOnConnectionFailedListener(this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();
        apiClient.connect();
        Log.i("user", "conectou ao play services");
    }

    private void initiRequestLocation() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(3000);
        locationRequest.setFastestInterval(1500);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    private void startLocationUpdates() {
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
        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient, locationRequest, this);
    }

    private void instanciarUsuarioDao(ConnectionSource connectionSource) {
        try {
            usuarioDao = new UsuarioDao(connectionSource);
            Log.i("user", "iniciou o banco");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void addOrUpdateUsuario(Usuario usuario) throws SQLException {
        usuarioDao.createOrUpdate(usuario);
        if (usuario.isStatus()) {
            this.usuario = usuario;
        }
    }

    public void deleteUsuario(Usuario usuario) throws SQLException {
        usuarioDao.delete(usuario);
        this.usuario = null;
    }

    public List<Usuario> searchAllPersonal() throws SQLException {
        List<Usuario> usuarios = new ArrayList<>();
        for (Usuario u : usuarioDao.queryForAll()) {
            if (u.isPersonal()) {
                usuarios.add(u);
            }
        }
        return usuarios;
    }

    public Usuario searchUserByStatus() throws SQLException {
        Usuario usuario = null;
        for (Usuario u : usuarioDao.queryForAll()) {
            if (u.isStatus()) {
                usuario = u;
            }
        }
        return usuario;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public int generatePinSingle() throws SQLException {
        List<Integer> pins = getAllPin();
        Random random = new Random();
        final int maxPin = 999;
        int pin = random.nextInt(maxPin);
        while (pins.contains(pin)) {
            pin = random.nextInt(maxPin);
        }
        return pin;
    }

    private List<Integer> getAllPin() throws SQLException {
        List<Integer> pins = new ArrayList<>();
        for (Usuario u : usuarioDao.queryForAll()) {
            pins.add(u.getPin());
        }
        return pins;
    }

    public List<Usuario> getAllUsers() throws SQLException {
        return usuarioDao.queryForAll();
    }

    @Override
    public void onConnected(Bundle bundle) {

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
        location = LocationServices.FusedLocationApi.getLastLocation(apiClient);

        initiRequestLocation();

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    public Location getLocation() {
        return location;
    }

    @Override
    public void onLocationChanged(Location location) {
        this.location = location;
    }

    private class AsyncApplication extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            connectGooglePlayServices();
            return null;
        }
    }
}
