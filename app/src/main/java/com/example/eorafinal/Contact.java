package com.example.eorafinal;

public class Contact {
    private String numero;
    private int img;

    public Contact (String numero, int img){
        this.numero = numero;
        this.img = img;
    }

    public String getNumero() {
        return numero;
    }

    public int getImg() {
        return img;
    }
}
