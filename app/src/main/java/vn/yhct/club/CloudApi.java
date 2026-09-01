package vn.yhct.club;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;

public class CloudApi {
    public static final String BASE = "https://muzibbeouqqvdrayqhkc.supabase.co";
    public static final String KEY = "sb_publishable_s-Tvj4RQ5I1UVqTkvJEJ0A_PJVFfGF0";
    private final SharedPreferences prefs;

    public CloudApi(Context c){ prefs=c.getSharedPreferences("yhct_cloud",Context.MODE_PRIVATE); }
    public String token(){ return prefs.getString("access_token",""); }
    public String userId(){ return prefs.getString("user_id",""); }
    public boolean signedIn(){ return !token().isEmpty() && !userId().isEmpty(); }
    public void signOut(){ prefs.edit().clear().apply(); }

    private String request(String method,String path,String body,boolean auth) throws Exception {
        URL u=new URL(BASE+path); HttpURLConnection c=(HttpURLConnection)u.openConnection();
        c.setRequestMethod(method); c.setConnectTimeout(15000); c.setReadTimeout(20000);
        c.setRequestProperty("apikey",KEY); c.setRequestProperty("Content-Type","application/json"); c.setRequestProperty("Accept","application/json"); c.setRequestProperty("Prefer","return=representation");
        if(auth && signedIn()) c.setRequestProperty("Authorization","Bearer "+token());
        if(body!=null){ c.setDoOutput(true); try(OutputStream os=c.getOutputStream()){ os.write(body.getBytes(StandardCharsets.UTF_8)); } }
        int code=c.getResponseCode(); BufferedReader r=new BufferedReader(new InputStreamReader(code>=200&&code<300?c.getInputStream():c.getErrorStream(),StandardCharsets.UTF_8));
        StringBuilder b=new StringBuilder(); String line; while((line=r.readLine())!=null)b.append(line); r.close();
        if(code<200||code>=300) throw new Exception("HTTP "+code+": "+b);
        return b.toString();
    }

    public JSONObject login(String email,String password) throws Exception {
        JSONObject p=new JSONObject(); p.put("email",email); p.put("password",password);
        JSONObject o=new JSONObject(request("POST","/auth/v1/token?grant_type=password",p.toString(),false));
        JSONObject user=o.getJSONObject("user"); prefs.edit().putString("access_token",o.getString("access_token")).putString("refresh_token",o.optString("refresh_token")).putString("user_id",user.getString("id")).putString("email",user.optString("email")).apply(); return o;
    }

    public JSONObject signUp(String email,String password,String displayName) throws Exception {
        JSONObject data=new JSONObject(); data.put("display_name",displayName);
        JSONObject p=new JSONObject(); p.put("email",email); p.put("password",password); p.put("data",data);
        JSONObject o=new JSONObject(request("POST","/auth/v1/signup",p.toString(),false));
        if(o.has("access_token") && !o.optString("access_token").isEmpty()){
            JSONObject user=o.getJSONObject("user"); prefs.edit().putString("access_token",o.getString("access_token")).putString("refresh_token",o.optString("refresh_token")).putString("user_id",user.getString("id")).putString("email",user.optString("email")).apply();
        }
        return o;
    }

    public JSONObject profile() throws Exception { JSONArray a=new JSONArray(request("GET","/rest/v1/profiles?id=eq."+userId()+"&select=*",null,true)); return a.length()>0?a.getJSONObject(0):new JSONObject(); }
    public JSONArray profiles(String q) throws Exception { String p="/rest/v1/profiles?select=id,email,display_name,avatar_url,bio,member_code,role,position,department,total_points,status&order=display_name.asc&limit=100"; if(q!=null&&!q.trim().isEmpty())p+="&display_name=ilike.*"+enc(q.trim())+"*"; return new JSONArray(request("GET",p,null,true)); }
    public void updateProfile(JSONObject values) throws Exception { request("PATCH","/rest/v1/profiles?id=eq."+userId(),values.toString(),true); }
    public void updateProfileById(String id,JSONObject values) throws Exception { request("PATCH","/rest/v1/profiles?id=eq."+id,values.toString(),true); }

    public JSONArray feed() throws Exception { return new JSONArray(request("GET","/rest/v1/posts?select=id,author_id,kind,content,media_url,visibility,pinned,reaction_count,comment_count,created_at,profiles!posts_author_id_fkey(display_name,avatar_url,position)&order=pinned.desc,created_at.desc&limit=100",null,true)); }
    public void createPost(String content,String kind) throws Exception { JSONObject p=new JSONObject();p.put("author_id",userId());p.put("content",content);p.put("kind",kind);p.put("visibility","CLUB");request("POST","/rest/v1/posts",p.toString(),true); }
    public void deletePost(String id) throws Exception { request("DELETE","/rest/v1/posts?id=eq."+id,null,true); }
    public JSONArray comments(String postId) throws Exception { return new JSONArray(request("GET","/rest/v1/comments?post_id=eq."+postId+"&select=id,content,created_at,author_id,profiles!comments_author_id_fkey(display_name,avatar_url)&order=created_at.asc",null,true)); }
    public void addComment(String postId,String content) throws Exception { JSONObject p=new JSONObject();p.put("post_id",postId);p.put("author_id",userId());p.put("content",content);request("POST","/rest/v1/comments",p.toString(),true); }
    public void react(String postId,String reaction) throws Exception { JSONObject p=new JSONObject();p.put("post_id",postId);p.put("user_id",userId());p.put("reaction",reaction);try{request("POST","/rest/v1/post_reactions?on_conflict=post_id,user_id",p.toString(),true);}catch(Exception e){request("PATCH","/rest/v1/post_reactions?post_id=eq."+postId+"&user_id=eq."+userId(),new JSONObject().put("reaction",reaction).toString(),true);} }
    public void follow(String otherId) throws Exception { JSONObject p=new JSONObject();p.put("follower_id",userId());p.put("following_id",otherId);request("POST","/rest/v1/follows?on_conflict=follower_id,following_id",p.toString(),true); }

