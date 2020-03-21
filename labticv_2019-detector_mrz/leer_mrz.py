from passporteye import read_mrz
from picamera import PiCamera
from time import sleep
from imutils import paths
import numpy as np
import argparse
import imutils
import string
import cv2
import pytesseract
from PIL import Image
import base64

def leer_mrz():
    camera = PiCamera()
    #camera.start_preview()
    #sleep(5)
    camera.capture('./Imagenes_Cedula/picture.jpg')
    #camera.stop_preview()
    camera.close()


    #Estos núcleos delimitarán los cuadrados de cada caracter y el rectángulo principal
    rectKernel = cv2.getStructuringElement(cv2.MORPH_RECT, (13, 5))
    sqKernel = cv2.getStructuringElement(cv2.MORPH_RECT, (21, 21))

    imagePath='./Imagenes_Cedula/picture.jpg'
    #Cargamos las imágenes, le modificamos el tamaño y convertimos a escala de grises
            
    image = cv2.imread(imagePath)

    #image = imutils.resize(image, height=1500)
    #cv2.imshow("Rotated (Problematic)", image)
    gray = cv2.cvtColor(image, cv2.COLOR_BGR2GRAY)

    # 'Suavizado' de la imágen utilizand Gaussian, luego aplicamos blackhat morphological operation 
    # blackhat morphological operation permite encontrar regiones oscuras en un fondo claro (texto MRZ)
    gray = cv2.GaussianBlur(gray, (3, 3), 0)
    blackhat = cv2.morphologyEx(gray, cv2.MORPH_BLACKHAT, rectKernel)

    # Utilizamos el operador Scharr para encontrar regiones de la imagen 
    # que no solo son oscuras sobre un fondo claro, sino que también contienen 
    # cambios verticales en el degradado, como la región de texto MRZ.
    gradX = cv2.Sobel(blackhat, ddepth=cv2.CV_32F, dx=1, dy=0, ksize=-1)
    gradX = np.absolute(gradX)
    #Luego tomamos esta imagen de degradado y la escalamos nuevamente dentro del rango [0, 255] 
    (minVal, maxVal) = (np.min(gradX), np.max(gradX))
    gradX = (255 * ((gradX - minVal) / (maxVal - minVal))).astype("uint8")

    # Aplicamos closing operation utilizando el rectángulo kernel
    gradX = cv2.morphologyEx(gradX, cv2.MORPH_CLOSE, rectKernel)
    thresh = cv2.threshold(gradX, 0, 255, cv2.THRESH_BINARY | cv2.THRESH_OTSU)[1]

    # Eliminamos los espacios entre las líneas detectadas. Utilizamos el sqKernel que se encarga de eliminar
    # los espacios entre las líneas del MRZ.
    thresh = cv2.morphologyEx(thresh, cv2.MORPH_CLOSE, sqKernel)
    thresh = cv2.erode(thresh, None, iterations=4)

    #Cuando hacemos la umbralización (https://www.lpi.tel.uva.es/~nacho/docencia/ing_ond_1/trabajos_03_04/sonificacion/cabroa_archivos/umbralizacion.html)
    #eliminamos la posibilidad de que los bordes se puedan agregar en forma erronea 
    #al MRZ. Para esto seteamos los bordes 5% a la derecha y lo mismo a la izquierda.
    p = int(image.shape[1] * 0.05)
    thresh[:, 0:p] = 0
    thresh[:, image.shape[1] - p:] = 0

    # Identificamos los rectangulos que se encontraron en la umbralización
    # y los ordenamos por tamaño
    cnts = cv2.findContours(thresh.copy(), cv2.RETR_EXTERNAL, cv2.CHAIN_APPROX_SIMPLE)[-2]
    cnts = sorted(cnts, key=cv2.contourArea, reverse=True)
    # Iteramos sobre los contornos
    for c in cnts:
        # Seteamos el cuadro delimitador del campo y usamos el contador
        # para computar la relación de aspecto y el radio de cobertura del cuadro delimitador
        # La relación de aspecto (aspect ratio) es el ancho de la caja delimitadora dividida por la altura
        # El radio de cobertura es el ancho del cuadro delimitador, dividido por el nacho de la imágen actual
        (x, y, w, h) = cv2.boundingRect(c)
        ar = w / float(h)
        crWidth = w / float(gray.shape[1])
        # verificamos si el aspect ratio y el radio de cobertura para
        # determinar si estamos analizando la región MRZ
        if ar > 5 and crWidth > 0.3: #La región MRZ debe acparar al menos el 50% de la imágen de entrada
            pX = int((x + w) * 0.03)
            pY = int((y + h) * 0.03)
            (x, y) = (x - pX, y - pY)
            (w, h) = (w + (pX * 2), h + (pY * 2))
            # Extraemos el ROI de la imágen y dibujamos un cuadro delimitador al rededor del MRZ
            roi = image[y:y + h, x:x + w].copy()
            found = True
            #print(crWidth)
            cv2.rectangle(image, (x, y), (x + w, y + h), (0, 255, 0), 2)
            break

    # Imprimimos resultados
    #cv2.imshow("Image", image)
    #cv2.imshow("ROI", roi)

    result = pytesseract.image_to_string(Image.fromarray(roi))
    mrz_string = result.replace("URYOOOOO","URY00000")
    print(mrz_string, '\n')
    #cv2.waitKey(0)
    return mrz_string