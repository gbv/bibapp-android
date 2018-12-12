package de.eww.bibapp.activity;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import de.eww.bibapp.PaiaHelper;
import de.eww.bibapp.R;

public class IdActivity extends BaseActivity {

    @BindView(R.id.barcode)
    ImageView barcodeView;

    @BindView(R.id.username)
    TextView usernameView;

    @BindView(R.id.name)
    TextView nameView;

    @BindView(R.id.email)
    TextView emailView;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_id);
        ButterKnife.bind(this);

        try {
            String username = PaiaHelper.getInstance().getUsername();

            this.createBarcode(username);
            this.usernameView.setText(username);

            String patronInformation = this.getIntent().getStringExtra("patron");
            JSONObject patronObject = new JSONObject(patronInformation);

            this.nameView.setText(patronObject.getString("name"));
            this.emailView.setText(patronObject.getString("email"));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void createBarcode(String data) throws WriterException {
        final int height = 300;
        final int width = 1200;

        MultiFormatWriter barcodeWriter = new MultiFormatWriter();
        BitMatrix barcodeBitMatrix = barcodeWriter.encode(data, BarcodeFormat.CODE_39, width, height);
        Bitmap barcodeBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                barcodeBitmap.setPixel(x, y, barcodeBitMatrix.get(x, y) ? Color.BLACK : Color.TRANSPARENT);
            }
        }

        this.barcodeView.setImageBitmap(barcodeBitmap);
    }
}
