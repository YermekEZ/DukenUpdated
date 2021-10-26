package com.example.duken.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatDialogFragment;

import com.example.duken.R;
import com.example.duken.data.SharedData;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

public class EnterCountDialog extends AppCompatDialogFragment {

    private TextInputLayout enterCount;
    private TextInputEditText enterCountEditText;

    private EnterCountDialogListener listener;

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater layoutInflater = getActivity().getLayoutInflater();
        View view = layoutInflater.inflate(R.layout.enter_count_layout, null);

        builder.setView(view)
                .setTitle("Enter number of products")
                .setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .setPositiveButton("apply", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String pieces = enterCount.getEditText().getText().toString();

                        listener.setCount(pieces);
                    }
                });

        enterCount = view.findViewById(R.id.enterCount);
        enterCountEditText = view.findViewById(R.id.enterCountEditText);

        enterCountEditText.setHint("Pieces (max. " + SharedData.getMaxCount() + ")");

        return builder.create();
    }

    public interface EnterCountDialogListener{
        void setCount(String pieces);
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        try {
            listener = (EnterCountDialog.EnterCountDialogListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + "implement interface first");
        }
    }

}