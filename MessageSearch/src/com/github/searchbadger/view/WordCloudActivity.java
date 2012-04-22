package com.github.searchbadger.view;

import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.github.searchbadger.core.SearchBadgerApplication;
import com.github.searchbadger.util.Message;

import cue.lang.Counter;
import cue.lang.NGramIterator;
import cue.lang.stop.StopWords;


public class WordCloudActivity extends Activity {
	protected CloudView cloudView;
    protected Counter<String> counter;
    protected ProgressDialog progDialog;
    protected ProgressThread progThread;
    protected List<Rect> wordRects = new LinkedList<Rect>();
    protected Rect canvasClip = new Rect();
    protected List<Entry<String, Integer>> entries;
    protected ConcurrentLinkedQueue<WordCloudText> wordCloudList = new ConcurrentLinkedQueue<WordCloudText>();
    protected List<Message> messages;
    protected WordCloudActivity _this;
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        _this = this;

        // create the custom view
		cloudView = new CloudView(this);
		setContentView(cloudView);
		
		messages = SearchBadgerApplication.getSearchModel().getLastThread();
		if(messages == null) return;
		if(messages.size() == 0) return;
		
    
        
        // create and show the progress bar
        progDialog = new ProgressDialog(this);
        progDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progDialog.setMessage("Initializing. Please wait.");
        progDialog.show();
        progThread = new ProgressThread(handler);
        progThread.start();
    }
    

    // handler that will update main UI thread
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            // update the progress bar and redraw the view
            int total = msg.getData().getInt("total");
            cloudView.postInvalidate();
            if (total >= entries.size()){
            	try {
            		progDialog.dismiss();
            	} catch (Exception e) {}
            	
            } 
            
            if(total == 0) {
            	try {
            		progDialog.dismiss();
            	} catch (Exception e) {}
                progDialog = new ProgressDialog(_this);
    	        progDialog.setMax(entries.size());
    	        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    	        progDialog.setMessage("Creating Word Cloud");
    	        progDialog.show();
            }
            progDialog.setProgress(total);
        }
    };
    

    @Override
	protected void onPause() {
		super.onPause();
		// stop the thread
		progThread.setState(ProgressThread.STOP);
    	try {
    		progDialog.dismiss();
    	} catch (Exception e) {}
	}


	// thread that will perform the world cloud generation
    private class ProgressThread extends Thread {	
        
        // Class constants defining state of the thread
        final public static int WAITING = 0;
        final public static int RUNNING = 1;
        final public static int STOP = 2;
        
        protected Handler handler;
        protected int state;
        protected int total;
    
        ProgressThread(Handler h) {
            handler = h;
        }
        
        @Override
        public void run() {
            state = WAITING;   
            total = 0;
			int x = 0;
			int y = 0;
			int padding = 3;
			Paint paint = new Paint();
			Random random = new Random(new Date().getTime());

			// clear the lists in case they are not empty
			wordRects.clear();
			wordCloudList.clear();


			// count the words
	        counter = new Counter<String>();
	        Iterator<Message> iter = messages.iterator();
	        StringBuilder allMsg = new StringBuilder();
	        while(iter.hasNext()) {
	        	Message m = iter.next();
	        	allMsg.append(m.getText().toLowerCase());
	        	allMsg.append(" ");
	        }
	        // TODO seems like the first time this is called is slow
	        for (final String s : new NGramIterator(1, allMsg.toString(), Locale.getDefault(), StopWords.English)) {
	            counter.note(s);
	        }
	        entries = counter.getAllByFrequency();
			updateStatus(total);
	        
			  
			// wait for the the view to be created.
			// TODO there's probably a better way to do this
			canvasClip = new Rect();
			while(state == WAITING || canvasClip.width() == 0) {
				try {
                    Log.d("SearchBadger", "Waiting...");
                    Thread.sleep(50);
                    cloudView.getDrawingRect(canvasClip);
                } catch (InterruptedException e) {
                    Log.e("ERROR", "Thread was Interrupted");
                }
			}

			// get the size of the canvas
            cloudView.getDrawingRect(canvasClip);
			canvasClip.inset(padding, padding); // add padding

			// compute the min and max range of the word counts
			int maxWordCount = 0;
			int minWordCount = Integer.MAX_VALUE;
			int count;
			String word;
	        for(Entry<String, Integer> e : entries) {
	        	count = e.getValue();
	        	if(count > maxWordCount) maxWordCount = count;
	        	if(count < minWordCount) minWordCount = count;
	        }

	        // compute the max range for a valid random integer position
			int rangeMax = Math.max(canvasClip.width(), canvasClip.height());
			
	        // compute the font weight
			// TODO probably better way to get font size ratio
			float maxFontHeight = (float)rangeMax / 8f;
			float minFontHeight = 8f;
	        float weightFont = (maxFontHeight - minFontHeight) / ((float)maxWordCount - (float)minWordCount);

				
	        float fontSize;
	        boolean rotate = false;
	        int color;
	        for(Entry<String, Integer> e : entries) {
	        	
	        	if(state == STOP) return;
	        	if(progDialog == null) return;
	        	
	        	word = e.getKey();
	        	count = e.getValue();
	        	
	        	// compute the text color and size
	        	fontSize = (float)(count - 1) * weightFont + 8.0f;
	        	color = random.nextInt() | 0xff000000;
				//paint.setColor( random.nextInt() | 0xff000000 );
				paint.setTextSize(fontSize);

				// get the text bounds
				Rect rect = new Rect();
				paint.getTextBounds(word, 0, word.length(), rect);
				
				// store the offset of the upper part of the height since origin 
				// starts at (0,0) but part of the letter can be drawn above and below
				int offset_y = rect.top;  
				
				// randomly rotate the text
				rotate = (random.nextInt() % 2) == 0;
				if(rotate) rotateRect90(rect);
				
				// pad the rect
				rect.inset(-2, -2);      
				
				
				/* 
				// get a random open region on the canvas
				do {
					x = random.nextInt(rangeMax);
					y = random.nextInt(rangeMax);
					rect.offsetTo(x, y);
				} while(!isRegionEmpty(rect));
				*/
				
				
				// get a random position inside canvase
				do{

					x = random.nextInt(rangeMax) + padding;
					y = random.nextInt(rangeMax) + padding;
					rect.offsetTo(x, y);
				} while(!canvasClip.contains(rect));
				

	        	//Log.d("SearchBadger", e.getKey() + ": " + e.getValue());
				
				// while the region is not empty, spiral through to find an open position
				int stepInterval = (int)(fontSize / 8f);
				int xBound = stepInterval;
				int yBound = stepInterval;
				int xNew, yNew;
				boolean foundRegion = isRegionEmpty(rect);
				while(!foundRegion) {
					
					// TODO this is not really doing a spiral, it's checking a rectangular bounding box
					//      and increasing the bound if no open region is found
					// check bottom spiral
					yNew = y + yBound;
					for(xNew = -xBound; xNew < xBound; xNew += stepInterval) {
						rect.offsetTo(xNew, yNew);
						if(isRegionEmpty(rect)) {
							foundRegion = true;
							x = xNew;
							y = yNew;
							break;
						}
					}
					if(foundRegion) break;

					
					// check right spiral
					xNew = x + xBound;
					for(yNew = -yBound; yNew < yBound; yNew += stepInterval) {
						rect.offsetTo(xNew, yNew);
						if(isRegionEmpty(rect)) {
							foundRegion = true;
							x = xNew;
							y = yNew;
							break;
						}
					}
					if(foundRegion) break;

					// check top spiral
					yNew = y - yBound;
					for(xNew = -xBound; xNew < xBound; xNew += stepInterval) {
						rect.offsetTo(xNew, yNew);
						if(isRegionEmpty(rect)) {
							foundRegion = true;
							x = xNew;
							y = yNew;
							break;
						}
					}
					if(foundRegion) break;
					
					// check left spiral
					xNew = x - xBound;
					for(yNew = -yBound; yNew < yBound; yNew += stepInterval) {
						rect.offsetTo(xNew, yNew);
						if(isRegionEmpty(rect)) {
							foundRegion = true;
							x = xNew;
							y = yNew;
							break;
						}
					}
					if(foundRegion) break;
					
					
					// increase the spiral
					xBound += stepInterval;
					yBound += stepInterval;
					
					// stop if all the new x and y will be outside the canvas
					Rect newBounds = new Rect(x - xBound, y - yBound, x + xBound, y + yBound);
					if(newBounds.contains(canvasClip)) {
						break;
					}
				}
				if(!foundRegion) {
					total++;
					updateStatus(total);
					continue;
				}
				
				WordCloudText wordCloudText = new WordCloudText(word, rect, rotate, x, y, offset_y, fontSize, color);
				wordCloudList.add(wordCloudText);
				

				// mark the region as occupied
				wordRects.add(rect);
				total++;
				updateStatus(total);
	        }

            
        }
        
        public void updateStatus(int processed) {
        	android.os.Message msg = handler.obtainMessage();
            Bundle b = new Bundle();
            b.putInt("total", processed);
            msg.setData(b);
            handler.sendMessage(msg);
        }
        
        public void setState(int state) {
            this.state = state;
        }
        
    }
    
    protected class WordCloudText {
    	protected String word;
    	protected Rect rect;
    	protected boolean isRotated;
    	protected int x;
    	protected int y;
    	protected int offsetFromOrigin;
    	protected float fontSize;
    	protected int color;
		public WordCloudText(String word, Rect rect, boolean isRotated,
				int x, int y, int offsetFromOrigin, float fontSize, int color) {
			this.word = word;
			this.rect = rect;
			this.isRotated = isRotated;
			this.x = x;
			this.y = y;
			this.offsetFromOrigin = offsetFromOrigin;
			this.fontSize = fontSize;
			this.color = color;
		}
		public float getFontSize() {
			return fontSize;
		}
		public int getColor() {
			return color;
		}
		public String getWord() {
			return word;
		}
		public Rect getRect() {
			return rect;
		}
		public boolean isRotated() {
			return isRotated;
		}
		public int getX() {
			return x;
		}
		public int getY() {
			return y;
		}
		public int getOffsetFromOrigin() {
			return offsetFromOrigin;
		}
    	    	
    }

    
    protected boolean isRegionEmpty(Rect rect) {

    	// make sure the rect is in the canvas
    	if(!canvasClip.contains(rect)) return false;
    	
    	// make sure the rect doesn't intersect other drawn items
    	Iterator<Rect> iter = wordRects.iterator();
    	while(iter.hasNext()) {
    		if(Rect.intersects(rect, iter.next())) return false;
    	}
    	
    	return true;
    }
    
    protected void rotateRect90(Rect rect) {
    	rect.set(rect.left, rect.top - rect.width(), rect.left + rect.height(), rect.top);
    }
    
    
    protected class CloudView extends View{
		public CloudView(Context context){
			super(context);
		}

		@Override protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);
			
			Paint paint = new Paint();
			
			// let the thread know that it can start
			if(progThread != null) progThread.setState(ProgressThread.RUNNING);

			// make the entire canvas white
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			canvas.drawPaint(paint);
			
			//turn anti-aliasing on
			paint.setAntiAlias(true);
			
			Iterator<WordCloudText> iter = wordCloudList.iterator();
			while(iter.hasNext()) {
				WordCloudText w = iter.next();
				
				paint.setColor(w.getColor());
				paint.setTextSize(w.getFontSize());
				if(w.isRotated) {
					// save the canvas so we can restore if we perform a rotation
					canvas.save();
					canvas.rotate(-90, w.getX(), w.getY());
					canvas.drawText(w.getWord(), w.getX()+1 - w.getRect().height(), w.getY()+1 - w.getOffsetFromOrigin(), paint);
					// restore the canvas in case it was rotated
					canvas.restore();
				}
				else {
					canvas.drawText(w.getWord(), w.getX()+1, w.getY()+1 - w.getOffsetFromOrigin(), paint);
				}
			}

		}
	}

}