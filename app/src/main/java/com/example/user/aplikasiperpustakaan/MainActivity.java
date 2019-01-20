package com.example.user.aplikasiperpustakaan;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.StringRequest;
import com.example.user.aplikasiperpustakaan.adapter.Adapter;
import com.example.user.aplikasiperpustakaan.app.AppController;
import com.example.user.aplikasiperpustakaan.data.Data;
import com.example.user.aplikasiperpustakaan.server.Server;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.user.aplikasiperpustakaan.Login.TAG_ID;
import static com.example.user.aplikasiperpustakaan.Login.TAG_USERNAME;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    Toolbar toolbar;
    FloatingActionButton fab;
    ListView list;
    SwipeRefreshLayout swipe;
    List<Data> itemList = new ArrayList<Data>();
    Adapter adapter;
    int success;
    AlertDialog.Builder dialog;
    LayoutInflater inflater;
    View dialogView;
    EditText edt_idbuku, edt_judulbuku, edt_alamat, edt_namapeminjam, edt_tglpinjam, edt_tglkembali,
    edt_notelepone;
    String idBuku, judulBuku, namaPeminjam, noTelepone, alamat, tglPinjam, tglKembali;
    SharedPreferences sharedPreferences;
    private static final String TAG = MainActivity.class.getSimpleName();

    private static String url_select    = Server.URL + "select.php";
    private static String url_insert    = Server.URL + "insert.php";
    private static String url_update    = Server.URL + "update.php";
    private static String url_edit      = Server.URL + "edit.php";
    private static String url_delete    = Server.URL + "delete.php";

    public static final String TAG_ID               = "id_buku";
    public static final String TAG_JUDUL_BUKU       = "judul_buku";
    public static final String TAG_NAMA_PEMINJAM    = "nama_peminjam";
    public static final String TAG_NO_TELEPONE      = "no_telepone";
    public static final String TAG_ALAMAT           = "alamat";
    public static final String TAG_TGL_PINJAM       = "tgl_pinjam";
    public static final String TAG_TGL_KEMBALI      = "tgl_kembali";
    private static final String TAG_SUCCESS          = "success";
    private static final String TAG_MESSAGE          = "message";

    public static final String TAG_ID_LOGIN         = "id";
    public static final String TAG_USERNAME_LOGIN   = "username";

    String tag_json_obj = "json_obj_req";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // menghubungkan variablel pada layout dan pada java
        fab     = (FloatingActionButton) findViewById(R.id.fab_add);
        swipe   = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        list    = (ListView) findViewById(R.id.list);

        // untuk mengisi data dari JSON ke dalam adapter
        adapter = new Adapter(MainActivity.this, itemList);
        list.setAdapter(adapter);

        sharedPreferences = getSharedPreferences(Login.my_shared_preferences, Context.MODE_PRIVATE);


        // menamilkan widget refresh
        swipe.setOnRefreshListener(this);


        swipe.post(new Runnable() {
                       @Override
                       public void run() {
                           swipe.setRefreshing(true);
                           itemList.clear();
                           adapter.notifyDataSetChanged();
                           callVolley();
                       }
                   });

        // fungsi floating action button memanggil form biodata
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DialogForm("", "", "", "",
                        "", "", "", "SIMPAN");
            }
        });

        // listview ditekan lama akan menampilkan dua pilihan edit atau delete data
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view,
                                           final int position, long id) {
                // TODO Auto-generated method stub
                final String idBukux = itemList.get(position).getIdBuku();

                final CharSequence[] dialogItem = {"Edit", "Delete"};
                dialog = new AlertDialog.Builder(MainActivity.this);
                dialog.setCancelable(true);
                dialog.setItems(dialogItem, new DialogInterface.OnClickListener() {

                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // TODO Auto-generated method stub
                        switch (which){
                            case 0:
                                edit(idBukux);
                                break;
                            case 1:
                                delete(idBukux);
                                break;
                        }
                    }
                }).show();
                return false;
            }
        });
    }
    @Override
    public void onRefresh() {
        itemList.clear();
        adapter.notifyDataSetChanged();
        callVolley();
    }
    private void kosong() {

        edt_idbuku.setText(null);
        edt_judulbuku.setText(null);
        edt_namapeminjam.setText(null);
        edt_tglpinjam.setText(null);
        edt_tglkembali.setText(null);
        edt_notelepone.setText(null);
        edt_alamat.setText(null);
    }

    // untuk menampilkan dialog form tb_pinjam
    private  void DialogForm(final String idBukux,  String judulBukux, String namaPeminjamx, String tglPinjamx,
                             String tglKembalix, String noTeleponex, String alamatx, String button) {
        dialog = new AlertDialog.Builder(MainActivity.this);
        inflater = getLayoutInflater();
        dialogView = inflater.inflate(R.layout.form_tb_pinjam, null);
        dialog.setView(dialogView);
        dialog.setCancelable(true);
        dialog.setIcon(R.mipmap.ic_launcher);
        dialog.setTitle("Form Peminjaman");

        edt_idbuku          = (EditText)dialogView.findViewById(R.id.edt_id_buku);
        edt_judulbuku       = (EditText)dialogView.findViewById(R.id.edt_judul_buku);
        edt_namapeminjam    = (EditText)dialogView.findViewById(R.id.edt_nama_peminjam);
        edt_tglpinjam       = (EditText)dialogView.findViewById(R.id.edt_tgl_pinjam);
        edt_tglkembali      = (EditText)dialogView.findViewById(R.id.edt_tgl_kembali);
        edt_notelepone      = (EditText)dialogView.findViewById(R.id.edt_no_telepone);
        edt_alamat          = (EditText)dialogView.findViewById(R.id.edt_alamat);

        if (!idBukux.isEmpty()){
            edt_idbuku.setText(idBukux);
            edt_judulbuku.setText(judulBukux);
            edt_namapeminjam.setText(namaPeminjamx);
            edt_tglpinjam.setText(tglPinjamx);
            edt_tglkembali.setText(tglKembalix);
            edt_notelepone.setText(noTeleponex);
            edt_alamat.setText(alamatx);
        }else {
            kosong();
        }

        dialog.setPositiveButton(button, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                idBuku      = edt_idbuku.getText().toString();
                judulBuku   = edt_judulbuku.getText().toString();
                namaPeminjam= edt_namapeminjam.getText().toString();
                tglPinjam   = edt_tglpinjam.getText().toString();
                tglKembali  = edt_tglkembali.getText().toString();
                noTelepone  = edt_notelepone.getText().toString();
                alamat      = edt_alamat.getText().toString();

                simpan_update();
                dialog.dismiss();
            }
        });

        dialog.setNegativeButton("BATAL", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                kosong();
            }
        });

        dialog.show();
    }

    // untuk menampilkan semua data pada listview
    private void callVolley(){
        itemList.clear();
        adapter.notifyDataSetChanged();
        swipe.setRefreshing(true);

        // membuat request JSON
        JsonArrayRequest jArr = new JsonArrayRequest(url_select, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.d(TAG, response.toString());

                // Parsing json
                for (int i = 0; i < response.length(); i++) {
                    try {
                        JSONObject obj = response.getJSONObject(i);

                        Data item = new Data();

                        item.setIdBuku(obj.getString(TAG_ID));
                        item.setJudulBuku(obj.getString(TAG_JUDUL_BUKU));
                        item.setNamaPeminjam(obj.getString(TAG_NAMA_PEMINJAM));
                        item.setNoTelepone(obj.getString(TAG_NO_TELEPONE));
                        item.setAlamat(obj.getString(TAG_ALAMAT));
                        item.setTglPinjam(obj.getString(TAG_TGL_PINJAM));
                        item.setTglKembali(obj.getString(TAG_TGL_KEMBALI));


                        // menambah item ke array
                        itemList.add(item);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                // notifikasi adanya perubahan data pada adapter
                adapter.notifyDataSetChanged();

                swipe.setRefreshing(false);
            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.d(TAG, "Error: " + error.getMessage());
                swipe.setRefreshing(false);
            }
        });

        // menambah request ke request queue
        AppController.getInstance().addToRequestQueue(jArr);
    }

    // fungsi untuk menyimpan atau update
    private void simpan_update() {
        String url;
        // jika id kosong maka simpan, jika id ada nilainya maka update
        if (idBuku.isEmpty()){
            url = url_insert;
        }else {
            url = url_update;
        }

        StringRequest strReq = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: "+ response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek Error node pada json
                    if (success == 1){
                        Log.d("Add/Update", jObj.toString());

                        callVolley();
                        kosong();

                        Toast.makeText(MainActivity.this,jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                        adapter.notifyDataSetChanged();

                    }else {
                        Toast.makeText(MainActivity.this,jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error){
                Log.e(TAG, "Error: "+ error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }){

            @Override
            protected Map<String, String> getParams(){
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();

                // jika id kosong maka simpan, jika id ada nilainya maka update
                if (idBuku.isEmpty()){
                    params.put( "judul_buku", judulBuku);
                    params.put("nama_peminjam", namaPeminjam);
                    params.put("tgl_pinjam", tglPinjam);
                    params.put("tgl_kembali", tglKembali);
                    params.put("no_telepone", noTelepone);
                    params.put("alamat", alamat);
                }else {
                    params.put("id_buku", idBuku);
                    params.put( "judul_buku", judulBuku);
                    params.put("nama_peminjam", namaPeminjam);
                    params.put("tgl_pinjam", tglPinjam);
                    params.put("tgl_kembali", tglKembali);
                    params.put("no_telepone", noTelepone);
                    params.put("alamat", alamat);
                }
                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    //fungsi untuk get edit data
    private void edit(final String idBukux){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_edit, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG,"Response :" + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1){
                        Log.d("get edit data", jObj.toString());
                        String idBukux      = jObj.getString(TAG_ID);
                        String judulBukux   = jObj.getString(TAG_JUDUL_BUKU);
                        String namaPeminjamx= jObj.getString(TAG_NAMA_PEMINJAM);
                        String tglPinjamx   = jObj.getString(TAG_TGL_PINJAM);
                        String tglKembalix  = jObj.getString(TAG_TGL_KEMBALI);
                        String noTeleponex  = jObj.getString(TAG_NO_TELEPONE);
                        String alamatx      = jObj.getString(TAG_ALAMAT);

                        DialogForm(idBukux,judulBukux,namaPeminjamx,tglPinjamx,tglKembalix,noTeleponex,alamatx, "UPDATE");


                        adapter.notifyDataSetChanged();
                    }else {
                        Toast.makeText(MainActivity.this,jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                }catch (JSONException e){
                    //JSON error
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener(){

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error : " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams(){
                // Posting parameters ke post
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_buku", idBukux);

                return params;
            }

        };
        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }

    // fungsi untuk menghapus
    private void delete(final String idBukux){
        StringRequest strReq = new StringRequest(Request.Method.POST, url_delete, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, "Response: " + response.toString());

                try {
                    JSONObject jObj = new JSONObject(response);
                    success = jObj.getInt(TAG_SUCCESS);

                    // Cek error node pada json
                    if (success == 1) {
                        Log.d("delete", jObj.toString());

                        callVolley();

                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();

                        adapter.notifyDataSetChanged();

                    } else {
                        Toast.makeText(MainActivity.this, jObj.getString(TAG_MESSAGE), Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    // JSON error
                    e.printStackTrace();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "Error: " + error.getMessage());
                Toast.makeText(MainActivity.this, error.getMessage(), Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters ke post url
                Map<String, String> params = new HashMap<String, String>();
                params.put("id_buku", idBukux);

                return params;
            }

        };

        AppController.getInstance().addToRequestQueue(strReq, tag_json_obj);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate( R.menu.menu_main, menu );
        return super.onCreateOptionsMenu( menu );
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_logout:
                logout();
                break;
        }
        return super.onOptionsItemSelected( item );
    }

    private void logout() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putBoolean( Login.session_status, false );
        editor.putString( TAG_ID, null );
        editor.putString( TAG_USERNAME, null );
        editor.commit();

        Intent intent = new Intent( MainActivity.this, Login.class );
        finish();
        startActivity( intent );
    }




}
