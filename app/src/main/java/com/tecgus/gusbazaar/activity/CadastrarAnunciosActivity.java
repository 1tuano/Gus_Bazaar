package com.tecgus.gusbazaar.activity;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.tecgus.gusbazaar.R;
import com.tecgus.gusbazaar.databinding.ActivityCadastrarAnunciosBinding;
import com.tecgus.gusbazaar.helper.ConfiguracaoFirebase;
import com.tecgus.gusbazaar.helper.Permissoes;
import com.tecgus.gusbazaar.model.Anuncio;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class CadastrarAnunciosActivity extends AppCompatActivity implements View.OnClickListener {

    ActivityCadastrarAnunciosBinding binding;

    private Anuncio anuncio;
    private StorageReference storage;
    private android.app.AlertDialog dialog;

    private String[] permissoes = new String[]{
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
    };

    private List<String> listaFotosRecuperadas = new ArrayList<>();
    private List<String> listaUrlFotos = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCadastrarAnunciosBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        storage = ConfiguracaoFirebase.getFirebaseStorage();

        //validar permissao
        Permissoes.validarPermissoes(permissoes, this, 2);
        carregarDadosSpinner();

        binding.imageCadastro1.setOnClickListener(this);
        binding.imageCadastro2.setOnClickListener(this);
        binding.imageCadastro3.setOnClickListener(this);


        //definir localidade para pt -> portugues Br
        Locale locale = new Locale("pt", "BR");
        binding.editValor.setLocale(locale);

        binding.btnCadastrarAnuncio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               validarDadosAnuncio();
            }
        });

    }

    public void salvarAnuncio() {

        dialog = new SpotsDialog.Builder()
                .setContext(this)
                .setMessage("Salvando Anuncio")
                .setCancelable(false)
                .build();
        dialog.show();

        /*
        * salvar imagem no store
        */
        for (int i = 0; i< listaFotosRecuperadas.size(); i++){
            String urlImagem = listaFotosRecuperadas.get(i);
            int tamanhoLista = listaFotosRecuperadas.size();
            salvarFotoStorage(urlImagem,tamanhoLista, i);

            
        }


    }

    private void salvarFotoStorage(String urlString, int totalFotos, int contador) {

        //criar nó no storage
       final StorageReference imagemAnuncio = storage.child("imagens")
                .child("anuncios")
                .child(anuncio.getIdAnuncio())
                .child("imagem"+contador);

        //fazer upload do arquivo
        UploadTask uploadTask = imagemAnuncio.putFile(Uri.parse(urlString));
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                imagemAnuncio.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {

                        Uri url = task.getResult();
                        String urlConvertida = url.toString();

                        listaUrlFotos.add(urlConvertida);

                        if (totalFotos == listaUrlFotos.size()){
                            anuncio.setFotos(listaUrlFotos);
                            anuncio.salvar();

                            dialog.dismiss();
                            finish();
                        }

                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                exibirMensagemErro("Falha ao fazer upload");
            }
        });

    }

    private Anuncio configuraAnuncio(){

        String estado = binding.spinnerEstado.getSelectedItem().toString();
        String categoria = binding.spinnerCategoria.getSelectedItem().toString();
        String titulo = binding.editTitulo.getText().toString();
        String valor = binding.editValor.getText().toString();
        String telefone = binding.editTelefone.getText().toString();
        String descricao = binding.editDescricao.getText().toString();

        Anuncio anuncio = new Anuncio();
        anuncio.setEstado(estado);
        anuncio.setCategoria(categoria);
        anuncio.setTitulo(titulo);
        anuncio.setValor(valor);
        anuncio.setTelefone(telefone);
        anuncio.setDescricao(descricao);

        return anuncio;

    }

    private void carregarDadosSpinner() {
        String[] estados = getResources().getStringArray(R.array.estados);
        String[] categorias = getResources().getStringArray(R.array.categoria);

        //estado
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, androidx.appcompat.
                R.layout.support_simple_spinner_dropdown_item, estados);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.spinnerEstado.setAdapter(adapter);

        //categoria
        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(this, androidx.appcompat.
                R.layout.support_simple_spinner_dropdown_item, categorias);
        adapter.setDropDownViewResource(androidx.appcompat.R.layout.support_simple_spinner_dropdown_item);
        binding.spinnerCategoria.setAdapter(adapter2);

    }

    public void validarDadosAnuncio(){

        anuncio = configuraAnuncio();
        String valor = String.valueOf(binding.editValor.getRawValue());

        if (listaFotosRecuperadas.size() != 0){
            if (!anuncio.getEstado().isEmpty() && !anuncio.getEstado().equals("Estado")){
                if (!anuncio.getCategoria().isEmpty() && !anuncio.getCategoria().equals("Categoria")){
                    if (!anuncio.getTitulo().isEmpty()){
                        if (!valor.isEmpty() && !valor.equals("0")){
                            if (!anuncio.getTelefone().isEmpty()){
                                if (!anuncio.getDescricao().isEmpty()){
                                    salvarAnuncio();
                                }else {
                                    exibirMensagemErro("Preencha o campo descrição!");
                                }
                            }else {
                                exibirMensagemErro("Preencha o campo telefone!");
                            }
                        }else {
                            exibirMensagemErro("Preencha o campo valor!");
                        }
                    }else {
                        exibirMensagemErro("Preencha o campo titulo!");
                    }
                }else {
                    exibirMensagemErro("Preencha o campo categoria!");
                }
            }else{
                exibirMensagemErro("Preencha o campo estado!");
            }
        }else{
            exibirMensagemErro("Selecione ao menos uma foto!");
        }

    }

    private void exibirMensagemErro(String mensagem){
        Toast.makeText(this, mensagem, Toast.LENGTH_SHORT).show();

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        for (int permissaoResultado : grantResults) {
            if (permissaoResultado == PackageManager.PERMISSION_DENIED) {
                alertaValidacaoPermissao();
            }
        }
    }

    private void alertaValidacaoPermissao() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Permissões Nnegadas");
        builder.setMessage("Para utilizar o app é necessario aceitar as permissões");
        builder.setCancelable(false);
        builder.setPositiveButton("Confirmar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.imageCadastro1:
                escolherImagem(1);
                break;
            case R.id.imageCadastro2:
                escolherImagem(2);
                break;
            case R.id.imageCadastro3:
                escolherImagem(3);
                break;
        }
    }

    private void escolherImagem(int requestCode) {
        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(i, requestCode);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == Activity.RESULT_OK) {

            Uri imagemSelecionada = data.getData();
            String caminhoImagem = imagemSelecionada.toString();

            //configura imagem no imagemView
            if (requestCode == 1) {
                binding.imageCadastro1.setImageURI(imagemSelecionada);

            } else if (requestCode == 2) {
                binding.imageCadastro2.setImageURI(imagemSelecionada);
            } else if (requestCode == 3) {
                binding.imageCadastro3.setImageURI(imagemSelecionada);
            }

            listaFotosRecuperadas.add(caminhoImagem);
        }
    }
}