package com.example.eorafinal;

class ModelKelolaDonatur {
    private String id_reg_donatur;
    private String nama_donatur;
    private String email_donatur;
    private String foto_donatur;

    public String getFoto_donatur() {
        return foto_donatur;
    }

    public void setFoto_donatur(String foto_donatur) {
        this.foto_donatur = foto_donatur;
    }

    public String getId_reg_donatur() {
        return id_reg_donatur;
    }

    public void setId_reg_donatur(String id_reg_donatur) {
        this.id_reg_donatur = id_reg_donatur;
    }

    public String getNama_donatur() {
        return nama_donatur;
    }

    public void setNama_donatur(String nama_donatur) {
        this.nama_donatur = nama_donatur;
    }

    public String getEmail_donatur() {
        return email_donatur;
    }

    public void setEmail_donatur(String email_donatur) {
        this.email_donatur = email_donatur;
    }
}
