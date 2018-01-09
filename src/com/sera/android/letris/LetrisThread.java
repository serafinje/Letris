package com.sera.android.letris;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.TreeSet;
import java.util.Vector;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.TextView;


/**
 * TODO: Records
 * TODO: Impedir poner en horizontal
 * 
 */
public class LetrisThread extends Thread 
{
	LetrisStatus Juego = new LetrisStatus(this);
	
	private LetrisMain letrisActivity;
	private Handler mHandler;
	
        /** Handle to the surface manager object we interact with */
        private SurfaceHolder mSurfaceHolder;
        private int mCanvasWidth = 320;
        private int mCanvasHeight = 480;
        int mSurfaceWidth = 320;
        int mSurfaceHeight = 430;
        
        // Estado del juego
        private boolean running=false;
        private int speed=5;

        /** The drawable to use as the background of the animation canvas */
        private Bitmap mBackgroundImage;
        Canvas background;
        
        private Letra[][] cuadricula;
        private Vector<Letra> vLetras;
        
        // fallingLetter -> la letra que se va añadiendo arriba
        // fallingColumn -> la columna a la que se añade
        private Letra fallingLetter;
        int fallingColumn;
        
        String currentWord="";
        private Vector<Letra> currentLetters;
        public static TreeSet<String> sDiccionario;
        private boolean bCorrectWord;
        
        // Para la generacion aleatoria de letras
    	static Random r= new Random();
        ArrayList<Letra> randomLetters;

        /*************************** MENUS, CONFIGURACION ***********************/
        /** Pointer to the text view to display "Paused.." etc. */
        private TextView mStatusText;
        public LetrisThread(SurfaceHolder surfaceHolder, Context context)
        {
        	// Burocracia
			this.mSurfaceHolder=surfaceHolder;
			mHandler = new Handler() {
	            @Override
	            public void handleMessage(Message m) {
	                mStatusText.setText(m.getData().getString("text"));
	                mStatusText.setVisibility(m.getData().getInt("viz"));
	            }
	        };
			
			// Inicializamos datos de juego
			cuadricula=new Letra[Juego.ALTO_CUADRICULA][Juego.ANCHO_CUADRICULA];
			vLetras = new Vector<Letra>();
			fallingColumn=0;
			currentLetters = new Vector<Letra>();

			while (sDiccionario==null) {
				Log.w(getClass().getName(),"Esperando a tener palabras");
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}

			bCorrectWord=false;
			
			// Inicializamos array paa letras aleatorias
			ArrayList<Letra> ar = new ArrayList<Letra>();
			Character[] grupo20 = { 'A','E','I','O' };
			Character[] grupo15 = { };
			Character[] grupo10 = { 'B','C','D','F','G','L','M','N','P','R','S','T','U' };
			Character[] grupo1 = { 'H','J','K','Q','V','W','X','Y','Z' } ;
			for (int i=0; i<grupo20.length; i++) {
				for (int j=0; j<20; j++) ar.add( new Letra(grupo20[i],1) );
			}
			for (int i=0; i<grupo15.length; i++) {
				for (int j=0; j<15; j++) ar.add( new Letra(grupo15[i],10) );
			}
			for (int i=0; i<grupo10.length; i++) {
				for (int j=0; j<10; j++) ar.add( new Letra(grupo10[i],15) );
			}
			for (int i=0; i<grupo1.length; i++) {
				ar.add( new Letra (grupo1[i],20) );
			}
			this.randomLetters = ar;

			// Cargamos imagen de fondo de pantalla
			letrisActivity = (LetrisMain)context;
			Resources res = context.getResources();
			mBackgroundImage = BitmapFactory.decodeResource(res,R.drawable.earthrise);
		}

        public void clean()
        {
        	vLetras.removeAllElements();
        	for (int j=0; j<cuadricula.length; j++) {
        		for (int i=0; i<cuadricula[j].length; i++) {
        			cuadricula[j][i]=null;
        		}
        	}
        	
        }
        
