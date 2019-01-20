package com.example.user.aplikasiperpustakaan.adapter;

import android.app.Activity;
import android.content.Context;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.user.aplikasiperpustakaan.R;
import com.example.user.aplikasiperpustakaan.data.Data;

import java.util.List;

public class Adapter extends BaseAdapter {
    Activity activity;
    private LayoutInflater inflater;
    private List<Data> items;

    public Adapter(Activity activity, List<Data>items){
        this.activity = activity;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int location) {
        return items.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View converterView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater)activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (converterView == null)
            converterView = inflater.inflate(R.layout.list_row, null);

        TextView idBuku = (TextView)converterView.findViewById(R.id.tv_id_buku);
        TextView judulBuku = (TextView)converterView.findViewById(R.id.tv_judul_buku);
        TextView namaPeminjam = (TextView)converterView.findViewById(R.id.tv_nama_peminjam);
        TextView noTelepone = (TextView)converterView.findViewById(R.id.tv_no_telepone);
        TextView alamat = (TextView)converterView.findViewById(R.id.tv_alamat);
        TextView tglPinjam = (TextView)converterView.findViewById(R.id.tv_tgl_pinjam);
        TextView tglKembali = (TextView)converterView.findViewById(R.id.tv_tgl_kembali);

        Data data = items.get(position);

        idBuku.setText(data.getIdBuku());
        judulBuku.setText(data.getJudulBuku());
        namaPeminjam.setText(data.getNamaPeminjam());
        noTelepone.setText(data.getNoTelepone());
        alamat.setText(data.getAlamat());
        tglPinjam.setText(data.getTglPinjam());
        tglKembali.setText(data.getTglKembali());

        return converterView;

    }
}
