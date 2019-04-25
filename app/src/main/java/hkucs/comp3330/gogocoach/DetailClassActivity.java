package hkucs.comp3330.gogocoach;

import android.os.Bundle;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import hkucs.comp3330.gogocoach.firebase.Classes;

public class DetailClassActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_detail);

        Classes c = (Classes) getIntent().getSerializableExtra("classes");
        TextView class_name = findViewById(R.id.class_name);
        class_name.setText(c.className);
        TextView type = findViewById(R.id.type);
        type.setText(c.type);
        TextView location = findViewById(R.id.location);
        location.setText(c.location);
        TextView time = findViewById(R.id.time);
        time.setText(c.time);
        TextView desc = findViewById(R.id.desc);
        desc.setText(c.description);
        TextView price = findViewById(R.id.price);
        price.setText("$"+c.price);
    }
}
