import tkinter as tk


LARGE_FONT= ("Verdana", 12)
MEDIUM_FONT= ("Verdana", 10)
SMALL_FONT= ("Verdana", 8)

def lectura_mrz(): return "Hola"

def verificacion_de_cara(): return True

class LogIn(tk.Tk):

    def __init__(self, *args, **kwargs):
        
        tk.Tk.__init__(self, *args, **kwargs)
        container = tk.Frame(self)

        container.pack(side="top", fill="both", expand = True)

        container.grid_rowconfigure(0, weight=1)
        container.grid_columnconfigure(0, weight=1)

        self.frames = {}
        pages = [StartPage, PageOne, PageTwo, PageThree, PageFour]

        for F in pages:

            frame = F(container, self)

            self.frames[F] = frame

            frame.grid(row=0, column=0, sticky="nsew")

        self.show_frame(StartPage)

    def show_frame(self, cont):

        frame = self.frames[cont]
        frame.tkraise()

        
class StartPage(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self,parent)

        label = tk.Label(self, text="\nBuenos dias.\nPara comenzar, ingrese la cedula y Presione Enter.", font=LARGE_FONT)
        label.pack(pady=10,padx=10)

        button = tk.Button(self, text="Enter", font=LARGE_FONT,
                            command=lambda: controller.show_frame(PageOne))
        button.pack()


class PageOne(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        label1 = tk.Label(self, text="Leyendo MRZ...", font=LARGE_FONT)
        label1.pack(pady=10,padx=10)

        mrz = lectura_mrz()

        label2 = tk.Label(self, text=f"MRZ = {mrz}", font=MEDIUM_FONT)
        label2.pack(pady=10,padx=10)

        label3 = tk.Label(self, text=f"Si es correcto presione Ok, sino vuelva a comenzar", font=SMALL_FONT)
        label3.pack(pady=10,padx=10)

        button1 = tk.Button(self, text="Ok",
                            command=lambda: controller.show_frame(PageTwo))
        button1.pack()

        button2 = tk.Button(self, text="Volver a empezar",
                            command=lambda: controller.show_frame(StartPage))
        button2.pack()


class PageTwo(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        label4 = tk.Label(self, text="Verificación Facial", font=LARGE_FONT)
        label4.pack(pady=10,padx=10)

        label5 = tk.Label(self, text=f"Estamos leyendo la imagen de tu cedula, por favor espere...", font=SMALL_FONT)
        label5.pack(pady=10,padx=10)

        validation = verificacion_de_cara()

        button3 = tk.Button(self, text="Continuar",
            command=lambda: controller.show_frame(PageThree) if validation else controller.show_frame(PageFour))
        button3.pack()
        

class PageThree(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        label6 = tk.Label(self, text="Okay! Gracias por tu tiempo, por favor pasa a la cabina Numero 1", font=LARGE_FONT)
        label6.pack(pady=10,padx=10)

        button3 = tk.Button(self, text="Volver a comenzar",
                            command=lambda: controller.show_frame(StartPage))
        button3.pack()

class PageFour(tk.Frame):

    def __init__(self, parent, controller):
        tk.Frame.__init__(self, parent)
        label7 = tk.Label(self, text="Lamentamos informarle que no logramos identificarte, por favor vuelva a comenzar o pida asistencia.", font=LARGE_FONT)
        label7.pack(pady=10,padx=10)

        button4 = tk.Button(self, text="Volver a comenzar",
                            command=lambda: controller.show_frame(StartPage))
        button4.pack()

app = LogIn()
app.mainloop()