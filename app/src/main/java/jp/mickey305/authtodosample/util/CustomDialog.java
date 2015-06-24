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

public class CustomDialog extends DialogFragment {
    private Dialog dialog;
    private OnClickListener listener;
    private String title;
    private TextView textViewTitle;

    public CustomDialog() { }

    public interface OnClickListener {
        void onClickPositiveButton();
        void onClickNegativeButton();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        dialog = new Dialog(getActivity(), R.style.DimDialogFragmentCustomDarkStyle);
        dialog.getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        dialog.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN);
        dialog.getWindow().setBackgroundDrawable(
                new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
        dialog.setContentView(R.layout.access_dialog);
        dialog.findViewById(R.id.buttonCancel).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onClickNegativeButton();
                dismiss();
            }
        });
        dialog.findViewById(R.id.buttonVerify).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(listener != null) listener.onClickPositiveButton();
                dismiss();
            }
        });
        dialog.setCanceledOnTouchOutside(false);
        textViewTitle = (TextView) dialog.findViewById(R.id.textViewTitle);
        if(title != null) textViewTitle.setText(title);
        return dialog;
    }

    public void setOnClickListener(OnClickListener callback) {
        listener = callback;
    }

    public void setTitle(String msg) {
        title = msg;
    }
}
