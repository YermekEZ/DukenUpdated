package com.example.duken;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.duken.adapter.OrderListAdapter;
import com.example.duken.data.AddProductData;
import com.example.duken.data.OrderData;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class ShoppingCartFragment extends Fragment implements OrderListAdapter.OnItemClick, EnterCountDialog.EnterCountDialogListener {

    private ShoppingCartFragmentListener shoppingCartFragmentListener;

    private TextView numberOfProductsTextView;
    private RecyclerView recyclerView;
    private Button makeOrderButton;
    private FloatingActionButton addProductFloatingActionButton;
    private ProgressBar progressBar;

    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    List<AddProductData> productDataList;
    OrderListAdapter listAdapter;

    int numberOfProducts = 0;
    int totalPrice = 0;
    int priceForOne = 0;
    String scannedCode;
    boolean exist = false;

    public interface ShoppingCartFragmentListener{
        void productsUpdated();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_shopping_cart, container, false);

        numberOfProductsTextView = view.findViewById(R.id.numberOfProductsTextView);
        recyclerView = view.findViewById(R.id.recyclerView);
        makeOrderButton = view.findViewById(R.id.saveButton);
        addProductFloatingActionButton = view.findViewById(R.id.addProductFloatingButton);
        progressBar = view.findViewById(R.id.progress_circular);

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference();

        makeOrderButton.setText("Make order for $" + totalPrice);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.item_divider));
        recyclerView.addItemDecoration(itemDecorator);
        productDataList = new ArrayList<>();

        listAdapter = new OrderListAdapter(productDataList, this);

        addProductFloatingActionButton.setOnClickListener(view1 -> {
            Intent intent = new Intent(getActivity(), ScanActivity.class);
            startActivityForResult(intent, 0);
        });

        makeOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressBar.setVisibility(View.VISIBLE);
                if(numberOfProducts == 0){
                    Toast.makeText(view.getContext(), "First add products!", Toast.LENGTH_SHORT).show();
                    return;
                }
                SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
                Date date = new Date();
                String currentDate = formatter.format(date);

                for(int i = 0; i < productDataList.size(); i++) {
                    final String barcode = productDataList.get(i).getmBarcodeNumber();
                    String name = productDataList.get(i).getmProductName();
                    String price = productDataList.get(i).getmPrice();
                    final String pieces = productDataList.get(i).getmPieces();

                    OrderData orderData = new OrderData(name, price, pieces, currentDate, Integer.toString(totalPrice));
                    mDatabaseReference.child("orders").child(SharedData.getPhoneNumber()).child(currentDate).child("products")
                            .child(barcode).setValue(orderData);

                    mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(barcode)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    AddProductData productData = snapshot.getValue(AddProductData.class);
                                    int maxPieces = Integer.parseInt(productData.getmPieces());
                                    maxPieces = maxPieces - Integer.parseInt(pieces);
                                    mDatabaseReference.child("products").child(SharedData.getPhoneNumber())
                                            .child(barcode).child("mPieces").setValue(Integer.toString(maxPieces));
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                }
                productDataList.clear();
                recyclerView.setAdapter(listAdapter);
                numberOfProducts = 0;
                numberOfProductsTextView.setText(numberOfProducts + " products");
                totalPrice = 0;
                makeOrderButton.setText("Make order for $" + totalPrice);

                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        shoppingCartFragmentListener.productsUpdated();
                        progressBar.setVisibility(View.INVISIBLE);
                        Toast.makeText(view.getContext(), "Order has been saved successfully!", Toast.LENGTH_SHORT).show();
                    }
                }, 3000);

            }
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
                    isInDatabase(barcodeNumber);
                } else {
                    Toast.makeText(getContext(), "No code found. Try again", Toast.LENGTH_SHORT).show();
                }
            }

        }
    }

    private void isInDatabase(String barcodeNumber) {
        mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.hasChild(barcodeNumber)){
                    doesExist(barcodeNumber);
                } else {
                    Toast.makeText(getContext(), "Scanned product has not been added yet. Please, add it first.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void doesExist(final String barcodeNumber) {

        mDatabaseReference.child("products").child(SharedData.getPhoneNumber()).child(barcodeNumber).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                AddProductData productData = snapshot.getValue(AddProductData.class);
                priceForOne = Integer.parseInt(productData.getmPrice());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        if(productDataList.isEmpty()){
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
            for(final AddProductData productData: productDataList){
                if(productData.getmBarcodeNumber().equals(barcodeNumber)) {
                    exist = true;
                    countSetter(productData);
                    return;
                }
            }
            newProduct();
        }

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

    private void openDialog() {
        EnterCountDialog enterCountDialog = new EnterCountDialog();
        enterCountDialog.show(getChildFragmentManager(), "Enter number of products");
    }

    @Override
    public void setCount(final String pieces) {
        int quantity = Integer.parseInt(pieces);
        if(quantity > SharedData.getMaxCount()){
            Toast.makeText(getContext(), "Maximum number of pieces for this product is " + SharedData.getMaxCount() +
                    ". Please, enter correct quantity for this product", Toast.LENGTH_SHORT).show();
        } else if (quantity == 0){
            Toast.makeText(getContext(), "You cannot add 0 pieces. Please, enter correct quantity.", Toast.LENGTH_SHORT).show();
        } else {
            setCountAndPrice(pieces);
        }

    }

    private void setCountAndPrice(String pieces) {
        if(exist == true) {
            for(AddProductData productData: productDataList) {
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
                    makeOrderButton.setText("Make order for $" + totalPrice);
                    productDataList.add(productData);
                    recyclerView.setAdapter(listAdapter);
                    numberOfProducts = productDataList.size();
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


    @Override
    public void onDelete(int position) {
        int price = Integer.parseInt(productDataList.get(position).getmPrice());
        totalPrice = totalPrice - price;
        productDataList.remove(position);
        makeOrderButton.setText("Make order for $" + totalPrice);
        numberOfProducts = productDataList.size();
        numberOfProductsTextView.setText(numberOfProducts + " products");
        listAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        if (context instanceof ShoppingCartFragment.ShoppingCartFragmentListener){
            shoppingCartFragmentListener = (ShoppingCartFragmentListener) context;
        } else {
            throw new RuntimeException(context.toString() + "implement ShoppingCartFragmentListener");
        }
    }

    @Override
    public void onDetach(){
        super.onDetach();
        shoppingCartFragmentListener = null;
    }
}