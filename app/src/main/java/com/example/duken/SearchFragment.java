package com.example.duken;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import com.example.duken.adapter.ListAdapter;
import com.example.duken.data.AddProductData;
import com.example.duken.data.SharedData;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment {

    int productsNumber = 0;

    private TextView numberOfProducts;
    private RecyclerView recyclerView;
    private EditText searchEditText;
    List<AddProductData> productDataList;
    ListAdapter listAdapter;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference mDatabaseReference;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search, container, false);
        numberOfProducts = view.findViewById(R.id.numberOfProductsTextView);
        recyclerView = view.findViewById(R.id.recyclerView);
        searchEditText = view.findViewById(R.id.searchEditText);

        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(view.getContext()));
        DividerItemDecoration itemDecorator = new DividerItemDecoration(view.getContext(), DividerItemDecoration.VERTICAL);
        itemDecorator.setDrawable(ContextCompat.getDrawable(view.getContext(), R.drawable.item_divider));
        recyclerView.addItemDecoration(itemDecorator);

        loadData();

        searchEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                filter(editable.toString());
            }
        });

        return view;
    }

    public void loadData() {
        productDataList = new ArrayList<>();

        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabaseReference = mFirebaseDatabase.getReference("products").child(SharedData.getPhoneNumber());

        mDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot:snapshot.getChildren()){
                    AddProductData addProductData = dataSnapshot.getValue(AddProductData.class);
                    productDataList.add(addProductData);
                }
                listAdapter = new ListAdapter(productDataList);
                recyclerView.setAdapter(listAdapter);
                productsNumber = productDataList.size();
                if(productsNumber == 1){
                    numberOfProducts.setText(productsNumber + " product");
                } else {
                    numberOfProducts.setText(productsNumber + " products");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void filter(String text) {
        List<AddProductData> filteredList = new ArrayList<>();
        for(AddProductData productData: productDataList){
            if(productData.getmProductName().toLowerCase().contains(text.toLowerCase())) {
                filteredList.add(productData);
            }
        }
        productsNumber = filteredList.size();
        if(productsNumber == 1){
            numberOfProducts.setText(productsNumber + " product");
        } else {
            numberOfProducts.setText(productsNumber + " products");
        }
        listAdapter.filtered(filteredList);
    }
}