package com.tecgus.gusbazaar.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Switch;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseAuthInvalidUserException;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseAuthWeakPasswordException;
import com.google.firebase.internal.api.FirebaseNoSignedInUserException;
import com.tecgus.gusbazaar.R;
import com.tecgus.gusbazaar.helper.ConfiguracaoFirebase;

public class CadastroActivity extends AppCompatActivity {

    private Button btnAcesso;
    private EditText campoEmail, campoSenha;
    private Switch tipoAcesso;

    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cadastro);

        iniciarComponentes();
        auth = ConfiguracaoFirebase.getFirebaseAutenticacao();

        btnAcesso.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String email = campoEmail.getText().toString();
                String senha = campoSenha.getText().toString();

                if (!email.isEmpty()){
                    if (!senha.isEmpty()){

                        //switch
                        if (tipoAcesso.isChecked()){

                            //cadastro
                            auth.createUserWithEmailAndPassword(
                                    email, senha
                            ).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(CadastroActivity.this, "Cadastro realizado com sucesso!", Toast.LENGTH_SHORT).show();


                                    }else {

                                        String erroExcecao = "";

                                        try {
                                            throw  task.getException();

                                        }catch (FirebaseAuthWeakPasswordException e) {
                                            erroExcecao = "Digite uma senha mais forte!";
                                        }catch (FirebaseAuthInvalidCredentialsException e) {
                                            erroExcecao = "Por favor, digite um e-mail valido";
                                        }catch (FirebaseAuthUserCollisionException e){
                                            erroExcecao = "Esse email já consta cadastrado";
                                            }catch (Exception e){
                                            erroExcecao = "ao cadastrar usuário" + e.getMessage();
                                            e.printStackTrace();
                                        }

                                        Toast.makeText(CadastroActivity.this, "Erro" + erroExcecao, Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });

                        }else{//login

                            auth.signInWithEmailAndPassword(email, senha).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (task.isSuccessful()){

                                        Toast.makeText(CadastroActivity.this, "Logado com sucesso!", Toast.LENGTH_SHORT).show();
                                        startActivity(new Intent(getApplicationContext(), AnunciosActivity.class));

                                    }else {


                                        Toast.makeText(CadastroActivity.this, "Erro ao fazer login : " + task.getException(), Toast.LENGTH_SHORT).show();
                                    }

                                }
                            });

                        }

                    }else{
                        Toast.makeText(CadastroActivity.this, "Preencha a senha!", Toast.LENGTH_SHORT).show();
                    }
                }else {
                    Toast.makeText(CadastroActivity.this, "Preencha o email!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void iniciarComponentes() {
        btnAcesso = findViewById(R.id.btnAcesso);
        campoEmail = findViewById(R.id.editEmail);
        campoSenha = findViewById(R.id.editSenha);
        tipoAcesso = findViewById(R.id.switch1);
    }
}