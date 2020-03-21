from leer_mrz import leer_mrz
import face_recognition
import cv2
import tkinter as tk
from PIL import Image, ImageTk
import os
import time
import requests
from subprocess import call
import shutil

LARGE_FONT= ("Verdana", 20)
MEDIUM_FONT= ("Verdana", 16)
SMALL_FONT= ("Verdana", 14)

IP = "169.254.220.189"

def lectura_mrz():
    try:
        mrz = leer_mrz()
    except Exception as e:
        mrz=f"Error: No pudo leerse el MRZ correctamente. {e}"
    return mrz

def verificacion_de_cara():
    orig_foto = face_recognition.load_image_file('./Imagenes_Cara/foto.png')
    orig_encodings = face_recognition.face_encodings(orig_foto)[0]

    verificacion=False
    
    call(["fswebcam", "-d", "/dev/video0", "-r", "1280x720", "--no-banner", "./Imagenes_Cara/real.jpg"])
    time.sleep(1)
    
    val_foto=face_recognition.load_image_file('./Imagenes_Cara/real.jpg')
    val_encodings = face_recognition.face_encodings(val_foto)
    
    if val_encodings:
        verificacion = face_recognition.compare_faces([orig_encodings], val_encodings[0])[0]
    else:
        verificacion=False
    print(verificacion)
    return verificacion

class LogIn(tk.Tk):

    def __init__(self, *args, **kwargs):
        
        tk.Tk.__init__(self, *args, **kwargs)
        self.title('LAB TIC V')
        self.geometry('1000x1100')
        container = tk.Frame(self)
        
        container.pack(fill=tk.BOTH, side="top", expand = True)

        container.grid_rowconfigure(0, weight=1)
        container.grid_columnconfigure(0, weight=1)
        
        self.frames = {}
        pages = [StartPage, PageTwo, PageThree, PageFour]

        for F in pages:

            frame = F(container, self)

            self.frames[F] = frame

            frame.grid(row=0, column=0, sticky="nsew")

        self.show_frame(StartPage)

    def show_frame(self, cont):

        frame = self.frames[cont]
        frame.tkraise()
        
def comenzar(self, parent, controller):
    mrz=lectura_mrz()
    controller.show_frame(PageTwo)

def continuar(self, parent, controller, mrz):
    mrz=mrz.replace(" ","").replace("O", "0").replace("O", "0").replace("c","<")
    #print(mrz)
    cod=mrz.split("<")
    cod=list(filter(None, cod))
    #print(cod)
    text1=cod[1]
    text2=cod[2]
    text3=cod[3]
    
    text1=text1[3:13]
    text2=text2[1:]
    text3=text3[0:7]
    
    text=f"{text1}{text2}{text3}"
    print(text)
    verificacion = False
    i=0
    mrz_code = text
    

    if mrz_code[0] == "0":
        #msg = requests.get(f'http://{IP}:8000/?{mrz_code}')
        #print(msg)
    
        #time.sleep(3)    
        #time.sleep(12)    
    
        #res = requests.get(f'http://{IP}:80/foto.png')    
        res=requests.get(f'http://{IP}:8000/?{mrz_code}')
        print(res)
        with open('./Imagenes_Cara/foto.png', 'wb') as out_file:
            out_file.write(res.content)
        
        while (not verificacion) and (i<2):
            verificacion = verificacion_de_cara()
            i=i+1
        if verificacion:
            controller.show_frame(PageFour)
        else:
            controller.show_frame(PageThree)
        
class StartPage(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self,parent)

        label = tk.Label(self, text="\nBuenos dias.\nPara comenzar, ingrese la cedula y Presione Enter.", font=LARGE_FONT)
        label.pack(pady=90,padx=10)

        button = tk.Button(self, text="Enter", font=LARGE_FONT,
                            command=lambda: comenzar(self, parent, controller))
        button.pack(ipadx=30, ipady=20)
        primero =requests.get(f'http://{IP}:8000/?inicio')

class PageTwo(tk.Frame):
    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        mrz = lectura_mrz()
        mrz=mrz.replace(" ","").replace("O", "0").replace("O", "0").replace("c","<")
        mensaje=f"Es tu MRZ = \n{mrz}\n?"
        label2 = tk.Label(self, text=mensaje, font=MEDIUM_FONT)
        label2.pack(pady=20,padx=10)
        
        button1 = tk.Button(self, text="Es Correcto",
            command=lambda: continuar(self, parent, controller, mrz))
        button1.pack(ipadx=30, ipady=20, pady=10)
        
        button2 = tk.Button(self, text="Reintentar",
            command=lambda: self.actualizar(parent, controller, label2, button1))
        button2.pack(ipadx=30, ipady=20, pady=10)
        
    def actualizar(self, parent, controller, label2, button1):
        mrz=lectura_mrz()
        mrz=mrz.replace(" ","").replace("O", "0").replace("O", "0").replace("c","<")
        mensaje=f"Es tu MRZ = \n{mrz}\n?"
        label2.config(text=mensaje)
        button1.config(command=lambda: continuar(self, parent, controller, mrz))
        

class PageThree(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        label3 = tk.Label(self, text="Lamentamos informarle que no \npudimos identificar su rostro, vuelva a intentar.", font=LARGE_FONT)
        label3.pack(pady=100,padx=10)

        button2 = tk.Button(self, text="Volver a comenzar.",
            command=lambda: comenzar(self, parent, controller))
        button2.pack(ipadx=30, ipady=20)
        
class PageFour(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        label4 = tk.Label(self, text="Identidad Verificada! Muchas gracias por su tiempo.", font=LARGE_FONT)
        label4.pack(pady=100,padx=10)

        button3 = tk.Button(self, text="Continuar",
            command=lambda: controller.show_frame(StartPage))
        button3.pack(ipadx=30, ipady=20)


app = LogIn()
app.mainloop()