        /**
         * Limpiamos datos (por si estamos reinciando partida), y ponemos estado running.
         */
        public void doStart() {
        	this.clean();
        	setState(LetrisStatus.STATE_RUNNING,null);
        	this.running=true;
        }        
        
        /**
         * Pone el juego en un estado determinado.
         * También puede colocar un mensaje en el campo de texto para mensajes.
         * El texto no se aplica directamente sobre el TextView, ya que pertenece a la
         * vista y parece que no es posible.
         * En su lugar, se envía un mensaje mediante el handler, para que sea el metodo
         * handleMessage() el que lo haga. 
         */
        public void setState(int mode, CharSequence message) 
        {
            synchronized (mSurfaceHolder) {
            	Juego.mMode = mode;

                Message msg = mHandler.obtainMessage();
                Bundle b = new Bundle();
                if (message==null) {
                	b.putString("text", "");
                } else {
                	b.putString("text", message.toString());
                }
                if (Juego.mMode == LetrisStatus.STATE_RUNNING) {
                	b.putInt("viz", View.INVISIBLE);
                } else {
                	b.putInt("viz", View.VISIBLE);
                }
                msg.setData(b);
                mHandler.sendMessage(msg);
                Log.w(getClass().getName(),"Poniendo estado "+Juego.mMode+" - "+message);
            }
        }

		public void setRunning(boolean b) {
			this.running=b;
		}

		public void touch(MotionEvent event) {
			if (event.getAction()==MotionEvent.ACTION_DOWN) {
				//switchPause();
				int x = (int)(event.getX()*this.mCanvasWidth/this.mSurfaceWidth);
				int y = (int)(event.getY()*this.mCanvasHeight/this.mSurfaceHeight);
				int evX = x / Letra.LADO;
				int evY = y / Letra.LADO;
				String cordsCorregidas = "CC(" + x + ","+y+") -> ("+evX+","+evY+")";
				Log.w(getClass().getName(), cordsCorregidas);
				
				// Click sobre una letra estable
				if (evX<Juego.ANCHO_CUADRICULA && evY<Juego.ALTO_CUADRICULA && cuadricula[evY][evX]!=null && Juego.mMode!=LetrisStatus.STATE_PAUSE) {
					try {
						Letra l = cuadricula[evY][evX];
						Log.w(getClass().getName(), "Letra "+l.letra);
						if (!l.selected) {
							this.currentWord = this.currentWord + l.letra;
							this.currentLetters.addElement(l);
							l.selected=true;
						} else {
							int pos = this.currentLetters.indexOf(l);
							this.currentLetters.remove(pos);
							if (pos==currentWord.length()-1)
								this.currentWord = this.currentWord.substring(0,pos);
							else                           
								this.currentWord = this.currentWord.substring(0,pos) + this.currentWord.substring(pos+1);
							l.selected=false;
						}
					} catch (Exception e) {
						for (int i=0; i<this.currentLetters.size(); i++) {
							Log.w(getClass().getName(),this.currentLetters.elementAt(i).toString());
						} 
						
						Log.e(getClass().getName(), "Error", e);
					}
					
					// Miramos si la palabra esta en el diccionario
					this.bCorrectWord =wordInDictionary(this.currentWord);					
				}
				
				if (y>(this.mCanvasHeight-Letra.LADO) && y<this.mCanvasHeight)
				{
					if (x<2*Letra.LADO){
						// Click sobre boton de pausa
						Juego.switchPause();
					} else {
						// Click sobre palabra de abajo
						this.currentWord="";
						if (!this.bCorrectWord) {
							// Si la palabra es incorrecta, la borramos y limpiamos
							Iterator<Letra> it = this.currentLetters.iterator();
							while (it.hasNext()) {
								(it.next()).selected=false;
							}
							this.currentLetters.removeAllElements();
						} else {
							// Si la palabra es correcta, borramos todas las letras
							for (int j=0; j<this.cuadricula.length; j++) {
								for (int i=0; i<this.cuadricula[0].length; i++) {
									if (cuadricula[j][i]!=null && this.currentLetters.contains(cuadricula[j][i])) {
										Juego.points += cuadricula[j][i].points;
										this.vLetras.removeElement(cuadricula[j][i]);
										this.currentLetters.removeElement(cuadricula[j][i]);
										cuadricula[j][i]=null;
										j--;
										while (j>0 && cuadricula[j][i]!=null) {
											cuadricula[j][i].falling=true;
											cuadricula[j][i]=null;
											j--;
										}
									}
								}
							}
						} // correctWord
					} // click en palabra
				} // click en barra de abajo
			} // Evento=Pulsacion
		} // touch()

		
		private boolean wordInDictionary(String word)
		{
			boolean encontrado=false;
			encontrado = sDiccionario.contains(word); 
			return encontrado;
		}
		
		
		public void run() {
			//generaLetrasDePrueba();
			while (this.running) {
				// 1) Crear nueva letra si la ultima ya ha tocado fondo
				if (fallingLetter==null || !fallingLetter.falling) {
					int rnd = r.nextInt(this.randomLetters.size());
					fallingLetter = new Letra(this.randomLetters.get(rnd));
					fallingLetter.setPos(fallingColumn,0);
					vLetras.add(fallingLetter);
					fallingColumn = fallingColumn+1;
					if (fallingColumn==Juego.ANCHO_CUADRICULA) {
						fallingColumn=0;
					}
				}
				
				// 2) Actualizar letras que estan cayendo
				if (Juego.mMode == LetrisStatus.STATE_RUNNING) {
					boolean finPartida = dejarCaerLetras(vLetras);
					if (finPartida) {
						letrisActivity.doLose();
					}
				}
				
				// 3) Recibir interacciones
			
				// 4) Dibujar todo
				draw();
			}
			
		}
		
