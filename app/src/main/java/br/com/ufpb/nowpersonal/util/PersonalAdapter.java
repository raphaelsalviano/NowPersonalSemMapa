package br.com.ufpb.nowpersonal.util;

import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import br.com.ufpb.nowpersonal.R;
import br.com.ufpb.nowpersonal.model.Usuario;

public class PersonalAdapter extends RecyclerView.Adapter<PersonalAdapter.PersonalViewHolder> {

    private Context context;
    private List<Usuario> personais;
    private LayoutInflater layoutInflater;

    public PersonalAdapter(Context context, List<Usuario> personais) {
        this.context = context;
        layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.personais = personais;
    }

    @Override
    public PersonalViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = layoutInflater.inflate(R.layout.list_personal, parent, false);

        return new PersonalViewHolder(view);
    }

    public void setPersonais(List<Usuario> personais) {
        this.personais = personais;
    }

    @Override
    public void onBindViewHolder(PersonalViewHolder holder, int position) {

        Usuario usuario = personais.get(position);

        holder.nome.setText(usuario.getNome() + " " + usuario.getSobrenome());
        holder.endereco.setText(usuario.getEndereco());
        holder.preco.setText("R$ " + usuario.getPreco());

    }

    @Override
    public int getItemCount() {
        return personais.size();
    }

    public class PersonalViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        private View itemClick;
        private TextView nome;
        private TextView endereco;
        private TextView preco;

        public PersonalViewHolder(View view) {
            super(view);

            itemClick = view.findViewById(R.id.item_click);
            itemClick.setOnClickListener(this);

            nome = (TextView) view.findViewById(R.id.nomePersonal);
            endereco = (TextView) view.findViewById(R.id.enderecoPersonal);
            preco = (TextView) view.findViewById(R.id.precoPersonal);
        }

        @Override
        public void onClick(View view) {
            View alertView = carregarViewAlert(getPosition());

            new AlertDialog.Builder(context)
                    .setTitle("Informações sobre o personal")
                    .setView(alertView)
                    .setPositiveButton("OK", null)
                    .create().show();
        }

        private View carregarViewAlert(int position) {
            View view = layoutInflater.inflate(R.layout.item_personal, null, false);
            Usuario usuario = personais.get(position);

            ((TextView) view.findViewById(R.id.nomePersonalitem)).setText(usuario.getNome() + " " + usuario.getSobrenome());
            ((TextView) view.findViewById(R.id.telefonePersonalitem)).setText("Telefone: " + usuario.getTelefone());
            ((TextView) view.findViewById(R.id.emailPersonalitem)).setText("Email: " + usuario.getEmail());
            ((TextView) view.findViewById(R.id.localPersonalitem)).setText("Local de trabalho: " + usuario.getLocalTrabalho());
            ((TextView) view.findViewById(R.id.enderecoPersonalPersonalitem)).setText("Endereço: " + usuario.getEndereco());
            ((TextView) view.findViewById(R.id.bairroPersonalitem)).setText("Bairro: " + usuario.getBairro());
            ((TextView) view.findViewById(R.id.precoPersonalPersonalitem)).setText("Preço: " + usuario.getPreco());
            ((TextView) view.findViewById(R.id.horariosPersonalitem)).setText("Horário de atendimento\n" + usuario.getHoraComeco() + " às " + usuario.getHoraFim());
            ((TextView) view.findViewById(R.id.crefPersonalitem)).setText("CREF: " + usuario.getCref());

            return view;
        }
    }
}
