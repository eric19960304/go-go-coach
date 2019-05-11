package hkucs.comp3330.gogocoach;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.InputStream;
import java.net.URL;

import hkucs.comp3330.gogocoach.firebase.Classes;

public class DetailClassActivity extends AppCompatActivity {

    private ImageView avatarImageView;
    static public String ACTION_BROWSE_PROFILE = "browseProfile";
    static public String ACTION_BOOKING = "book";

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
        FloatingActionButton profile_fab = findViewById(R.id.profile_fab);

        final String userId = c.id;
        final String photoUrl = c.photoUrl;
        profile_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("action", ACTION_BROWSE_PROFILE);
                i.putExtra("userId", userId);
                i.putExtra("photoUrl", photoUrl);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        final Classes classToBook = c;
        FloatingActionButton booking_fab = findViewById(R.id.booking_fab);
        booking_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent();
                i.putExtra("action", ACTION_BOOKING);
                i.putExtra("classToBook", classToBook);
                setResult(RESULT_OK, i);
                finish();
            }
        });

        avatarImageView = (ImageView) findViewById(R.id.avatarImageView);
        final String url = c.photoUrl;
        (new Thread(new Runnable(){
            @Override
            public void run() {
                final Bitmap avatar = getCroppedBitmap(Bitmap.createScaledBitmap(loadImageFromNetwork(url), 500, 500, false));
                avatarImageView.post(new Runnable(){
                    @Override
                    public void run() {
                        avatarImageView.setImageBitmap(avatar);
                    }
                });
            }
        })).start();
    }

    public Bitmap loadImageFromNetwork(String url) {
        try {
            Bitmap bitmap = BitmapFactory.decodeStream
                    ((InputStream) new URL(url).getContent());
            return bitmap;
        } catch(
                Exception e)

        {
            e.printStackTrace();
        }
        return null;
    }

    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        // canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        canvas.drawCircle(bitmap.getWidth() / 2, bitmap.getHeight() / 2,
                bitmap.getWidth() / 2, paint);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        //Bitmap _bmp = Bitmap.createScaledBitmap(output, 60, 60, false);
        //return _bmp;
        return output;
    }
}