		void generaLetrasDePrueba()
		{
			Letra l= null;
			l = new Letra(this.randomLetters.get(r.nextInt(this.randomLetters.size()))); l.posX=0; 								l.posY=0; 			 					l.letra='0'; vLetras.add(l);
			l = new Letra(this.randomLetters.get(r.nextInt(this.randomLetters.size()))); l.posX=this.mCanvasWidth-Letra.LADO; l.posY=0; 								l.letra='1'; vLetras.add(l);
			l = new Letra(this.randomLetters.get(r.nextInt(this.randomLetters.size()))); l.posX=0; 								l.posY=this.mCanvasHeight-Letra.LADO-1;	l.letra='2'; vLetras.add(l);
			l = new Letra(this.randomLetters.get(r.nextInt(this.randomLetters.size()))); l.posX=this.mCanvasWidth-Letra.LADO; l.posY=this.mCanvasHeight-Letra.LADO-1;	l.letra='3'; vLetras.add(l);
			l = new Letra(this.randomLetters.get(r.nextInt(this.randomLetters.size()))); l.posX=(this.mCanvasWidth-Letra.LADO)/2; 	l.posY=(this.mCanvasHeight-Letra.LADO)/2;	l.letra='4'; vLetras.add(l);
		}
		
		boolean dejarCaerLetras(Vector<Letra> v)
		{
			boolean finPartida=false;
			
            Iterator<Letra> i = v.iterator();
            while (i.hasNext()) {
            	Letra l = i.next();
            	if (l.falling) {
            		l.fall(speed);
            		
            		// Si la posicion coincide con una pos de la cuadricula, miramos si hay algo debajo
            		if (l.posY % Juego.ALTO_CUADRICULA < speed) {
            			int row = l.getRow();
            			int col = l.getCol();
            			
            			if (row==Juego.ALTO_CUADRICULA-1 || (cuadricula[row+1][col]!=null && !cuadricula[row+1][col].falling)) {
            				l.falling=false;
            				cuadricula[row][col]=l;
            				l.setPos(col, row);
                			if (row==0) {
                				finPartida=true;
                			}
                		}
            		}
            	}
            }
            
            return finPartida;
		}
		
