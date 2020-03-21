from pypassport.reader import ReaderManager,PcscReader
from pypassport.epassport import EPassport, mrz,apdu
import io
import pypassport.apdu as apd
#from PIL import Image


# entradas: 
# ruta del directorio, nombre de archivo y extension
#           por ejemplo "/home/user/passp3/pypassport-master/passport.png"
# mrz: un texto con los 10 caracteres siguientes al URY de la primera fila
#       y los 14 de las dos primeras fechas
#       por ejemplo  "000057A29784011210401127"
def obtener_img(ruta_archivo_formato,mrz):
    #print(vars(r))
    lector = PcscReader()
    lector.connect(1)
    #print(vars(lector))
    con = lector._pcsc_connection
    lector._pcsc_connection = con
    #con.transmit()
    res = con.transmit([0x00,0xA4,0x04,0x0C,0x07,0xA0,0x00,0x00,0x02,0x47,0x10,0x01])
    rep = apd.ResponseAPDU(apd.hexListToBin(res[0]), res[1], res[2])
    #print(rep)
    #print(lector.getReaderList())

    p = EPassport(lector,mrz)
    p.doBasicAccessControl()
    passportimage = p['DG2']['A1']['5F2E']
    
    #print(len(passportimage))
    # pasarlo a nivel de la funcion
    #imgfp = io.BytesIO(passportimage)
    #img = Image.open(imgfp)
    #img.save(ruta_archivo_formato)
    return passportimage
