package com.github.searchbadger.util;

/*
 * This class was taken from the Hackbook android example
 * included in the android Facebook sdk which is
 * licensed under the Apache license
 * 
 * https://github.com/facebook/facebook-android-sdk
 * 
 */

import java.io.BufferedInputStream;
import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.Hashtable;
import java.util.Stack;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.BaseAdapter;

/*
 * Fetch friends profile pictures request via AsyncTask
 */
public class FriendsGetProfilePics {

    Hashtable<String, Bitmap> friendsImages;
    Hashtable<String, String> positionRequested;
    BaseAdapter listener;
    int runningCount = 0;
    Stack<ItemPair> queue;

    /*
     * 15 max async tasks at any given time.
     */
    final static int MAX_ALLOWED_TASKS = 15;

    public FriendsGetProfilePics() {
        friendsImages = new Hashtable<String, Bitmap>();
        positionRequested = new Hashtable<String, String>();
        queue = new Stack<ItemPair>();
    }

    /*
     * Inform the listener when the image has been downloaded. listener is
     * FriendsList here.
     */
    public void setListener(BaseAdapter listener) {
        this.listener = listener;
        reset();
    }

    public void reset() {
        positionRequested.clear();
        runningCount = 0;
        queue.clear();
    }

    /*
     * If the profile picture has already been downloaded and cached, return it
     * else execute a new async task to fetch it - if total async tasks >15,
     * queue the request.
     */
    public Bitmap getImage(String uid, String url) {
        Bitmap image = friendsImages.get(uid);
        if (image != null) {
            return image;
        }
        if (!positionRequested.containsKey(uid)) {
            positionRequested.put(uid, "");
            if (runningCount >= MAX_ALLOWED_TASKS) {
                queue.push(new ItemPair(uid, url));
            } else {
                runningCount++;
                new GetProfilePicAsyncTask().execute(uid, url);
            }
        }
        return null;
    }

    public void getNextImage() {
        if (!queue.isEmpty()) {
            ItemPair item = queue.pop();
            new GetProfilePicAsyncTask().execute(item.uid, item.url);
        }
    }

    /*
     * Start a AsyncTask to fetch the request
     */
    private class GetProfilePicAsyncTask extends AsyncTask<Object, Void, Bitmap> {
        String uid;

        @Override
        protected Bitmap doInBackground(Object... params) {
            this.uid = (String) params[0];
            String url = (String) params[1];
            return getBitmap(url);
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            runningCount--;
            if (result != null) {
                friendsImages.put(uid, result);
                listener.notifyDataSetChanged();
                getNextImage();
            }
        }
    }

    class ItemPair {
        String uid;
        String url;

        public ItemPair(String uid, String url) {
            this.uid = uid;
            this.url = url;
        }
    }


    public static Bitmap getBitmap(String url) {
        Bitmap bm = null;
        try {
            URL aURL = new URL(url);
            URLConnection conn = aURL.openConnection();
            conn.connect();
            InputStream is = conn.getInputStream();
            BufferedInputStream bis = new BufferedInputStream(is);
            bm = BitmapFactory.decodeStream(new FlushedInputStream(is));
            bis.close();
            is.close();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
           // if (httpclient != null) {
           //     httpclient.close();
           // }
        }
        return bm;
    }
    

    static class FlushedInputStream extends FilterInputStream {
        public FlushedInputStream(InputStream inputStream) {
            super(inputStream);
        }

        
        public long skip(long n) throws IOException {
            long totalBytesSkipped = 0L;
            while (totalBytesSkipped < n) {
                long bytesSkipped = in.skip(n - totalBytesSkipped);
                if (bytesSkipped == 0L) {
                    int b = read();
                    if (b < 0) {
                        break; // we reached EOF
                    } else {
                        bytesSkipped = 1; // we read one byte
                    }
                }
                totalBytesSkipped += bytesSkipped;
            }
            return totalBytesSkipped;
        }
    }

}
