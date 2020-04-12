package pantallas;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.tananaev.passportreader.R;

import java.util.ArrayList;

public class Adaptador extends BaseAdapter {

    private Context context;
    private ArrayList<Entidad> listItems;

    public Adaptador(Context context, ArrayList<Entidad> listItems){
        this.context = context;
        this.listItems = listItems;
    }

    public int getCount(){
        return listItems.size();
    }

    public Object getItem(int position){
        return listItems.get(position);
    }

    public long getItemId(int position){
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entidad Item = (Entidad) getItem(position);

        convertView = LayoutInflater.from(context).inflate(R.layout.item,null);
        ImageView imgFoto = (ImageView) convertView.findViewById(R.id.imgFoto);
        TextView tituloTarjeta = (TextView) convertView.findViewById(R.id.tituloTarjeta);
        TextView contenidoTarjeta = (TextView) convertView.findViewById(R.id.contenidoTarjeta);
        Button btnSolicitar = (Button) convertView.findViewById(R.id.btnSolicitar);
        Button btnVerDetalles = (Button) convertView.findViewById(R.id.btnVerDetalles);

        imgFoto.setImageResource(Item.getImgFoto());
        tituloTarjeta.setText(Item.getTitulo());
        contenidoTarjeta.setText(Item.getContenido());

        return convertView;
    }


}

