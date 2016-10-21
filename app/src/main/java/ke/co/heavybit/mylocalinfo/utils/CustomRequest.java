package ke.co.heavybit.mylocalinfo.utils;

import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.toolbox.HttpHeaderParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by heavybit on 7/26/2016.
 */
public class CustomRequest extends Request<JSONObject> {

    private Response.Listener<JSONObject> listener;
    private Map<String, String> params;

    public CustomRequest(String url, Map<String, String> params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(Method.GET, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        //Log.e("params", params.toString());
    }

    public CustomRequest(int method, String url, Map<String, String> params, Response.Listener<JSONObject> reponseListener, Response.ErrorListener errorListener) {
        super(method, url, errorListener);
        this.listener = reponseListener;
        this.params = params;
        Log.e("url in == ", url.toString());
        Log.e("params in == ", params.toString());
    }

    //@Override

    //public Map<String, String> getHeaders() throws AuthFailureError {
        /*
        Map<String, String>  params = new HashMap<String, String>();
        String credentials = Config.API_USERNAME;
        String auth = "Basic "
                + Base64.encodeToString(credentials.getBytes(),
                Base64.NO_WRAP);
        params.put("Authorization", auth);
        return params;
        */
        //return null;
    //}

    protected Map<String, String> getParams()
            throws com.android.volley.AuthFailureError {
        //return params;
        return checkParams(params);
    };

    private Map<String, String> checkParams(Map<String, String> map){
        Iterator<Map.Entry<String, String>> it = map.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry<String, String> pairs = (Map.Entry<String, String>)it.next();
            if(pairs.getValue()==null){
                map.put(pairs.getKey(), "");
            }
        }
        return map;
    }

    @Override
    protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
        try {
            String jsonString = new String(response.data, HttpHeaderParser.parseCharset(response.headers));
            Log.e("jsonString", jsonString);
            return Response.success(new JSONObject(jsonString), HttpHeaderParser.parseCacheHeaders(response));
        } catch (UnsupportedEncodingException e) {
            return Response.error(new ParseError(e));
        } catch (JSONException je) {
            return Response.error(new ParseError(je));
        }
    }

    @Override
    protected void deliverResponse(JSONObject response) {
        // TODO Auto-generated method stub
        listener.onResponse(response);
    }

}

