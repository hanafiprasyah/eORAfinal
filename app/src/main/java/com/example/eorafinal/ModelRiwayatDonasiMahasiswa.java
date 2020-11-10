package com.example.eorafinal;

class ModelRiwayatDonasiMahasiswa {
    private String NIM;
    private String nama_donatur;
    private String status_donasi;

    public String getNIM() {
        return NIM;
    }

    public void setNIM(String NIM) {
        this.NIM = NIM;
    }

    public String getNama_donatur() {
        return nama_donatur;
    }

    public void setNama_donatur(String nama_donatur) {
        this.nama_donatur = nama_donatur;
    }

    public String getStatus_donasi() {
        return status_donasi;
    }

    public void setStatus_donasi(String status_donasi) {
        this.status_donasi = status_donasi;
    }
}
