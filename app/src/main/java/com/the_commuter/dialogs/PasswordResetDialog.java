package com.the_commuter.dialogs;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.the_commuter.R;

import java.util.Objects;

import static android.content.ContentValues.TAG;

public class PasswordResetDialog extends DialogFragment {

    private EditText  mEmail;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.dialogue_password_reset, container, false);
        mEmail = view.findViewById(R.id.email);
        mContext = getActivity();
        TextView confirmDialog = (TextView) view.findViewById(R.id.dialogConfirm);

        confirmDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (isEmpty(mEmail.getText().toString())) {

                    //temporarily authenticate and resend verification email
                    authenticateAndResendEmail(mEmail.getText().toString());
                } else {
                    Toast.makeText(mContext, "email does not exist", Toast.LENGTH_SHORT).show();
                }
            }
        });

        TextView cancelDialog =  view.findViewById(R.id.dialogCancel);
        cancelDialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(getDialog()).dismiss();
            }
        });

        return view;

    }

    private void authenticateAndResendEmail(String email) {
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "Email sent.");
                            Toast.makeText(mContext.getApplicationContext(), "check your email to reset the password", Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                        } else {
                            Toast.makeText(mContext.getApplicationContext(), task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            getDialog().dismiss();
                        }
                    }
                });
    }

//    private void sendVerificationEmail() {
//        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
//
//        if (user != null) {
//            user.sendEmailVerification()
//                    .addOnCompleteListener(new OnCompleteListener<Void>() {
//                        @Override
//                        public void onComplete(@NonNull Task<Void> task) {
//                            if (task.isSuccessful()) {
//                                Toast.makeText(mContext, "Sent Verification Email", Toast.LENGTH_SHORT).show();
//                            } else {
//                                Toast.makeText(mContext, "couldn't send email", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                    });
//        }
//
//    }

    private boolean isEmpty(String string) {
        return !string.equals("");
    }
}
