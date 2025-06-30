package com.example.chess_mobile.view.activities;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.chess_mobile.R;
import com.example.chess_mobile.settings.board_color.BoardColorInstance;
import com.example.chess_mobile.view.interfaces.IColorSelectedListener;

import java.util.Arrays;
import java.util.List;

public class ColorSelectActivity extends AppCompatActivity {
    View colorLastMove, colorSelectedHighlight, colorHighlight,colorCaptureHighlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.color_select_activity);

        colorLastMove = findViewById(R.id.colorLastMove);
        colorSelectedHighlight = findViewById(R.id.colorSelectedHighlight);
        colorHighlight = findViewById(R.id.colorHighlight);
        colorCaptureHighlight=findViewById(R.id.colorCaptureHighlight);

        colorLastMove.setBackgroundColor(BoardColorInstance.getInstance().getLastMoveCellColor());
        colorSelectedHighlight.setBackgroundColor( BoardColorInstance.getInstance().getSelectedCellHighlightColor());
        colorHighlight.setBackgroundColor( BoardColorInstance.getInstance().getCellHighlightColor());
        colorCaptureHighlight.setBackgroundColor( BoardColorInstance.getInstance().getCellHighlightCapturedColor());

        List<View> colorViews = Arrays.asList(colorLastMove, colorSelectedHighlight, colorHighlight, colorCaptureHighlight);

        for (View view : colorViews) {
            view.setOnClickListener(v -> showColorPickerDialog(this, view.getSolidColor(), new IColorSelectedListener() {
                @Override
                public void onColorSelected(int color) {

                    setColor(view.getId(),color);
                    view.setBackgroundColor(color);
                }
            }));
        }
    }

    private void openColorPicker(View targetView, int id) {
        int initialColor = Color.WHITE;


    }
    public void setColor(int id, int color) {
        if(id==R.id.colorLastMove){
            BoardColorInstance.getInstance().setLastMoveCellColor(color);
        }
        if(id==R.id.colorSelectedHighlight) {
            BoardColorInstance.getInstance().setSelectedCellHighlightColor(color);
        }
        if(id==R.id.colorHighlight) {
            BoardColorInstance.getInstance().setCellHighlightColor(color);
        }
        if(id==R.id.colorCaptureHighlight) {
            BoardColorInstance.getInstance().setCellHighlightCapturedMoveColor(color);
        }
    }
    private void showColorPickerDialog(Context context, int initialColor, IColorSelectedListener listener) {
        Dialog dialog = new Dialog(context);
        dialog.setContentView(R.layout.dialog_color_picker);

        SeekBar seekRed = dialog.findViewById(R.id.seekRed);
        SeekBar seekGreen = dialog.findViewById(R.id.seekGreen);
        SeekBar seekBlue = dialog.findViewById(R.id.seekBlue);
        View colorPreview = dialog.findViewById(R.id.colorPreview);
        Button btnOk = dialog.findViewById(R.id.btnOk);
        Button btnCancel = dialog.findViewById(R.id.btnCancel);

        int red = Color.red(initialColor);
        int green = Color.green(initialColor);
        int blue = Color.blue(initialColor);

        seekRed.setProgress(red);
        seekGreen.setProgress(green);
        seekBlue.setProgress(blue);

        // Cập nhật preview màu
        View.OnClickListener updatePreview = v -> {
            int r = seekRed.getProgress();
            int g = seekGreen.getProgress();
            int b = seekBlue.getProgress();


            colorPreview.setBackgroundColor(Color.rgb(r, g, b));
        };

        SeekBar.OnSeekBarChangeListener seekBarChangeListener = new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int i, boolean b) { updatePreview.onClick(null); }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        };

        seekRed.setOnSeekBarChangeListener(seekBarChangeListener);
        seekGreen.setOnSeekBarChangeListener(seekBarChangeListener);
        seekBlue.setOnSeekBarChangeListener(seekBarChangeListener);

        updatePreview.onClick(null); // hiển thị ban đầu

        btnOk.setOnClickListener(v -> {
            int r = seekRed.getProgress();
            int g = seekGreen.getProgress();
            int b = seekBlue.getProgress();
            listener.onColorSelected(Color.rgb(r, g, b));
            dialog.dismiss();
        });

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        dialog.show();
    }


}