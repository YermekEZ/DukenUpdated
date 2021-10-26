package com.example.duken;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duken.adapter.OrderListAdapter;
import com.example.duken.data.AddProductData;
import com.example.duken.data.SharedData;
import com.example.duken.dialog.EnterCountDialog;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ShoppingCartFragment extends Fragment implements OrderListAdapter.OnItemClick{

    private TextView numberOfProductsTextView;
    private RecyclerView recyclerView;
    private Button makeOrderButton;
    private FloatingActionButton addProductFloatingActionButton;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    List<AddProductData> addProductDataList;
    OrderListAdapter listAdapter;

    int numberOfProducts = 0;
    int totalPrice = 0;
    int priceForOne = 0;
    String scannedCode;
    boolean exist = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        numberOfProductsTextView = view.findViewById(R.id.numberOfProductsTextView);
        recyclerView = view.findViewById(R.id.recyclerView);
        makeOrderButton = view.findViewById(R.id.saveButton);
        addProductFloatingActionButton = view.findViewById(R.id.addProductFloatingButton);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        makeOrderButton.setText(R.string.makeOrder + totalPrice);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.item_divider));
        recyclerView.addItemDecoration(itemDecorator);
        addProductDataList = new ArrayList<>();

        listAdapter = new OrderListAdapter(addProductDataList, this);

        addProductFloatingActionButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            startActivityForResult(intent, 0);
        });


        return view;
    }

    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode == 0) {
            if(resultCode == CommonStatusCodes.SUCCESS) {
                if(data != null) {
                    Barcode barcode = data.getParcelableExtra("scannedCode");
                    final String barcodeNumber = barcode.displayValue;
                    SharedData.setBarcodeNumber(barcodeNumber);
                    scannedCode = barcodeNumber;
                    doesExist(barcodeNumber);
                } else {
                    Toast.makeText(getContext(), "No code found. Try again", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void doesExist(String barcodeNumber) {
        mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(barcodeNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AddProductData addProductData = snapshot.getValue(AddProductData.class);
                priceForOne = Integer.parseInt(addProductData.getmPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(addProductDataList.isEmpty()){
            mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(barcodeNumber).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AddProductData productData = snapshot.getValue(AddProductData.class);
                    SharedData.setMaxCount(Integer.parseInt(productData.getmPieces()));
                    openDialog();
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        } else {
            for(final AddProductData productData: addProductDataList){
                if(productData.getmBarcodeNumber().equals(barcodeNumber)) {
                    exist = true;
                    countSetter(productData);
                    return;
                }
            }
            newProduct();
        }
    }

    private void countSetter(final AddProductData productData) {
        mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(scannedCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AddProductData productData1 = snapshot.getValue(AddProductData.class);
                int totalPieces = Integer.parseInt(productData1.getmPieces());
                int currentPieces = Integer.parseInt(productData.getmPieces());
                SharedData.setMaxCount(totalPieces - currentPieces);
                openDialog();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

    private void newProduct() {
        mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(scannedCode).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AddProductData productData = snapshot.getValue(AddProductData.class);
                SharedData.setMaxCount(Integer.parseInt(productData.getmPieces()));
                openDialog();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void openDialog() {
        EnterCountDialog enterCountDialog = new EnterCountDialog();
        enterCountDialog.show(getChildFragmentManager(), "Dialog");
    }

    @Override
    public void onDelete(int position) {
        int price = Integer.parseInt(addProductDataList.get(position).getmPrice());
        totalPrice = totalPrice - price;
        addProductDataList.remove(position);
        makeOrderButton.setText(R.string.makeOrder + totalPrice);
        numberOfProducts = addProductDataList.size();
        numberOfProductsTextView.setText(numberOfProducts + " products");
        listAdapter.notifyItemRemoved(position);
    }

    public void setCount(final String pieces) {
        if(exist == true) {
            for(AddProductData productData: addProductDataList) {
                if(scannedCode.equals(productData.getmBarcodeNumber())) {
                    int curPieces = Integer.parseInt(productData.getmPieces());
                    int piecesToAdd = Integer.parseInt(pieces);
                    productData.setmPieces(Integer.toString(curPieces + piecesToAdd));
                    productData.setmPrice(Integer.toString(priceForOne * (curPieces + piecesToAdd)));
                    totalPrice -= priceForOne * curPieces;
                    totalPrice += priceForOne * (curPieces + piecesToAdd);
                    makeOrderButton.setText("Make order for $" + totalPrice);
                    recyclerView.setAdapter(listAdapter);
                    exist = false;
                }
            }
        } else {
            mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(scannedCode).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    AddProductData productData = snapshot.getValue(AddProductData.class);
                    productData.setmPieces(pieces);
                    int piecesInt = Integer.parseInt(pieces);
                    String tPrice = Integer.toString(priceForOne * piecesInt);
                    productData.setmPrice(tPrice);
                    totalPrice += priceForOne * Integer.parseInt(pieces);
                    makeOrderButton.setText(R.string.makeOrder + totalPrice);
                    addProductDataList.add(productData);
                    recyclerView.setAdapter(listAdapter);
                    numberOfProducts = addProductDataList.size();
                    if(numberOfProducts == 1){
                        numberOfProductsTextView.setText(numberOfProducts + " product");
                    } else {
                        numberOfProductsTextView.setText(numberOfProducts + " products");
                    }
                    exist = false;
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }
}