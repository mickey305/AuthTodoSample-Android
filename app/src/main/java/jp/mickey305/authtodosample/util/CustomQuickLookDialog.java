package jp.mickey305.authtodosample.util;

import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import jp.mickey305.authtodosample.R;

public class CustomQuickLookDialog extends DialogFragment {
    private Dialog dialog;
    private OnClickListener listener;
    private String title, body;
    private TextView textViewTitle, textViewBody;

    public CustomQuickLookDialog() { }

    public interface OnClickListener {
        void onClickNegativeButton();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        dialog = new Dialog(getActivity(), R.style.DimDialogFragmentCustomLightStyle);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.custom_quick_look_dialog);
        dialog.findViewById(R.id.quick_look_buttonCancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onClickNegativeButton();
                dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(true);
        textViewTitle = (TextView) dialog.findViewById(R.id.quick_look_textViewTitle);
        textViewBody = (TextView) dialog.findViewById(R.id.quick_look_textViewBody);
        if(title != null && !title.equals("")) textViewTitle.setText(title);
        if(body != null && !body.equals("")) textViewBody.setText(body);
        return dialog;
    }

    public void setOnClickListener(OnClickListener callback) {
        listener = callback;
    }

    public void setTitle(String msg) {
        title = msg;
    }

    public void setBody(String msg) {
        body = msg;
    }
}
