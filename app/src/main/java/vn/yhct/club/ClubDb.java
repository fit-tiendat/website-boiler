package vn.yhct.club;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ClubDb extends SQLiteOpenHelper {
    public static final String DB_NAME = "yhct_club.db";
    public static final int DB_VERSION = 3;

    public static class User {
        public long id; public String username; public String role; public long memberId; public String displayName;
    }
    public static class Member {
        public long id; public String code, fullName, phone, email, position, department, joinedAt, status; public int totalPoints;
    }
    public static class ActivityItem {
        public long id; public String title, category, date, description, status; public int points;
    }
    public static class RewardItem {
        public long id; public String memberName, title, date, note;
    }
    public static class NewsItem {
        public long id; public String title, body, date, author;
    }
    public static class AuditItem {
        public long id; public String actor, action, detail, date;
    }

    public ClubDb(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE members(id INTEGER PRIMARY KEY AUTOINCREMENT, code TEXT UNIQUE NOT NULL, full_name TEXT NOT NULL, phone TEXT, email TEXT, position TEXT, department TEXT, joined_at TEXT, status TEXT NOT NULL DEFAULT 'ACTIVE', total_points INTEGER NOT NULL DEFAULT 0)");
        db.execSQL("CREATE TABLE users(id INTEGER PRIMARY KEY AUTOINCREMENT, username TEXT UNIQUE NOT NULL, password_hash TEXT NOT NULL, role TEXT NOT NULL, member_id INTEGER, active INTEGER NOT NULL DEFAULT 1, FOREIGN KEY(member_id) REFERENCES members(id))");
        db.execSQL("CREATE TABLE activities(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, category TEXT, activity_date TEXT NOT NULL, points_default INTEGER NOT NULL DEFAULT 0, description TEXT, status TEXT NOT NULL DEFAULT 'OPEN')");
        db.execSQL("CREATE TABLE participations(id INTEGER PRIMARY KEY AUTOINCREMENT, activity_id INTEGER NOT NULL, member_id INTEGER NOT NULL, points INTEGER NOT NULL, note TEXT, created_by TEXT, created_at TEXT NOT NULL, UNIQUE(activity_id,member_id), FOREIGN KEY(activity_id) REFERENCES activities(id), FOREIGN KEY(member_id) REFERENCES members(id))");
        db.execSQL("CREATE TABLE rewards(id INTEGER PRIMARY KEY AUTOINCREMENT, member_id INTEGER NOT NULL, title TEXT NOT NULL, reward_date TEXT NOT NULL, note TEXT, FOREIGN KEY(member_id) REFERENCES members(id))");
        db.execSQL("CREATE TABLE news(id INTEGER PRIMARY KEY AUTOINCREMENT, title TEXT NOT NULL, body TEXT NOT NULL, published_at TEXT NOT NULL, created_by TEXT)");
        db.execSQL("CREATE TABLE audit(id INTEGER PRIMARY KEY AUTOINCREMENT, actor TEXT, action TEXT NOT NULL, detail TEXT, created_at TEXT NOT NULL)");
        seed(db);
    }

    @Override public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 3) {
            db.execSQL("DROP TABLE IF EXISTS audit"); db.execSQL("DROP TABLE IF EXISTS news"); db.execSQL("DROP TABLE IF EXISTS rewards");
            db.execSQL("DROP TABLE IF EXISTS participations"); db.execSQL("DROP TABLE IF EXISTS activities"); db.execSQL("DROP TABLE IF EXISTS users"); db.execSQL("DROP TABLE IF EXISTS members");
            onCreate(db);
        }
    }

    private void seed(SQLiteDatabase db) {
        long m1 = insertMember(db,"TV001","Nguyễn Văn An","0901000001","an@yhct.local","Chủ nhiệm","Ban Chủ nhiệm","01/01/2025","ACTIVE",145);
        long m2 = insertMember(db,"TV002","Trần Thị Bình","0901000002","binh@yhct.local","Trưởng ban","Ban Chuyên môn","10/02/2025","ACTIVE",110);
        long m3 = insertMember(db,"TV003","Lê Minh Châu","0901000003","chau@yhct.local","Hội viên","Ban Hoạt động","12/03/2025","ACTIVE",65);
        insertUser(db,"admin","Yhct@2026","ADMIN",m1); insertUser(db,"mod","Mod@2026","MOD",m2); insertUser(db,"member","Member@2026","MEMBER",m3);
        long a1=insertActivity(db,"Dưỡng sinh & điều tức","Chuyên môn","15/09/2026",10,"Sinh hoạt chuyên đề về dưỡng sinh, điều tức và chăm sóc sức khỏe chủ động.","OPEN");
        long a2=insertActivity(db,"Khám tư vấn sức khỏe cộng đồng","Thiện nguyện","28/09/2026",20,"Hoạt động tư vấn sức khỏe cộng đồng của câu lạc bộ.","OPEN");
        insertActivity(db,"Nhận biết và sử dụng dược liệu","Tập huấn","12/10/2026",15,"Tập huấn nhận biết dược liệu thông dụng và nguyên tắc sử dụng an toàn.","OPEN");
        insertParticipation(db,a1,m1,10,"Tham gia đầy đủ","admin"); insertParticipation(db,a2,m1,20,"Hỗ trợ chuyên môn","admin");
        ContentValues r=new ContentValues(); r.put("member_id",m1); r.put("title","Hội viên tích cực quý III/2026"); r.put("reward_date","01/09/2026"); r.put("note","Ghi nhận đóng góp tích cực cho hoạt động CLB."); db.insert("rewards",null,r);
        insertNews(db,"Ra mắt ứng dụng quản lý CLB","Ứng dụng quản lý hội viên, hoạt động, điểm và khen thưởng được đưa vào sử dụng thử nghiệm.","01/09/2026","admin");
        insertNews(db,"Chương trình sức khỏe cộng đồng tháng 9","CLB chuẩn bị hoạt động tư vấn sức khỏe và dưỡng sinh cộng đồng.","25/08/2026","mod");
    }

    private long insertMember(SQLiteDatabase db,String code,String name,String phone,String email,String position,String department,String joined,String status,int points){
        ContentValues v=new ContentValues(); v.put("code",code);v.put("full_name",name);v.put("phone",phone);v.put("email",email);v.put("position",position);v.put("department",department);v.put("joined_at",joined);v.put("status",status);v.put("total_points",points); return db.insert("members",null,v);
    }
    private void insertUser(SQLiteDatabase db,String user,String pass,String role,long memberId){ ContentValues v=new ContentValues();v.put("username",user);v.put("password_hash",hash(pass));v.put("role",role);v.put("member_id",memberId);db.insert("users",null,v); }
    private long insertActivity(SQLiteDatabase db,String title,String category,String date,int points,String desc,String status){ ContentValues v=new ContentValues();v.put("title",title);v.put("category",category);v.put("activity_date",date);v.put("points_default",points);v.put("description",desc);v.put("status",status);return db.insert("activities",null,v); }
    private void insertParticipation(SQLiteDatabase db,long activityId,long memberId,int points,String note,String actor){ ContentValues v=new ContentValues();v.put("activity_id",activityId);v.put("member_id",memberId);v.put("points",points);v.put("note",note);v.put("created_by",actor);v.put("created_at",now());db.insertWithOnConflict("participations",null,v,SQLiteDatabase.CONFLICT_IGNORE); }
    private void insertNews(SQLiteDatabase db,String title,String body,String date,String actor){ ContentValues v=new ContentValues();v.put("title",title);v.put("body",body);v.put("published_at",date);v.put("created_by",actor);db.insert("news",null,v); }

    public User authenticate(String username,String password){
        SQLiteDatabase db=getReadableDatabase(); Cursor c=db.rawQuery("SELECT u.id,u.username,u.role,COALESCE(u.member_id,0),COALESCE(m.full_name,u.username) FROM users u LEFT JOIN members m ON m.id=u.member_id WHERE lower(u.username)=lower(?) AND u.password_hash=? AND u.active=1",new String[]{username.trim(),hash(password)});
        User u=null; if(c.moveToFirst()){u=new User();u.id=c.getLong(0);u.username=c.getString(1);u.role=c.getString(2);u.memberId=c.getLong(3);u.displayName=c.getString(4);} c.close(); if(u!=null) log(u.username,"LOGIN","Đăng nhập thành công"); return u;
    }

    public boolean changePassword(String username,String oldPass,String newPass){
        User u=authenticate(username,oldPass); if(u==null||newPass==null||newPass.length()<8) return false;
        ContentValues v=new ContentValues(); v.put("password_hash",hash(newPass)); int n=getWritableDatabase().update("users",v,"id=?",new String[]{String.valueOf(u.id)}); if(n>0)log(username,"PASSWORD_CHANGE","Đổi mật khẩu");return n>0;
    }

    public int count(String table){ Cursor c=getReadableDatabase().rawQuery("SELECT COUNT(*) FROM "+table,null);int n=c.moveToFirst()?c.getInt(0):0;c.close();return n; }
    public int sumPoints(){ Cursor c=getReadableDatabase().rawQuery("SELECT COALESCE(SUM(total_points),0) FROM members WHERE status='ACTIVE'",null);int n=c.moveToFirst()?c.getInt(0):0;c.close();return n; }

    public List<Member> searchMembers(String q){
        List<Member> out=new ArrayList<>(); String like="%"+(q==null?"":q.trim())+"%"; Cursor c=getReadableDatabase().rawQuery("SELECT id,code,full_name,phone,email,position,department,joined_at,status,total_points FROM members WHERE code LIKE ? OR full_name LIKE ? ORDER BY full_name LIMIT 100",new String[]{like,like}); while(c.moveToNext())out.add(readMember(c));c.close();return out;
    }
    public Member getMember(long id){ Cursor c=getReadableDatabase().rawQuery("SELECT id,code,full_name,phone,email,position,department,joined_at,status,total_points FROM members WHERE id=?",new String[]{String.valueOf(id)});Member m=c.moveToFirst()?readMember(c):null;c.close();return m; }
    private Member readMember(Cursor c){ Member m=new Member();m.id=c.getLong(0);m.code=c.getString(1);m.fullName=c.getString(2);m.phone=c.getString(3);m.email=c.getString(4);m.position=c.getString(5);m.department=c.getString(6);m.joinedAt=c.getString(7);m.status=c.getString(8);m.totalPoints=c.getInt(9);return m; }

    public long addMember(String code,String name,String phone,String email,String position,String dept,String actor){
        if(code==null||code.trim().isEmpty()||name==null||name.trim().isEmpty())return -1;
        ContentValues v=new ContentValues();v.put("code",code.trim().toUpperCase(Locale.ROOT));v.put("full_name",name.trim());v.put("phone",phone);v.put("email",email);v.put("position",position);v.put("department",dept);v.put("joined_at",today());v.put("status","ACTIVE");v.put("total_points",0);
        long id=getWritableDatabase().insert("members",null,v); if(id>0)log(actor,"ADD_MEMBER",code+" - "+name);return id;
    }

    public boolean updateMember(long id,String name,String phone,String email,String position,String dept,String status,String actor){ ContentValues v=new ContentValues();v.put("full_name",name);v.put("phone",phone);v.put("email",email);v.put("position",position);v.put("department",dept);v.put("status",status);int n=getWritableDatabase().update("members",v,"id=?",new String[]{String.valueOf(id)});if(n>0)log(actor,"UPDATE_MEMBER",String.valueOf(id));return n>0; }

    public List<ActivityItem> listActivities(){ List<ActivityItem> out=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT id,title,category,activity_date,points_default,description,status FROM activities ORDER BY substr(activity_date,7,4)||substr(activity_date,4,2)||substr(activity_date,1,2) DESC",null);while(c.moveToNext()){ActivityItem a=new ActivityItem();a.id=c.getLong(0);a.title=c.getString(1);a.category=c.getString(2);a.date=c.getString(3);a.points=c.getInt(4);a.description=c.getString(5);a.status=c.getString(6);out.add(a);}c.close();return out; }
    public long addActivity(String title,String category,String date,int points,String desc,String actor){ long id=insertActivity(getWritableDatabase(),title,category,date,points,desc,"OPEN");if(id>0)log(actor,"ADD_ACTIVITY",title);return id; }

    public boolean awardPoints(long activityId,long memberId,int points,String note,String actor){
        SQLiteDatabase db=getWritableDatabase(); db.beginTransaction(); try{
            ContentValues v=new ContentValues();v.put("activity_id",activityId);v.put("member_id",memberId);v.put("points",points);v.put("note",note);v.put("created_by",actor);v.put("created_at",now()); long id=db.insertWithOnConflict("participations",null,v,SQLiteDatabase.CONFLICT_ABORT); if(id<0)return false;
            db.execSQL("UPDATE members SET total_points=total_points+? WHERE id=?",new Object[]{points,memberId}); ContentValues a=new ContentValues();a.put("actor",actor);a.put("action","AWARD_POINTS");a.put("detail","member="+memberId+", activity="+activityId+", points="+points);a.put("created_at",now());db.insert("audit",null,a);db.setTransactionSuccessful();return true;
        }catch(Exception e){return false;} finally{db.endTransaction();}
    }

    public List<String> pointHistory(long memberId){ List<String> out=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT p.points,a.title,a.activity_date,COALESCE(p.note,'') FROM participations p JOIN activities a ON a.id=p.activity_id WHERE p.member_id=? ORDER BY p.id DESC",new String[]{String.valueOf(memberId)});while(c.moveToNext())out.add((c.getInt(0)>=0?"+":"")+c.getInt(0)+" điểm • "+c.getString(1)+" • "+c.getString(2)+(c.getString(3).isEmpty()?"":"\n"+c.getString(3)));c.close();return out; }
    public List<RewardItem> rewards(long memberId){ List<RewardItem> out=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT r.id,m.full_name,r.title,r.reward_date,COALESCE(r.note,'') FROM rewards r JOIN members m ON m.id=r.member_id WHERE (?=0 OR r.member_id=?) ORDER BY r.id DESC",new String[]{String.valueOf(memberId),String.valueOf(memberId)});while(c.moveToNext()){RewardItem r=new RewardItem();r.id=c.getLong(0);r.memberName=c.getString(1);r.title=c.getString(2);r.date=c.getString(3);r.note=c.getString(4);out.add(r);}c.close();return out; }
    public long addReward(long memberId,String title,String note,String actor){ ContentValues v=new ContentValues();v.put("member_id",memberId);v.put("title",title);v.put("reward_date",today());v.put("note",note);long id=getWritableDatabase().insert("rewards",null,v);if(id>0)log(actor,"ADD_REWARD",title+" member="+memberId);return id; }

    public List<NewsItem> listNews(){ List<NewsItem> out=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT id,title,body,published_at,COALESCE(created_by,'') FROM news ORDER BY id DESC LIMIT 100",null);while(c.moveToNext()){NewsItem n=new NewsItem();n.id=c.getLong(0);n.title=c.getString(1);n.body=c.getString(2);n.date=c.getString(3);n.author=c.getString(4);out.add(n);}c.close();return out; }
    public long addNews(String title,String body,String actor){ ContentValues v=new ContentValues();v.put("title",title);v.put("body",body);v.put("published_at",today());v.put("created_by",actor);long id=getWritableDatabase().insert("news",null,v);if(id>0)log(actor,"ADD_NEWS",title);return id; }

    public boolean createOrUpdateUser(String username,String password,String role,long memberId,String actor){
        if(username==null||username.trim().isEmpty()||password==null||password.length()<8)return false; ContentValues v=new ContentValues();v.put("password_hash",hash(password));v.put("role",role);v.put("member_id",memberId);v.put("active",1); SQLiteDatabase db=getWritableDatabase(); Cursor c=db.rawQuery("SELECT id FROM users WHERE lower(username)=lower(?)",new String[]{username.trim()});boolean exists=c.moveToFirst();long id=exists?c.getLong(0):0;c.close();int n;
        if(exists)n=db.update("users",v,"id=?",new String[]{String.valueOf(id)});else{v.put("username",username.trim());n=db.insert("users",null,v)>0?1:0;} if(n>0)log(actor,"UPSERT_USER",username+" role="+role);return n>0;
    }

    public List<AuditItem> audit(){List<AuditItem> out=new ArrayList<>();Cursor c=getReadableDatabase().rawQuery("SELECT id,COALESCE(actor,''),action,COALESCE(detail,''),created_at FROM audit ORDER BY id DESC LIMIT 100",null);while(c.moveToNext()){AuditItem a=new AuditItem();a.id=c.getLong(0);a.actor=c.getString(1);a.action=c.getString(2);a.detail=c.getString(3);a.date=c.getString(4);out.add(a);}c.close();return out;}
    public void log(String actor,String action,String detail){ContentValues v=new ContentValues();v.put("actor",actor);v.put("action",action);v.put("detail",detail);v.put("created_at",now());getWritableDatabase().insert("audit",null,v);}

    public JSONObject exportJson() throws Exception { JSONObject root=new JSONObject();root.put("format","YHCT_BACKUP_V1");root.put("createdAt",now());String[] tables={"members","users","activities","participations","rewards","news","audit"};for(String t:tables)root.put(t,tableToJson(t));return root; }
    private JSONArray tableToJson(String table) throws Exception {JSONArray a=new JSONArray();Cursor c=getReadableDatabase().rawQuery("SELECT * FROM "+table,null);String[] cols=c.getColumnNames();while(c.moveToNext()){JSONObject o=new JSONObject();for(int i=0;i<cols.length;i++){if(c.isNull(i))o.put(cols[i],JSONObject.NULL);else if(c.getType(i)==Cursor.FIELD_TYPE_INTEGER)o.put(cols[i],c.getLong(i));else o.put(cols[i],c.getString(i));}a.put(o);}c.close();return a;}

    public void importBackup(JSONObject root,String actor) throws Exception { if(!"YHCT_BACKUP_V1".equals(root.optString("format")))throw new Exception("Định dạng sao lưu không hợp lệ");SQLiteDatabase db=getWritableDatabase();db.beginTransaction();try{String[] tables={"participations","rewards","news","audit","users","activities","members"};for(String t:tables)db.delete(t,null,null);String[] restore={"members","users","activities","participations","rewards","news","audit"};for(String t:restore)jsonToTable(db,t,root.getJSONArray(t));ContentValues v=new ContentValues();v.put("actor",actor);v.put("action","RESTORE_BACKUP");v.put("detail","Khôi phục dữ liệu từ tệp sao lưu");v.put("created_at",now());db.insert("audit",null,v);db.setTransactionSuccessful();}finally{db.endTransaction();}}
    private void jsonToTable(SQLiteDatabase db,String table,JSONArray arr) throws Exception {for(int i=0;i<arr.length();i++){JSONObject o=arr.getJSONObject(i);ContentValues v=new ContentValues();java.util.Iterator<String> it=o.keys();while(it.hasNext()){String k=it.next();if(o.isNull(k))v.putNull(k);else{Object x=o.get(k);if(x instanceof Number)v.put(k,((Number)x).longValue());else v.put(k,String.valueOf(x));}}db.insertOrThrow(table,null,v);}}

    public int importMembersCsv(String csv,String actor){int ok=0;String[] lines=csv.replace("\r","").split("\n");for(int i=0;i<lines.length;i++){String line=lines[i].trim();if(line.isEmpty())continue;String[] p=parseCsvLine(line);if(i==0&&p.length>1&&p[0].toLowerCase(Locale.ROOT).contains("ma"))continue;if(p.length<2)continue;try{String code=p[0].trim();String name=p[1].trim();String phone=p.length>2?p[2].trim():"";String email=p.length>3?p[3].trim():"";String position=p.length>4?p[4].trim():"Hội viên";String dept=p.length>5?p[5].trim():"";long id=addMember(code,name,phone,email,position,dept,actor);if(id>0)ok++;}catch(Exception ignored){}}log(actor,"IMPORT_CSV","Đã nhập "+ok+" hội viên");return ok;}
    private String[] parseCsvLine(String line){List<String> out=new ArrayList<>();StringBuilder b=new StringBuilder();boolean quote=false;for(int i=0;i<line.length();i++){char ch=line.charAt(i);if(ch=='\"'){if(quote&&i+1<line.length()&&line.charAt(i+1)=='\"'){b.append('\"');i++;}else quote=!quote;}else if((ch==','||ch==';')&&!quote){out.add(b.toString());b.setLength(0);}else b.append(ch);}out.add(b.toString());return out.toArray(new String[0]);}

    public static String hash(String s){try{MessageDigest d=MessageDigest.getInstance("SHA-256");byte[] b=d.digest(("YHCT-CLUB-2026|"+(s==null?"":s)).getBytes(StandardCharsets.UTF_8));StringBuilder x=new StringBuilder();for(byte q:b)x.append(String.format(Locale.US,"%02x",q));return x.toString();}catch(Exception e){return "";}}
    public static String now(){return new SimpleDateFormat("dd/MM/yyyy HH:mm",Locale.getDefault()).format(new Date());}
    public static String today(){return new SimpleDateFormat("dd/MM/yyyy",Locale.getDefault()).format(new Date());}
}
