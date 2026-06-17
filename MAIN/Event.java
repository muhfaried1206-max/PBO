public class Event {

    private String id;
    private String nama;
    private String lokasi;
    private String tanggal;
    private double harga;

    public Event(String id, String nama,
                 String lokasi, String tanggal,
                 double harga) {

        this.id = id;
        this.nama = nama;
        this.lokasi = lokasi;
        this.tanggal = tanggal;
        this.harga = harga;
    }

    public String getId() {
        return id;
    }

    public String getNama() {
        return nama;
    }

    public void setNama(String nama) {
        this.nama = nama;
    }

    public String getLokasi() {
        return lokasi;
    }

    public void setLokasi(String lokasi) {
        this.lokasi = lokasi;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public double getHarga() {
        return harga;
    }

    public void setHarga(double harga) {
        this.harga = harga;
    }
}