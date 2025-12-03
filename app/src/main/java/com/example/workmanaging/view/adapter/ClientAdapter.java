package com.example.workmanaging.view.adapter;

import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.workmanaging.R;
import com.example.workmanaging.model.entity.Cliente;
import java.util.ArrayList;
import java.util.List;

public class ClientAdapter extends RecyclerView.Adapter<ClientAdapter.ClientViewHolder> {

    private List<Cliente> clients = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Cliente cliente);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setClients(List<Cliente> clients) {
        this.clients = clients;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ClientViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_client, parent, false);
        return new ClientViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull ClientViewHolder holder, int position) {
        Cliente currentClient = clients.get(position);
        holder.tvName.setText(currentClient.nome);
        holder.tvCompany.setText(currentClient.azienda != null ? currentClient.azienda : "N/A");

        if (currentClient.img != null && !currentClient.img.isEmpty()) {
            holder.ivImage.setVisibility(View.VISIBLE);
            holder.tvInitial.setVisibility(View.GONE);
            try {
                holder.ivImage.setImageURI(Uri.parse(currentClient.img));
            } catch (Exception e) {
                e.printStackTrace();
                holder.ivImage.setVisibility(View.GONE);
                holder.tvInitial.setVisibility(View.VISIBLE);
                setInitial(holder, currentClient);
            }
        } else {
            holder.ivImage.setVisibility(View.GONE);
            holder.tvInitial.setVisibility(View.VISIBLE);
            setInitial(holder, currentClient);
        }
        
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(currentClient);
            }
        });
    }

    private void setInitial(ClientViewHolder holder, Cliente client) {
        String initial = "";
        if (client.nome != null && !client.nome.isEmpty()) {
            initial = client.nome.substring(0, 1).toUpperCase();
        }
        holder.tvInitial.setText(initial);
    }

    @Override
    public int getItemCount() {
        return clients.size();
    }

    static class ClientViewHolder extends RecyclerView.ViewHolder {
        private TextView tvName;
        private TextView tvCompany;
        private ImageView ivImage;
        private TextView tvInitial;

        public ClientViewHolder(@NonNull View itemView) {
            super(itemView);
            tvName = itemView.findViewById(R.id.tv_client_name);
            tvCompany = itemView.findViewById(R.id.tv_company_name);
            ivImage = itemView.findViewById(R.id.iv_client_image);
            tvInitial = itemView.findViewById(R.id.tv_client_initial);
        }
    }
}
