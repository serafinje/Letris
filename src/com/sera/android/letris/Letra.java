package com.sera.android.letris;

import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Paint.Style;
import android.graphics.drawable.shapes.Shape;

public class Letra extends Shape
{
	public static final int LADO=40;
	char letra;
	boolean falling;
	boolean selected;
	int posX,posY;
	int points;
	 
	public Letra(char l, int puntuacion)
	{
		this.letra=l;
		falling=true;
		selected=false;
		posY=0;
		this.points = puntuacion;
	}

	public Letra(Letra l)
	{
		this.letra=l.letra;
		falling=true;
		selected=false;
		posY=0;
		this.points = l.points;
	}

	public void setPos(int col,int row)
	{
		posX = LADO*col;
		posY = LADO*row;
	}
	
	public int getRow()
	{
		return posY/LADO;
	}
	
	public int getCol() {
		return posX/LADO;
	}
	public void fall(int speed)
	{
		posY+=speed;
	}
	
	@Override
	public void draw(Canvas canvas, Paint p)
	{
		Paint paint = new Paint();
		if (this.selected) {
			paint.setColor(Color.RED);			
		} else {
			paint.setColor(Color.BLUE);			
		}
		Rect rect=new Rect();
		rect.top=posY;
		rect.left=posX;
		rect.right=rect.left+LADO;
		rect.bottom=rect.top+LADO;
		canvas.drawRect(rect, paint);
		paint.setColor(Color.WHITE);
		paint.setStyle(Style.STROKE);
		canvas.drawRect(rect, paint);
		paint.setTextSize(36);
		canvas.drawText(letra+"", posX+8, posY+LADO-7, paint);
	}
	
	public String toString()
	{
		return "("+posX+","+posY+")"+letra;
	}

}
