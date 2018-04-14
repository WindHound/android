package windshift.windhound.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;

import windshift.windhound.R;

public class SelectBoatDialogFragment extends DialogFragment {

    private String[] boats;

    public interface SelectBoatDialogListener {
        public void onDialogBoatClick(String boat);
    }

    public static SelectBoatDialogFragment newInstance(String[] boats) {
        SelectBoatDialogFragment selectBoat = new SelectBoatDialogFragment();

        // Supply num input as an argument.
        Bundle bundle = new Bundle();
        bundle.putStringArray("BOATS", boats);
        selectBoat.setArguments(bundle);

        return selectBoat;
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
        boats = getArguments().getStringArray("BOATS");
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity(),
                R.style.AppThemeDialog);
        builder.setTitle("Select Boat")
                .setItems(boats, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                    // The 'which' argument contains the index position of the selected item
                    listener.onDialogBoatClick(boats[which]);
                }});
        // Create the AlertDialog object and return it
        return builder.create();
    }

}
