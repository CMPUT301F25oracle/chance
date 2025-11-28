package com.example.chance.customviews;

import android.content.Context;
import android.content.res.TypedArray;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.chance.R;
import com.google.android.flexbox.AlignItems;
import com.google.android.flexbox.FlexboxLayout;

public class ChanceTextInput extends FlexboxLayout {
    private final ImageView icon;
    private final EditText textInput;

    final static int BLACK = 0xFF000000;
    final static int WHITE = 0xFFFFFFFF;


    public ChanceTextInput(Context context, AttributeSet attrs) {
        super(context, attrs);
        icon = new ImageView(context);
        textInput = new EditText(context);

        // grabs list of custom attributes
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ChanceTextInput);

        //region: flexbox styles
        setAlignItems(AlignItems.CENTER);
        setBackgroundResource(R.drawable.chance_text_input_background);
        //endregion

        //region: icon styles
        int iconResource = a.getResourceId(R.styleable.ChanceTextInput_icon, -1);
        int iconDimensions = a.getInteger(R.styleable.ChanceTextInput_iconDimensions, 40);
        TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics());
        FlexboxLayout.LayoutParams iconLayoutParams = new FlexboxLayout.LayoutParams(
                dpToPx(iconDimensions), dpToPx(iconDimensions));
        iconLayoutParams.setMargins(
                dpToPx(10), dpToPx(10),
                dpToPx(10), dpToPx(10));
        iconLayoutParams.setFlexShrink(0);
        icon.setLayoutParams(iconLayoutParams);
        icon.setScaleType(ImageView.ScaleType.FIT_CENTER);
        if (iconResource != -1) {
            icon.setImageResource(iconResource);
        }
        //endregion

        //region: edit text styles
        String hint = a.getString(R.styleable.ChanceTextInput_hint);
        if (hint == null) hint = "";
        boolean hiddenInput = a.getBoolean(R.styleable.ChanceTextInput_hiddenInput, false);
        FlexboxLayout.LayoutParams textInputLayoutParams = new FlexboxLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, dpToPx(40));
        textInputLayoutParams.setFlexGrow(1f);
        textInput.setLayoutParams(textInputLayoutParams);
        textInput.setHint(hint);
        textInput.setTextColor(BLACK);
        textInput.setBackgroundTintList(
                context.getResources().getColorStateList(R.color.white, context.getTheme())
        );
        if (hiddenInput) {
            textInput.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
        //endregion
        a.recycle();

        addView(icon);
        addView(textInput);
    }

    //src: https://stackoverflow.com/questions/4605527/converting-pixels-to-dp
    private int dpToPx(float dpValue) {
        float densityPx = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpValue, getResources().getDisplayMetrics());
        return Math.round(densityPx);
    }

    public String getText() {
        return textInput.getText().toString();
    }

    public void addTextChangedListener(TextWatcher textWatcher) {
        textInput.addTextChangedListener(textWatcher);
    }
}
