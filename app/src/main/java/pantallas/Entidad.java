package pantallas;

public class Entidad {

    private int imgFoto;
    private String titulo;
    private String contenido;
    private String detalles;

    public Entidad(int imgFoto, String titulo, String contenido, String detalles){
        this.imgFoto = imgFoto;
        this.titulo = titulo;
        this.contenido = contenido;
        this.detalles = detalles;
    }

    public int getImgFoto(){return imgFoto;}

    public String getTitulo(){return titulo;}

    public String getContenido(){return contenido;}

    public String getDetalles(){return detalles;}

}
