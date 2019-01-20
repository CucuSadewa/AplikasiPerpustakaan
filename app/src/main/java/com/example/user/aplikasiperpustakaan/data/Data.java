package com.example.user.aplikasiperpustakaan.data;

public class Data {
    private String idBuku, namaPeminjam, judulBuku, noTelepone, alamat, tglPinjam, tglKembali;

    public Data() {
    }

    public Data(String idBuku, String namaPeminjam, String judulBuku, String noTelepone, String alamat, String tglPinjam,
                String tglKembali) {
        this.idBuku = idBuku;
        this.namaPeminjam = namaPeminjam;
        this.judulBuku = judulBuku;
        this.noTelepone = noTelepone;
        this.alamat = alamat;
        this.tglPinjam = tglPinjam;
        this.tglKembali = tglKembali;
    }

    public String getIdBuku() {
        return idBuku;
    }

    public void setIdBuku(String idBuku) {
        this.idBuku = idBuku;
    }

    public String getNamaPeminjam() {
        return namaPeminjam;
    }

    public void setNamaPeminjam(String namaPeminjam) {
        this.namaPeminjam = namaPeminjam;
    }

    public String getJudulBuku() {
        return judulBuku;
    }

    public void setJudulBuku(String judulBuku) {
        this.judulBuku = judulBuku;
    }

    public String getNoTelepone() {
        return noTelepone;
    }

    public void setNoTelepone(String noTelepone) {
        this.noTelepone = noTelepone;
    }

    public String getAlamat() {
        return alamat;
    }

    public void setAlamat(String alamat) {
        this.alamat = alamat;
    }

    public String getTglPinjam() {
        return tglPinjam;
    }

    public void setTglPinjam(String tglPinjam) {
        this.tglPinjam = tglPinjam;
    }

    public String getTglKembali() {
        return tglKembali;
    }

    public void setTglKembali(String tglKembali) {
        this.tglKembali = tglKembali;
    }
}

