package br.com.videoaula.agendacontato;

import android.app.AlertDialog;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.*;
import android.widget.*;
import android.content.*;

import android.database.sqlite.*;
import android.database.*;

import br.com.videoaula.agendacontato.app.MessageBox;
import br.com.videoaula.agendacontato.database.DataBase;
import br.com.videoaula.agendacontato.dominio.RepositorioContato;
import br.com.videoaula.agendacontato.dominio.entidades.Contato;

public class ActContato extends ActionBarActivity implements View.OnClickListener, AdapterView.OnItemClickListener{

    private ImageButton btnAdicionar;
    private EditText edtPesquisa;
    private ListView lstContatos;
    private ArrayAdapter<Contato> adpContatos;

    private DataBase dataBase;
    private SQLiteDatabase conn;
    private RepositorioContato repositorioContato;

    public static final String PAR_CONTATO = "CONTATO";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_contato);

        btnAdicionar = (ImageButton)findViewById(R.id.btnAdicionar);
        edtPesquisa  = (EditText)findViewById(R.id.edtPesquisa);
        lstContatos  = (ListView)findViewById(R.id.lstContatos);

        btnAdicionar.setOnClickListener(this);
        lstContatos.setOnItemClickListener(this);

        try {

            dataBase = new DataBase(this);
            conn = dataBase.getWritableDatabase();
            repositorioContato = new RepositorioContato(conn);

            adpContatos = repositorioContato.buscaContatos(this);

            lstContatos.setAdapter(adpContatos);

            FiltraDados filtraDados = new FiltraDados(adpContatos);
            edtPesquisa.addTextChangedListener(filtraDados);


        }catch(SQLException ex)
        {
            MessageBox.show(this, "Erro", "Erro ao criar o banco: " + ex.getMessage() );
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (conn != null){
            conn.close();
        }
    }

    @Override
    public void onClick(View v) {

        Intent it = new Intent(this, ActCadContatos.class);
        startActivityForResult(it, 0);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        adpContatos = repositorioContato.buscaContatos(this);
        lstContatos.setAdapter(adpContatos);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

        Contato contato = adpContatos.getItem(position);

        Intent it = new Intent(this, ActCadContatos.class);

        it.putExtra(PAR_CONTATO, contato);

        startActivityForResult(it, 0);

    }


    private class FiltraDados implements TextWatcher
    {

        private ArrayAdapter<Contato> arrayAdapter;

        private FiltraDados(ArrayAdapter<Contato> arrayAdapter)
        {
            this.arrayAdapter = arrayAdapter;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            arrayAdapter.getFilter().filter(s);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    }




}
