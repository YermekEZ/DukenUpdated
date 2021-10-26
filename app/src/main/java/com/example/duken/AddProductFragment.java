package com.example.duken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duken.data.AddProductData;
import com.example.duken.data.SharedData;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddProductFragment extends Fragment {

    private AddProductFragmentListener addProductFragmentListener;
    private TextInputLayout productName, barcodeNumber, price, quantity;
    private ImageButton scanImageButton;
    private Button saveButton;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    public interface AddProductFragmentListener{
        void productsUpdated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_add_product, container, false);
        productName = view.findViewById(R.id.productName);
        barcodeNumber = view.findViewById(R.id.barcodeNumber);
        price = view.findViewById(R.id.price);
        quantity = view.findViewById(R.id.quantity);
        scanImageButton = view.findViewById(R.id.scanImageButton);
        saveButton = view.findViewById(R.id.saveButton);

        scanImageButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ScanActivity.class);
                startActivityForResult(intent, 0);
            }
        });

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("products");

        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveData();
            }
        });

        // Inflate the layout for this fragment
        return view;
    }

    private void saveData() {
        String mName = productName.getEditText().getText().toString();
        String mBarcode = barcodeNumber.getEditText().getText().toString();
        String mPrice = price.getEditText().getText().toString();
        String mQuantity = quantity.getEditText().getText().toString();

        AddProductData addProductData = new AddProductData(mName, mBarcode, mPrice, mQuantity);

        mDatabaseReference.child(SharedData.getPhoneNumber()).child(mBarcode).setValue(addProductData);

        Toast.makeText(getContext(), "Data have been saved successfully!", Toast.LENGTH_SHORT).show();

        productName.getEditText().getText().clear();
        barcodeNumber.getEditText().getText().clear();
        price.getEditText().getText().clear();
        quantity.getEditText().getText().clear();

        addProductFragmentListener.productsUpdated();
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0) {
            if(resultCode == CommonStatusCodes.SUCCESS) {
                if(data != null) {
                    Barcode barcode = data.getParcelableExtra("scannedCode");
                    barcodeNumber.getEditText().setText(barcode.displayValue);
                } else {
                    barcodeNumber.getEditText().setText("No code found");
                }
            }

        }
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof AddProductFragmentListener){
            addProductFragmentListener = (AddProductFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + "implement AddProductFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        addProductFragmentListener = null;
    }

}