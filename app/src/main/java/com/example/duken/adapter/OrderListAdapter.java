package com.example.duken.adapter;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.duken.R;
import com.example.duken.data.AddProductData;

import java.util.List;

public class OrderListAdapter extends RecyclerView.Adapter {

    List<AddProductData> aProductData;
    private OnItemClick mListener;

    public interface OnItemClick {
        void onDelete(int position);
    }

    public OrderListAdapter(List<AddProductData> aProductData, OnItemClick onItemClick){
        this.aProductData = aProductData;
        this.mListener = onItemClick;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.order_list_layout, parent, false);
        ViewHolderClass viewHolderClass = new OrderListAdapter.ViewHolderClass(view, mListener);

        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClass viewHolderClass = (ViewHolderClass) holder;

        AddProductData addProductData = aProductData.get(position);
        viewHolderClass.productName.setText(addProductData.getmProductName());
        viewHolderClass.barcodeNumber.setText(addProductData.getmBarcodeNumber());
        viewHolderClass.pieces.setText(addProductData.getmPieces() + "pcs.");
        viewHolderClass.price.setText("$" + addProductData.getmPrice());

    }

    @Override
    public int getItemCount() {
        return aProductData.size();
    }

    public class ViewHolderClass extends RecyclerView.ViewHolder{

        TextView productName, barcodeNumber, pieces, price;
        ImageButton deleteProductImageButton;
        private OnItemClick mListener;

        public ViewHolderClass(@NonNull View itemView, OnItemClick onItemClick) {
            super(itemView);
            productName = itemView.findViewById(R.id.productName);
            barcodeNumber = itemView.findViewById(R.id.barcodeNumber);
            pieces = itemView.findViewById(R.id.pieces);
            price = itemView.findViewById(R.id.price);
            deleteProductImageButton = itemView.findViewById(R.id.deleteProductImageButton);

            this.mListener = onItemClick;

            deleteProductImageButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mListener.onDelete(getAdapterPosition());
                }
            });

        }
    }
}