    public JSONArray groups() throws Exception { return new JSONArray(request("GET","/rest/v1/groups?select=id,name,description,cover_url,is_private,owner_id,created_at&order=created_at.desc",null,true)); }
    public void createGroup(String name,String description,boolean privateGroup) throws Exception { JSONObject p=new JSONObject();p.put("name",name);p.put("description",description);p.put("is_private",privateGroup);p.put("owner_id",userId());String res=request("POST","/rest/v1/groups?select=id",p.toString(),true);JSONArray a=new JSONArray(res);if(a.length()>0){JSONObject gm=new JSONObject();gm.put("group_id",a.getJSONObject(0).getString("id"));gm.put("user_id",userId());gm.put("member_role","OWNER");request("POST","/rest/v1/group_members",gm.toString(),true);} }
    public void joinGroup(String groupId) throws Exception { JSONObject p=new JSONObject();p.put("group_id",groupId);p.put("user_id",userId());p.put("member_role","MEMBER");request("POST","/rest/v1/group_members",p.toString(),true); }

    public JSONArray events() throws Exception { return new JSONArray(request("GET","/rest/v1/events?select=id,title,description,location,starts_at,ends_at,points,capacity,cover_url,creator_id&order=starts_at.asc",null,true)); }
    public void createEvent(String title,String desc,String location,String startsAt,int points) throws Exception { JSONObject p=new JSONObject();p.put("creator_id",userId());p.put("title",title);p.put("description",desc);p.put("location",location);p.put("starts_at",startsAt);p.put("points",points);request("POST","/rest/v1/events",p.toString(),true); }
    public void attendEvent(String eventId) throws Exception { JSONObject p=new JSONObject();p.put("event_id",eventId);p.put("user_id",userId());p.put("status","GOING");request("POST","/rest/v1/event_attendees?on_conflict=event_id,user_id",p.toString(),true); }
    public void awardPoints(String memberId,String eventId,int points,String reason) throws Exception { JSONObject p=new JSONObject();p.put("user_id",memberId);if(eventId!=null&&!eventId.isEmpty())p.put("event_id",eventId);p.put("points",points);p.put("reason",reason);p.put("created_by",userId());request("POST","/rest/v1/point_ledger",p.toString(),true); }
    public void awardReward(String memberId,String title,String description) throws Exception { JSONObject p=new JSONObject();p.put("user_id",memberId);p.put("title",title);p.put("description",description);p.put("awarded_by",userId());request("POST","/rest/v1/rewards",p.toString(),true); }

    public JSONArray conversations() throws Exception { return new JSONArray(request("GET","/rest/v1/conversation_members?user_id=eq."+userId()+"&select=conversation_id,joined_at,conversations(id,title,is_group,created_at)&order=joined_at.desc",null,true)); }
    public String createConversation(String otherUserId,String title) throws Exception { JSONObject c=new JSONObject();c.put("title",title);c.put("is_group",false);c.put("created_by",userId());JSONArray a=new JSONArray(request("POST","/rest/v1/conversations?select=id",c.toString(),true));String id=a.getJSONObject(0).getString("id");JSONObject m1=new JSONObject();m1.put("conversation_id",id);m1.put("user_id",userId());request("POST","/rest/v1/conversation_members",m1.toString(),true);JSONObject m2=new JSONObject();m2.put("conversation_id",id);m2.put("user_id",otherUserId);request("POST","/rest/v1/conversation_members",m2.toString(),true);return id; }
    public JSONArray messages(String conversationId) throws Exception { return new JSONArray(request("GET","/rest/v1/messages?conversation_id=eq."+conversationId+"&select=id,body,created_at,sender_id,profiles!messages_sender_id_fkey(display_name)&order=created_at.asc&limit=200",null,true)); }
    public void sendMessage(String conversationId,String body) throws Exception { JSONObject p=new JSONObject();p.put("conversation_id",conversationId);p.put("sender_id",userId());p.put("body",body);request("POST","/rest/v1/messages",p.toString(),true); }

    public JSONArray notifications() throws Exception { return new JSONArray(request("GET","/rest/v1/notifications?user_id=eq."+userId()+"&select=*&order=created_at.desc&limit=100",null,true)); }
    public void markNotificationRead(String id) throws Exception { request("PATCH","/rest/v1/notifications?id=eq."+id,new JSONObject().put("is_read",true).toString(),true); }
    public JSONArray releases() throws Exception { return new JSONArray(request("GET","/rest/v1/app_releases?select=*&order=version_code.desc&limit=1",null,true)); }

    private static String enc(String s) throws Exception { return URLEncoder.encode(s,"UTF-8"); }
}
