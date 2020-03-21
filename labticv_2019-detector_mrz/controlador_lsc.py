from pypassport.obtener_imagen import obtener_img
from PIL import Image
import io
def main():
    ruta_archivo_formato = "/home/pi/Desktop/labticv_2019/Imagenes_Cedula/passport.png"
    mrz = "00001JUDA197082172904087"
    imagen = obtener_img(ruta_archivo_formato,mrz)
    #with open('./imagenHexadecimal.txt','a') as a:
    #    a.write(imagen.hex())

    # salta error del pilow
    imgfp = io.BytesIO(imagen)
    img = Image.open(imgfp)
    img.save(ruta_archivo_formato)

if __name__ == "__main__":
    main()
