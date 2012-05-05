package com.github.searchbadger.view;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.Map.Entry;
import java.util.Random;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import com.github.searchbadger.R;
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
        progDialog = null;

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
    	try {
    		// stop the thread
    		if(progThread != null) progThread.setState(ProgressThread.STOP);
    		progDialog.dismiss();
    	} catch (Exception e) {}
	}


	// thread that will perform the world cloud generation
    private class ProgressThread extends Thread {	
        
        // Class constants defining state of the thread
        //final public static int WAITING = 0;
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
            state = RUNNING;   
            total = 0;
			//int x = 0;
			//int y = 0;
			int xNew = 0;
			int yNew = 0;
			int xStart = -1;
			int yStart = -1;
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
	        
	        // TODO remove
	        // sample words
	        //allMsg = new StringBuilder();
	        //allMsg.append("Freedom of Speech, Press, Religion and Petition   Congress shall make no law respecting an establishment of religion, or prohibiting the free exercise thereof; or abridging the freedom of speech, or of the press; or the right of the people peaceably to assemble, and to petition the Government for a redress of grievances.   Right to keep and bear arms   A well-regulated militia, being necessary to the security of a free State, the right of the people to keep and bear arms, shall not be infringed.   Conditions for quarters of soldiers   No soldier shall, in time of peace be quartered in any house, without the consent of the owner, nor in time of war, but in a manner to be prescribed by law.   Right of search and seizure regulated   The right of the people to be secure in their persons, houses, papers, and effects, against unreasonable searches and seizures, shall not be violated, and no warrants shall issue, but upon probable cause, supported by oath or affirmation, and particularly describing the place to be searched, and the persons or things to be seized.   Provisons concerning prosecution   No person shall be held to answer for a capital, or otherwise infamous crime, unless on a presentment or indictment of a Grand Jury, except in cases arising in the land or naval forces, or in the militia, when in actual service in time of war or public danger; nor shall any person be subject for the same offense to be twice put in jeopardy of life or limb; nor shall be compelled in any criminal case to be a witness against himself, nor be deprived of life, liberty, or property, without due process of law; nor shall private property be taken for public use without just compensation.   Right to a speedy trial, witnesses, etc.   In all criminal prosecutions, the accused shall enjoy the right to a speedy and public trial, by an impartial jury of the State and district wherein the crime shall have been committed, which district shall have been previously ascertained by law, and to be informed of the nature and cause of the accusation; to be confronted with the witnesses against him; to have compulsory process for obtaining witnesses in his favor, and to have the assistance of counsel for his defense.   Right to a trial by jury   In suits at common law, where the value in controversy shall exceed twenty dollars, the right of trial by jury shall be preserved, and no fact tried by a jury shall be otherwise reexamined in any court of the United States, than according to the rules of the common law.   Excessive bail, cruel punishment   Excessive bail shall not be required, nor excessive fines imposed, nor cruel and unusual punishments inflicted.   Rule of construction of Constitution   The enumeration in the Constitution, of certain rights, shall not be construed to deny or disparage others retained by the people.   Rights of the States under Constitution   The powers not delegated to the United States by the Constitution, nor prohibited by it to the States, are reserved to the States respectively, or to the people.");
	        
	        
	        // TODO seems like the first time this is called is slow
	        for (final String s : new NGramIterator(1, allMsg.toString(), Locale.getDefault(), StopWords.English)) {
	            counter.note(s);
	        }
	        entries = counter.getAllByFrequency();
			updateStatus(total);
	        
			  

			// get the size of the canvas
			canvasClip = new Rect();
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
			int canvasWidth = canvasClip.width();
			int canvasHeight = canvasClip.height();
			
	        // compute the font weight
			// TODO probably better way to get font size ratio
			float maxFontHeight = (float)rangeMax / 8f;
			float minFontHeight = 14f;
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
	        	fontSize = (float)(count - 1) * weightFont + minFontHeight;
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

				
				// get a random position inside canvas around center
				if(xStart == -1) {
					do{
						xNew = random.nextInt(canvasWidth/2) + canvasWidth/4;
						yNew = random.nextInt(canvasHeight/2) + canvasHeight/4;
						rect.offsetTo(xNew, yNew);
					} while(!canvasClip.contains(rect));
					xStart = rect.centerX();
					yStart = rect.centerY();
					
				}
				else {
					rect.offsetTo(xStart, yStart);
				}

				// check around an Archimedean spiral from the initial first word center pos
				boolean foundRegion = isRegionEmpty(rect);
				if(!foundRegion) {

			        double xx, yy, rr;
					double iterations = 2 * 100 * Math.PI; // TODO this will cause it to stop after 100 circles
			        for (double iDx = 0; iDx < iterations; iDx += 0.05) {
			        	// TODO could cache how much of the beginning spiral is already filled
			        	
			            rr = 2 * iDx;
			            xx = rr * Math.cos(iDx);
			            yy = rr * Math.sin(iDx);
			            xNew = (int)xx + xStart;
			            yNew = (int)yy + yStart;
			            rect.offsetTo(xNew, yNew);
						if(isRegionEmpty(rect)) {
							foundRegion = true;
							break;
						}
						
						// TODO find a better way to stop earlier
						//if(!canvasClip.contains(xNew,yNew)) break; // stop if we go outside canvas
			        }
				}
				
				// give up
				if(!foundRegion) {
					total = entries.size();
					updateStatus(total);
					return;
				}
				
				// add the word to the list
				WordCloudText wordCloudText = new WordCloudText(word, rect, rotate, xNew, yNew, offset_y, fontSize, color);
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
			
			// start the Word Cloud thread since we now have a canvas
			if(progThread == null) {
		        progThread = new ProgressThread(handler);
		        progThread.start();
			}

			// make the entire canvas white
			paint.setStyle(Paint.Style.FILL);
			paint.setColor(Color.WHITE);
			canvas.drawPaint(paint);
			
			//turn anti-aliasing on
			paint.setAntiAlias(true);

			paint.setColor(Color.RED);


			/*
	        // draw an Archimedean spiral for testing
			Rect c = new Rect();
            cloudView.getDrawingRect(c);
			int canvasWidth = c.width();
			int canvasHeight = c.height();
	        double x, y, r;
			double iterations = 2 * 100 * Math.PI;
	        for (double iDx = 0; iDx < iterations; iDx += 0.02) {
	            r = 2 * iDx;
	            x = r * Math.cos(iDx);
	            y = r * Math.sin(iDx);
	            canvas.drawPoint((float)x + canvasWidth/2, (float)y + canvasHeight/2, paint);
	            if(!c.contains((int) x+ canvasWidth/2, (int)y + canvasHeight/2)) break;
	        }
			if(true) return;
			*/
			
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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.menu_word_cloud, menu);
	    return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
	    // Handle item selection
	    switch (item.getItemId()) {
	    
	        case R.id.menu_save:
	        	SaveWordCloud();
	            return true;
	            
	        default:
	            return super.onOptionsItemSelected(item);
	    }
	}
	
	public void SaveWordCloud() {
		// get path and create SearchBadger folder
		File directory = Environment.getExternalStorageDirectory();
		String image_path = directory + "/SearchBadger/";
		File searchBadgerFile = new File(image_path);
		searchBadgerFile.mkdirs();
		Pattern pattern = Pattern.compile("\\d+");
		
		// construct unique filename by using next highest number
		int maxNumber = 0;
		File[] files = searchBadgerFile.listFiles();
		for (File inFile : files) {
			Matcher matcher = pattern.matcher(inFile.getName());
			if(matcher.find()) {
			    int number = Integer.parseInt(matcher.group());
			    if(number > 0) maxNumber = number;
			}
		}
		maxNumber++;
		String image_name = "world_could " + maxNumber + ".png";

		// save image
		OutputStream outStream = null;
		File file = new File(image_path, image_name);
		try {
			outStream = new FileOutputStream(file);

			Bitmap bitmap = Bitmap.createBitmap(cloudView.getWidth(),
					cloudView.getHeight(), Bitmap.Config.ARGB_8888);
			Canvas canvas = new Canvas(bitmap);
			cloudView.draw(canvas);
			bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
		} catch (IOException e) {
			// TODO add some error message
		}
	}

}