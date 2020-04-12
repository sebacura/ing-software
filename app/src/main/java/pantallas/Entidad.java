package pantallas;

import android.widget.Button;

public class Entidad {

    private int imgFoto;
    private String titulo;
    private String contenido;
    private Button btnSolicitar;
    private Button btnVerDetalles;

    public Entidad(int imgFoto, String titulo, String contenido){
        this.imgFoto = imgFoto;
        this.titulo = titulo;
        this.contenido = contenido;
    }

    public int getImgFoto(){return imgFoto;}

    public String getTitulo(){return titulo;}

    public String getContenido(){return contenido;}

}