		synchronized void draw() {
			Canvas canvas=null;
			try{
				Paint p = new Paint();
				synchronized (mSurfaceHolder) {
					canvas = mSurfaceHolder.lockCanvas(null);

					// Fondo pantalla juego
					Rect r=new Rect(0,0,mCanvasWidth,mCanvasHeight-Letra.LADO);
					canvas.drawBitmap(mBackgroundImage, null, r, null);

					// Puntuación
					p.setColor(Color.RED);
					p.setTextSize(16);
					p.setTypeface(Typeface.create("comic-sans", Typeface.BOLD));
					String points = ""+Juego.points;
					canvas.drawText(points, mCanvasWidth - points.length()*p.getTextSize()/3, p.getTextSize(), p);
					
					// Barra Controles
					RectF r2=new RectF(0,mCanvasHeight-Letra.LADO,mCanvasWidth,mCanvasHeight);
					p.setColor(Color.LTGRAY);
					canvas.drawRoundRect(r2, (float)5, (float)5, p);
					p.setColor(Color.BLACK);
					p.setTextSize(24);
					p.setTypeface(Typeface.DEFAULT_BOLD);
					canvas.drawText("PAUSA", 0, mCanvasHeight - p.getTextSize()/3, p);
					
					// Palabra
					if (!this.currentWord.equals("")) {
						p.setTextSize(32);
						p.setTypeface(Typeface.DEFAULT);
						p.setTextAlign(Paint.Align.CENTER);

						int x1=(int)(mCanvasWidth-this.currentWord.length()*p.getTextSize())/2;
						int y1=mCanvasHeight-Letra.LADO;
						int x2=(int)(mCanvasWidth+this.currentWord.length()*p.getTextSize())/2;
						int y2=mCanvasHeight;
						r2=new RectF(x1,y1,x2,y2);
						if (this.bCorrectWord) {
							p.setColor(Color.GREEN);							
						} else {
							p.setColor(Color.RED);
						}
						canvas.drawRoundRect(r2, (float)0.5, (float)0.5, p);			
						p.setColor(Color.WHITE);
						canvas.drawText(this.currentWord, mCanvasWidth/2, mCanvasHeight - p.getTextSize()/3, p);
					}
				}
				
				// Letras
				synchronized(vLetras) {
					Iterator<Letra> i = vLetras.iterator();
					while (i.hasNext()) {
						Letra l = i.next();
						l.draw(canvas,p);
					}
				}
				//Thread.sleep(1000);
			} 
			//catch(InterruptedException e) {}
			finally {
				if (canvas!=null)
					mSurfaceHolder.unlockCanvasAndPost(canvas);
			}

		}


        /* Callback invoked when the surface dimensions change. */
        public void setSurfaceSize(int width, int height) {

        	// synchronized to make sure these all change atomically
            synchronized (this) {
                mCanvasWidth = width;
                mCanvasHeight = height;
            	Log.w(getClass().getName(),"Cambiando dimensiones: Canvas=("+this.mCanvasWidth+","+this.mCanvasHeight+") / Surface=("+this.mSurfaceWidth+";"+this.mSurfaceHeight+")");
            }
        }
        
        
        
        /**
         * Sets the current difficulty.
         * 
         * @param difficulty
         */
        public void setDifficulty(int difficulty) {
            synchronized (mSurfaceHolder) {
            }
        }


        
        /**
         * Pauses the physics update & animation.
         */
        public void pause() {
            synchronized (mSurfaceHolder) {
                if (Juego.mMode == LetrisStatus.STATE_RUNNING) setState(LetrisStatus.STATE_PAUSE,"Pausa");
            }
        }
        public void unpause() {
            synchronized (mSurfaceHolder) {
                setState(LetrisStatus.STATE_RUNNING,null);
            }
        }

        /**
         * Installs a pointer to the text view used for messages.
         */
        public void setTextView(TextView textView) {
            mStatusText = textView;
        }

}
