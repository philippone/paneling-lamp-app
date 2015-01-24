package util;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import net.philippschardt.panelinglamp.R;

/**
 * Created by philipp on 24.01.15.
 */
public class AddNewFormDialog  extends DialogFragment{


    public interface OnAddNewFormDialogListener {
        public void confirmName(String name);
        public void dispatchTakePictureIntent();

    }


    OnAddNewFormDialogListener mListener;


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        super.onCreateDialog(savedInstanceState);
        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_add_new_form, null, false);

        final TextView nameView = (TextView) v.findViewById(R.id.dialog_new_form_name_view);

        // Build the dialog and set up the button click handlers
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder
                .setTitle(R.string.dialog_new_form_title)
                .setView(v)
                .setPositiveButton(R.string.dialog_new_form_take_pic_and_save, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Send the positive button event back to the host activity
                        //if (!nameView.getText().toString().trim().isEmpty() /*TODO isEMPTy*/){
                            mListener.confirmName(nameView.getText().toString());
                            mListener.dispatchTakePictureIntent();
                        /*} else {
                            // TODO show hint to
                            nameView.setHint(R.string.hint_name);
                        }*/
                    }
                });
        return builder.create();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (OnAddNewFormDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
    }
}
