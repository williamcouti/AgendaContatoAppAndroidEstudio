package br.com.videoaula.agendacontato;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import br.com.videoaula.agendacontato.app.MessageBox;
import br.com.videoaula.agendacontato.app.ViewHelper;
import br.com.videoaula.agendacontato.database.DataBase;
import br.com.videoaula.agendacontato.dominio.RepositorioContato;
import br.com.videoaula.agendacontato.dominio.entidades.Contato;
import br.com.videoaula.agendacontato.util.DateUtils;

public class ActCadContatos extends ActionBarActivity  {


    private EditText edtNome;
    private EditText edtEmail;
    private EditText edtTelefone;
    private EditText edtEndereco;
    private EditText edtDatasEspeciais;
    private EditText edtGrupos;

    private Spinner spnTipoEmail;
    private Spinner spnTipoTelefone;
    private Spinner spnTipoEndereco;
    private Spinner spnTipoDatasEspeciais;

    private ArrayAdapter<String> adpTipoEmail;
    private ArrayAdapter<String> adpTipoTelefone;
    private ArrayAdapter<String> adpTipoEndereco;
    private ArrayAdapter<String> adpTipoDatasEspeciais;

    private DataBase dataBase;
    private SQLiteDatabase conn;
    private RepositorioContato repositorioContato;
    private Contato contato;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.act_cad_contatos);

        edtNome = (EditText)findViewById(R.id.edtNome);
        edtEmail = (EditText)findViewById(R.id.edtEmail);
        edtEndereco = (EditText)findViewById(R.id.edtEndereco);
        edtDatasEspeciais = (EditText)findViewById(R.id.edtDatasEspeciais);
        edtGrupos = (EditText)findViewById(R.id.edtGrupos);
        edtTelefone = (EditText)findViewById(R.id.edtTelefone);

        spnTipoEmail = (Spinner)findViewById(R.id.spnTipoEmail);
        spnTipoTelefone = (Spinner)findViewById(R.id.spnTipoTelefone);
        spnTipoEndereco = (Spinner)findViewById(R.id.spnTipoEndereco);
        spnTipoDatasEspeciais = (Spinner)findViewById(R.id.spnDatasEspeciais);

        adpTipoEmail          = ViewHelper.createArrayAdapter(this, spnTipoEmail);
        adpTipoTelefone       = ViewHelper.createArrayAdapter(this, spnTipoTelefone);
        adpTipoEndereco       = ViewHelper.createArrayAdapter(this, spnTipoEndereco);
        adpTipoDatasEspeciais = ViewHelper.createArrayAdapter(this, spnTipoDatasEspeciais);

        /*adpTipoEmail = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpTipoEmail.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adpTipoTelefone = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpTipoTelefone.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adpTipoEndereco = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpTipoEndereco.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        adpTipoDatasEspeciais = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item);
        adpTipoDatasEspeciais.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spnTipoEmail.setAdapter(adpTipoEmail);
        spnTipoTelefone.setAdapter(adpTipoTelefone);
        spnTipoEndereco.setAdapter(adpTipoEndereco);
        spnTipoDatasEspeciais.setAdapter(adpTipoDatasEspeciais);
*/
        adpTipoEmail.add("Casa");
        adpTipoEmail.add("Trabalho");
        adpTipoEmail.add("Outros");

        adpTipoTelefone.add("Celular");
        adpTipoTelefone.add("Trabalho");
        adpTipoTelefone.add("Casa");
        adpTipoTelefone.add("Principal");
        adpTipoTelefone.add("Fax trabalho");
        adpTipoTelefone.add("Fax casa");
        adpTipoTelefone.add("Pager");
        adpTipoTelefone.add("Outros");

        adpTipoEndereco.add("Casa");
        adpTipoEndereco.add("Trabalho");
        adpTipoEndereco.add("Outros");

        adpTipoDatasEspeciais.add("Aniversário");
        adpTipoDatasEspeciais.add("Data comemorativa");
        adpTipoDatasEspeciais.add("Outros");

        ExibeDataListener listener = new ExibeDataListener();

        edtDatasEspeciais.setOnClickListener(  listener );
        edtDatasEspeciais.setOnFocusChangeListener( listener );

        Bundle bundle = getIntent().getExtras();

        if ((bundle != null) && (bundle.containsKey(ActContato.PAR_CONTATO)))
        {
            contato = (Contato)bundle.getSerializable(ActContato.PAR_CONTATO);
            preencheDados();
        }
        else
            contato = new Contato();

        try {


            dataBase = new DataBase(this);
            conn = dataBase.getWritableDatabase();

            repositorioContato = new RepositorioContato(conn);

        }catch(SQLException ex)
        {
            MessageBox.show(this, "Erro", "Erro ao criar o banco: " + ex.getMessage());
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
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_act_cad_contatos, menu);

        if (contato.getId() != 0)
            menu.getItem(1).setVisible(true);

        return super.onCreateOptionsMenu(menu);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId())
        {

            case R.id.mni_acao1:

                salvar();
                finish();

            break;

            case R.id.mni_acao2:

                excluir();
                finish();

            break;
        }


        return super.onOptionsItemSelected(item);
    }


    private void preencheDados()
    {
        edtNome.setText( contato.getNome() );
        edtTelefone.setText( contato.getTelefone() );
        spnTipoTelefone.setSelection(  Integer.parseInt(contato.getTipoTelefone()) );
        edtEmail.setText( contato.getEmail() );
        spnTipoEmail.setSelection(  Integer.parseInt(contato.getTipoEmail()) );
        edtEndereco.setText( contato.getEndereco() );
        spnTipoEndereco.setSelection(  Integer.parseInt(contato.getTipoEndereco()) );

        DateFormat format = DateFormat.getDateInstance(DateFormat.SHORT);
        String dt = format.format( contato.getDatasEspeciais() );

        edtDatasEspeciais.setText( dt );
        spnTipoDatasEspeciais.setSelection(  Integer.parseInt(contato.getTipoDatasEspeciais()) );

        edtGrupos.setText( contato.getGrupos() );

    }

    private void excluir()
    {
        try
        {
            repositorioContato.excluir( contato.getId() );

        }catch(Exception ex)
        {
            MessageBox.show(this, "Erro", "Erro ao excluir os dados: " + ex.getMessage());
        }

    }


    private void salvar()
    {

        try
        {

            contato.setNome(edtNome.getText().toString());
            contato.setTelefone(edtTelefone.getText().toString());
            contato.setEmail(edtEmail.getText().toString());
            contato.setEndereco(edtEndereco.getText().toString());

            contato.setGrupos(edtGrupos.getText().toString());

            contato.setTipoTelefone( String.valueOf(spnTipoTelefone.getSelectedItemPosition()) );
            contato.setTipoEmail(String.valueOf(spnTipoEmail.getSelectedItemPosition()));
            contato.setTipoEndereco(String.valueOf(spnTipoEndereco.getSelectedItemPosition()));
            contato.setTipoDatasEspeciais( String.valueOf(spnTipoDatasEspeciais.getSelectedItemPosition()) );

            if (contato.getId() == 0)
                repositorioContato.inserir(contato);
            else
                repositorioContato.alterar(contato);

        }catch(Exception ex)
        {
            MessageBox.show(this, "Erro", "Erro ao salvar os dados: " + ex.getMessage());
        }

    }

    private void exibeData()
    {
        Calendar calendar = Calendar.getInstance();

        int ano =  calendar.get(Calendar.YEAR);
        int mes =  calendar.get(Calendar.MONTH);
        int dia =  calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog dlg = new DatePickerDialog(this, new SelecionaDataListener(), ano, mes, dia);
        dlg.show();
    }


    private class ExibeDataListener implements View.OnClickListener, View.OnFocusChangeListener
    {
        @Override
        public void onClick(View v) {
            exibeData();
        }

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus)
                exibeData();
        }

    }


    private class SelecionaDataListener implements DatePickerDialog.OnDateSetListener
    {

        @Override
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {

            String dt = DateUtils.dateToString(year, monthOfYear, dayOfMonth);
            Date data = DateUtils.getDate(year, monthOfYear, dayOfMonth);

            edtDatasEspeciais.setText(dt);

            contato.setDatasEspeciais(data);

        }

    }



}
