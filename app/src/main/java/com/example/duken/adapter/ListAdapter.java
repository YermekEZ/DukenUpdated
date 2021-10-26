package com.example.duken.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duken.R;
import com.example.duken.data.AddProductData;

import java.util.List;

public class ListAdapter extends RecyclerView.Adapter {

    List<AddProductData> addProductData;

    public ListAdapter(List<AddProductData> addProductData){
        this.addProductData = addProductData;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        ViewHolderClass viewHolderClass = new ViewHolderClass(view);

        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        AddProductData productData = addProductData.get(position);
        viewHolderClass.productName.setText(productData.getmProductName());
        viewHolderClass.barcodeNumber.setText(productData.getmBarcodeNumber());
        viewHolderClass.pieces.setText(productData.getmPieces() + "pcs.");
        viewHolderClass.price.setText("$" + productData.getmPrice());
    }

    @Override
    public int getItemCount() {
        return addProductData.size();
    }

    public void filtered(List<AddProductData> filteredList) {
        addProductData = filteredList;
        notifyDataSetChanged();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{

        TextView productName, barcodeNumber, pieces, price;

        public ViewHolderClass(@NonNull View itemView) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            barcodeNumber = itemView.findViewById(R.id.barcodeNumber);
            pieces = itemView.findViewById(R.id.pieces);
            price = itemView.findViewById(R.id.price);
        }
    }
}
