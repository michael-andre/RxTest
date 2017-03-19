package com.wapplix.rx;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

/**
 * Created by Mike on 18/03/2017.
 */

public class RxProgressFragment extends DialogFragment {

    private static final String ARG_MESSAGE = "message";

    public static RxProgressFragment newInstance(String message) {
        Bundle args = new Bundle();
        args.putString(ARG_MESSAGE, message);
        RxProgressFragment fragment = new RxProgressFragment();
        fragment.setArguments(args);
        return fragment;
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        ProgressDialog dialog = new ProgressDialog(getContext(), getTheme());
        dialog.setMessage(getArguments().getString(ARG_MESSAGE));
        return dialog;
    }

}
