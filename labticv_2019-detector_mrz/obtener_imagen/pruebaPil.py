import io
from PIL import Image
import struct


def hexRepToBin(string):
    #"""'AABB' --> \xaa\xbb'"""
    output= b''
    x= 0
    while x < len(string):
            output += struct.pack('B', int(string[x:x + 2],16))
            x += 2
    return output

# anda desde python3
#picture = Image.open("/home/pi/Desktop/labticv_2019/Imagenes_Cedula/picture.jpg")
image_path ="/home/pi/Desktop/labticv_2019/obtener_imagen/prueba.jpeg"
#picture.save(image_path, "JPEG",optimize=True,quality=65)
with open("../imagenHexadecimal.txt") as ix:
        linea = ix.readline()
        picture = hexRepToBin(linea)

imgfp = io.BytesIO(picture)
img = Image.open(imgfp)
img.save(image_path)
