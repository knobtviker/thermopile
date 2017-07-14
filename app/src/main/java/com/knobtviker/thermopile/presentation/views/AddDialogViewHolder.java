package com.knobtviker.thermopile.presentation.views;

import android.support.annotation.NonNull;
import android.support.v7.widget.AppCompatCheckBox;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.SeekBar;
import android.widget.TimePicker;

import com.knobtviker.thermopile.R;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnCheckedChanged;

/**
 * Created by bojan on 12/07/2017.
 */

public class AddDialogViewHolder {

    @BindView(R.id.button_preview)
    public Button buttonPreview;

    @BindView(R.id.timepicker_start)
    public TimePicker timePickerStart;

    @BindView(R.id.timepicker_end)
    public TimePicker timePickerEnd;

    @BindView(R.id.seekbar_temperature)
    public SeekBar seekBarTemperature;

    @BindView(R.id.recyclerview_colors)
    public RecyclerView recyclerViewColors;

    @BindView(R.id.checkbox_mode_save)
    public AppCompatCheckBox checkBoxModeSave;

    @BindView(R.id.edittext_mode_name)
    public EditText editTextModeName;

    public AddDialogViewHolder(@NonNull final View view) {

        ButterKnife.bind(this, view);

        init(view);
    }

    @OnCheckedChanged(R.id.checkbox_mode_save)
    public void onSaveModeChanged(final boolean isChecked) {
        editTextModeName.setVisibility(isChecked ? View.VISIBLE : View.GONE);
    }

    private void init(@NonNull final View view) {
        timePickerStart.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        recyclerViewColors.setHasFixedSize(true);
        recyclerViewColors.setLayoutManager(new LinearLayoutManager(view.getContext(), LinearLayoutManager.HORIZONTAL, false));
    }
}
