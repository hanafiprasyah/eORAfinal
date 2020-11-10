package com.example.eorafinal;

class ModelLihatMahasiswa {
    private String NIM_mahasiswa;
    private String nama_mahasiswa;
    private String tempat_lahir;
    private String tanggal_lahir;
    private String jk;
    private String email_mahasiswa;
    private String notel_mahasiswa;
    private String foto_mahasiswa;

    public ModelLihatMahasiswa() {
        this.NIM_mahasiswa = NIM_mahasiswa;
        this.nama_mahasiswa = nama_mahasiswa;
        this.tempat_lahir = tempat_lahir;
        this.tanggal_lahir = tanggal_lahir;
        this.jk = jk;
        this.email_mahasiswa = email_mahasiswa;
        this.notel_mahasiswa = notel_mahasiswa;
        this.foto_mahasiswa = foto_mahasiswa;
    }

    public String getNIM_mahasiswa() {
        return NIM_mahasiswa;
    }

    public void setNIM_mahasiswa(String NIM_mahasiswa) {
        this.NIM_mahasiswa = NIM_mahasiswa;
    }

    public String getNama_mahasiswa() {
        return nama_mahasiswa;
    }

    public void setNama_mahasiswa(String nama_mahasiswa) {
        this.nama_mahasiswa = nama_mahasiswa;
    }

    public String getTempat_lahir() {
        return tempat_lahir;
    }

    public void setTempat_lahir(String tempat_lahir) {
        this.tempat_lahir = tempat_lahir;
    }

    public String getTanggal_lahir() {
        return tanggal_lahir;
    }

    public void setTanggal_lahir(String tanggal_lahir) {
        this.tanggal_lahir = tanggal_lahir;
    }

    public String getJk() {
        return jk;
    }

    public void setJk(String jk) {
        this.jk = jk;
    }

    public String getEmail_mahasiswa() {
        return email_mahasiswa;
    }

    public void setEmail_mahasiswa(String email_mahasiswa) {
        this.email_mahasiswa = email_mahasiswa;
    }

    public String getNotel_mahasiswa() {
        return notel_mahasiswa;
    }

    public void setNotel_mahasiswa(String notel_mahasiswa) {
        this.notel_mahasiswa = notel_mahasiswa;
    }

    public String getFoto_mahasiswa() {
        return foto_mahasiswa;
    }

    public void setFoto_mahasiswa(String foto_mahasiswa) {
        this.foto_mahasiswa = foto_mahasiswa;
    }
}
