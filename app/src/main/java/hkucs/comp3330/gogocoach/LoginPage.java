package hkucs.comp3330.gogocoach;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

public class LoginPage extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_page);

        Button loginButton = findViewById(R.id.loginSubmitButton);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d("myTest", "hi");
                Intent myIntent = new Intent(LoginPage.this, MainActivity.class);
                LoginPage.this.startActivity(myIntent);
            }
        });
    }



}
