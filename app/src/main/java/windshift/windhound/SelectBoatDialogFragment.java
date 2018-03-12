package windshift.windhound;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

public class SelectBoatDialogFragment extends DialogFragment {

    public interface SelectBoatDialogListener {
        public void onDialogBoatClick(String boat);
    }

    SelectBoatDialogListener listener;

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        // Verify that the host activity implements the callback interface
        try {
            listener = (SelectBoatDialogListener) context;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(context.toString()
                    + " must implement SelectBoatDialogListener");
        }
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppThemeDialog);
        builder.setTitle("Select Boat")
                .setItems(new String[] {"Boat 0", "Boat 1", "Boat 2"},
                        new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position of the selected item
                    listener.onDialogBoatClick(Integer.toString(which));
                }});
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
