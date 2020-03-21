from pypassport.obtener_imagen import obtener_img

def main():
    #ruta_formato_archivo = "/home/user/passp3/obtener_imagen/passport.png"
    ruta_formato_archivo = "/home/pi/de_imagen/foto.png"
    mrz = "00001K6JF794032132904250"
    #mrz = "00001JUDA197082172904087"
    obtener_img(ruta_formato_archivo,mrz)


if __name__ == "__main__":
    main()
