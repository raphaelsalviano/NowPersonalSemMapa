package br.com.ufpb.nowpersonal.fragments;

import android.app.Dialog;
import android.app.TimePickerDialog;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;

import com.daimajia.androidanimations.library.Techniques;
import com.daimajia.androidanimations.library.YoYo;
import com.squareup.picasso.Picasso;

import java.sql.SQLException;
import java.util.Calendar;

import br.com.ufpb.nowpersonal.NowPersonalApplication;
import br.com.ufpb.nowpersonal.R;
import br.com.ufpb.nowpersonal.model.Usuario;
import br.com.ufpb.nowpersonal.util.CircleTransform;

public class RegisterFragment extends Fragment {

    private ImageView mImageProfile;
    private TextView mName;
    private TextView mLastName;
    private TextView mDateOfBirth;
    private TextView mPhone;
    private TextView mEmail;
    private TextView mPassword;

    private Switch isPersonal;

    private View mLayoutPersonal;
    private TextView mAndress;
    private TextView mBairro;
    private TextView mOfficeHome;
    private View mOurStart;
    private View mOurEnd;
    private TextView mPrice;
    private TextView mCref;

    private View mRegister;

    public Usuario usuario;

    private String hora;

    private NowPersonalApplication application;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        application = (NowPersonalApplication) getActivity().getApplicationContext();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_register, container, false);

        findViewById(view);

        if(getArguments() != null){
            String TAG = getArguments().getString("TAG");

            if(TAG != null && TAG.equalsIgnoreCase("info")){
                try {
                    editInfo();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        Picasso.with(getActivity()).load(R.drawable.ic_people).transform(new CircleTransform()).into(mImageProfile);
        isPersonal.setOnCheckedChangeListener(new CheckedChangeListener());

        usuario = new Usuario();

        mOurStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertDialogTime();
                usuario.setHoraComeco(hora);
            }
        });
        mOurEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAlertDialogTime();
                usuario.setHoraFim(hora);
            }
        });

        mRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                attempCadastro(view);
            }
        });

        return view;
    }

    private void editInfo() throws SQLException {

        Usuario usuario = application.searchUserByStatus();

        mName.setText(usuario.getNome());
        mLastName.setText(usuario.getSobrenome());
        mDateOfBirth.setText(usuario.getDataNascimento());
        mPhone.setText(usuario.getTelefone());
        mEmail.setText(usuario.getEmail());
        mPassword.setVisibility(View.GONE);

        if(usuario.isPersonal()){
            isPersonal.setChecked(true);
            YoYo.with(Techniques.FadeInDown).duration(1000).playOn(mLayoutPersonal);
            mLayoutPersonal.setVisibility(View.VISIBLE);

            mAndress.setText(usuario.getEndereco());
            mBairro.setText(usuario.getBairro());
            mOfficeHome.setText(usuario.getLocalTrabalho());
            mPrice.setText(usuario.getHoraComeco());
            mCref.setText(usuario.getHoraFim());

        }
    }

    private void attempCadastro(View view) {

        usuario.setNome("" + mName.getText().toString());
        usuario.setSobrenome("" + mLastName.getText().toString());
        usuario.setDataNascimento("" + mDateOfBirth.getText().toString());
        usuario.setTelefone("" + mPhone.getText().toString());
        usuario.setEmail("" + mEmail.getText().toString());
        usuario.setSenha("" + mPassword.getText().toString());

        if (isPersonal.isChecked()) {
            String endereco = "" + mAndress.getText().toString();
            usuario.setLocalTrabalho("" + mOfficeHome.getText().toString());
            usuario.setPreco("" + mPrice.getText().toString());
            usuario.setCref(Integer.parseInt("" + mCref.getText().toString()));
            usuario.setBairro("" + mBairro.getText().toString());
            usuario.setEndereco(endereco);

            try {
                new AsyncRegister().execute((Void) null);
                application.addOrUpdateUsuario(usuario);
                resetEspacos();
                Snackbar.make(view, "Usuário criado com sucesso", Snackbar.LENGTH_SHORT).show();
                return;
            } catch (SQLException e) {
                e.printStackTrace();
            }

        }

        try {
            new AsyncRegister().execute((Void) null);
            application.addOrUpdateUsuario(usuario);
            resetEspacos();
            Snackbar.make(view, "Usuário criado com sucesso", Snackbar.LENGTH_SHORT).show();
            return;
        } catch (SQLException e) {
            e.printStackTrace();
        }

    }

    private void resetEspacos() {
        mName.setText(null);
        mLastName.setText(null);
        mDateOfBirth.setText(null);
        mPhone.setText(null);
        mEmail.setText(null);
        mPassword.setText(null);

        isPersonal.setChecked(false);
        YoYo.with(Techniques.FadeOutDown).duration(1000).playOn(mLayoutPersonal);
        mLayoutPersonal.setVisibility(View.GONE);

        mAndress.setText(null);
        mOfficeHome.setText(null);
        this.hora = null;
        mPrice.setText(null);
        mCref.setText(null);
    }

    private void findViewById(View view) {
        mImageProfile = (ImageView) view.findViewById(R.id.image_user);
        mName = (TextView) view.findViewById(R.id.register_name);
        mLastName = (TextView) view.findViewById(R.id.register_lastName);
        mDateOfBirth = (TextView) view.findViewById(R.id.register_calendar);
        mPhone = (TextView) view.findViewById(R.id.register_phone);
        mEmail = (TextView) view.findViewById(R.id.register_mail);
        mPassword = (TextView) view.findViewById(R.id.register_password);

        isPersonal = (Switch) view.findViewById(R.id.you_personal);

        mLayoutPersonal = view.findViewById(R.id.field_personal);
        mAndress = (TextView) view.findViewById(R.id.register_address);
        mBairro = (TextView) view.findViewById(R.id.register_bairro);
        mOfficeHome = (TextView) view.findViewById(R.id.register_attendance);
        mOurStart = view.findViewById(R.id.register_office_our1);
        mOurEnd = view.findViewById(R.id.register_office_our2);
        mPrice = (TextView) view.findViewById(R.id.register_price);
        mCref = (TextView) view.findViewById(R.id.register_cref);

        mRegister = view.findViewById(R.id.btn_register);
    }

    private Typeface getTypeface() {
        return Typeface.createFromAsset(getActivity().getAssets(), "fonts/roboto_regular.ttf");
    }

    private class CheckedChangeListener implements CompoundButton.OnCheckedChangeListener {

        @Override
        public void onCheckedChanged(CompoundButton compoundButton, boolean isPersonal) {
            if (isPersonal) {
                YoYo.with(Techniques.FadeInDown).duration(1000).playOn(mLayoutPersonal);
                mLayoutPersonal.setVisibility(View.VISIBLE);
            } else {
                YoYo.with(Techniques.FadeOutDown).duration(1000).playOn(mLayoutPersonal);
                mLayoutPersonal.setVisibility(View.GONE);
            }
        }
    }

    private void createAlertDialogTime() {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
        hora = ((TimePickerFragment) newFragment).getHour();
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        private String hour;

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current time as the default values for the picker
            final Calendar c = Calendar.getInstance();
            int hour = c.get(Calendar.HOUR_OF_DAY);
            int minute = c.get(Calendar.MINUTE);

            // Create a new instance of TimePickerDialog and return it
            return new TimePickerDialog(getActivity(), this, hour, minute,
                    DateFormat.is24HourFormat(getActivity()));
        }

        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            hour = "" + hourOfDay + " : " + minute;
        }

        public String getHour() {
            return hour;
        }
    }

    private class AsyncRegister extends AsyncTask<Void, Void, Integer> {

        @Override
        protected Integer doInBackground(Void... voids) {
            int pin = -1;

            try {
                pin = application.generatePinSingle();
            } catch (SQLException e) {
                e.printStackTrace();
            }

            return pin;
        }

        @Override
        protected void onPostExecute(Integer integer) {
            usuario.setPin(integer);
        }
    }

}