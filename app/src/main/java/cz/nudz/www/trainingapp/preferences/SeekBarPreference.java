package cz.nudz.www.trainingapp.preferences;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Parcel;
import android.os.Parcelable;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;

import cz.nudz.www.trainingapp.R;

public class SeekBarPreference extends DialogPreference {

    private SeekBar seekBar;
    private TextView valueLabel;
    private int initialValue;

    private final int offset;
    private final int threshold;
    private final int multiplier;

    public SeekBarPreference(Context context, AttributeSet attrs) {
        super(context, attrs);

        int minValue = -1;
        int maxValue = -1;
        int stepValue = -1;
        for (int i = 0; i < attrs.getAttributeCount(); i++) {
            String name = attrs.getAttributeName(i);
            if (name.equals("minValue")) {
                minValue = attrs.getAttributeIntValue(i, -1);
            }
            if (name.equals("maxValue")) {
                maxValue = attrs.getAttributeIntValue(i, -1);
            }
            if (name.equals("stepValue")) {
                stepValue = attrs.getAttributeIntValue(i, -1);
            }
        }

        if (minValue == -1 || maxValue == -1 || stepValue == -1) {
            throw new IllegalArgumentException("One of the following attributes was left unspecified: minValue, maxValue, stepValue");
        }
        if (stepValue == 0) {
            throw new IllegalArgumentException("Step cannot be a zero value");
        }

        this.offset = minValue;
        this.multiplier = stepValue;
        this.threshold = (maxValue - minValue) / stepValue;

        setDialogLayoutResource(R.layout.seek_bar_fragment);
        setPositiveButtonText(R.string.ok);
        setNegativeButtonText(R.string.cancelBtnText);
        setDialogIcon(null);
    }

    @Override
    protected View onCreateDialogView() {
        View root = super.onCreateDialogView();

        // workaround for https://stackoverflow.com/questions/20545550/refresh-dialogpreference-without-closing-preferenceactivity/
        initialValue = getPreferenceManager().getSharedPreferences().getInt(getKey(), offset);
        int progress = (initialValue - offset) / multiplier;

        valueLabel = root.findViewById(R.id.seekBarValue);
        valueLabel.setText(String.valueOf(initialValue));

        seekBar = root.findViewById(R.id.seekBar);
        seekBar.setMax(threshold);
        seekBar.setProgress(progress);
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress <= threshold) {
                    valueLabel.setText(String.valueOf(progress * multiplier + offset));
                    seekBar.setSecondaryProgress(progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        return root;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        // When the user selects "OK", persist the new value
        if (positiveResult) {
            persistInt(seekBar.getProgress() * multiplier + offset);
        }
    }

    @Override
    protected void onSetInitialValue(boolean restorePersistedValue, Object defaultValue) {
        if (restorePersistedValue) {
            // Restore existing state
            initialValue = this.getPersistedInt(0);
        } else {
            // Set default state from the XML attribute
            initialValue = (Integer) defaultValue;
            persistInt(initialValue);
        }
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getInteger(index, 0);
    }

    private static class SavedState extends BaseSavedState {
        // Member that holds the setting's value
        // Change this data type to match the type saved by your Preference
        int value;

        public SavedState(Parcelable superState) {
            super(superState);
        }

        public SavedState(Parcel source) {
            super(source);
            // Get the current preference's value
            value = source.readInt();  // Change this to read the appropriate data type
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            super.writeToParcel(dest, flags);
            // Write the preference's value
            dest.writeInt(value);  // Change this to write the appropriate data type
        }

        // Standard creator object using an instance of this class
        public static final Parcelable.Creator<SavedState> CREATOR =
                new Parcelable.Creator<SavedState>() {

                    public SavedState createFromParcel(Parcel in) {
                        return new SavedState(in);
                    }

                    public SavedState[] newArray(int size) {
                        return new SavedState[size];
                    }
                };
    }
}
